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
package org.primefaces.component.layout;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "layout/layout.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "layout/layout.js")
})
public class Layout extends LayoutBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Layout";

    public static final String UNIT_CLASS = "ui-layout-unit ui-widget ui-widget-content ui-corner-all";
    public static final String UNIT_HEADER_CLASS = "ui-layout-unit-header ui-widget-header ui-corner-all";
    public static final String UNIT_CONTENT_CLASS = "ui-layout-unit-content ui-widget-content";
    public static final String UNIT_FOOTER_CLASS = "ui-layout-unit-footer ui-widget-header ui-corner-all";
    public static final String UNIT_HEADER_TITLE_CLASS = "ui-layout-unit-header-title";
    public static final String UNIT_FOOTER_TITLE_CLASS = "ui-layout-unit-footer-title";
    public static final String UNIT_HEADER_ICON_CLASS = "ui-layout-unit-header-icon ui-state-default ui-corner-all";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("close", CloseEvent.class)
            .put("resize", ResizeEvent.class)
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

    protected LayoutUnit getLayoutUnitByPosition(String name) {
        for (UIComponent child : getChildren()) {
            if (child instanceof LayoutUnit) {
                LayoutUnit layoutUnit = (LayoutUnit) child;

                if (layoutUnit.getPosition().equalsIgnoreCase(name)) {
                    return layoutUnit;
                }
            }
        }

        return null;
    }

    public boolean isNested() {
        return getParent() instanceof LayoutUnit;
    }

    public boolean isElementLayout() {
        return !isNested() && !isFullPage();
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
    }

    private boolean isSelfRequest(FacesContext context) {
        return getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = getClientId(context);

        if (isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if (eventName.equals("toggle")) {
                boolean collapsed = Boolean.parseBoolean(params.get(clientId + "_collapsed"));
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;
                unit.setCollapsed(collapsed);

                wrapperEvent = new ToggleEvent(unit, behaviorEvent.getBehavior(), visibility);
            }
            else if (eventName.equals("close")) {
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                unit.setVisible(false);

                wrapperEvent = new CloseEvent(unit, behaviorEvent.getBehavior());
            }
            else if (eventName.equals("resize")) {
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                String position = unit.getPosition();
                int width = Integer.valueOf(params.get(clientId + "_width"));
                int height = Integer.valueOf(params.get(clientId + "_height"));

                if (position.equals("west") || position.equals("east")) {
                    unit.setSize(String.valueOf(width));
                }
                else if (position.equals("north") || position.equals("south")) {
                    unit.setSize(String.valueOf(height));
                }

                wrapperEvent = new ResizeEvent(unit, behaviorEvent.getBehavior(), width, height);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
}