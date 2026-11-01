/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.inputotp;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.MessageFactory;

import java.util.Objects;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.LengthValidator;

@FacesComponent(value = InputOtp.COMPONENT_TYPE, namespace = InputOtp.COMPONENT_FAMILY)
@FacesComponentInfo(description = "InputOtp is an input for one-time passwords and PINs, one input per character.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "inputotp/inputotp.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "inputotp/inputotp.js")
public class InputOtp extends InputOtpBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.InputOtp";

    public static final String STYLE_CLASS = "p-inputotp p-widget";
    public static final String INPUT_CLASS = "p-inputotp-input p-inputfield p-inputtext p-widget p-state-default";
    public static final String SEPARATOR_CLASS = "p-inputotp-separator";

    /**
     * @return the client id of the input of the first character, a label references it
     */
    @Override
    public String getInputClientId() {
        return getInputClientId(0);
    }

    /**
     * @param index the position of the character, starting at 0
     * @return the client id of the input of the character
     */
    public String getInputClientId(int index) {
        return getClientId(getFacesContext()) + "_" + index;
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_hidden";
    }

    @Override
    protected void validateValue(FacesContext context, Object newValue) {
        // an incomplete code is invalid before the validators run, they only get complete codes
        String submittedValue = Objects.toString(getSubmittedValue(), "");
        if (submittedValue.isEmpty() || validateCode(context, submittedValue)) {
            super.validateValue(context, newValue);
        }
    }

    /**
     * Validates the length of a code and, with integerOnly, its characters.
     *
     * @param context the {@link FacesContext}
     * @param submittedValue the submitted code, not empty
     * @return whether the code is valid, otherwise a message is added and the component is invalid
     */
    protected boolean validateCode(FacesContext context, String submittedValue) {
        FacesMessage message = null;
        String customMessage = null;
        // one character per input: a character outside the BMP (e.g. an emoji) is one code point like in the widget
        int length = submittedValue.codePointCount(0, submittedValue.length());
        if (length != getLength()) {
            message = MessageFactory.getFacesMessage(context,
                    length < getLength() ? LengthValidator.MINIMUM_MESSAGE_ID : LengthValidator.MAXIMUM_MESSAGE_ID,
                    FacesMessage.SEVERITY_ERROR, getLength(), ComponentUtils.getLabel(context, this));
            customMessage = getValidatorMessage();
        }
        else if (isIntegerOnly() && !submittedValue.chars().allMatch(c -> c >= '0' && c <= '9')) {
            message = MessageFactory.getFacesMessage(context, CONVERSION_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                    ComponentUtils.getLabel(context, this));
            customMessage = getConverterMessage();
        }

        if (message == null) {
            return true;
        }
        if (customMessage != null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, customMessage, customMessage);
        }
        context.addMessage(getClientId(context), message);
        setValid(false);
        return false;
    }
}
