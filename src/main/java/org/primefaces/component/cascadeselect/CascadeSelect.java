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
package org.primefaces.component.cascadeselect;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class CascadeSelect extends CascadeSelectBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.CascadeSelect";

    public static final String STYLE_CLASS = "ui-cascadeselect ui-widget ui-state-default ui-corner-all";
    public static final String LABEL_CLASS = "ui-cascadeselect-label ui-inputfield ui-corner-all";
    public static final String PANEL_CLASS = "ui-cascadeselect-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-shadow ui-input-overlay";
    public static final String ITEMS_WRAPPER_CLASS = "ui-cascadeselect-items-wrapper";
    public static final String PANEL_ITEMS_CLASS = "ui-cascadeselect-panel ui-cascadeselect-items";
    public static final String TRIGGER_CLASS = "ui-cascadeselect-trigger";
    public static final String TRIGGER_ICON_CLASS = "ui-cascadeselect-trigger-icon pi pi-chevron-down";
    public static final String ITEM_CLASS = "ui-cascadeselect-item";
    public static final String ITEM_GROUP_CLASS = "ui-cascadeselect-item-group";
    public static final String ITEM_CONTENT_CLASS = "ui-cascadeselect-item-content";
    public static final String ITEM_TEXT_CLASS = "ui-cascadeselect-item-text";
    public static final String GROUP_ICON_CLASS = "ui-cascadeselect-group-icon pi pi-angle-right";
    public static final String SUBLIST_CLASS = "ui-cascadeselect-sublist";
    public static final String LABEL_EMPTY_CLASS = "ui-cascadeselect-label-empty";

    private static final String DEFAULT_EVENT = "itemSelect";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("itemSelect", SelectEvent.class)
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

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if ("itemSelect".equals(eventName)) {
                Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                        context,
                        "javax.faces.SelectOne",
                        "javax.faces.Listbox");
                Object item = renderer.getConvertedValue(context, this, getSubmittedValue());
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), item);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
    }

    public String getPanelClientId() {
        return getClientId(getFacesContext()) + "_panel";
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }
}