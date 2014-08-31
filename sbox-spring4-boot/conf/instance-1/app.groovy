import com.github.lburgazzoli.sandbox.spring4.boot.ApplicationInfo

beans {
    applicationInfo(ApplicationInfo) {
        name = ref('instanceName')
        description = ref('instanceDescription')
        path = new File(
            environment.getProperty("app.path")).getAbsoluteFile()
        pathProperties = new File(
            environment.getProperty("app.path"),
            environment.getProperty("app.name") + ".properties").getAbsoluteFile()
        pathGroovy = new File(
            environment.getProperty("app.path"),
            environment.getProperty("app.name") + ".groovy").getAbsoluteFile()
    }
}