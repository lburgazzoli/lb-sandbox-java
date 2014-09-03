/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.github.lburgazzoli.sandbox.log4j2.dsl
import org.apache.logging.log4j.core.config.plugins.util.PluginManager
import org.apache.logging.log4j.core.util.Patterns

class ConfigurationDsl {
    def node

    def configuration(args, closure) {
        if(args) {
            args.each { k,v ->
                if("packages".equalsIgnoreCase(k)) {
                    PluginManager.addPackages(Arrays.asList(v.split(Patterns.COMMA_SEPARATOR)))
                }

                /*
                final String key   = k
                final String value = strSubstitutor.replace(v)

                if ("status".equalsIgnoreCase(key)) {
                    statusConfig.withStatus(value)
                } else if ("dest".equalsIgnoreCase(key)) {
                    statusConfig..withDestination(value)
                } else if ("shutdownHook".equalsIgnoreCase(key)) {
                    //isShutdownHookEnabled = !"disable".equalsIgnoreCase(value)
                } else if ("verbose".equalsIgnoreCase(key)) {
                    statusConfig..withVerbosity(value)
                } else if ("packages".equalsIgnoreCase(key)) {
                    PluginManager.addPackages(Arrays.asList(value.split(Patterns.COMMA_SEPARATOR)))
                } else if ("name".equalsIgnoreCase(key)) {
                    //setName(value)
                } else if ("strict".equalsIgnoreCase(key)) {
                    //strict = Boolean.parseBoolean(value)
                } else if ("schema".equalsIgnoreCase(key)) {
                    //schemaResource = value
                } else if ("monitorInterval".equalsIgnoreCase(key)) {
                    final int interval = Integer.parseInt(value)

                    if (interval > 0 && configFile != null) {
                        monitor = new FileConfigurationMonitor(this, configFile, listeners, interval)
                    }
                } else if ("advertiser".equalsIgnoreCase(key)) {
                    //createAdvertiser(value, configSource, buffer, "text/xml")
                }
                */
            }
        }

        if(closure) {
            closure.delegate = new ItemsDsl(node: node)
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }
}
