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
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tcp.InProcessChronicleSink;
import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ChronicleDefaultTcpSinkMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChronicleDefaultTcpSinkMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            boolean run = true;
            String path = ChronicleDefaultTcp.path("default.sink");

            ChronicleTools.warmup();
            ChronicleTools.deleteOnExit(path);

            final Chronicle sink = new InProcessChronicleSink(new IndexedChronicle(path),"localhost",ChronicleDefaultTcp.PORT);
            final ChronicleDefaultTcp.DataReader reader = new ChronicleDefaultTcp.DataReader(sink);

            long start = System.nanoTime();
            while (reader.count() < ChronicleDefaultTcp.UPDATES) {
                reader.read();
            }

            long end = System.nanoTime();

            LOGGER.info(
                String.format("Took an average of %.2f us to read %d",
                    (end - start) / ChronicleDefaultTcp.UPDATES / 1e3,
                    reader.count())
            );

            sink.close();
        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
