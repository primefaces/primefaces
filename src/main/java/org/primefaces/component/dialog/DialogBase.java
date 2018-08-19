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
package org.primefaces.component.dialog;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class DialogBase extends UIPanel implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DialogRenderer";

    public enum PropertyKeys {

        widgetVar,
        header,
        draggable,
        resizable,
        modal,
        visible,
        width,
        height,
        minWidth,
        minHeight,
        style,
        styleClass,
        showEffect,
        hideEffect,
        position,
        closable,
        onShow,
        onHide,
        appendTo,
        showHeader,
        footer,
        dynamic,
        minimizable,
        maximizable,
        closeOnEscape,
        dir,
        focus,
        fitViewport,
        positionType,
        responsive
    }

    public DialogBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getHeader() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(java.lang.String _header) {
        getStateHelper().put(PropertyKeys.header, _header);
    }

    public boolean isDraggable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
    }

    public void setDraggable(boolean _draggable) {
        getStateHelper().put(PropertyKeys.draggable, _draggable);
    }

    public boolean isResizable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
    }

    public void setResizable(boolean _resizable) {
        getStateHelper().put(PropertyKeys.resizable, _resizable);
    }

    public boolean isModal() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.modal, false);
    }

    public void setModal(boolean _modal) {
        getStateHelper().put(PropertyKeys.modal, _modal);
    }

    public boolean isVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean _visible) {
        getStateHelper().put(PropertyKeys.visible, _visible);
    }

    public java.lang.String getWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(java.lang.String _width) {
        getStateHelper().put(PropertyKeys.width, _width);
    }

    public java.lang.String getHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(java.lang.String _height) {
        getStateHelper().put(PropertyKeys.height, _height);
    }

    public int getMinWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minWidth, java.lang.Integer.MIN_VALUE);
    }

    public void setMinWidth(int _minWidth) {
        getStateHelper().put(PropertyKeys.minWidth, _minWidth);
    }

    public int getMinHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minHeight, java.lang.Integer.MIN_VALUE);
    }

    public void setMinHeight(int _minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, _minHeight);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getShowEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showEffect, null);
    }

    public void setShowEffect(java.lang.String _showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, _showEffect);
    }

    public java.lang.String getHideEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.hideEffect, null);
    }

    public void setHideEffect(java.lang.String _hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, _hideEffect);
    }

    public java.lang.String getPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.position, null);
    }

    public void setPosition(java.lang.String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public boolean isClosable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closable, true);
    }

    public void setClosable(boolean _closable) {
        getStateHelper().put(PropertyKeys.closable, _closable);
    }

    public java.lang.String getOnShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(java.lang.String _onShow) {
        getStateHelper().put(PropertyKeys.onShow, _onShow);
    }

    public java.lang.String getOnHide() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(java.lang.String _onHide) {
        getStateHelper().put(PropertyKeys.onHide, _onHide);
    }

    public java.lang.String getAppendTo() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(java.lang.String _appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, _appendTo);
    }

    public boolean isShowHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
    }

    public void setShowHeader(boolean _showHeader) {
        getStateHelper().put(PropertyKeys.showHeader, _showHeader);
    }

    public java.lang.String getFooter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(java.lang.String _footer) {
        getStateHelper().put(PropertyKeys.footer, _footer);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    public boolean isMinimizable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.minimizable, false);
    }

    public void setMinimizable(boolean _minimizable) {
        getStateHelper().put(PropertyKeys.minimizable, _minimizable);
    }

    public boolean isMaximizable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.maximizable, false);
    }

    public void setMaximizable(boolean _maximizable) {
        getStateHelper().put(PropertyKeys.maximizable, _maximizable);
    }

    public boolean isCloseOnEscape() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closeOnEscape, false);
    }

    public void setCloseOnEscape(boolean _closeOnEscape) {
        getStateHelper().put(PropertyKeys.closeOnEscape, _closeOnEscape);
    }

    public java.lang.String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(java.lang.String _dir) {
        getStateHelper().put(PropertyKeys.dir, _dir);
    }

    public java.lang.String getFocus() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.focus, null);
    }

    public void setFocus(java.lang.String _focus) {
        getStateHelper().put(PropertyKeys.focus, _focus);
    }

    public boolean isFitViewport() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.fitViewport, false);
    }

    public void setFitViewport(boolean _fitViewport) {
        getStateHelper().put(PropertyKeys.fitViewport, _fitViewport);
    }

    public java.lang.String getPositionType() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.positionType, "fixed");
    }

    public void setPositionType(java.lang.String _positionType) {
        getStateHelper().put(PropertyKeys.positionType, _positionType);
    }

    public boolean isResponsive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean _responsive) {
        getStateHelper().put(PropertyKeys.responsive, _responsive);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }

    @Override
    public boolean isRTL() {
        return "rtl".equalsIgnoreCase(getDir());
    }
}