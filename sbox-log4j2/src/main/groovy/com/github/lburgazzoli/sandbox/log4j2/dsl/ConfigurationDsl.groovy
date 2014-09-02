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
package com.github.lburgazzoli.sandbox.log4j2.dsl

import com.github.lburgazzoli.sandbox.log4j2.GroovyConfigurationAware
import org.apache.logging.log4j.core.config.plugins.util.PluginManager
import org.apache.logging.log4j.core.util.Patterns


class ConfigurationDsl extends GroovyConfigurationAware {
    def configuration(args, closure) {
        if(args.packages) {
            PluginManager.addPackages(
                Arrays.asList(args.packages.split(Patterns.COMMA_SEPARATOR))
            )
        }

        if(closure) {
            closure.delegate = this;
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }

    def appender(args, closure) {
        println "appender"

        if(closure) {
            closure.delegate = this;
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }

    def logger(args, closure) {
        println "logger"

        if(closure) {
            closure.delegate = this;
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }
}
