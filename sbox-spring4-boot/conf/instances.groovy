
instances {
    instance1 {
        path = './conf/instance-1'
        conf = "${path}/app.properties"
        ext  = "${path}/app.groovy"
    }
    instance2 {
        path = './conf/instance-2'
        conf = "${path}/app.properties"
    }
}