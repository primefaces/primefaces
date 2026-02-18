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
package org.primefaces.component.chips;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Chips.COMPONENT_TYPE, namespace = Chips.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Chips is used to enter multiple values on an inputfield.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Chips extends ChipsBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Chips";
    public static final String STYLE_CLASS = "ui-chips ui-widget";
    public static final String CONTAINER_CLASS = "ui-chips-container ui-inputfield ui-state-default";
    public static final String TOKEN_DISPLAY_CLASS = "ui-chips-token ui-state-active";
    public static final String TOKEN_LABEL_CLASS = "ui-chips-token-label";
    public static final String TOKEN_CLOSE_ICON_CLASS = "ui-chips-token-icon ui-icon ui-icon-close";
    public static final String TOKEN_INPUT_CLASS = "ui-chips-input-token";

    private static final List<PrimeClientBehaviorEventKeys> UNOBTRUSIVE_EVENT_KEYS = LangUtils.unmodifiableList(
            ClientBehaviorEventKeys.itemSelect, ClientBehaviorEventKeys.itemUnselect);

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
    }

    @Override
    public Collection<PrimeClientBehaviorEventKeys> getUnobtrusiveClientBehaviorEventKeys() {
        return UNOBTRUSIVE_EVENT_KEYS;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
                Object selectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemSelect"));
                SelectEvent<?> selectEvent = new SelectEvent<>(this, ajaxBehaviorEvent.getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemUnselect)) {
                Object unselectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemUnselect"));
                UnselectEvent<?> unselectEvent = new UnselectEvent<>(this, ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(unselectEvent);
            }
            else {
                //e.g. blur, focus, change
                super.queueEvent(event);
            }
        }
        else {
            //e.g. valueChange
            super.queueEvent(event);
        }
    }
}