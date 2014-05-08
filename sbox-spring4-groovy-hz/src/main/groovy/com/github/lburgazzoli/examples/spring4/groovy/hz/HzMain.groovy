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
package com.github.lburgazzoli.examples.spring4.groovy.hz

import com.hazelcast.config.AwsConfig
import com.hazelcast.config.Config
import com.hazelcast.config.GroupConfig
import com.hazelcast.config.InterfacesConfig
import com.hazelcast.config.JoinConfig
import com.hazelcast.config.MulticastConfig
import com.hazelcast.config.NetworkConfig
import com.hazelcast.config.TcpIpConfig
import com.hazelcast.core.Hazelcast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.support.GenericGroovyApplicationContext
import org.springframework.core.io.FileSystemResource


class HzMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(HzMain.class)

    public static void main(String[] args) {
        def cfg = new ConfigSlurper().parse(new FileSystemResource(args[0]).URL)
        def ctx = new GenericGroovyApplicationContext()

        ctx.reader.beans {
            hzConfig(Config) {
                properties = [
                    'hazelcast.logging.type' : 'slf4j'
                ]
                groupConfig = {
                    GroupConfig groupConfig ->
                        name     = cfg.hz.group.name
                        password = cfg.hz.group.password
                }
                networkConfig = {
                    NetworkConfig networkConfig ->
                        interfaces = {
                            InterfacesConfig interfacesConfig ->
                                enabled = false
                                interfaces = ["127.0.0.1"]
                        }
                        join = {
                            JoinConfig joinConfig ->
                                multicastConfig = {
                                    MulticastConfig multicastConfig ->
                                        enabled = false
                                }
                                tcpIpConfig = {
                                    TcpIpConfig tcpIpConfig ->
                                        enabled = false
                                }
                                awsConfig = {
                                    AwsConfig awsConfig ->
                                        enabled = false
                                }
                        }
                }
            }

            hzInstance(Hazelcast, hzConfig) { bean ->
                bean.factoryMethod = 'newHazelcastInstance'
                bean.destroyMethod = 'shutdown'
            }
        }

        ctx.refresh()

        ctx.beanDefinitionNames.each {
            LOGGER.info("bean : {}", it)
        }

        ctx.close()
    }
}
