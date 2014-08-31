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

class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class)

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) {
        def cli = new CliBuilder(usage: 'boot -pn "server"')
        cli.p(longOpt: 'path', 'path', required: true  , args: 1 )
        cli.n(longOpt: 'name', 'name', required: true  , args: 1 )

        def opt = cli.parse(args)
        if(opt) {
            def ctx = SpringApplication.run(
                [ ApplicationConfig.class,
                  new FileSystemResource("${opt.p}/${opt.n}.groovy") ] as Object[],
                [ "--app.name=$opt.n",
                  "--app.path=$opt.p",
                  "--spring.config.location=${opt.p}/${opt.n}.properties" ] as String[])

            ctx.beanDefinitionNames.each {
                LOGGER.info("bean : {} ==> {}", it, ctx.getBean(it));
            }

            ctx.close();
        } else {
            cli.usage()
        }
    }
}
