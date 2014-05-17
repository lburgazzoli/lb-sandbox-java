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
import com.github.lburgazzoli.sandbox.jooq.db.Tables
import groovy.sql.Sql
import org.jooq.SQLDialect
import org.jooq.impl.DSL

class JooqMain {
    public static void main(String[] args) {
        if(args.length == 1) {
            def cfg = new ConfigSlurper().parse(new File(args[0]).toURI().toURL())
            def sql = Sql.newInstance(cfg.db.url, cfg.db.usr, cfg.db.pwd, cfg.db.driver)

            try {
                def ctx = DSL.using(sql.connection, SQLDialect.H2)

                for(int i=1;i<10;i++) {
                    ctx.insertInto(Tables.TEST, Tables.TEST.ID, Tables.TEST.DATA)
                       .values(i, "Data_" + i)
                       .values(i * 100, "Data_" + (i * 100))
                       .execute()
                }

                for (def rec : ctx.selectFrom(Tables.TEST).orderBy(Tables.TEST.ID).fetch()) {
                    println "> $rec.id, $rec.data"
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
