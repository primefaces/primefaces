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
package org.primefaces.component.password;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = Password.COMPONENT_TYPE, namespace = Password.COMPONENT_FAMILY)
@FacesComponentDescription("Password is an extended version of standard inputSecret component with theme integration and strength indicator.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Password extends PasswordBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Password";

    public static final String STYLE_CLASS = "ui-password";
    public static final String INPUT_CLASS = "ui-inputfield ui-widget ui-state-default";
    public static final String WRAPPER_CLASS = "ui-inputwrapper";
    public static final String ICON_CLASS = "ui-password-icon";
    public static final String MASKED_CLASS = "ui-password-masked";
    public static final String UNMASKED_CLASS = "ui-password-unmasked";

    public static final String INVALID_MATCH_KEY = "primefaces.password.INVALID_MATCH";

    @Override
    protected void validateValue(FacesContext context, Object value) {
        super.validateValue(context, value);
        String match = getMatch();
        Object submittedValue = getSubmittedValue();

        if (isValid() && LangUtils.isNotBlank(match)) {
            Password matchWith = (Password) SearchExpressionUtils.contextlessResolveComponent(context, this, match);

            if (submittedValue != null && !submittedValue.equals(matchWith.getSubmittedValue())) {
                setValid(false);
                matchWith.setValid(false);

                String validatorMessage = getValidatorMessage();
                FacesMessage msg = null;

                if (validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = ComponentUtils.getLabel(context, this);
                    params[1] = ComponentUtils.getLabel(context, matchWith);

                    msg = MessageFactory.getFacesMessage(context, Password.INVALID_MATCH_KEY, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
    }
}