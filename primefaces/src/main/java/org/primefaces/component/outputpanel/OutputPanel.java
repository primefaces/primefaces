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
package org.primefaces.component.outputpanel;

import org.primefaces.cdk.api.FacesComponentDescription;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = OutputPanel.COMPONENT_TYPE, namespace = OutputPanel.COMPONENT_FAMILY)
@FacesComponentDescription("OutputPanel is a container component with deferred loading and layout options.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class OutputPanel extends OutputPanelBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.OutputPanel";

    public static final String CONTAINER_CLASS = "ui-outputpanel ui-widget";
    public static final String LOADING_CLASS = "ui-outputpanel-loading ui-icon pi pi-spin pi-spinner";

    public boolean isContentLoadRequest(FacesContext context) {
        String clientId = getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_load");
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (shouldSkipChildren(context)) {
            decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!shouldSkipChildren(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!shouldSkipChildren(context)) {
            super.processUpdates(context);
        }
    }

    private boolean shouldSkipChildren(FacesContext context) {
        return isContentLoadRequest(context);
    }
}