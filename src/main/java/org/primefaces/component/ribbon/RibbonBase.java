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
package org.primefaces.component.ribbon;

import org.primefaces.component.tabview.TabView;


abstract class RibbonBase extends TabView {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RibbonRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        activeIndex
    }

    public RibbonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    @Override
    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    @Override
    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    @Override
    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    @Override
    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    @Override
    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public int getActiveIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
    }

    @Override
    public void setActiveIndex(int activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, activeIndex);
    }

}