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
package com.github.lburgazzoli.sandbox;

import com.github.lburgazzoli.sandbox.util.ThreadUtils;
import sun.jvm.hotspot.tools.Tool;

import java.util.List;

public class PrintThreadIds extends Tool {
    public static void main(String[] args) {
        PrintThreadIds tool = new PrintThreadIds();
        tool.start(args);
        tool.stop();
    }

    @Override
    public void run() {
        List<ThreadUtils.ThreadInfo> infos = ThreadUtils.getAllThreadInfos();
        for (ThreadUtils.ThreadInfo info : infos) {
            System.out.printf("Thread@%s: tid=%d nid=0x%x\n",
                info.getAddress(),
                info.getTid(),
                info.getNid()
            );
        }
    }
}
