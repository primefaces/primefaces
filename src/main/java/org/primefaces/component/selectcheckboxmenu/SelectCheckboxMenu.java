/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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