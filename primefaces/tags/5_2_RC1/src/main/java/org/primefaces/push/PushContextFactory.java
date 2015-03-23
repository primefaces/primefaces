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
package org.primefaces.push;

import org.atmosphere.cpr.MetaBroadcaster;

/**
 * Factory for retrieving {@link PushContext}
 * @deprecated With PrimeFaces 4.1 and up, it is recommended to use {@link EventBus}
 */
public class PushContextFactory {

    private static PushContextFactory p;
    private final PushContext pushContext;
    private final MetaBroadcaster metaBroadcaster;

    protected PushContextFactory(MetaBroadcaster metaBroadcaster) {
        this.metaBroadcaster = metaBroadcaster;
        pushContext = new PushContextImpl(this.metaBroadcaster);
        p = this;
    }

    /**
     * Return the default factory
     * @return the default factory
     */
    public final static PushContextFactory getDefault() {
        return p;
    }

    /**
     * Retrieve a {@link PushContext}
     * @return
     */
    public PushContext getPushContext(){
        return pushContext;
    }

}
