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

import com.github.lburgazzoli.sandbox.hft.collections.test.values.NativeLongValue;
import net.openhft.collections.SharedHashMap;
import net.openhft.collections.SharedHashMapBuilder;
import net.openhft.lang.values.LongValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 */
public class SharedHashMapTest {

    private StringBuilder sb;

    // *************************************************************************
    //
    // *************************************************************************

    @Before
    public void before() {
        sb = new StringBuilder();
    }

    @After
    public void after() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    //@Test
    public void testAcquire1() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        int entries = 1000 * 1000;
        SharedHashMap<CharSequence, LongValue> map = getSharedMap(entries, 128, 24);

        LongValue value1 = new NativeLongValue();
        LongValue value2 = new NativeLongValue();
        LongValue value3 = new NativeLongValue();

        for (int j = 1; j <= 3; j++) {
            for (int i = 0; i < entries; i++) {
                CharSequence userCS = getUserCharSequence(i);

                if (j > 1) {
                    assertNotNull(map.getUsing(userCS, value1));
                } else {
                    map.acquireUsing(userCS, value1);
                }

                assertEquals(j - 1, value1.getValue());

                value1.addAtomicValue(1);

                assertEquals(value2, map.acquireUsing(userCS, value2));
                assertEquals(j, value2.getValue());

                assertEquals(value3, map.getUsing(userCS, value3));
                assertEquals(j, value3.getValue());
            }
        }

        map.close();
    }

    @Test
    public void testOverSizedSntries() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        int entries = 1;

        SharedHashMap<Integer,CharSequence> map =
            getSharedMap(entries, 128, 24,Integer.class,CharSequence.class);

        CharSequence cs1 = new StringBuilder("MyValue");
        CharSequence cs2 = map.acquireUsing(1, cs1);
        CharSequence cs3 = map.getUsing(1, new StringBuilder());

        assertNotNull(cs2);
        assertNotNull(cs3);

        assertTrue(cs1 == cs2);
        assertTrue(cs1 != cs3);

        assertEquals("MyValue".length(),cs1.length());
        assertEquals("MyValue".length(),cs2.length());
        assertEquals("MyValue".length(),cs3.length());

        assertEquals("MyValue",cs1.toString());
        assertEquals("MyValue",cs2.toString());
        assertEquals("MyValue",cs3.toString());

        assertEquals(cs1.toString(),cs2.toString());
        assertEquals(cs1.toString(),cs3.toString());

        map.close();
    }

    // *************************************************************************
    //
    // *************************************************************************

    private static SharedHashMap<CharSequence, LongValue> getSharedMap(
        long entries, int segments, int entrySize) throws IOException {
        return getSharedMap(entries,segments,entrySize,CharSequence.class, LongValue.class);
    }

    private static <K,V> SharedHashMap<K,V> getSharedMap(
        long entries, int segments, int entrySize, Class<K> keyType, Class<V> valueType) throws IOException {
        return new SharedHashMapBuilder()
            .entries(entries)
            .segments(segments)
            .entrySize(entrySize) // TODO not enough protection from over sized entries.
            .create(
                getPersistenceFile(),
                keyType,
                valueType);
    }

    private CharSequence getUserCharSequence(int i) {
        sb.setLength(0);
        sb.append("user:");
        sb.append(i);

        return sb;
    }

    private static File getPersistenceFile() {
        String TMP = System.getProperty("java.io.tmpdir");
        File file = new File(TMP,"hft-collections-shm-test");
        file.delete();
        file.deleteOnExit();

        return file;
    }
}
