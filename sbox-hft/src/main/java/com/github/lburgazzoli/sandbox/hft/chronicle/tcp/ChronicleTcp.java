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

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class ChronicleTcp {
    public static final String BASEPATH    = "./data/chronicle/tcp";
    public static final int    UPDATES     = 1000000;
    public static final int    PORT        = 12345;
    public static final char   CODE_PX     = 'P';

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param id
     * @return
     */
    public static String path(final String id) {
        return BASEPATH + "/" +  id;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static final class DataWriter {
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
    public static final class DataReader {
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
}
