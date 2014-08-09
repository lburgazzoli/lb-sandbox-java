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
package com.github.lburgazzoli.sandbox.rx.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.ClientBuilder;
import io.reactivex.netty.client.RxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TcpClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);

    private final TcpClientConfig m_config;
    private final AtomicBoolean m_active;

    private final Random m_random;
    private final Supplier<InetSocketAddress> m_endpoints;
    private final PublishSubject<ByteBuf> m_dataSubject;
    private final PublishSubject<TcpClient> m_stateSubject;

    private RxClient<ByteBuf,ByteBuf> m_client;
    private Observable<ByteBuf> m_observable;
    private ObservableConnection<ByteBuf,ByteBuf> m_connection;
    private int m_attempt;
    private long m_delay;

    public TcpClient(final TcpClientConfig config) {
        m_config = config;
        m_active = new AtomicBoolean(false);
        m_endpoints = roundRobin(m_config.addresses());
        m_random = new Random();
        m_attempt = 0;
        m_delay = 0;
        m_client = null;
        m_dataSubject = PublishSubject.create();
        m_stateSubject = PublishSubject.create();
    }

    public void start() {
        if(!m_active.get()) {
            connect();
            m_active.set(true);
        }
    }

    public void stop() {
        if(m_active.get()) {
            disconnect();
            m_active.set(false);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    private void connect() {
        m_client = TcpClient.this.client(m_endpoints.get());
        m_observable = (m_attempt > 0)
            ? m_client.connect().delaySubscription(
                    determineReconnectDelay(m_attempt),
                    m_config.timeUnit())
                .flatMap(this::onConnect)
            : m_client.connect()
                .flatMap(this::onConnect);

        m_observable.subscribe(this::onData, this::onError, this::onComplete);
    }

    private void reconnect() {
        if(m_active.get()) {
            LOGGER.debug("reconnect");
            if(m_config.maxReconnectAttempt() > ++m_attempt) {
                connect();
            }
        }
    }

    private void disconnect() {
        m_client.shutdown();
        m_client = null;
    }

    private Observable<ByteBuf> onConnect(final ObservableConnection<ByteBuf,ByteBuf> connection) {
        LOGGER.info("onConnect");

        m_attempt = 0;
        m_connection = connection;

        m_stateSubject.onNext(this);
        return m_connection.getInput();
    }

    private void onData(final ByteBuf data) {
        LOGGER.info("onData");
        m_dataSubject.onNext(data);
    }

    private void onError(final Throwable error) {
        LOGGER.info("onError {}", error.getMessage());

        reconnect();
    }

    private void onComplete() {
        LOGGER.info("onComplete");
        m_stateSubject.onCompleted();
        reconnect();
    }

    // *************************************************************************
    //
    // *************************************************************************

    private RxClient<ByteBuf,ByteBuf> client(final InetSocketAddress address) {
        final ClientBuilder<ByteBuf, ByteBuf> builder =
            RxNetty.newTcpClientBuilder(
                address.getHostName(),
                address.getPort()
        );

        builder.channelOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
        builder.channelOption(ChannelOption.SO_KEEPALIVE, true);
        builder.channelOption(ChannelOption.TCP_NODELAY, true);
        builder.withIdleConnectionsTimeoutMillis(m_config.readIdleTimeout());
        builder.pipelineConfigurator(m_config.pipeline());
        builder.enableWireLogging(null);

        return builder.build();
    }

    private long determineReconnectDelay(int attempt) {
        if (m_config.maxReconnectAttempt() > attempt) {
            long nextDelay = m_config.reconnectDelay();

            if (m_delay > 0) {
                if (m_config.reconnectDelayExponential() && m_config.reconnectDelayMultiplier() > 1) {
                    nextDelay = m_delay * m_config.reconnectDelayMultiplier();
                    if (m_config.reconnectDelayMax() != -1 && nextDelay > m_config.reconnectDelayMax()) {
                        nextDelay = Math.max(m_config.reconnectDelayMax(), m_config.reconnectDelay());
                    }
                } else {
                    nextDelay = m_delay;
                }
            }

            if (m_config.collisionAvoidanceFactor() > 0) {
                nextDelay += (nextDelay
                    * (m_random.nextBoolean()
                    ? m_config.collisionAvoidanceFactor()
                    : -m_config.collisionAvoidanceFactor())
                    * m_random.nextDouble()
                );
            }

            return nextDelay;
        }

        return 0;
    }

    private <T> Supplier<T> roundRobin(final List<T> objs) {
        final AtomicInteger count = new AtomicInteger();
        final int len = objs.size();

        return () -> objs.get(count.getAndIncrement() % len);
    }
}
