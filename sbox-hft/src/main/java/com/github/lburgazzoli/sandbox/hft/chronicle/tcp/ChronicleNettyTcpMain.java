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
package com.github.lburgazzoli.sandbox.hft.chronicle.tcp;

import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ChronicleNettyTcpMain {
    private static final Logger LOGGER      = LoggerFactory.getLogger(ChronicleNettyTcpMain.class);
    private static final String BASEPATH    = "./data/chronicle/tcp";
    private static final String PATH_SOURCE = BASEPATH + "/netty.source";
    private static final String PATH_SINK   = BASEPATH + "/netty.sink";
    private static final int    UPDATES     = 1000000;
    private static final int    PORT        = 12345;
    private static final char   CODE_PX     = 'P';

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            ChronicleTools.warmup();
            ChronicleTools.deleteOnExit(PATH_SOURCE);
            ChronicleTools.deleteOnExit(PATH_SINK);

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
