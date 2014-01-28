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
package com.github.lburgazzoli.sandbox.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;

/**
 *
 */
public abstract class MediatorRouteBuilder extends RouteBuilder implements IMediatorRoutesBuilder {
    /**
     * c-tor
     */
    public MediatorRouteBuilder() {
    }

    /**
     * @param id
     * @return
     */
    protected RouteDefinition fromQueue(String id) {
        return fromF("seda:%s",id).routeId(id);
    }

    /**
     * @param id
     * @return
     */
    protected RouteDefinition fromDirect(String id) {
        return fromF("direct:%s",id).routeId(id);
    }

    @Override
    public RoutesDefinition getRoutesDefinition() {
        return super.getRouteCollection();
    }
}
