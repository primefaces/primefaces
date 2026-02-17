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
package org.primefaces.component.progressbar;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

@FacesComponent(value = ProgressBar.COMPONENT_TYPE, namespace = ProgressBar.COMPONENT_FAMILY)
@FacesComponentInfo(description = "ProgressBar is a process status indicator component.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class ProgressBar extends ProgressBarBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.ProgressBar";

    public static final String CONTAINER_CLASS = "ui-progressbar ui-widget ui-widget-content";
    public static final String DETERMINATE_CLASS = "ui-progressbar-determinate";
    public static final String INDETERMINATE_CLASS = "ui-progressbar-indeterminate";
    public static final String SEVERITY_INFO_CLASS = "ui-progressbar-info";
    public static final String SEVERITY_SUCCESS_CLASS = "ui-progressbar-success";
    public static final String SEVERITY_WARNING_CLASS = "ui-progressbar-warning";
    public static final String SEVERITY_DANGER_CLASS = "ui-progressbar-danger";
    public static final String VALUE_CLASS = "ui-progressbar-value ui-widget-header";
    public static final String LABEL_CLASS = "ui-progressbar-label";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (isAjaxRequestSource(context)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            behaviorEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);

            super.queueEvent(behaviorEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

}
