configuration(
        status   : 'error',
        name     : 'log4j2-groovy',
        packages : 'com.github.lburgazzoli.sandbox,com.github.lburgazzoli.sandbox.log4j2') {

    appenders {
        appender(name: 'appender1', type: 'type1') {
        }
    }

    loggers {
    }
}
