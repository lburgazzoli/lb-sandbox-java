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

import net.openhft.lang.io.Bytes;
import net.openhft.lang.model.Byteable;

/**
 *
 */
public class SharedHashMapData implements Byteable {
    private Bytes bytes;
    private long offset;
    private int size;

    public SharedHashMapData() {
       this(0);
    }

    public SharedHashMapData(int size) {
        this.bytes = null;
        this.offset = 0;
        this.size = size;
    }

    @Override
    public void bytes(Bytes bytes, long offset) {
        this.bytes = bytes;
        this.offset = offset;
    }

    @Override
    public Bytes bytes() {
        return this.bytes;
    }

    @Override
    public long offset() {
        return this.offset;
    }

    @Override
    public int maxSize() {
        return this.size;
    }
}
