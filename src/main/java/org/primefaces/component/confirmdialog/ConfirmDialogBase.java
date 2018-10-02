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
package org.primefaces.component.confirmdialog;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ConfirmDialogBase extends UIPanel implements Widget, RTLAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ConfirmDialogRenderer";

    public enum PropertyKeys {

        widgetVar,
        message,
        header,
        severity,
        width,
        height,
        style,
        styleClass,
        closable,
        appendTo,
        visible,
        showEffect,
        hideEffect,
        closeOnEscape,
        dir,
        global,
        responsive
    }

    public ConfirmDialogBase() {
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

    public String getMessage() {
        return (String) getStateHelper().eval(PropertyKeys.message, null);
    }

    public void setMessage(String message) {
        getStateHelper().put(PropertyKeys.message, message);
    }

    public String getHeader() {
        return (String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        getStateHelper().put(PropertyKeys.header, header);
    }

    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, "alert");
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
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

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, true);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
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

    public boolean isGlobal() {
        return (Boolean) getStateHelper().eval(PropertyKeys.global, false);
    }

    public void setGlobal(boolean global) {
        getStateHelper().put(PropertyKeys.global, global);
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