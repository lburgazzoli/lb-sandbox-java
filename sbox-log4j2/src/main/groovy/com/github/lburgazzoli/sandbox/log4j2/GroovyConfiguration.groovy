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
package com.github.lburgazzoli.sandbox.log4j2

import org.apache.logging.log4j.core.config.AbstractConfiguration
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.ConfigurationSource
import org.apache.logging.log4j.core.config.Reconfigurable
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil
import org.apache.logging.log4j.core.config.status.StatusConfiguration
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

public class GroovyConfiguration extends AbstractConfiguration implements Reconfigurable {

    protected GroovyConfiguration(final ConfigurationSource configurationSource) {
        super(configurationSource)
    }

    @Override
    public void setup() {
        def binding = new Binding();
        binding.setVariable('log4j2', new GroovyConfigurationBuilder(node: rootNode))
        binding.setVariable('groovyConfig', this)
        binding.setVariable('strSubstitutor', strSubstitutor)
        binding.setVariable('pluginManager', pluginManager)
        binding.setVariable('statusConfig', new StatusConfiguration()
                .withVerboseClasses(ResolverUtil.class.getName())
                .withStatus(defaultStatus))

        def shell = new GroovyShell(binding , configuration())
        def script = shell.parse(configurationSource.getFile())
        script.setBinding(binding)
        script.run();
    }

    @Override
    public Configuration reconfigure() {
        return null
    }

    // *************************************************************************
    //
    // *************************************************************************

    private def configuration() {
        /*
       def core = 'ch.qos.logback.core'
       customizer.addStarImports(core, "${core}.encoder", "${core}.read", "${core}.rolling", "${core}.status",
               "ch.qos.logback.classic.net")
       customizer.addImports(PatternLayoutEncoder.class.name)
       customizer.addStaticStars(Level.class.name)
       customizer.addStaticImport('off', Level.class.name, 'OFF')
       customizer.addStaticImport('error', Level.class.name, 'ERROR')
       customizer.addStaticImport('warn', Level.class.name, 'WARN')
       customizer.addStaticImport('info', Level.class.name, 'INFO')
       customizer.addStaticImport('debug', Level.class.name, 'DEBUG')
       customizer.addStaticImport('trace', Level.class.name, 'TRACE')
       customizer.addStaticImport('all', Level.class.name, 'ALL')
       customizer
       */
        def customizer = new ImportCustomizer()

        def configuration = new CompilerConfiguration()
        //configuration.scriptBaseClass = DelegatingScript.class.name
        configuration.addCompilationCustomizers(customizer)

        return  configuration;
    }
}
