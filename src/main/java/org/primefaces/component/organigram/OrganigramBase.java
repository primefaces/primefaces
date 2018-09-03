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
package org.primefaces.component.organigram;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class OrganigramBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OrganigramRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        var,
        selection,
        style,
        styleClass,
        leafNodeConnectorHeight,
        zoom,
        autoScrollToSelection
    }

    public OrganigramBase() {
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

    public org.primefaces.model.OrganigramNode getValue() {
        return (org.primefaces.model.OrganigramNode) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(org.primefaces.model.OrganigramNode value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public org.primefaces.model.OrganigramNode getSelection() {
        return (org.primefaces.model.OrganigramNode) getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(org.primefaces.model.OrganigramNode selection) {
        getStateHelper().put(PropertyKeys.selection, selection);
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

    public int getLeafNodeConnectorHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.leafNodeConnectorHeight, 10);
    }

    public void setLeafNodeConnectorHeight(int leafNodeConnectorHeight) {
        getStateHelper().put(PropertyKeys.leafNodeConnectorHeight, leafNodeConnectorHeight);
    }

    public boolean isZoom() {
        return (Boolean) getStateHelper().eval(PropertyKeys.zoom, false);
    }

    public void setZoom(boolean zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public boolean isAutoScrollToSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoScrollToSelection, false);
    }

    public void setAutoScrollToSelection(boolean autoScrollToSelection) {
        getStateHelper().put(PropertyKeys.autoScrollToSelection, autoScrollToSelection);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}