import com.github.lburgazzoli.sandbox.spring4.boot.Application

beans {
    application(Application) {
        name = ref('instanceName')
        description = ref('instanceDescription')
    }
}