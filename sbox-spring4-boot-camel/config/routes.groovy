
// *****************************************************************************
//
// *****************************************************************************

class MyRouteBuilder extends org.apache.camel.builder.RouteBuilder {
    @Override
    void configure() throws Exception {
        from('timer://sbt?fixedRate=true&period=1000')
            .to('log:spring-boot-camel?level=INFO');
    }
}

// *****************************************************************************
//
// *****************************************************************************

beans {
    myRouteBuilder(MyRouteBuilder)
}