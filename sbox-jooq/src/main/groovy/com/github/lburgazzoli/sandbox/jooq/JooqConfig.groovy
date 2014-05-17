package com.github.lburgazzoli.sandbox.jooq

db {
    driver = "org.h2.Driver"
    url    = "jdbc:h2:mem:openors;INIT=RUNSCRIPT FROM 'classpath:scripts/create.sql'";
    usr    = "sa"
    pwd    = ""

    jooq {
        database = 'org.jooq.util.h2.H2Database'
        pkg      = "com.github.lburgazzoli.sandbox.jooq.db"
        output   = 'src/main/java'
    }
}
