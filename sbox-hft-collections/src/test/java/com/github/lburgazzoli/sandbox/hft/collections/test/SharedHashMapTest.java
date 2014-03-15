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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;

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
