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

import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.ConfigurationSource
import org.apache.logging.log4j.core.config.Order
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.util.Loader

@Plugin(
    name     = "GroovyConfigurationFactory",
    category = "ConfigurationFactory")
@Order(1)
class GroovyConfigurationFactory extends ConfigurationFactory {

    private final boolean isActive;

    public GroovyConfigurationFactory() {
        if (!Loader.isClassAvailable(ConfigSlurper.class.getName())) {
            LOGGER.debug("Missing dependencies for Groovy support")
            isActive = false
        } else {
            isActive = true
        }
    }

    @Override
    protected boolean isActive() {
        return isActive;
    }

    @Override
    protected String[] getSupportedTypes() {
        return [ '.groovy' ]
    }

    @Override
    public Configuration getConfiguration(ConfigurationSource source) {
        return isActive ? new GroovyConfiguration(source) : null
    }
}
