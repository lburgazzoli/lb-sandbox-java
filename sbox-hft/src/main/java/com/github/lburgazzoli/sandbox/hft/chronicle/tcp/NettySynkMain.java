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

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tcp.InProcessChronicleSource;
import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class NettySynkMain {
    private static final Logger LOGGER      = LoggerFactory.getLogger(NettySynkMain.class);
    private static final String BASEPATH    = "./data/chronicle/netty";
    private static final String PATH_SOURCE = BASEPATH + "/netty.source";
    private static final String PATH_SINK   = BASEPATH + "/netty.sink";
    private static final int    PORT        = 12345;


    // *************************************************************************
    //
    // *************************************************************************

    private class DataPublisher {
        private final ExcerptAppender m_excerpt;

        /**
         * c-tor
         *
         * @param chronicle
         * @throws Exception
         */
        public DataPublisher(Chronicle chronicle) throws Exception {
            m_excerpt = chronicle.createAppender();
        }

        public void publish() {

        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            ChronicleTools.warmup();
            ChronicleTools.deleteOnExit(PATH_SOURCE);
            ChronicleTools.deleteOnExit(PATH_SINK);

            final Chronicle source = new InProcessChronicleSource(new IndexedChronicle(PATH_SOURCE),PORT);
            final ExcerptAppender appender = source.createAppender();

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
