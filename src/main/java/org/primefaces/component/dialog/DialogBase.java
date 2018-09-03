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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getHeader() {
        return (String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        getStateHelper().put(PropertyKeys.header, header);
    }

    public boolean isDraggable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
    }

    public void setDraggable(boolean draggable) {
        getStateHelper().put(PropertyKeys.draggable, draggable);
    }

    public boolean isResizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
    }

    public void setResizable(boolean resizable) {
        getStateHelper().put(PropertyKeys.resizable, resizable);
    }

    public boolean isModal() {
        return (Boolean) getStateHelper().eval(PropertyKeys.modal, false);
    }

    public void setModal(boolean modal) {
        getStateHelper().put(PropertyKeys.modal, modal);
    }

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public int getMinWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.minWidth, Integer.MIN_VALUE);
    }

    public void setMinWidth(int minWidth) {
        getStateHelper().put(PropertyKeys.minWidth, minWidth);
    }

    public int getMinHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.minHeight, Integer.MIN_VALUE);
    }

    public void setMinHeight(int minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, minHeight);
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

    public String getShowEffect() {
        return (String) getStateHelper().eval(PropertyKeys.showEffect, null);
    }

    public void setShowEffect(String showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, showEffect);
    }

    public String getHideEffect() {
        return (String) getStateHelper().eval(PropertyKeys.hideEffect, null);
    }

    public void setHideEffect(String hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, hideEffect);
    }

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, null);
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, true);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
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

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public boolean isShowHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
    }

    public void setShowHeader(boolean showHeader) {
        getStateHelper().put(PropertyKeys.showHeader, showHeader);
    }

    public String getFooter() {
        return (String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(String footer) {
        getStateHelper().put(PropertyKeys.footer, footer);
    }

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    public boolean isMinimizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.minimizable, false);
    }

    public void setMinimizable(boolean minimizable) {
        getStateHelper().put(PropertyKeys.minimizable, minimizable);
    }

    public boolean isMaximizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.maximizable, false);
    }

    public void setMaximizable(boolean maximizable) {
        getStateHelper().put(PropertyKeys.maximizable, maximizable);
    }

    public boolean isCloseOnEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closeOnEscape, false);
    }

    public void setCloseOnEscape(boolean closeOnEscape) {
        getStateHelper().put(PropertyKeys.closeOnEscape, closeOnEscape);
    }

    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public String getFocus() {
        return (String) getStateHelper().eval(PropertyKeys.focus, null);
    }

    public void setFocus(String focus) {
        getStateHelper().put(PropertyKeys.focus, focus);
    }

    public boolean isFitViewport() {
        return (Boolean) getStateHelper().eval(PropertyKeys.fitViewport, false);
    }

    public void setFitViewport(boolean fitViewport) {
        getStateHelper().put(PropertyKeys.fitViewport, fitViewport);
    }

    public String getPositionType() {
        return (String) getStateHelper().eval(PropertyKeys.positionType, "fixed");
    }

    public void setPositionType(String positionType) {
        getStateHelper().put(PropertyKeys.positionType, positionType);
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

    @Override
    public boolean isRTL() {
        return "rtl".equalsIgnoreCase(getDir());
    }
}