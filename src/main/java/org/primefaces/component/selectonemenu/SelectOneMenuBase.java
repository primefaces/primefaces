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
package org.primefaces.component.selectonemenu;

import javax.faces.component.html.HtmlSelectOneMenu;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SelectOneMenuBase extends HtmlSelectOneMenu implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectOneMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        effect,
        effectSpeed,
        panelStyle,
        panelStyleClass,
        var,
        height,
        editable,
        filter,
        filterMatchMode,
        filterFunction,
        filterPlaceholder,
        caseSensitive,
        maxlength,
        appendTo,
        title,
        syncTooltip,
        labelTemplate,
        placeholder,
        autoWidth,
        dynamic
    }

    public SelectOneMenuBase() {
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

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectSpeed() {
        return (String) getStateHelper().eval(PropertyKeys.effectSpeed, null);
    }

    public void setEffectSpeed(String effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, effectSpeed);
    }

    public String getPanelStyle() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(String panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, panelStyle);
    }

    public String getPanelStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(String panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, panelStyleClass);
    }

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, "200");
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
    }

    public boolean isFilter() {
        return (Boolean) getStateHelper().eval(PropertyKeys.filter, false);
    }

    public void setFilter(boolean filter) {
        getStateHelper().put(PropertyKeys.filter, filter);
    }

    public String getFilterMatchMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(String filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, filterMatchMode);
    }

    public String getFilterFunction() {
        return (String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(String filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, filterFunction);
    }

    public String getFilterPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.filterPlaceholder, null);
    }

    public void setFilterPlaceholder(String filterPlaceholder) {
        getStateHelper().put(PropertyKeys.filterPlaceholder, filterPlaceholder);
    }

    public boolean isCaseSensitive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.caseSensitive, false);
    }

    public void setCaseSensitive(boolean caseSensitive) {
        getStateHelper().put(PropertyKeys.caseSensitive, caseSensitive);
    }

    public int getMaxlength() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxlength, Integer.MAX_VALUE);
    }

    public void setMaxlength(int maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, maxlength);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    @Override
    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    @Override
    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    public boolean isSyncTooltip() {
        return (Boolean) getStateHelper().eval(PropertyKeys.syncTooltip, false);
    }

    public void setSyncTooltip(boolean syncTooltip) {
        getStateHelper().put(PropertyKeys.syncTooltip, syncTooltip);
    }

    public String getLabelTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.labelTemplate, null);
    }

    public void setLabelTemplate(String labelTemplate) {
        getStateHelper().put(PropertyKeys.labelTemplate, labelTemplate);
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public boolean isAutoWidth() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoWidth, true);
    }

    public void setAutoWidth(boolean autoWidth) {
        getStateHelper().put(PropertyKeys.autoWidth, autoWidth);
    }

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}