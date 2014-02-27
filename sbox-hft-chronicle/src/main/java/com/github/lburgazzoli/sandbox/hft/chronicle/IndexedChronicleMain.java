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

import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 */
public class IndexedChronicleMain {
    private static final Logger LOGGER   = LoggerFactory.getLogger(IndexedChronicleMain.class);
    private static final String BASEPATH = "./data/chronicle/simple";

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            for (String name : new String[]{BASEPATH + ".data", BASEPATH + ".index"}) {
                File file = new File(name);
                file.delete();
            }

            IndexedChronicle chronicle = new IndexedChronicle(BASEPATH);

            ExcerptAppender appender = chronicle.createAppender();
            appender.startExcerpt();
            appender.write(1);
            appender.writeEnum("TestMessage");
            appender.finish();

            appender.startExcerpt();
            appender.write(2);
            appender.writeEnum("Hello World");
            appender.finish();

            appender.startExcerpt();
            appender.write(3);
            appender.writeEnum("Bye for now");
            appender.finish();

            chronicle.close();

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
