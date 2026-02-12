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
package org.primefaces.component.radiobutton;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.expression.SearchExpressionUtils;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = RadioButton.COMPONENT_TYPE, namespace = RadioButton.COMPONENT_FAMILY)
@FacesComponentInfo(description = "RadioButton is a helper component of SelectOneRadio to implement custom layouts.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class RadioButton extends RadioButtonBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.RadioButton";

    @Override
    public String getInputClientId() {
        FacesContext context = FacesContext.getCurrentInstance();
        return SearchExpressionUtils.contextlessResolveComponent(context, this, getFor()).getClientId(context)
                + UINamingContainer.getSeparatorChar(context) + getItemIndex() + "_clone";
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
}
