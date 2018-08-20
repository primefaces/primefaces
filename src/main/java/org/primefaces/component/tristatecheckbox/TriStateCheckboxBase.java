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
package org.primefaces.component.tristatecheckbox;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TriStateCheckboxBase extends HtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TriStateCheckboxRenderer";

    public enum PropertyKeys {

        widgetVar,
        stateOneIcon,
        stateTwoIcon,
        stateThreeIcon,
        itemLabel,
        stateOneTitle,
        stateTwoTitle,
        stateThreeTitle;
    }

    public TriStateCheckboxBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getStateOneIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateOneIcon, null);
    }

    public void setStateOneIcon(String stateOneIcon) {
        getStateHelper().put(PropertyKeys.stateOneIcon, stateOneIcon);
    }

    public String getStateTwoIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateTwoIcon, null);
    }

    public void setStateTwoIcon(String stateTwoIcon) {
        getStateHelper().put(PropertyKeys.stateTwoIcon, stateTwoIcon);
    }

    public String getStateThreeIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateThreeIcon, null);
    }

    public void setStateThreeIcon(String stateThreeIcon) {
        getStateHelper().put(PropertyKeys.stateThreeIcon, stateThreeIcon);
    }

    public String getItemLabel() {
        return (String) getStateHelper().eval(PropertyKeys.itemLabel, null);
    }

    public void setItemLabel(String itemLabel) {
        getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
    }

    public String getStateOneTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateOneTitle, null);
    }

    public void setStateOneTitle(String stateOneTitle) {
        getStateHelper().put(PropertyKeys.stateOneTitle, stateOneTitle);
    }

    public String getStateTwoTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateTwoTitle, null);
    }

    public void setStateTwoTitle(String stateTwoTitle) {
        getStateHelper().put(PropertyKeys.stateTwoTitle, stateTwoTitle);
    }

    public String getStateThreeTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateThreeTitle, null);
    }

    public void setStateThreeTitle(String stateThreeTitle) {
        getStateHelper().put(PropertyKeys.stateThreeTitle, stateThreeTitle);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}