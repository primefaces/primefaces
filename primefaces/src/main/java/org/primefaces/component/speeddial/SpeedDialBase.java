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
package org.primefaces.component.speeddial;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

public abstract class SpeedDialBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SpeedDialRenderer";

    public enum PropertyKeys {
        widgetVar,
        model,
        disabled,
        visible,
        direction,
        transitionDelay,
        type,
        radius,
        mask,
        hideOnClickOutside,
        style,
        styleClass,
        buttonStyleClass,
        buttonStyle,
        maskStyleClass,
        maskStyle,
        showIcon,
        hideIcon,
        rotateAnimation,
        onVisibleChange,
        onClick,
        onShow,
        onHide,
        keepOpen,
        badge
    }

    public SpeedDialBase() {
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

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
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

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public String getDirection() {
        return (String) getStateHelper().eval(PropertyKeys.direction, "up");
    }

    public void setDirection(String direction) {
        getStateHelper().put(PropertyKeys.direction, direction);
    }

    public int getTransitionDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.transitionDelay, 30);
    }

    public void setTransitionDelay(int transitionDelay) {
        getStateHelper().put(PropertyKeys.transitionDelay, transitionDelay);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, "linear");
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public int getRadius() {
        return (Integer) getStateHelper().eval(PropertyKeys.radius, 0);
    }

    public void setRadius(int radius) {
        getStateHelper().put(PropertyKeys.radius, radius);
    }

    public boolean isMask() {
        return (Boolean) getStateHelper().eval(PropertyKeys.mask, false);
    }

    public void setMask(boolean mask) {
        getStateHelper().put(PropertyKeys.mask, mask);
    }

    public boolean isHideOnClickOutside() {
        return (Boolean) getStateHelper().eval(PropertyKeys.hideOnClickOutside, true);
    }

    public void setHideOnClickOutside(boolean hideOnClickOutside) {
        getStateHelper().put(PropertyKeys.hideOnClickOutside, hideOnClickOutside);
    }

    public String getButtonStyle() {
        return (String) getStateHelper().eval(PropertyKeys.buttonStyle, null);
    }

    public void setButtonStyle(String buttonStyle) {
        getStateHelper().put(PropertyKeys.buttonStyle, buttonStyle);
    }

    public String getButtonStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.buttonStyleClass, null);
    }

    public void setButtonStyleClass(String buttonStyleClass) {
        getStateHelper().put(PropertyKeys.buttonStyleClass, buttonStyleClass);
    }

    public String getMaskStyle() {
        return (String) getStateHelper().eval(PropertyKeys.maskStyle, null);
    }

    public void setMaskStyle(String maskStyle) {
        getStateHelper().put(PropertyKeys.maskStyle, maskStyle);
    }

    public String getMaskStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.maskStyleClass, null);
    }

    public void setMaskStyleClass(String maskStyleClass) {
        getStateHelper().put(PropertyKeys.maskStyleClass, maskStyleClass);
    }

    public String getShowIcon() {
        return (String) getStateHelper().eval(PropertyKeys.showIcon, "pi pi-plus");
    }

    public void setShowIcon(String showIcon) {
        getStateHelper().put(PropertyKeys.showIcon, showIcon);
    }

    public String getHideIcon() {
        return (String) getStateHelper().eval(PropertyKeys.hideIcon, null);
    }

    public void setHideIcon(String hideIcon) {
        getStateHelper().put(PropertyKeys.hideIcon, hideIcon);
    }

    public boolean isRotateAnimation() {
        return (Boolean) getStateHelper().eval(PropertyKeys.rotateAnimation, true);
    }

    public void setRotateAnimation(boolean rotateAnimation) {
        getStateHelper().put(PropertyKeys.rotateAnimation, rotateAnimation);
    }

    public String getOnVisibleChange() {
        return (String) getStateHelper().eval(PropertyKeys.onVisibleChange, null);
    }

    public void setOnVisibleChange(String onVisibleChange) {
        getStateHelper().put(PropertyKeys.onVisibleChange, onVisibleChange);
    }

    public String getOnClick() {
        return (String) getStateHelper().eval(PropertyKeys.onClick, null);
    }

    public void setOnClick(String onClick) {
        getStateHelper().put(PropertyKeys.onClick, onClick);
    }

    public String getOnShow() {
        return (String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(String onShow) {
        getStateHelper().put(PropertyKeys.onShow, onShow);
    }

    public String getOnHide() {
        return (String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(String onHide) {
        getStateHelper().put(PropertyKeys.onHide, onHide);
    }

    public boolean isKeepOpen() {
        return (Boolean) getStateHelper().eval(PropertyKeys.keepOpen, false);
    }

    public void setKeepOpen(boolean keepOpen) {
        getStateHelper().put(PropertyKeys.keepOpen, keepOpen);
    }

    public Object getBadge() {
        return getStateHelper().eval(PropertyKeys.badge, null);
    }

    public void setBadge(Object badge) {
        getStateHelper().put(PropertyKeys.badge, badge);
    }
}
