/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.effect;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class EffectBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.EffectRenderer";

    public enum PropertyKeys {

        widgetVar,
        event,
        type,
        forValue("for"),
        speed,
        delay,
        queue;

        private String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public EffectBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getEvent() {
        return (String) getStateHelper().eval(PropertyKeys.event, null);
    }

    public void setEvent(String event) {
        getStateHelper().put(PropertyKeys.event, event);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public int getSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.speed, 1000);
    }

    public void setSpeed(int speed) {
        getStateHelper().put(PropertyKeys.speed, speed);
    }

    public int getDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.delay, 0);
    }

    public void setDelay(int delay) {
        getStateHelper().put(PropertyKeys.delay, delay);
    }

    public boolean isQueue() {
        return (Boolean) getStateHelper().eval(PropertyKeys.queue, true);
    }

    public void setQueue(boolean queue) {
        getStateHelper().put(PropertyKeys.queue, queue);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}