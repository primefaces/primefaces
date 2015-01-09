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

import org.atmosphere.cpr.MetaBroadcaster;
import org.primefaces.push.impl.EventBusImpl;

/**
 * A Factory for retrieving the current {@link EventBus}
 */
public class EventBusFactory {

    private final EventBus eventBus;
    private static EventBusFactory f = null;

    protected EventBusFactory(MetaBroadcaster metaBroadcaster) {
        eventBus = new EventBusImpl(metaBroadcaster);
        f = this;
    }

    /**
     * Return the default factory
     * @return the default factory
     */
    public final static EventBusFactory getDefault() {
        return f;
    }

    /**
     * Return a {@link EventBus}
     * @return a {@link EventBus}
     */
    public EventBus eventBus(){
        return eventBus;
    }

}