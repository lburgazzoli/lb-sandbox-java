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
package com.github.lburgazzoli.sandbox.spring4.boot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.core.io.FileSystemResource

class ApplicationMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMain.class)

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        final String conf =  System.getProperty('app.conf')
        final String name =  System.getProperty('app.name')

        if(conf && name) {
            def cfg = new ConfigSlurper().parse(new File(conf).toURI().toURL())

            def appPath = cfg.instances."$name".path
            def appCfg  = cfg.instances."$name".conf
            def appExt  = cfg.instances."$name".ext
            def sources = [ ApplicationConfig.class, Application.class, ApplicationInstance.class ]

            if(appExt) {
                sources += [ new FileSystemResource(appExt) ]
            }

            def ctx = SpringApplication.run(
                sources as Object[],
                [ "--instance.cfg=$appCfg",
                  "--instance.ext=$appExt",
                  "--instance.path=$appPath",
                  "--spring.config.location=${appCfg}" ] as String[])

            LOGGER.info("app: {} ",ctx.getBean('application'))

            ctx.close();
        } else {
            if(!conf) {
                println "missing app.conf"
            }
            if(!name) {
                println "missing app.name"
            }
        }
    }
}
