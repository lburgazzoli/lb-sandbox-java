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
package com.github.lburgazzoli.sandbox.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.io.Buffer;

/**
 *
 */
public class ReactorMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        try {
            Buffer b = new Buffer();
            b.append("1234567890");
            b.flip();

            LOGGER.debug("Capacity  {}",b.capacity());
            LOGGER.debug("Remaining {}",b.remaining());
            LOGGER.debug("GetAt 0   {}",Character.getNumericValue(b.byteBuffer().get(0)));
            LOGGER.debug("getAt 1   {}",Character.getNumericValue(b.byteBuffer().get(1)));
            LOGGER.debug("getAt 2   {}",Character.getNumericValue(b.byteBuffer().get(2)));
            LOGGER.debug("Remaining {}",b.remaining());

        } catch(Exception e) {
            LOGGER.warn("Main Exception", e);
        }
    }
}
