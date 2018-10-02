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
package org.primefaces.component.idlemonitor;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class IdleMonitorBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.IdleMonitorRenderer";

    public enum PropertyKeys {

        widgetVar,
        timeout,
        onidle,
        onactive,
        multiWindowSupport
    }

    public IdleMonitorBase() {
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

    public int getTimeout() {
        return (Integer) getStateHelper().eval(PropertyKeys.timeout, 300000);
    }

    public void setTimeout(int timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    public String getOnidle() {
        return (String) getStateHelper().eval(PropertyKeys.onidle, null);
    }

    public void setOnidle(String onidle) {
        getStateHelper().put(PropertyKeys.onidle, onidle);
    }

    public String getOnactive() {
        return (String) getStateHelper().eval(PropertyKeys.onactive, null);
    }

    public void setOnactive(String onactive) {
        getStateHelper().put(PropertyKeys.onactive, onactive);
    }

    public boolean isMultiWindowSupport() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiWindowSupport, false);
    }

    public void setMultiWindowSupport(boolean multiWindowSupport) {
        getStateHelper().put(PropertyKeys.multiWindowSupport, multiWindowSupport);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}