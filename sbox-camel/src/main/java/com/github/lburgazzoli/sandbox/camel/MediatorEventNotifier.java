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

import org.apache.camel.management.event.ExchangeSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MediatorEventNotifier extends AbstractEventNotifier<ExchangeSentEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediatorEventNotifier.class);

    /**
     * c-tor
     */
    public MediatorEventNotifier() {
        super(ExchangeSentEvent.class);
    }

    @Override
    protected void onEvent(ExchangeSentEvent event) {
        LOGGER.info("Took {} millis to send to: {}",
            event.getTimeTaken(),
            event.getEndpoint()
        );
    }
}
