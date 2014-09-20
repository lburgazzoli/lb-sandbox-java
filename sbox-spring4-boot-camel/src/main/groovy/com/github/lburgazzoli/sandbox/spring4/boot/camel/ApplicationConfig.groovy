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
package com.github.lburgazzoli.sandbox.spring4.boot.camel
import org.apache.camel.CamelContext
import org.apache.camel.RoutesBuilder
import org.apache.camel.spring.SpringCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

@Configuration
@ComponentScan
@ImportResource('${camel.route.builder}')
public class ApplicationConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RoutesBuilder[] routesBuilders;

    @Bean(destroyMethod = 'destroy')
    CamelContext camelContext() throws Exception {
        final CamelContext camelContext = new SpringCamelContext(applicationContext);
        if (routesBuilders != null) {
            for (final RoutesBuilder routesBuilder : routesBuilders) {
                camelContext.addRoutes(routesBuilder);
            }
        }

        return camelContext;
    }
}