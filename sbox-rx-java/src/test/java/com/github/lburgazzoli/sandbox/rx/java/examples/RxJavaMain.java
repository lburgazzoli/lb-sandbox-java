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
package com.github.lburgazzoli.sandbox.rx.java.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.CountDownLatch;

/**
 * @author lburgazzoli
 */
public class RxJavaMain {
    public static final Logger LOGGER = LoggerFactory.getLogger(RxJavaMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public void run(final String[] args) throws Exception {
        final int items = 100;
        final PublishSubject<Integer> ob = PublishSubject.create();
        final CountDownLatch latch = new CountDownLatch(100);

        ob.observeOn(Schedulers.newThread());
        ob.observeOn(Schedulers.io()).subscribe(arg -> {
            if (arg % 5 == 0) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }

            LOGGER.info("> {}", arg);
            latch.countDown();
        });

        for(int i=0;i<100;i++) {
            if (i % 4 == 0) {
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                }
            }

            LOGGER.info("before # {}", i);
            ob.onNext(i);
            LOGGER.info("after  # {}", i);
        }

        latch.await();
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            new RxJavaMain().run(args);
        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }
    }
}
