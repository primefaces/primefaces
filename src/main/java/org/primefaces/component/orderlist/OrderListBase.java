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
package org.primefaces.component.orderlist;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class OrderListBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OrderListRenderer";

    public enum PropertyKeys {

        widgetVar,
        var,
        itemLabel,
        itemValue,
        style,
        styleClass,
        disabled,
        effect,
        moveUpLabel,
        moveTopLabel,
        moveDownLabel,
        moveBottomLabel,
        controlsLocation,
        responsive
    }

    public OrderListBase() {
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

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public String getItemLabel() {
        return (String) getStateHelper().eval(PropertyKeys.itemLabel, null);
    }

    public void setItemLabel(String itemLabel) {
        getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
    }

    public Object getItemValue() {
        return getStateHelper().eval(PropertyKeys.itemValue, null);
    }

    public void setItemValue(Object itemValue) {
        getStateHelper().put(PropertyKeys.itemValue, itemValue);
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

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getMoveUpLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveUpLabel, "Move Up");
    }

    public void setMoveUpLabel(String moveUpLabel) {
        getStateHelper().put(PropertyKeys.moveUpLabel, moveUpLabel);
    }

    public String getMoveTopLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveTopLabel, "Move Top");
    }

    public void setMoveTopLabel(String moveTopLabel) {
        getStateHelper().put(PropertyKeys.moveTopLabel, moveTopLabel);
    }

    public String getMoveDownLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveDownLabel, "Move Down");
    }

    public void setMoveDownLabel(String moveDownLabel) {
        getStateHelper().put(PropertyKeys.moveDownLabel, moveDownLabel);
    }

    public String getMoveBottomLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveBottomLabel, "Move Bottom");
    }

    public void setMoveBottomLabel(String moveBottomLabel) {
        getStateHelper().put(PropertyKeys.moveBottomLabel, moveBottomLabel);
    }

    public String getControlsLocation() {
        return (String) getStateHelper().eval(PropertyKeys.controlsLocation, "left");
    }

    public void setControlsLocation(String controlsLocation) {
        getStateHelper().put(PropertyKeys.controlsLocation, controlsLocation);
    }

    public boolean isResponsive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean responsive) {
        getStateHelper().put(PropertyKeys.responsive, responsive);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}