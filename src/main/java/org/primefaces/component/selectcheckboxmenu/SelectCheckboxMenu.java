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
package org.primefaces.component.selectcheckboxmenu;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class SelectCheckboxMenu extends SelectCheckboxMenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectCheckboxMenu";

    public static final String STYLE_CLASS = "ui-selectcheckboxmenu ui-widget ui-state-default ui-corner-all";
    public static final String LABEL_CONTAINER_CLASS = "ui-selectcheckboxmenu-label-container";
    public static final String LABEL_CLASS = "ui-selectcheckboxmenu-label ui-corner-all";
    public static final String TRIGGER_CLASS = "ui-selectcheckboxmenu-trigger ui-state-default ui-corner-right";
    public static final String PANEL_CLASS = "ui-selectcheckboxmenu-panel ui-widget-content ui-corner-all ui-helper-hidden";
    public static final String LIST_CLASS = "ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public static final String ITEM_CLASS = "ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all ui-helper-clearfix";
    public static final String MULTIPLE_CLASS = "ui-selectcheckboxmenu-multiple";
    public static final String MULTIPLE_CONTAINER_CLASS = "ui-selectcheckboxmenu-multiple-container ui-widget ui-inputfield ui-state-default ui-corner-all";
    public static final String TOKEN_DISPLAY_CLASS = "ui-selectcheckboxmenu-token ui-state-active ui-corner-all";
    public static final String TOKEN_LABEL_CLASS = "ui-selectcheckboxmenu-token-label";
    public static final String TOKEN_ICON_CLASS = "ui-selectcheckboxmenu-token-icon ui-icon ui-icon-close";

    private static final String DEFAULT_EVENT = "change";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("change", null)
            .put("toggleSelect", ToggleSelectEvent.class)
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
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if (event instanceof AjaxBehaviorEvent && eventName.equals("toggleSelect")) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            boolean checked = Boolean.parseBoolean(params.get(clientId + "_checked"));
            ToggleSelectEvent toggleSelectEvent = new ToggleSelectEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), checked);
            toggleSelectEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(toggleSelectEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
}