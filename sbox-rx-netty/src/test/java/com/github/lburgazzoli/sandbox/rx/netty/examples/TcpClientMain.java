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
package com.github.lburgazzoli.sandbox.rx.netty.examples;

import com.github.lburgazzoli.sandbox.rx.netty.TcpClient;
import com.github.lburgazzoli.sandbox.rx.netty.TcpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class TcpClientMain {
    public static final Logger LOGGER = LoggerFactory.getLogger(TcpClientMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public void run(final String[] args) throws Exception {
        LOGGER.info(">>>> connecting");

        final TcpClient client = new TcpClient(
            new TcpClientConfig()
                .addresses(new InetSocketAddress("localhost",9876))
                .timeUnit(TimeUnit.MILLISECONDS)
                .reconnectDelay(5, TimeUnit.SECONDS)
        );

        client.start();

        for(int i=0;i<100;i++) {
            LOGGER.info(">>>> sleep...");
            Thread.sleep(2500);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            new TcpClientMain().run(args);
        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }
    }
}
