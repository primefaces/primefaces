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

import org.atmosphere.cpr.MetaBroadcaster;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.push.EventBus;

public class EventBusImpl implements EventBus {

    // TODO: Add caching support here.

    @Override
    public EventBus encodeToJsonAndFire(Object o) {
        MetaBroadcaster.getDefault().broadcastTo("/*", toJSON(o));
        return this;
    }

    @Override
    public EventBus encodeToJsonAndFire(String path, Object o) {
        MetaBroadcaster.getDefault().broadcastTo(path, toJSON(o));
        return this;
    }

    @Override
    public EventBus fire(Object o) {
        MetaBroadcaster.getDefault().broadcastTo("/*", o);
        return this;
    }

    @Override
    public EventBus fire(String path, Object o) {
        MetaBroadcaster.getDefault().broadcastTo(path, o);
        return this;
    }

    private String toJSON(Object data) {
        try {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            if(isBean(data)) {
                jsonBuilder.append("\"").append("data").append("\":").append(new JSONObject(data).toString());
            }
            else {
                String json = new JSONObject().put("data", data).toString();

                jsonBuilder.append(json.substring(1, json.length() - 1));
            }

            jsonBuilder.append("}");

            return jsonBuilder.toString();
        }

        catch(JSONException e) {
            System.out.println(e.getMessage());

            throw new RuntimeException(e);
        }

    }

    private boolean isBean(Object value) {
        if(value == null) {
            return false;
        }

        if(value instanceof Boolean || value instanceof String || value instanceof Number) {
            return false;
        }

        return true;
    }
}
