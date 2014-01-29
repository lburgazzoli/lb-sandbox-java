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
package com.github.lburgazzoli.sandbox.hft.chronicle;

import net.openhft.chronicle.Excerpt;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tools.ChronicleTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IndexedChronicleMain {
    private static final Logger LOGGER   = LoggerFactory.getLogger(IndexedChronicleMain.class);
    private static final String BASEPATH = "./data/chronicle";

    // *************************************************************************
    //
    // *************************************************************************

    private static void write(int data) throws Exception {
        IndexedChronicle ic = new IndexedChronicle(BASEPATH);
        ExcerptAppender ex = ic.createAppender();
        ex.startExcerpt(4);
        ex.writeInt(data);
        ex.finish();

        LOGGER.debug("write.index = {}",ex.index());
    }

    public static void read() throws Exception {
        IndexedChronicle ic = new IndexedChronicle(BASEPATH);
        Excerpt ex = ic.createExcerpt();

        hasNext(ex);
        hasNext(ex);
        hasNext(ex);
    }

    public static boolean hasNext(Excerpt ex) throws Exception {
        long    index   = ex.index();
        boolean hasNext = ex.nextIndex();
        ex.index(index);

        LOGGER.debug("index : hasNext={},before={},after={}",hasNext,index,ex.index());

        return hasNext;
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            write(0);
            write(1);
            write(2);

            read();

            ChronicleTools.deleteOnExit(BASEPATH);

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
