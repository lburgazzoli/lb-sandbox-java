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

import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventObject;

/**
 *
 */
public abstract class AbstractEventNotifier<T> extends EventNotifierSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventNotifier.class);
    private final Class<T> m_type;

    /**
     * c-tor
     *
     * @param type
     */
    protected AbstractEventNotifier(final Class<T> type) {
        m_type = type;
    }

    @Override
    public void notify(EventObject event) throws Exception {
        if (event.getClass().isAssignableFrom(m_type)) {
            T evt = m_type.cast(event);
            LOGGER.debug("notify: {}",evt);

            if(evt != null) {
                onEvent(evt);
            }
        }
    }

    @Override
    public boolean isEnabled(EventObject event) {
        return event.getClass().isAssignableFrom(m_type);
    }

    @Override
    protected void doStart() throws Exception {
    }

    @Override
    protected void doStop() throws Exception {
    }

    /**
     *
     * @param event
     */
    protected abstract void onEvent(T event);
}
