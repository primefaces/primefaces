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
package org.primefaces.component.outputpanel;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class OutputPanelBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OutputPanelRenderer";

    public enum PropertyKeys {

        style,
        styleClass,
        deferred,
        deferredMode,
        layout
    }

    public OutputPanelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isDeferred() {
        return (Boolean) getStateHelper().eval(PropertyKeys.deferred, false);
    }

    public void setDeferred(boolean deferred) {
        getStateHelper().put(PropertyKeys.deferred, deferred);
    }

    public String getDeferredMode() {
        return (String) getStateHelper().eval(PropertyKeys.deferredMode, "load");
    }

    public void setDeferredMode(String deferredMode) {
        getStateHelper().put(PropertyKeys.deferredMode, deferredMode);
    }

    public String getLayout() {
        return (String) getStateHelper().eval(PropertyKeys.layout, "block");
    }

    public void setLayout(String layout) {
        getStateHelper().put(PropertyKeys.layout, layout);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}