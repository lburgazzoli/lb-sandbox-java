/*
 * Copyright 2015 Luca Burgazzoli
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
package com.github.lburgazzoli.sandbox.osgi.karaf.ds;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Modified;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MyServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServiceImpl.class);

    @Activate
    public void activate(
        final ComponentContext ctx,
        final Map<String, Object> properties)
    {
        LOGGER.info("MyService <{}> activate, properties : {}",
            this,
            ToStringBuilder.reflectionToString(
                properties,
                ToStringStyle.MULTI_LINE_STYLE
        ));
    }

    @Deactivate
    public void deactivate(
        final ComponentContext ctx)
    {
        LOGGER.info("MyService <{}> deactivate", this);
    }

    @Modified
    public synchronized void modified(
        final Map<String, Object> properties)
    {
        LOGGER.info("MyService <{}> modified, properties : {}",
            this,
            ToStringBuilder.reflectionToString(
                properties,
                ToStringStyle.MULTI_LINE_STYLE
            ));
    }
}
