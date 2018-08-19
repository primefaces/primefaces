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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEffectSpeed() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, null);
    }

    public void setEffectSpeed(java.lang.String _effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
    }

    public java.lang.String getPanelStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(java.lang.String _panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, _panelStyle);
    }

    public java.lang.String getPanelStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(java.lang.String _panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, _panelStyleClass);
    }

    public java.lang.String getVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
    }

    public java.lang.String getHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.height, "200");
    }

    public void setHeight(java.lang.String _height) {
        getStateHelper().put(PropertyKeys.height, _height);
    }

    public boolean isEditable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean _editable) {
        getStateHelper().put(PropertyKeys.editable, _editable);
    }

    public boolean isFilter() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.filter, false);
    }

    public void setFilter(boolean _filter) {
        getStateHelper().put(PropertyKeys.filter, _filter);
    }

    public java.lang.String getFilterMatchMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(java.lang.String _filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
    }

    public java.lang.String getFilterFunction() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(java.lang.String _filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
    }

    public java.lang.String getFilterPlaceholder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterPlaceholder, null);
    }

    public void setFilterPlaceholder(java.lang.String _filterPlaceholder) {
        getStateHelper().put(PropertyKeys.filterPlaceholder, _filterPlaceholder);
    }

    public boolean isCaseSensitive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitive, false);
    }

    public void setCaseSensitive(boolean _caseSensitive) {
        getStateHelper().put(PropertyKeys.caseSensitive, _caseSensitive);
    }

    public int getMaxlength() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxlength, Integer.MAX_VALUE);
    }

    public void setMaxlength(int _maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, _maxlength);
    }

    public java.lang.String getAppendTo() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(java.lang.String _appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, _appendTo);
    }

    @Override
    public java.lang.String getTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.title, null);
    }

    @Override
    public void setTitle(java.lang.String _title) {
        getStateHelper().put(PropertyKeys.title, _title);
    }

    public boolean isSyncTooltip() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.syncTooltip, false);
    }

    public void setSyncTooltip(boolean _syncTooltip) {
        getStateHelper().put(PropertyKeys.syncTooltip, _syncTooltip);
    }

    public java.lang.String getLabelTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.labelTemplate, null);
    }

    public void setLabelTemplate(java.lang.String _labelTemplate) {
        getStateHelper().put(PropertyKeys.labelTemplate, _labelTemplate);
    }

    public java.lang.String getPlaceholder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(java.lang.String _placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, _placeholder);
    }

    public boolean isAutoWidth() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoWidth, true);
    }

    public void setAutoWidth(boolean _autoWidth) {
        getStateHelper().put(PropertyKeys.autoWidth, _autoWidth);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}