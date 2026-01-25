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
package org.primefaces.component.multiselectlistbox;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;

import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = MultiSelectListbox.COMPONENT_TYPE, namespace = MultiSelectListbox.COMPONENT_FAMILY)
@FacesComponentDescription("MultiSelectListbox is used to select an item from a collection of listboxes that are in parent-child relationship.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class MultiSelectListbox extends MultiSelectListboxBaseImpl {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    public static final String COMPONENT_TYPE = "org.primefaces.component.MultiSelectListbox";
    public static final String CONTAINER_CLASS = "ui-multiselectlistbox ui-widget ui-helper-clearfix";
    public static final String LIST_CONTAINER_CLASS = "ui-multiselectlistbox-listcontainer";
    public static final String LIST_HEADER_CLASS = "ui-multiselectlistbox-header ui-widget-header";
    public static final String LIST_CLASS = "ui-multiselectlistbox-list ui-inputfield ui-widget-content";
    public static final String ITEM_CLASS = "ui-multiselectlistbox-item";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
                Object selectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(clientId + "_itemSelect"));
                SelectEvent<?> selectEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemUnselect)) {
                Object unselectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(clientId + "_itemUnselect"));
                UnselectEvent<?> unselectEvent = new UnselectEvent<>(this, behaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(behaviorEvent.getPhaseId());
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
