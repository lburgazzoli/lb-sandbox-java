
configuration(
    packages : "org.apache.logging.log4j,org.apache.logging.log4j.test",
    status   : "error",
    name     : "gog4j2") {

    appender(name: "app1", type: 'type1') {
    }

    logger(name: "logegr1", type: 'type2') {
    }
}