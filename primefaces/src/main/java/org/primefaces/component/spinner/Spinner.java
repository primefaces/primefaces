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
package org.primefaces.component.spinner;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.util.LocaleUtils;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = Spinner.COMPONENT_TYPE, namespace = Spinner.COMPONENT_FAMILY)
@FacesComponentDescription("Spinner is an input component to provide a numerical input via increment and decrement buttons.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Spinner extends SpinnerBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Spinner";

    public static final String CONTAINER_CLASS = "ui-spinner ui-widget";
    public static final String BUTTONS_CLASS_PREFIX = "ui-spinner-";
    public static final String INPUT_CLASS = "ui-spinner-input ui-inputfield ui-state-default";
    public static final String UP_BUTTON_CLASS = "ui-spinner-button ui-spinner-up ui-button ui-widget ui-state-default ui-button-text-only";
    public static final String DOWN_BUTTON_CLASS = "ui-spinner-button ui-spinner-down ui-button ui-widget ui-state-default ui-button-text-only";
    public static final String ICON_BASE_CLASS = "ui-icon ui-c";
    public static final String STACKED_UP_ICON_CLASS = "ui-icon-triangle-1-n";
    public static final String STACKED_DOWN_ICON_CLASS = "ui-icon-triangle-1-s";
    public static final String HORIZONTAL_UP_ICON_CLASS = "pi pi-plus";
    public static final String HORIZONTAL_DOWN_ICON_CLASS = "pi pi-minus";

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
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

    @Override
    public String getDecimalSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.decimalSeparator,
            () -> LocaleUtils.getDecimalSeparator(getFacesContext()));
    }

    @Override
    public String getThousandSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.thousandSeparator,
            () -> LocaleUtils.getThousandSeparator(getFacesContext()));
    }
}