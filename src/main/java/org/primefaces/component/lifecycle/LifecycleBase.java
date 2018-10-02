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
package org.primefaces.component.lifecycle;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class LifecycleBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LifecycleRenderer";

    public enum PropertyKeys {

        widgetVar
    }

    public LifecycleBase() {
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}