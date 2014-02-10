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


import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.Excerpt;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NettyChronicleSink implements Chronicle {
    private final Chronicle m_chronicle;
    private final String m_host;
    private final int m_port;

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
    }

    @Override
    public String name() {
        return null;
    }

    @NotNull
    @Override
    public Excerpt createExcerpt() throws IOException {
        return null;
    }

    @NotNull
    @Override
    public ExcerptTailer createTailer() throws IOException {
        return null;
    }

    @NotNull
    @Override
    public ExcerptAppender createAppender() throws IOException {
        return null;
    }

    @Override
    public long lastWrittenIndex() {
        return 0;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void close() throws IOException {
    }
}
