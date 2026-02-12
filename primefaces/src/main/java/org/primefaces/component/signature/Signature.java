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
package org.primefaces.component.signature;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = Signature.COMPONENT_TYPE, namespace = Signature.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Signature is used to draw a signature as an input.")
@ResourceDependency(library = "primefaces", name = "signature/signature.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "signature/signature.js")
public class Signature extends SignatureBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Signature";

    public static final String STYLE_CLASS = "ui-inputfield ui-inputtextarea ui-widget ui-state-default";
    public static final String READONLY_STYLE_CLASS = "ui-widget ui-widget-content";

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);
        String base64Value = this.getBase64Value();

        if (base64Value != null) {
            ValueExpression ve = this.getValueExpression(PropertyKeys.base64Value.toString());
            if (ve != null) {
                ve.setValue(context.getELContext(), base64Value);
                getStateHelper().put(PropertyKeys.base64Value, null);
            }
        }

        String textValue = this.getTextValue();
        if (textValue != null) {
            ValueExpression ve = this.getValueExpression(PropertyKeys.textValue.toString());
            if (ve != null) {
                ve.setValue(context.getELContext(), textValue);
                getStateHelper().put(PropertyKeys.textValue, null);
            }
        }
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_canvas";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_value";
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