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
package org.primefaces.component.password;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Password extends PasswordBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Password";

    public static final String STYLE_CLASS = "ui-inputfield ui-password ui-widget ui-state-default ui-corner-all";

    public static final String INVALID_MATCH_KEY = "primefaces.password.INVALID_MATCH";

    @Override
    protected void validateValue(FacesContext context, Object value) {
        super.validateValue(context, value);
        String match = getMatch();
        Object submittedValue = getSubmittedValue();

        if (isValid() && !LangUtils.isValueBlank(match)) {
            Password matchWith = (Password) SearchExpressionFacade.resolveComponent(context, this, match);

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
                    params[0] = MessageFactory.getLabel(context, this);
                    params[1] = MessageFactory.getLabel(context, matchWith);

                    msg = MessageFactory.getMessage(Password.INVALID_MATCH_KEY, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
    }
}