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
package org.primefaces.component.dashboard;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.DashboardReorderEvent;

import jakarta.faces.component.UIPanel;
import jakarta.faces.component.behavior.ClientBehaviorHolder;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "reorder", event = DashboardReorderEvent.class, description = "Fired when dashboard panels are reordered", defaultEvent = true)
})
public abstract class DashboardBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DashboardRenderer";

    public DashboardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Dashboard model instance representing the layout of the UI.")
    public abstract org.primefaces.model.dashboard.DashboardModel getModel();

    @Property(description = "Disables the component.", defaultValue = "false")
    public abstract boolean isDisabled();

    @Property(description = "Allow reordering of panels.", defaultValue = "true")
    public abstract boolean isReordering();

    @Property(description = "Inline style of the dashboard container.")
    public abstract String getStyle();

    @Property(description = "Style class of the dashboard container.")
    public abstract String getStyleClass();

    @Property(description = "In responsive mode, PrimeFlex can be used to dynamically size columns and responsiveness.", defaultValue = "false")
    public abstract boolean isResponsive();

    @Property(description = "Name of collection iterator which will be the value of DashboardWidget.getValue().")
    public abstract String getVar();

    @Property(description = "Scope for dashboard drag and drop behaviour. Items can be dragged between multiple dashboards with the same scope.",
            implicitDefaultValue = "dashboard")
    public abstract String getScope();
}