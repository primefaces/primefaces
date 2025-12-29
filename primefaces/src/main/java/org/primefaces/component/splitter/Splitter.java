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
package org.primefaces.component.splitter;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.SplitterResizeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Splitter.COMPONENT_TYPE, namespace = Splitter.COMPONENT_FAMILY)
@FacesComponentDescription("Splitter is utilized to separate and resize panels.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Splitter extends SplitterBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Splitter";

    public static final String STYLE_CLASS = "ui-splitter ui-widget";
    public static final String LAYOUT_VERTICAL_CLASS = "ui-splitter-vertical";
    public static final String LAYOUT_HORIZONTAL_CLASS = "ui-splitter-horizontal";
    public static final String GUTTER_CLASS = "ui-splitter-gutter";
    public static final String GUTTER_HANDLE_CLASS = "ui-splitter-gutter-handle";


    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (isAjaxBehaviorEventSource(event)) {
            String clientId = getClientId(context);

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.resizeEnd)) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                List<Double> panelSizes = new ArrayList<>();
                String[] sizes = params.get(clientId + "_panelSizes").split("_");

                for (int i = 0; i < sizes.length; i++) {
                    panelSizes.add(Double.valueOf(sizes[i]));
                }

                super.queueEvent(new SplitterResizeEvent(this, behaviorEvent.getBehavior(), panelSizes));
            }
        }
        else {
            super.queueEvent(event);
        }
    }
}
