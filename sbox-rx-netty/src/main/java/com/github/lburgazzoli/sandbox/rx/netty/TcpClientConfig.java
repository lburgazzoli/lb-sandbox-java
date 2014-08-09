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

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.pipeline.PipelineConfigurator;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TcpClientConfig {

    private PipelineConfigurator<ByteBuf, ByteBuf> m_pipeline;
    private List<InetSocketAddress> m_addresses;
    private TimeUnit m_timeUnit;
    private long m_reconnectDelay;
    private long m_reconnectDelayMax;
    private int m_reconnectDelayMultiplier;
    private boolean m_reconnectDelayExponential;
    private int m_maxReconnectAttempt;
    private double m_collisionAvoidanceFactor;
    private long m_readIdleTimeout;
    private long m_writeIdleTimeout;

    public TcpClientConfig() {
        m_pipeline = null;
        m_addresses = Lists.newLinkedList();
        m_reconnectDelay = 5;
        m_reconnectDelayMax = 20;
        m_reconnectDelayMultiplier = 5;
        m_maxReconnectAttempt = Integer.MAX_VALUE;
        m_reconnectDelayExponential = false;
        m_collisionAvoidanceFactor = 0d;
        m_readIdleTimeout = 0;
        m_writeIdleTimeout = 0;
        m_timeUnit = TimeUnit.MILLISECONDS;
    }

    // *************************************************************************
    //
    // *************************************************************************

    public TcpClientConfig pipeline(
        final PipelineConfigurator<ByteBuf, ByteBuf> pipeline) {
        m_pipeline = pipeline;
        return this;
    }

    public PipelineConfigurator<ByteBuf, ByteBuf> pipeline() {
        return m_pipeline;
    }

    public TcpClientConfig addresses(final List<InetSocketAddress> addresses) {
        m_addresses.clear();
        m_addresses.addAll(addresses);

        return this;
    }

    public TcpClientConfig addresses(final InetSocketAddress...addresses) {
        m_addresses.clear();
        for(final InetSocketAddress address : addresses) {
            m_addresses.add(address);
        }

        return this;
    }

    public List<InetSocketAddress> addresses() {
        return m_addresses;
    }

    public TimeUnit timeUnit() {
        return m_timeUnit;
    }

    public TcpClientConfig timeUnit(TimeUnit timeUnit) {
        if(m_timeUnit.compareTo(timeUnit) != 0) {
            m_reconnectDelay = timeUnit.convert(m_reconnectDelay, m_timeUnit);
            m_reconnectDelayMax = timeUnit.convert(m_reconnectDelayMax, m_timeUnit);
            m_readIdleTimeout = timeUnit.convert(m_readIdleTimeout, m_timeUnit);
            m_writeIdleTimeout = timeUnit.convert(m_writeIdleTimeout, m_timeUnit);

            m_timeUnit = timeUnit;
        }

        return this;
    }

    public TcpClientConfig reconnectDelay(long reconnectDelay) {
        m_reconnectDelay = reconnectDelay;

        return this;
    }

    public TcpClientConfig reconnectDelay(long reconnectDelay, final TimeUnit timeUnit) {
        return reconnectDelay(m_timeUnit.convert(reconnectDelay, timeUnit));
    }

    public long reconnectDelay() {
        return m_reconnectDelay;
    }

    public TcpClientConfig reconnectDelayMax(long reconnectDelayMax) {
        m_reconnectDelayMax = reconnectDelayMax;

        return this;
    }

    public TcpClientConfig reconnectDelayMax(long reconnectDelayMax, final TimeUnit timeUnit) {
        return reconnectDelayMax(m_timeUnit.convert(reconnectDelayMax, timeUnit));
    }

    public long reconnectDelayMax() {
        return m_reconnectDelayMax;
    }

    public TcpClientConfig reconnectDelayMultiplier(int reconnectDelayMultiplier) {
        m_reconnectDelayMultiplier = reconnectDelayMultiplier;

        return this;
    }

    public int reconnectDelayMultiplier() {
        return m_reconnectDelayMultiplier;
    }

    public TcpClientConfig reconnectDelayExponential(boolean reconnectDelayExponential) {
        m_reconnectDelayExponential = reconnectDelayExponential;

        return this;
    }

    public boolean reconnectDelayExponential() {
        return m_reconnectDelayExponential;
    }

    public TcpClientConfig collisionAvoidancePercent(short collisionAvoidancePercent) {
        m_collisionAvoidanceFactor = collisionAvoidancePercent * 0.01d;

        return this;
    }

    public short collisionAvoidancePercent() {
        return (short) Math.round(m_collisionAvoidanceFactor * 100);
    }

    public TcpClientConfig collisionAvoidanceFactor(double collisionAvoidanceFactor) {
        m_collisionAvoidanceFactor = collisionAvoidanceFactor;

        return this;
    }

    public double collisionAvoidanceFactor() {
        return m_collisionAvoidanceFactor;
    }

    public TcpClientConfig readIdleTimeout(long readIdleTimeout) {
        m_readIdleTimeout = readIdleTimeout;

        return this;
    }

    public TcpClientConfig readIdleTimeout(long readIdleTimeout, final TimeUnit timeUnit) {
        return readIdleTimeout(m_timeUnit.convert(readIdleTimeout, timeUnit));
    }

    public long readIdleTimeout() {
        return m_readIdleTimeout;
    }

    public TcpClientConfig writeIdleTimeout(long writeIdleTimeout) {
        m_writeIdleTimeout = writeIdleTimeout;

        return this;
    }

    public TcpClientConfig writeIdleTimeout(long writeIdleTimeout, final TimeUnit timeUnit) {
        return writeIdleTimeout(m_timeUnit.convert(writeIdleTimeout, timeUnit));
    }

    public long writeIdleTimeout() {
        return m_writeIdleTimeout;
    }

    public int maxReconnectAttempt() {
        return m_maxReconnectAttempt;
    }

    public TcpClientConfig maxReconnectAttempt(int maxReconnectAttempt) {
        m_maxReconnectAttempt = maxReconnectAttempt;
        return this;
    }
}
