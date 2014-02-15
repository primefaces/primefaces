/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.push.impl;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterListenerAdapter;
import org.atmosphere.cpr.MetaBroadcaster;
import org.primefaces.push.EventBus;

public class EventBusImpl implements EventBus {

    public EventBusImpl() {
    }

    // TODO: Add caching support here.
    //@Override
    public EventBus publish(Object o) {
        MetaBroadcaster.getDefault().broadcastTo("/*", o);
        return this;
    }

    //@Override
    public EventBus publish(String path, Object o) {
        if (!path.startsWith("/")) path = "/" + path;

        MetaBroadcaster.getDefault().broadcastTo(path, o);
        return this;
    }

    //@Override
    public EventBus publish(String path, Object o, final Reply r) {
        MetaBroadcaster.getDefault().addBroadcasterListener(new BroadcasterListenerAdapter() {
            public void onComplete(Broadcaster b) {
                try {
                    r.completed(b.getID());
                } finally {
                    MetaBroadcaster.getDefault().removeBroadcasterListener(this);
                }
            }
        });
        return null;
    }

}
