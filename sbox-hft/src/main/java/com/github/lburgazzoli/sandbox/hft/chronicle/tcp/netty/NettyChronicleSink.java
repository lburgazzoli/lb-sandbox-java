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
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.Excerpt;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptCommon;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.tools.WrappedExcerpt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class NettyChronicleSink implements Chronicle {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChronicleSink.class);

    private final Chronicle m_chronicle;
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
    public NettyChronicleSink(final Chronicle chronicle,final String host,final int port) {
        m_chronicle = chronicle;
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
        return new SinkExcerpt(m_chronicle.createExcerpt());
    }

    @NotNull
    @Override
    public ExcerptTailer createTailer() throws IOException {
        return new SinkExcerpt(m_chronicle.createTailer());
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

            if(true) {
                m_bootstrap.option(
                    ChannelOption.ALLOCATOR,
                    new PooledByteBufAllocator(true));
            }


            InetSocketAddress addr = new InetSocketAddress(m_host,m_port);
            m_bootstrap.connect(addr).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isDone() && future.isSuccess()) {
                        m_connected.set(true);
                        LOGGER.debug("Connected : {}",future.channel());
                    } else if (!future.isSuccess() && !future.isCancelled()) {
                        LOGGER.warn("Error", future.cause());
                        m_connected.set(false);
                    }
                }
            });

            /**
            m_bootstrap.handler(new NettyChannelInitializer(this.settings));

            doConnect();
            */
        } else {
            throw new RuntimeException("Connection is alive");
        }
    }

    private boolean readNext() {
        return false;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private class SinkExcerpt extends WrappedExcerpt {

        /**
         * c-tor
         *
         * @param excerpt
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        public SinkExcerpt(ExcerptCommon excerpt) throws IOException {
            super(excerpt);
        }

        @Override
        public boolean nextIndex() {
            return super.nextIndex() || readNext() && super.nextIndex();
        }

        @Override
        public boolean index(long index) throws IndexOutOfBoundsException {
            if (super.index(index)) {
                return true;
            }

            return index >= 0 && readNext() && super.index(index);
        }
    }
}
