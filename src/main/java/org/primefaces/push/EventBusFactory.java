/*
 * Copyright 2013 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.primefaces.push;

import org.primefaces.push.impl.EventBusImpl;

/**
 * A Factory for retrieving the current {@link EventBus}
 */
public class EventBusFactory {

    private static EventBusFactory p;
    private EventBus eventBus;

    protected EventBusFactory() {
        eventBus = new EventBusImpl();
        // Quite ugly
        p = this;
    }

    protected EventBusFactory(EventBus eventBus) {
        this.eventBus = eventBus;
        p = this;
    }

    /**
     * Return the default factory
     * @return the default factory
     */
    public final static EventBusFactory getDefault() {
        return p;
    }

    /**
     * Return a {@link EventBus}
     * @return a {@link EventBus}
     */
    public EventBus eventBus(){
        return eventBus;
    }

}
