/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.selectmanymenu;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectManyMenu extends SelectManyMenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectManyMenu";

    public static final String CONTAINER_CLASS = "ui-selectmanymenu ui-inputfield ui-widget ui-widget-content ui-corner-all";
    public static final String LIST_CONTAINER_CLASS = "ui-selectlistbox-listcontainer";
    public static final String LIST_CLASS = "ui-selectlistbox-list";
    public static final String ITEM_CLASS = "ui-selectlistbox-item ui-corner-all";
    public static final String CHECKBOX_CLASS = "ui-selectlistbox-chkbox";
    public static final String FILTER_CONTAINER_CLASS = "ui-selectlistbox-filter-container";
    public static final String FILTER_CLASS = "ui-selectlistbox-filter ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_ICON_CLASS = "ui-icon ui-icon-search";
    public static final List<String> DOM_EVENTS = LangUtils.unmodifiableList("onchange", "onclick", "ondblclick");

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("itemSelect", SelectEvent.class)
            .put("itemUnselect", UnselectEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = LangUtils.concat(BEHAVIOR_EVENT_MAPPING.keySet(), DEFAULT_SELECT_EVENT_NAMES);

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if (eventName != null && event instanceof AjaxBehaviorEvent) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;
            if ("itemSelect".equals(eventName)) {
                String clientId = getClientId(context);
                Object selectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemSelect"));
                SelectEvent toggleSelectEvent = new SelectEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), selectedItemValue);
                toggleSelectEvent.setPhaseId(event.getPhaseId());

                super.queueEvent(toggleSelectEvent);
            }
            else if ("itemUnselect".equals(eventName)) {
                Object unselectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemUnselect"));
                UnselectEvent unselectEvent = new UnselectEvent(this, ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(unselectEvent);
            }
            else {
                super.queueEvent(event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }
}