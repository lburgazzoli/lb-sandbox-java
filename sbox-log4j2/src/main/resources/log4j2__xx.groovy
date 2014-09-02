
configuration(
    status   : 'error',
    name     : 'log4j2-groovy',
    packages : 'com.github.lburgazzoli.sandbox,com.github.lburgazzoli.sandbox.log4j2') {


    appender(type : 'Console', name : 'STDOUT') {
        layout(type : 'PatternLayout', pattern : '%m MDC%X%n')
        filters {
            filter(type='MarkerFilter', marker='FLOW', onMatch='DENY', onMismatch='NEUTRAL')
        }
    }
}