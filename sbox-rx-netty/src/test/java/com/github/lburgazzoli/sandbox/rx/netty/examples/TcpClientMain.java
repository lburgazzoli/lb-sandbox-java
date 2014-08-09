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

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientMain {
    public static final Logger LOGGER = LoggerFactory.getLogger(TcpClientMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public void run(final String[] args) throws Exception {
        RxClient<String,String> client = RxNetty.createTcpClient(
            "www.google.com",
            80,
            PipelineConfigurators.textOnlyConfigurator());

        client.connect()
            .doOnCompleted(() -> {
                System.out.println("--");
                LOGGER.info("doOnCompleted");
            })
            .doOnError((final Throwable error) -> {
                System.out.println("--");
                LOGGER.warn("doOnError", error);
            })
            .doOnNext((final ObservableConnection<String,String> cnx) -> {
                System.out.println("--");
                LOGGER.warn("doOnNext", cnx);
            }
        );
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
