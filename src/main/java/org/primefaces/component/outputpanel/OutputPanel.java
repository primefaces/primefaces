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
package org.primefaces.component.outputpanel;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;

import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class OutputPanel extends OutputPanelBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.OutputPanel";

    public static final String CONTAINER_CLASS = "ui-outputpanel ui-widget";
    public static final String LOADING_CLASS = "ui-outputpanel-loading ui-widget";

    private static final String DEFAULT_EVENT = "load";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("load", null)
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