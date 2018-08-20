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
package org.primefaces.component.growl;

import javax.faces.component.UIMessages;

import org.primefaces.component.api.UINotification;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class GrowlBase extends UIMessages implements Widget, UINotification {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GrowlRenderer";

    public enum PropertyKeys {

        widgetVar,
        sticky,
        life,
        escape,
        severity,
        keepAlive
    }

    public GrowlBase() {
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

    public boolean isSticky() {
        return (Boolean) getStateHelper().eval(PropertyKeys.sticky, false);
    }

    public void setSticky(boolean sticky) {
        getStateHelper().put(PropertyKeys.sticky, sticky);
    }

    public int getLife() {
        return (Integer) getStateHelper().eval(PropertyKeys.life, 6000);
    }

    public void setLife(int life) {
        getStateHelper().put(PropertyKeys.life, life);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, null);
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
    }

    public boolean isKeepAlive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.keepAlive, false);
    }

    public void setKeepAlive(boolean keepAlive) {
        getStateHelper().put(PropertyKeys.keepAlive, keepAlive);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}