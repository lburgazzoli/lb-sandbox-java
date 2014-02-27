/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.sandbox.hft.chronicle.tcp.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.Excerpt;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.IndexedChronicle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class NettyHandlerChronicleSink implements Chronicle {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHandlerChronicleSink.class);

    private final Chronicle m_chronicle;
    private final ExcerptAppender m_excerpt;
    private final String m_host;
    private final int m_port;
    private final AtomicBoolean m_connected;
    private Bootstrap m_bootstrap;

    /**
     * c-tor
     *
     * @param chronicle
     * @param host
     * @param port
     */
    public NettyHandlerChronicleSink(final Chronicle chronicle, final String host, final int port) throws IOException {
        m_chronicle = chronicle;
        m_excerpt = m_chronicle.createAppender();
        m_host = host;
        m_port = port;
        m_connected = new AtomicBoolean(false);
        m_bootstrap = null;

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    @Override
    public String name() {
        return m_chronicle.name();
    }

    @NotNull
    @Override
    public Excerpt createExcerpt() throws IOException {
        if(!m_connected.get()) {
            connect();
        }

        return m_chronicle.createExcerpt();
    }

    @NotNull
    @Override
    public ExcerptTailer createTailer() throws IOException {
        if(!m_connected.get()) {
            connect();
        }

        return m_chronicle.createTailer();
    }

    @NotNull
    @Override
    public ExcerptAppender createAppender() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long lastWrittenIndex() {
        return m_chronicle.lastWrittenIndex();
    }

    @Override
    public long size() {
        return m_chronicle.size();
    }

    @Override
    public void close() throws IOException {
        m_bootstrap.group().shutdownGracefully();
    }

    // *************************************************************************
    //
    // *************************************************************************

    private void connect() {
        if(m_bootstrap == null) {
            m_bootstrap = new Bootstrap();
            m_bootstrap.group(new NioEventLoopGroup());
            m_bootstrap.channel(NioSocketChannel.class);
            m_bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            m_bootstrap.option(ChannelOption.TCP_NODELAY,true);
            m_bootstrap.option(ChannelOption.SO_RCVBUF,256 * 1024);

            m_bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(256 * 1024));
            //m_bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            m_bootstrap.option(ChannelOption.ALLOCATOR,new PooledByteBufAllocator(true));

            m_bootstrap.handler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast("handler", new ExcerptHandler());
                }
            });


            InetSocketAddress addr = new InetSocketAddress(m_host,m_port);
            m_bootstrap.connect(addr).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isDone() && future.isSuccess()) {
                        m_connected.set(true);
                        LOGGER.debug("Connected : {}", future.channel());
                    } else if (!future.isSuccess() && !future.isCancelled()) {
                        LOGGER.warn("Error", future.cause());
                        m_connected.set(false);
                    }
                }
            });
        } else {
            throw new RuntimeException("Connection is alive");
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private class ExcerptHandler extends ChannelInboundHandlerAdapter {
        public static final int INDEX_SIZE = 8;
        public static final int SIZE_SIZE = 4;
        public static final int MIN_SIZE = 4 + 8;
        public static final int MIN_SIZE_FIRST = MIN_SIZE + 8;

        private boolean m_first;
        private final ByteOrder m_byteOrder;
        private ByteBuf m_data;


        /**
         * c-tor
         */
        public ExcerptHandler() {
            m_first = true;
            m_data = null;
            m_byteOrder = ByteOrder.nativeOrder();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf index = ctx.alloc().buffer(8);
            index.writeLong(m_chronicle.lastWrittenIndex());
            ctx.writeAndFlush(index);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof ByteBuf) {
                ByteBuf buf = ((ByteBuf)msg).order(m_byteOrder);

                if(m_data == null) {
                    m_data = buf;
                } else {
                    if (m_data.writerIndex() > m_data.maxCapacity() - buf.readableBytes()) {
                        long start = System.nanoTime();
                        ByteBuf oldData = m_data;
                        m_data = ctx.alloc().buffer(oldData.readableBytes() + buf.readableBytes());
                        m_data.writeBytes(oldData);
                        oldData.release();
                        long end = System.nanoTime();

                        LOGGER.debug("reallocation : {} us", end-start);
                    }

                    //int oldrb = m_data.readableBytes();
                    //long start = System.nanoTime();

                    m_data.writeBytes(buf);

                    //long end = System.nanoTime();
                    //int newrb = m_data.readableBytes();

                    //LOGGER.debug("copy : old={}, new={}, delta={}, time={}us", oldrb,newrb,newrb - oldrb,end-start);

                    buf.release();
                }

                decode(m_data);

                if(!m_data.isReadable()) {
                    m_data.release();
                    m_data = null;
                }
            } else {
                ctx.fireChannelRead(msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.warn("Unexpected exception from downstream.", cause);
            ctx.close();
        }

        // *********************************************************************
        //
        // *********************************************************************

        /**
         *
         * @param data
         * @throws Exception
         */
        protected void decode(ByteBuf data) throws Exception {
            int offset;
            int sizeOffset;
            int minSize;

            while(true) {
                offset = data.readerIndex();
                sizeOffset = offset;
                minSize = m_first ? MIN_SIZE_FIRST : MIN_SIZE;

                if(data.readableBytes() >= minSize) {
                    if(m_first) {
                        long index = data.getLong(offset);
                        if (index != m_chronicle.size()) {
                            throw new StreamCorruptedException("Expected index " + m_chronicle.size() + " but got " + index);
                        }

                        sizeOffset += INDEX_SIZE;

                        m_first = false;
                    }
                } else {
                    break;
                }

                int size = data.getInt(sizeOffset);
                switch (size) {
                    case -128:
                        m_data.readerIndex(sizeOffset + SIZE_SIZE);
                        return;
                    case -127:
                        m_data.readerIndex(sizeOffset + SIZE_SIZE);
                        m_excerpt.startExcerpt(((IndexedChronicle)m_chronicle).config().dataBlockSize() - 1);
                        return;
                    default:
                        break;
                }

                if (size > 128 << 20 || size < 0) {
                    throw new StreamCorruptedException("size was " + size);
                }

                if(data.readableBytes() >= size + SIZE_SIZE) {
                    if(data.nioBufferCount() > 0) {
                        m_excerpt.startExcerpt(size);
                        m_excerpt.write(data.nioBuffer(sizeOffset + SIZE_SIZE,size));
                        m_excerpt.finish();
                        m_data.readerIndex(sizeOffset + SIZE_SIZE + size);
                    } else if(data.hasArray()) {
                        m_excerpt.startExcerpt(size);
                        m_excerpt.write(data.array(),sizeOffset + SIZE_SIZE,size);
                        m_excerpt.finish();
                    } else {
                        byte[] buffer = new byte[size];
                        data.readerIndex(sizeOffset + 4);
                        data.readBytes(buffer);

                        m_excerpt.startExcerpt(buffer.length);
                        m_excerpt.write(buffer);
                        m_excerpt.finish();
                    }
                } else {
                    break;
                }
            }
        }
    }
}
