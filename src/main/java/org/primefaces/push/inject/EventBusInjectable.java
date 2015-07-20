/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.push.inject;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.inject.Injectable;
import org.atmosphere.inject.annotation.ApplicationScoped;
import org.primefaces.push.EventBus;
import org.primefaces.push.impl.EventBusImpl;

import java.lang.reflect.Type;

@ApplicationScoped
public class EventBusInjectable implements Injectable<EventBus> {

    private EventBus eventBus;

    public boolean supportedType(Type t) {
        return (t instanceof Class) && EventBus.class.isAssignableFrom((Class) t);
    }

    public EventBus injectable(AtmosphereConfig config) {
        if (eventBus == null) {
            eventBus = new EventBusImpl(config.metaBroadcaster());
        }
        return eventBus;
    }
}
