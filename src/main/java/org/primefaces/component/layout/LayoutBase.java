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
package org.primefaces.component.layout;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class LayoutBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LayoutRenderer";

    public enum PropertyKeys {

        widgetVar,
        fullPage,
        style,
        styleClass,
        onResize,
        onClose,
        onToggle,
        resizeTitle,
        collapseTitle,
        expandTitle,
        closeTitle,
        stateful
    }

    public LayoutBase() {
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

    public boolean isFullPage() {
        return (Boolean) getStateHelper().eval(PropertyKeys.fullPage, false);
    }

    public void setFullPage(boolean fullPage) {
        getStateHelper().put(PropertyKeys.fullPage, fullPage);
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

    public String getOnResize() {
        return (String) getStateHelper().eval(PropertyKeys.onResize, null);
    }

    public void setOnResize(String onResize) {
        getStateHelper().put(PropertyKeys.onResize, onResize);
    }

    public String getOnClose() {
        return (String) getStateHelper().eval(PropertyKeys.onClose, null);
    }

    public void setOnClose(String onClose) {
        getStateHelper().put(PropertyKeys.onClose, onClose);
    }

    public String getOnToggle() {
        return (String) getStateHelper().eval(PropertyKeys.onToggle, null);
    }

    public void setOnToggle(String onToggle) {
        getStateHelper().put(PropertyKeys.onToggle, onToggle);
    }

    public String getResizeTitle() {
        return (String) getStateHelper().eval(PropertyKeys.resizeTitle, null);
    }

    public void setResizeTitle(String resizeTitle) {
        getStateHelper().put(PropertyKeys.resizeTitle, resizeTitle);
    }

    public String getCollapseTitle() {
        return (String) getStateHelper().eval(PropertyKeys.collapseTitle, "Collapse");
    }

    public void setCollapseTitle(String collapseTitle) {
        getStateHelper().put(PropertyKeys.collapseTitle, collapseTitle);
    }

    public String getExpandTitle() {
        return (String) getStateHelper().eval(PropertyKeys.expandTitle, null);
    }

    public void setExpandTitle(String expandTitle) {
        getStateHelper().put(PropertyKeys.expandTitle, expandTitle);
    }

    public String getCloseTitle() {
        return (String) getStateHelper().eval(PropertyKeys.closeTitle, "Close");
    }

    public void setCloseTitle(String closeTitle) {
        getStateHelper().put(PropertyKeys.closeTitle, closeTitle);
    }

    public boolean isStateful() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stateful, false);
    }

    public void setStateful(boolean stateful) {
        getStateHelper().put(PropertyKeys.stateful, stateful);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}