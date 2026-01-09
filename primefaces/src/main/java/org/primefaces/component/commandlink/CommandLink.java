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
package org.primefaces.component.commandlink;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.component.api.DialogReturnAware;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = CommandLink.COMPONENT_TYPE, namespace = CommandLink.COMPONENT_FAMILY)
@FacesComponentDescription("CommandLink extends standard Faces commandLink with extra PrimeFaces capabilities.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class CommandLink extends CommandLinkBaseImpl implements DialogReturnAware {

    public static final String COMPONENT_TYPE = "org.primefaces.component.CommandLink";

    public static final String STYLE_CLASS = "ui-commandlink ui-widget";
    public static final String DISABLED_STYLE_CLASS = "ui-commandlink ui-widget ui-state-disabled";

    private String confirmationScript;

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        if (isDialogReturnEvent(event, context)) {
            queueDialogReturnEvent(event, context, this, super::queueEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues) != null);
    }

    @Override
    public boolean isAjaxified() {
        return isAjax();
    }

    @Override
    public String getConfirmationScript() {
        return confirmationScript;
    }

    @Override
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }

    @Override
    public boolean requiresConfirmation() {
        return confirmationScript != null;
    }
}