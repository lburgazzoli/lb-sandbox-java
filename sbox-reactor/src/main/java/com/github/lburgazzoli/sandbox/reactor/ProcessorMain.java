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
package com.github.lburgazzoli.sandbox.reactor;

import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.processor.Operation;
import reactor.core.processor.Processor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.function.Consumer;
import reactor.function.Supplier;


import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ProcessorMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorMain.class);


    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private static class Message {
        public int type = -1;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     *
     */
    private static class MessageSupplier implements Supplier<Message> {
        public Message get() {
            return new Message();
        }
    }

    /**
     *
     */
    private static class ThrottlingMessageConsumer implements Consumer<Message> {
        public ThrottlingMessageConsumer(int permitsPerSecond) {
        }

        public void accept(Message message) {
            LOGGER.debug("{}", message);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            final Processor<Message> processor = new ProcessorSpec<Message>()
                .dataSupplier(new MessageSupplier())
                .consume(new ThrottlingMessageConsumer(10))
                .singleThreadedProducer()
                .dataBufferSize(1024)
                .get();

            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);

            Thread th = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        Operation<Message> op = processor.get();
                        op.get().type = i;
                        op.commit();
                    }
                }
            });

            th.start();
            th.join();

            processor.shutdown();

            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
