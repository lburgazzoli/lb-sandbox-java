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
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.Excerpt;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptCommon;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tools.WrappedExcerpt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class NettyChronicleSink implements Chronicle {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChronicleSink.class);

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
    public NettyChronicleSink(final Chronicle chronicle,final String host,final int port) throws IOException {
        m_chronicle = chronicle;
        m_excerpt = m_chronicle.createAppender();
        m_host = host;
        m_port = port;
        m_connected = new AtomicBoolean(false);
        m_bootstrap = null;
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
            m_bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            m_bootstrap.option(ChannelOption.TCP_NODELAY,true);
            m_bootstrap.option(ChannelOption.SO_RCVBUF,256 * 1024);


            if(true) {
                m_bootstrap.option(
                    ChannelOption.ALLOCATOR,
                    new PooledByteBufAllocator(true));
            }


            m_bootstrap.handler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast("decoder",new ExcerptDecoder());
                    channel.pipeline().addLast("handler",new ExcerptHandler());
                }
            });


            InetSocketAddress addr = new InetSocketAddress(m_host,m_port);
            m_bootstrap.connect(addr).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isDone() && future.isSuccess()) {
                        m_connected.set(true);
                        LOGGER.debug("Connected : {}", future.channel());
                    }
                    else if (!future.isSuccess() && !future.isCancelled()) {
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
    private class ExcerptDecoder extends ByteToMessageDecoder {

        private boolean m_first;

        /**
         * c-tor
         */
        public ExcerptDecoder() {
            m_first = true;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int offset = in.readerIndex();
            int sizeOffset = offset;
            long index = -9;

            if(in.readableBytes() > (m_first ? 12 : 4)) {
                if(m_first) {
                    index = in.getLong(offset);
                    if (index != m_chronicle.size()) {
                        throw new StreamCorruptedException("Expected index " + m_chronicle.size() + " but got " + index);
                    }
                    sizeOffset += 8;

                    m_first = false;
                }
            }

            int size = in.getInt(sizeOffset);

            LOGGER.debug("First {}, offset={}, sizeOffset={}, readableBytes={}, index={}, size={}",
                m_first,offset,sizeOffset,in.readableBytes(),index,size);

            if (size > 128 << 20 || size < 0) {
                throw new StreamCorruptedException("size was " + size);
            }

            if(in.readableBytes() >= size) {
                byte[] buffer = new byte[size];
                in.readerIndex(sizeOffset + 4);
                in.readBytes(buffer);
                out.add(buffer);
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private class ExcerptHandler extends SimpleChannelInboundHandler<byte[]> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.debug("channelActive, lastWrittenIndex={}",m_chronicle.lastWrittenIndex());

            ByteBuf index = ctx.alloc().buffer(8);
            index.writeLong(m_chronicle.lastWrittenIndex());

            ChannelFuture f = ctx.writeAndFlush(index).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    LOGGER.debug("channelActive, lastIndex written");
                }
            });
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] data) throws Exception {
            m_excerpt.startExcerpt(data.length);
            m_excerpt.write(data);
            m_excerpt.finish();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.warn("Unexpected exception from downstream.", cause);
            ctx.close();
        }
    }
}
