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
package org.primefaces.component.selectmanymenu;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = SelectManyMenu.COMPONENT_TYPE, namespace = SelectManyMenu.COMPONENT_FAMILY)
@FacesComponentInfo(description = "SelectManyMenu is an extended version of the standard Faces SelectManyMenu.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectManyMenu extends SelectManyMenuBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectManyMenu";

    public static final String CONTAINER_CLASS = "ui-selectmanymenu ui-inputfield ui-widget ui-widget-content";
    public static final String LIST_CONTAINER_CLASS = "ui-selectlistbox-listcontainer";
    public static final String LIST_CLASS = "ui-selectlistbox-list";
    public static final String ITEM_CLASS = "ui-selectlistbox-item";
    public static final String CHECKBOX_CLASS = "ui-selectlistbox-chkbox";
    public static final String FILTER_CONTAINER_CLASS = "ui-selectlistbox-filter-container";
    public static final String FILTER_CLASS = "ui-selectlistbox-filter ui-inputfield ui-widget ui-state-default";
    public static final String FILTER_ICON_CLASS = "ui-icon ui-icon-search";
    public static final List<String> DOM_EVENTS = LangUtils.unmodifiableList("onchange", "onclick", "ondblclick");

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
    public String getAriaDescribedBy() {
        return (String) getStateHelper().get("ariaDescribedBy");
    }

    @Override
    public void setAriaDescribedBy(String ariaDescribedBy) {
        getStateHelper().put("ariaDescribedBy", ariaDescribedBy);
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (isAjaxBehaviorEvent(event)) {
            FacesContext context = getFacesContext();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
                Object selectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemSelect"));
                SelectEvent<?> selectEvent = new SelectEvent<>(this, ((AjaxBehaviorEvent) event).getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemUnselect)) {
                Object unselectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemUnselect"));
                UnselectEvent<?> unselectEvent = new UnselectEvent<>(this, ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
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