/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.orderlist;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class OrderListBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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
}