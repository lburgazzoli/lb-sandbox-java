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
package com.github.lburgazzoli.sandbox.hft.collections.test;

import net.openhft.collections.SharedHashMap;
import net.openhft.collections.SharedHashMapBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class SharedHashMapTest {

    // *************************************************************************
    //
    // *************************************************************************

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Test
    public void testSharedHashMapConfiguration() throws IOException {
        final File path = new File(FileUtils.getTempDirectoryPath(),"shm-config");
        path.delete();
        path.deleteOnExit();

        final SharedHashMap<Integer,SharedHashMapData> shm = new SharedHashMapBuilder()
            .entries(10)
            .entrySize(64)
            .minSegments(10)
            .create(
                path,
                Integer.class,
                SharedHashMapData.class);


        SharedHashMapData shmd = shm.acquireUsing(0,new SharedHashMapData(64));

        shm.close();

    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * @return
     */
    private static File getPersistenceFile(String name) {
        File file = new File(FileUtils.getTempDirectory(),name);
        file.delete();
        file.deleteOnExit();

        return file;
    }
}
