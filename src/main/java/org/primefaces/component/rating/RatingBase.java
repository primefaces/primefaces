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
package org.primefaces.component.rating;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class RatingBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RatingRenderer";

    public enum PropertyKeys {

        widgetVar,
        stars,
        disabled,
        readonly,
        onRate,
        style,
        styleClass,
        cancel
    }

    public RatingBase() {
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

    public int getStars() {
        return (Integer) getStateHelper().eval(PropertyKeys.stars, 5);
    }

    public void setStars(int stars) {
        getStateHelper().put(PropertyKeys.stars, stars);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public boolean isReadonly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.readonly, false);
    }

    public void setReadonly(boolean readonly) {
        getStateHelper().put(PropertyKeys.readonly, readonly);
    }

    public String getOnRate() {
        return (String) getStateHelper().eval(PropertyKeys.onRate, null);
    }

    public void setOnRate(String onRate) {
        getStateHelper().put(PropertyKeys.onRate, onRate);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isCancel() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cancel, true);
    }

    public void setCancel(boolean cancel) {
        getStateHelper().put(PropertyKeys.cancel, cancel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}