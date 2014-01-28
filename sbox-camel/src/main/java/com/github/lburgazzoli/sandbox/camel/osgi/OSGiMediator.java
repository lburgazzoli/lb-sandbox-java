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
package com.github.lburgazzoli.sandbox.camel.osgi;

import com.github.lburgazzoli.sandbox.camel.IMediator;
import com.github.lburgazzoli.sandbox.camel.IMediatorRoutesBuilder;
import com.github.lburgazzoli.sandbox.camel.MediatorEventNotifier;
import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.blueprint.BlueprintCamelContext;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.LifecycleStrategySupport;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class OSGiMediator extends BlueprintCamelContext implements IMediator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OSGiMediator.class);

    private SimpleRegistry m_registry;

    /**
     * c-tor
     *
     * @param contextId
     * @param bundleContext
     * @param blueprintContainer
     */
    public OSGiMediator(String contextId, BundleContext bundleContext, BlueprintContainer blueprintContainer) {
        super(bundleContext, blueprintContainer);
        super.setName(contextId);

        m_registry = new SimpleRegistry();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() throws Exception {
        super.init();

        getManagementStrategy().addEventNotifier(new MediatorEventNotifier());
        addLifecycleStrategy(new LifecycleStrategySupport() {
            @Override
            public void onContextStart(CamelContext context) throws VetoCamelContextStartException {
                LOGGER.debug("ContextStart");
            }
            @Override
            public void onContextStop(CamelContext context) {
                LOGGER.debug("nContextStop");
            }
        });
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected Registry createRegistry() {
        CompositeRegistry reg = new CompositeRegistry();
        reg.addRegistry(m_registry);
        reg.addRegistry(super.createRegistry());

        return reg;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void addRoutesBuilder(IMediatorRoutesBuilder routesBuilder) {
        LOGGER.debug(">> addRoutesBuilder {}",routesBuilder);
        if(routesBuilder != null) {

            for(Map.Entry<String,Object> entry : routesBuilder.getBeans().entrySet()) {
                LOGGER.debug("Register bean: {} => {}",entry.getKey(),entry.getValue());
                m_registry.put(entry.getKey(), entry.getValue());
            }

            try {
                addRoutes(routesBuilder);
            } catch(Exception e) {
                LOGGER.warn("Exception",e);
            }
        }
    }

    @Override
    public void removeRoutesBuilder(IMediatorRoutesBuilder routesBuilder) {
        LOGGER.debug(">> removeRoutesBuilder {}",routesBuilder);
        if(routesBuilder != null) {

            for(Map.Entry<String,Object> entry : routesBuilder.getBeans().entrySet()) {
                LOGGER.debug("Unregister bean: {} => {}",entry.getKey(),entry.getValue());
                m_registry.remove(entry.getKey());
            }

            for(RouteDefinition rd  : routesBuilder.getRoutesDefinition().getRoutes()) {
                try {
                    LOGGER.debug("Remove route <{}/{}>",rd.getGroup(),rd.getId());
                    removeRouteDefinition(rd);
                } catch(Exception e) {
                    LOGGER.warn("Exception",e);
                }
            }
        }
    }
}
