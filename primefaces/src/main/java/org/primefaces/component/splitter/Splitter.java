/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.primefaces.event.SplitterResizeEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Splitter extends SplitterBase {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Splitter";

    public static final String STYLE_CLASS = "ui-splitter ui-widget";
    public static final String LAYOUT_VERTICAL_CLASS = "ui-splitter-vertical";
    public static final String LAYOUT_HORIZONTAL_CLASS = "ui-splitter-horizontal";
    public static final String GUTTER_CLASS = "ui-splitter-gutter";
    public static final String GUTTER_HANDLE_CLASS = "ui-splitter-gutter-handle";

    private static final String DEFAULT_EVENT = "resizeEnd";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put(DEFAULT_EVENT, SplitterResizeEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (ComponentUtils.isRequestSource(this, context)) {
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            if (DEFAULT_EVENT.equals(eventName)) {
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
