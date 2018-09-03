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

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class ContextMenuBase extends AbstractMenu implements Widget {

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

    public ContextMenuBase() {
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

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public String getNodeType() {
        return (String) getStateHelper().eval(PropertyKeys.nodeType, null);
    }

    public void setNodeType(String nodeType) {
        getStateHelper().put(PropertyKeys.nodeType, nodeType);
    }

    public String getEvent() {
        return (String) getStateHelper().eval(PropertyKeys.event, null);
    }

    public void setEvent(String event) {
        getStateHelper().put(PropertyKeys.event, event);
    }

    public String getBeforeShow() {
        return (String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, beforeShow);
    }

    public String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, "multiple");
    }

    public void setSelectionMode(String selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, selectionMode);
    }

    public String getTargetFilter() {
        return (String) getStateHelper().eval(PropertyKeys.targetFilter, null);
    }

    public void setTargetFilter(String targetFilter) {
        getStateHelper().put(PropertyKeys.targetFilter, targetFilter);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}