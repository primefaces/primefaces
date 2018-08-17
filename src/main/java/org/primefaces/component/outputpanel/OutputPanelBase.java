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

import org.primefaces.util.ComponentUtils;


abstract class OutputPanelBase extends UIPanel implements org.primefaces.component.api.Widget, javax.faces.component.behavior.ClientBehaviorHolder, org.primefaces.component.api.PrimeClientBehaviorHolder {

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

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public boolean isDeferred() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.deferred, false);
    }

    public void setDeferred(boolean _deferred) {
        getStateHelper().put(PropertyKeys.deferred, _deferred);
    }

    public java.lang.String getDeferredMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.deferredMode, "load");
    }

    public void setDeferredMode(java.lang.String _deferredMode) {
        getStateHelper().put(PropertyKeys.deferredMode, _deferredMode);
    }

    public java.lang.String getLayout() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.layout, "block");
    }

    public void setLayout(java.lang.String _layout) {
        getStateHelper().put(PropertyKeys.layout, _layout);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}