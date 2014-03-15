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

import com.github.lburgazzoli.sandbox.hft.chronicle.tcp.netty.NettyCodecChronicleSink;
import com.github.lburgazzoli.sandbox.hft.chronicle.tcp.netty.NettyHandlerChronicleSink;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tcp.InProcessChronicleSink;
import net.openhft.chronicle.tools.ChronicleTools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ChronicleTcpSinkMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChronicleTcpSinkMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {

            if(args.length == 1) {
                boolean run = true;
                String path = ChronicleTcp.path("tcp.sink");

                ChronicleTools.warmup();

                for(int i=0;i<ChronicleTcp.LOOPS;i++) {
                    ChronicleTools.deleteOnExit(path);

                    Chronicle sink = null;
                    if(StringUtils.equalsIgnoreCase("default",args[0])) {
                        sink = new InProcessChronicleSink(new IndexedChronicle(path),"localhost", ChronicleTcp.PORT);
                    } else if(StringUtils.equalsIgnoreCase("codec",args[0])) {
                        sink = new NettyCodecChronicleSink(new IndexedChronicle(path),"localhost", ChronicleTcp.PORT);
                    } else if(StringUtils.equalsIgnoreCase("handler",args[0])) {
                        sink = new NettyHandlerChronicleSink(new IndexedChronicle(path),"localhost", ChronicleTcp.PORT);
                    }

                    final ChronicleTcp.DataReader reader = new ChronicleTcp.DataReader(sink);

                    long start = System.nanoTime();
                    while (reader.count() < ChronicleTcp.UPDATES) {
                        reader.read();
                    }

                    long end = System.nanoTime();

                    LOGGER.info(
                        String.format("Took an average of %.2f us to read %d",
                            (end - start) / ChronicleTcp.UPDATES / 1e3,
                            reader.count())
                    );

                    sink.close();
                }
            }
        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
