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
package com.github.lburgazzoli.sandbox.spring4.boot.camel

import org.apache.camel.CamelContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication

class ApplicationMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMain.class)

    public static void main(String[] args) throws Exception {
        def sctx = null
        def cctx = null

        try {
            sctx = SpringApplication.run([ ApplicationConfig.class ] as Object[], args)
            cctx = sctx.getBean(CamelContext.class)

            cctx.start()

            for(int i=0;i<1000 && sctx.active;i++) {
                LOGGER.info("Sleep...")
                Thread.sleep(1000)
            }

        } finally {
            if(cctx) {
                cctx.stop()
            }

            if(sctx) {
                sctx.close()
            }
        }
    }
}
