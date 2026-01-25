/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.toggleswitch;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIInput;
import jakarta.faces.component.behavior.ClientBehaviorHolder;

public abstract class ToggleSwitchBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ToggleSwitchRenderer";

    public enum PropertyKeys {

        ariaLabel,
        disabled,
        label,
        offIcon,
        onblur,
        onchange,
        onfocus,
        onIcon,
        readonly,
        style,
        styleClass,
        tabindex,
        widgetVar,
    }

    public ToggleSwitchBase() {
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

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getAriaLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaLabel, null);
    }

    public void setAriaLabel(String ariaLabel) {
        getStateHelper().put(PropertyKeys.ariaLabel, ariaLabel);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getOnchange() {
        return (String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(String onchange) {
        getStateHelper().put(PropertyKeys.onchange, onchange);
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

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, null);
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public String getOnfocus() {
        return (String) getStateHelper().eval(PropertyKeys.onfocus, null);
    }

    public void setOnfocus(String onfocus) {
        getStateHelper().put(PropertyKeys.onfocus, onfocus);
    }

    public String getOnblur() {
        return (String) getStateHelper().eval(PropertyKeys.onblur, null);
    }

    public void setOnblur(String onblur) {
        getStateHelper().put(PropertyKeys.onblur, onblur);
    }

    public String getOnIcon() {
        return (String) getStateHelper().eval(PropertyKeys.onIcon, null);
    }

    public void setOnIcon(String onIcon) {
        getStateHelper().put(PropertyKeys.onIcon, onIcon);
    }

    public String getOffIcon() {
        return (String) getStateHelper().eval(PropertyKeys.offIcon, null);
    }

    public void setOffIcon(String offIcon) {
        getStateHelper().put(PropertyKeys.offIcon, offIcon);
    }

    public boolean isReadonly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.readonly, false);

    }

    public void setReadonly(boolean readonly) {
        getStateHelper().put(PropertyKeys.readonly, readonly);
    }

    @Override
    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return this.getInputClientId();
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public String getAriaDescribedBy() {
        return (String) getStateHelper().get("ariaDescribedBy");
    }

    @Override
    public void setAriaDescribedBy(String ariaDescribedBy) {
        getStateHelper().put("ariaDescribedBy", ariaDescribedBy);
    }
}