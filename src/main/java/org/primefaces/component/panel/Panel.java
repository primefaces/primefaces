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
package org.primefaces.component.panel;

import java.util.Collection;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.component.menu.Menu;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Panel extends PanelBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Panel";

    public static final String PANEL_CLASS = "ui-panel ui-widget ui-widget-content ui-corner-all";
    public static final String PANEL_TITLEBAR_CLASS = "ui-panel-titlebar ui-widget-header ui-helper-clearfix ui-corner-all";
    public static final String PANEL_TITLE_CLASS = "ui-panel-title";
    public static final String PANEL_TITLE_ICON_CLASS = "ui-panel-titlebar-icon ui-corner-all ui-state-default";
    public static final String PANEL_CONTENT_CLASS = "ui-panel-content ui-widget-content";
    public static final String PANEL_FOOTER_CLASS = "ui-panel-footer ui-widget-content";
    public static final String PANEL_ACTIONS_CLASS = "ui-panel-actions";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("toggle", ToggleEvent.class)
            .put("close", CloseEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private Menu optionsMenu;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public Menu getOptionsMenu() {
        if (optionsMenu == null) {
            UIComponent optionsFacet = getFacet("options");
            if (optionsFacet != null) {
                if (optionsFacet instanceof Menu) {
                    optionsMenu = (Menu) optionsFacet;
                }
                else {
                    optionsMenu = (Menu) optionsFacet.getChildren().get(0);
                }
            }

        }

        return optionsMenu;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = getClientId(context);

        if (isSelfRequest(context)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("toggle")) {
                boolean collapsed = Boolean.parseBoolean(params.get(clientId + "_collapsed"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;

                ToggleEvent eventToQueue = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility);
                eventToQueue.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(new ToggleEvent(this, behaviorEvent.getBehavior(), visibility));

            }
            else if (eventName.equals("close")) {
                CloseEvent eventToQueue = new CloseEvent(this, behaviorEvent.getBehavior());
                eventToQueue.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(eventToQueue);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (isSelfRequest(context)) {
            decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isSelfRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isSelfRequest(context)) {
            super.processUpdates(context);
        }

        FacesContext facesContext = getFacesContext();
        ELContext eLContext = facesContext.getELContext();

        ValueExpression collapsedVE = getValueExpression(PropertyKeys.collapsed.toString());
        if (collapsedVE != null && !collapsedVE.isReadOnly(eLContext)) {
            collapsedVE.setValue(eLContext, isCollapsed());
            getStateHelper().put(Panel.PropertyKeys.collapsed, null);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }
}