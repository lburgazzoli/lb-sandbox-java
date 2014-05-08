import org.springframework.core.io.FileSystemResource

def url    = new FileSystemResource('conf/instance-1/instance-1-cfg.groovy').URL
def config = new ConfigSlurper().parse(url)

// *****************************************************************************
//
// *****************************************************************************


beans {
    importBeans('file:conf/instance-1/instance-1-beans-1.groovy')
    importBeans('file:conf/instance-1/instance-1-beans-2.groovy')

    bean0 String, 'bean0'
}