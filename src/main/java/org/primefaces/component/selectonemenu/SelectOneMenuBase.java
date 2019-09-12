/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.selectonemenu;

import javax.faces.component.html.HtmlSelectOneMenu;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;

public abstract class SelectOneMenuBase extends HtmlSelectOneMenu implements Widget, InputHolder, RTLAware {

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
        dynamic,
        dir
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

    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }
}