/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.component.column.Column;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = SelectCheckboxMenu.COMPONENT_TYPE, namespace = SelectCheckboxMenu.COMPONENT_FAMILY)
@FacesComponentInfo(description = "SelectCheckboxMenu is a multi select component that displays options in an overlay.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectCheckboxMenu extends SelectCheckboxMenuBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectCheckboxMenu";

    public static final String STYLE_CLASS = "p-selectcheckboxmenu p-widget p-state-default";
    public static final String LABEL_CONTAINER_CLASS = "p-selectcheckboxmenu-label-container";
    public static final String LABEL_CLASS = "p-selectcheckboxmenu-label";
    public static final String TRIGGER_CLASS = "p-selectcheckboxmenu-trigger p-state-default";
    public static final String TRIGGER_ICON_CLASS = "p-icon p-dropdown-icon p-selectcheckboxmenu-dropdown-icon";
    public static final String PANEL_CLASS = "p-selectcheckboxmenu-panel p-widget p-widget-content p-hidden p-input-overlay";
    public static final String HEADER_CLASS = "p-widget-header p-selectcheckboxmenu-header";
    public static final String FOOTER_CLASS = "p-selectcheckboxmenu-footer";
    public static final String FILTER_CONTAINER_CLASS = "p-selectcheckboxmenu-filter-container";
    public static final String FILTER_CLASS = "p-selectcheckboxmenu-filter p-inputfield p-inputtext p-widget p-state-default";
    public static final String FILTER_ICON_CLASS = "p-icon p-search-icon p-selectcheckboxmenu-filter-icon";
    public static final String CLOSER_CLASS = "p-selectcheckboxmenu-close";
    public static final String CLOSER_ICON_CLASS = "p-icon p-close-icon p-selectcheckboxmenu-close-icon";
    public static final String ITEMS_WRAPPER_CLASS = "p-selectcheckboxmenu-items-wrapper";
    public static final String LIST_CLASS = "p-selectcheckboxmenu-items p-selectcheckboxmenu-list p-widget-content p-widget";
    public static final String TABLE_CLASS = "p-selectcheckboxmenu-items p-selectcheckboxmenu-table p-widget-content p-widget";
    public static final String ITEM_CLASS = "p-selectcheckboxmenu-item p-selectcheckboxmenu-list-item";
    public static final String ROW_ITEM_GROUP_CLASS = "p-selectcheckboxmenu-item-group p-selectcheckboxmenu-row p-widget-content";
    public static final String ROW_ITEM_CLASS = "p-selectcheckboxmenu-item p-selectcheckboxmenu-row p-widget-content";
    public static final String MULTIPLE_CLASS = "p-selectcheckboxmenu-multiple";
    public static final String MULTIPLE_CONTAINER_CLASS = "p-selectcheckboxmenu-multiple-container p-widget p-inputfield p-state-default";
    public static final String TOKEN_DISPLAY_CLASS = "p-selectcheckboxmenu-token p-state-active";
    public static final String TOKEN_LABEL_CLASS = "p-selectcheckboxmenu-token-label";
    public static final String TOKEN_ICON_CLASS = "p-selectcheckboxmenu-token-icon p-icon p-close-icon p-selectcheckboxmenu-chip-remove-icon";
    public static final String CHECKBOX_INPUT_CLASS = "p-selectcheckboxmenu-item-input";

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_focus";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext());
    }

    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof Column column) {
                columns.add(column);
            }
        }

        return columns;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (isAjaxBehaviorEventSource(event)) {
            FacesContext context = getFacesContext();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.toggleSelect)) {
                String clientId = getClientId(context);
                boolean checked = Boolean.parseBoolean(params.get(clientId + "_checked"));
                ToggleSelectEvent toggleSelectEvent = new ToggleSelectEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), checked);
                toggleSelectEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(toggleSelectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
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