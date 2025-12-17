/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.behavior.validate;

import org.primefaces.cdk.api.FacesBehaviorDescription;
import org.primefaces.component.api.InputHolder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.FacesBehavior;

@FacesBehavior(ClientValidatorBehavior.BEHAVIOR_ID)
@FacesBehaviorDescription("ClientValidator is a behavior element used in Client Side Validation to do instant validation in case you do not want"
        + " to wait for the users to fill in the form and hit commandButton/commandLink.")
public class ClientValidatorBehavior extends ClientValidatorBehaviorBaseImpl {

    public static final String BEHAVIOR_ID = "org.primefaces.behavior.ClientValidatorBehavior";

    private static final Logger LOGGER = Logger.getLogger(ClientValidatorBehavior.class.getName());

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        if (isDisabled()) {
            return null;
        }

        if (!behaviorContext.getFacesContext().isProjectStage(ProjectStage.Production)) {
            if (!PrimeApplicationContext.getCurrentInstance(behaviorContext.getFacesContext()).getConfig().isClientSideValidationEnabled()) {
                LOGGER.log(Level.WARNING, Constants.ContextParams.CSV + " must be enabled for p:clientValidator!");
            }
        }

        UIComponent component = behaviorContext.getComponent();
        String target = (component instanceof InputHolder) ? "'" + ((InputHolder) component).getValidatableInputClientId() + "'" : "this";

        return "return PrimeFaces.vi(" + target + ", true, true)";
    }

}
