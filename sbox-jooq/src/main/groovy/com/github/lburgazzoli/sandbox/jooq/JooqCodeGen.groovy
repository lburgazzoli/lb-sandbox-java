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
package com.github.lburgazzoli.sandbox.jooq

import org.jooq.util.GenerationTool
import org.jooq.util.jaxb.*

class JooqCodeGen {
    public static void main(String[] args) {
        if(args.length == 1) {
            def cfg = new ConfigSlurper().parse(new File(args[0]).toURI().toURL())

            def gen = new Generate()
            gen.deprecated = false
            gen.fluentSetters = true
            gen.instanceFields = true
            gen.immutablePojos = true
            gen.interfaces = true

            GenerationTool.main(
                new Configuration()
                    .withJdbc(new Jdbc()
                        .withDriver(cfg.db.driver)
                        .withUrl(cfg.db.url)
                        .withUser(cfg.db.pwd)
                        .withPassword(""))
                    .withGenerator(new Generator()
                        .withGenerate(gen)
                        .withName("org.jooq.util.DefaultGenerator")
                    .withDatabase(new Database()
                        .withDateAsTimestamp(true)
                        .withName(cfg.db.jooq.database)
                        .withIncludes(".*")
                        .withExcludes("")
                        .withInputSchema("PUBLIC"))
                    .withTarget(new Target()
                        .withPackageName(cfg.db.jooq.pkg)
                        .withDirectory(cfg.db.jooq.output))
                ))
        }
    }
}
