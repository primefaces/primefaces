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
package org.primefaces.component.dnd;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class DroppableBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DroppableRenderer";

    public enum PropertyKeys {

        widgetVar,
        forValue("for"),
        disabled,
        hoverStyleClass,
        activeStyleClass,
        onDrop,
        accept,
        scope,
        tolerance,
        datasource,
        greedy;

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

    public DroppableBase() {
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

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getHoverStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.hoverStyleClass, null);
    }

    public void setHoverStyleClass(String hoverStyleClass) {
        getStateHelper().put(PropertyKeys.hoverStyleClass, hoverStyleClass);
    }

    public String getActiveStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.activeStyleClass, null);
    }

    public void setActiveStyleClass(String activeStyleClass) {
        getStateHelper().put(PropertyKeys.activeStyleClass, activeStyleClass);
    }

    public String getOnDrop() {
        return (String) getStateHelper().eval(PropertyKeys.onDrop, null);
    }

    public void setOnDrop(String onDrop) {
        getStateHelper().put(PropertyKeys.onDrop, onDrop);
    }

    public String getAccept() {
        return (String) getStateHelper().eval(PropertyKeys.accept, null);
    }

    public void setAccept(String accept) {
        getStateHelper().put(PropertyKeys.accept, accept);
    }

    public String getScope() {
        return (String) getStateHelper().eval(PropertyKeys.scope, null);
    }

    public void setScope(String scope) {
        getStateHelper().put(PropertyKeys.scope, scope);
    }

    public String getTolerance() {
        return (String) getStateHelper().eval(PropertyKeys.tolerance, null);
    }

    public void setTolerance(String tolerance) {
        getStateHelper().put(PropertyKeys.tolerance, tolerance);
    }

    public String getDatasource() {
        return (String) getStateHelper().eval(PropertyKeys.datasource, null);
    }

    public void setDatasource(String datasource) {
        getStateHelper().put(PropertyKeys.datasource, datasource);
    }

    public boolean isGreedy() {
        return (Boolean) getStateHelper().eval(PropertyKeys.greedy, false);
    }

    public void setGreedy(boolean greedy) {
        getStateHelper().put(PropertyKeys.greedy, greedy);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}