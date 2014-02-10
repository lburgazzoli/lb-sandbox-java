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
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tcp.InProcessChronicleSink;
import net.openhft.chronicle.tcp.InProcessChronicleSource;
import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class ChronicleDefaultTcpMain {
    private static final Logger LOGGER      = LoggerFactory.getLogger(ChronicleDefaultTcpMain.class);
    private static final String BASEPATH    = "./data/chronicle/tcp";
    private static final String PATH_SOURCE = BASEPATH + "/default.source";
    private static final String PATH_SINK   = BASEPATH + "/default.sink";
    private static final int    UPDATES     = 1000000;
    private static final int    PORT        = 12345;
    private static final char   CODE_PX     = 'P';

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private static class DataWriter {
        private final ExcerptAppender m_excerpt;

        /**
         * c-tor
         *
         * @param chronicle
         * @throws Exception
         */
        public DataWriter(final Chronicle chronicle) throws Exception {
            m_excerpt = chronicle.createAppender();
        }

        /**
         *
         * @param ts
         * @param symbol
         * @param bp
         * @param bq
         * @param ap
         * @param aq
         */
        public void write(long ts, String symbol, double bp, int bq, double ap, int aq) {
            m_excerpt.startExcerpt();
            m_excerpt.writeByte(CODE_PX);
            m_excerpt.writeLong(ts);
            m_excerpt.writeEnum(symbol);
            m_excerpt.writeDouble(bp);
            m_excerpt.writeInt(bq);
            m_excerpt.writeDouble(ap);
            m_excerpt.writeInt(aq);
            m_excerpt.finish();
        }
    }


    /**
     *
     */
    private static class DataReader {
        private final ExcerptTailer m_excerpt;
        private final AtomicInteger m_count;

        /**
         * c-tor
         *
         * @param chronicle
         * @throws Exception
         */
        public DataReader(final Chronicle chronicle) throws Exception {
            m_excerpt = chronicle.createTailer();
            m_count = new AtomicInteger();
        }

        public void read() {
            if(m_excerpt.nextIndex()) {
                char ch = (char) m_excerpt.readByte();
                switch (ch) {
                    case CODE_PX: {
                        long   ts     = m_excerpt.readLong();
                        String symbol = m_excerpt.readEnum(String.class);
                        double bp     = m_excerpt.readDouble();
                        int    bq     = m_excerpt.readInt();
                        double ap     = m_excerpt.readDouble();
                        int    aq     = m_excerpt.readInt();

                        m_count.incrementAndGet();
                        break;
                    }
                    default:
                        throw new AssertionError("Unexpected code " + ch);
                }
            }

        }

        /**
         *
         * @return
         */
        public int count() {
            return m_count.get();
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

            final Chronicle  source = new InProcessChronicleSource(new IndexedChronicle(PATH_SOURCE),PORT);
            final Chronicle  sink   = new InProcessChronicleSink(new IndexedChronicle(PATH_SINK),"localhost",PORT);

            final DataWriter writer = new DataWriter(source);
            final DataReader reader = new DataReader(sink);

            long start = System.nanoTime();
            for (int i = 1; i <= UPDATES; i++) {
                writer.write(i,"symbol", 99.9, i, 100.1, i + 1);
            }

            long mid = System.nanoTime();
            while (reader.count() < UPDATES) {
                reader.read();
            }

            long end = System.nanoTime();

            LOGGER.info(
                String.format("Took an average of %.2f us to write %d and %.2f us to read %d",
                    (mid - start) / UPDATES / 1e3,
                    UPDATES,
                    (end - mid) / UPDATES / 1e3,
                    reader.count())
            );

            source.close();
            sink.close();

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
