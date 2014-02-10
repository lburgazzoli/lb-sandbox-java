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
import net.openhft.chronicle.tcp.InProcessChronicleSource;
import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ChronicleDefaultTcpSourceMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChronicleDefaultTcpSourceMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            boolean run = true;
            String path = ChronicleTcp.path("default.source");

            ChronicleTools.warmup();
            ChronicleTools.deleteOnExit(path);

            final Chronicle source = new InProcessChronicleSource(new IndexedChronicle(path), ChronicleTcp.PORT);
            final ChronicleTcp.DataWriter writer = new ChronicleTcp.DataWriter(source);

            long start = System.nanoTime();
            for (int i = 1; i <= ChronicleTcp.UPDATES; i++) {
                writer.write(i,"symbol", 99.9, i, 100.1, i + 1);
            }

            long end = System.nanoTime();

            LOGGER.info(
                String.format("Took an average of %.2f us to write %d",
                    (end - start) / ChronicleTcp.UPDATES / 1e3,
                    ChronicleTcp.UPDATES)
            );

            while(run) {
                Thread.sleep(1000);
            }

            source.close();

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
