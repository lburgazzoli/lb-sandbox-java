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
package com.github.lburgazzoli.sandbox.spring4.groovy.hz

import com.hazelcast.config.*
import com.hazelcast.core.EntryAdapter
import com.hazelcast.core.EntryEvent
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
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
                    'hazelcast.logging.type'      : 'slf4j',
                    'hazelcast.memcache.enabled'  : 'false',
                    'hazelcast.rest.enabled'      : 'false',
                    'hazelcast.socket.keep.alive' : 'true' ,
                    'hazelcast.socket.no.delay'   : 'true' ,
                    'hazelcast.jmx'               : 'false',
                ]
                groupConfig = {
                    GroupConfig groupConfig ->
                        name     = cfg.hz.group.name
                        password = cfg.hz.group.password
                }
                networkConfig = {
                    NetworkConfig networkConfig ->
                        portAutoIncrement = true

                        interfaces = {
                            InterfacesConfig interfacesConfig ->
                                enabled    = false
                        }
                        join = {
                            JoinConfig joinConfig ->
                                multicastConfig = {
                                    MulticastConfig multicastConfig ->
                                        enabled = true
                                        trustedInterfaces = [ "eth0" ]
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
                mapConfigs = [
                    'hz_map_1' : [
                        name              : 'hz_map_1',
                        backupCount       : 2,
                        statisticsEnabled : true,
                        timeToLiveSeconds : 5,
                        evictionPolicy    : MapConfig.EvictionPolicy.LFU
                    ] as MapConfig
                ]
            }

            hzInstance(Hazelcast, hzConfig) { bean ->
                bean.factoryMethod = 'newHazelcastInstance'
                bean.destroyMethod = 'shutdown'
            }
        }

        ctx.refresh()

        def hzi    = ctx.getBean('hzInstance',HazelcastInstance.class);
        def hzMap1 = hzi.getMap('hz_map_1');
        def hzMap2 = hzi.getMap('hz_map_2');

        hzMap1.put('key','val')
        hzMap1.addEntryListener(new EntryAdapter<String,String>() {
            void entryEvicted(EntryEvent<String,String> event) {
                LOGGER.info("entryEvicted {}",event)
            }
        },false)

        hzMap2.put('key','val1')

        try {
            Thread.sleep(15000)
        } catch(Exception e) {
        }

        ctx.close()
    }
}
