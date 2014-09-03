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
package com.github.lburgazzoli.sandbox.log4j2

class GroovyConfigurationBuilder extends BuilderSupport {
    Node rootNode

    public GroovyConfigurationBuilder(Node rootNode) {
        this.rootNode = rootNode
    }

    // *************************************************************************
    // BuilderSupport
    // *************************************************************************

    @Override
    protected void setParent(final Object parent, final Object child) {
    }

    @Override
    protected Object createNode(final Object name) {
        return createNode(name, null, null);
    }

    @Override
    protected Object createNode(final Object name, final Object value) {
        return createNode(name, null, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object createNode(final Object name, final Map attributes)
    {
        return createNode(name, attributes, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object createNode(final Object name,final Map attributes,final Object value)
    {
    }

    /*
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
            }
        }
    }
    */


    def methodMissing(name, args) {
        println "methodMissing: $name"
        /*
        if(args) {
            final Node childNode = new Node(node, args.name, args.type);
            args.each { k, v ->
                if(!"name".equalsIgnoreCase(name) && !"type".equalsIgnoreCase(name))
                    childNode.getAttributes().put(k, v)
            }

            node.getChildren().add(childNode)
            node = childNode
        }
        */
    }
}
