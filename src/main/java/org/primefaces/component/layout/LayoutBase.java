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
package org.primefaces.component.layout;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class LayoutBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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
}