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
package org.primefaces.component.contextmenu;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class ContextMenuBase extends AbstractMenu implements org.primefaces.component.api.Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ContextMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        forValue("for"),
        style,
        styleClass,
        model,
        nodeType,
        event,
        beforeShow,
        selectionMode,
        targetFilter;

        String toString;

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

    public ContextMenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getFor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(java.lang.String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
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

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel _model) {
        getStateHelper().put(PropertyKeys.model, _model);
    }

    public java.lang.String getNodeType() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.nodeType, null);
    }

    public void setNodeType(java.lang.String _nodeType) {
        getStateHelper().put(PropertyKeys.nodeType, _nodeType);
    }

    public java.lang.String getEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.event, null);
    }

    public void setEvent(java.lang.String _event) {
        getStateHelper().put(PropertyKeys.event, _event);
    }

    public java.lang.String getBeforeShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(java.lang.String _beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, _beforeShow);
    }

    public java.lang.String getSelectionMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, "multiple");
    }

    public void setSelectionMode(java.lang.String _selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
    }

    public java.lang.String getTargetFilter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.targetFilter, null);
    }

    public void setTargetFilter(java.lang.String _targetFilter) {
        getStateHelper().put(PropertyKeys.targetFilter, _targetFilter);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}