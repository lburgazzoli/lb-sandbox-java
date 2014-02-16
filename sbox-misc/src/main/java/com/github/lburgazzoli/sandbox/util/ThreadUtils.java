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
package com.github.lburgazzoli.sandbox.util;

import com.google.common.collect.Lists;
import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.oops.Field;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.LongField;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.runtime.JavaThread;
import sun.jvm.hotspot.runtime.Threads;
import sun.jvm.hotspot.runtime.VM;
import sun.jvmstat.perfdata.monitor.v2_0.TypeCode;

import java.util.List;

public class ThreadUtils {

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static final class ThreadInfo {
        private final Address address;
        private final long tid;
        private final long nid;

        /**
         *
         * @param address
         * @param tid
         * @param nid
         */
        public ThreadInfo(final Address address, final long tid, final long nid) {
            this.address = address;
            this.tid = tid;
            this.nid = nid;
        }

        /**
         *
         * @return
         */
        public Address getAddress() {
            return address;
        }

        /**
         *
         * @return
         */
        public long getTid() {
            return tid;
        }

        /**
         *
         * @return
         */
        public long getNid() {
            return nid;
        }
    }

    /**
     *
     */
    public static class TypeCode {
        public static final String BOOLEAN = "Z";
        public static final String BYTE = "B";
        public static final String CHAR = "C";
        public static final String SHORT = "S";
        public static final String INT = "I";
        public static final String LONG = "J";
        public static final String FLOAT = "F";
        public static final String DOUBLE = "D";
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public static List<ThreadInfo> getAllThreadInfos() {
        List<ThreadInfo> infos = Lists.newArrayList();
        Threads threads = VM.getVM().getThreads();
        for (JavaThread thread = threads.first(); thread != null; thread = thread.next()) {
            Address address = thread.getAddress();
            long tid = getTid(thread);
            long nid = Long.parseLong(thread.getThreadProxy().toString());
            infos.add(new ThreadInfo(address, tid, nid));
        }
        return infos;
    }

    /**
     *
     * @param thread
     * @return
     */
    public static long getTid(JavaThread thread) {
        final long BAD_TID = -1L;

        Oop threadObj = thread.getThreadObj();
        Klass klass = threadObj.getKlass();
        if (!(klass instanceof InstanceKlass)) {
            return BAD_TID;
        }

        InstanceKlass instanceKlass = (InstanceKlass) klass;
        Field tidField = instanceKlass.findField("tid", TypeCode.LONG);
        if (!(tidField instanceof LongField)) return BAD_TID;

        long tid = ((LongField) tidField).getValue(threadObj);
        return tid;

    }
}
