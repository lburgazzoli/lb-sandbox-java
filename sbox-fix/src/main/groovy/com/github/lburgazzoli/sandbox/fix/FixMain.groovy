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
package com.github.lburgazzoli.sandbox.fix

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import quickfix.*

class FixMain {

    private static final Logger LOGGER   = LoggerFactory.getLogger(FixMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        SocketInitiator initiator = null;
        try {
            final def settings = new SessionSettings(args[0]);

            initiator = new SocketInitiator(
                new FixApp(),
                new MemoryStoreFactory(),
                settings,
                new SLF4JLogFactory(settings),
                new DefaultMessageFactory());

            initiator.start();

            while(!initiator.isLoggedOn()) {
                try {
                    Thread.sleep(1000);
                    System.out.println("Loggeed: " + initiator.isLoggedOn());
                } catch (InterruptedException e) {
                    LOGGER.warn("Exception",e);
                }
            }

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        } finally {
            if (initiator != null) {
                initiator.stop(true);
            }
        }
    }
}
