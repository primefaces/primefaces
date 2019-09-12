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
package org.primefaces.component.accordionpanel;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.Widget;

public abstract class AccordionPanelBase extends UITabPanel implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AccordionPanelRenderer";

    public enum PropertyKeys {

        widgetVar,
        activeIndex,
        style,
        styleClass,
        onTabChange,
        onTabShow,
        onTabClose,
        cache,
        multiple,
        dir,
        tabindex,
        tabController
    }

    public AccordionPanelBase() {
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

    public String getActiveIndex() {
        return (String) getStateHelper().eval(PropertyKeys.activeIndex, "0");
    }

    public void setActiveIndex(String activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, activeIndex);
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

    public String getOnTabChange() {
        return (String) getStateHelper().eval(PropertyKeys.onTabChange, null);
    }

    public void setOnTabChange(String onTabChange) {
        getStateHelper().put(PropertyKeys.onTabChange, onTabChange);
    }

    public String getOnTabShow() {
        return (String) getStateHelper().eval(PropertyKeys.onTabShow, null);
    }

    public void setOnTabShow(String onTabShow) {
        getStateHelper().put(PropertyKeys.onTabShow, onTabShow);
    }

    public String getOnTabClose() {
        return (String) getStateHelper().eval(PropertyKeys.onTabClose, null);
    }

    public void setOnTabClose(String onTabClose) {
        getStateHelper().put(PropertyKeys.onTabClose, onTabClose);
    }

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

    public boolean isMultiple() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean multiple) {
        getStateHelper().put(PropertyKeys.multiple, multiple);
    }

    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public javax.el.MethodExpression getTabController() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.tabController, null);
    }

    public void setTabController(javax.el.MethodExpression tabController) {
        getStateHelper().put(PropertyKeys.tabController, tabController);
    }
}