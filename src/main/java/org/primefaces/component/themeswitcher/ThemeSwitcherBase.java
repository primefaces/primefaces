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
package org.primefaces.component.themeswitcher;

import org.primefaces.component.api.Widget;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.util.ComponentUtils;


abstract class ThemeSwitcherBase extends SelectOneMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ThemeSwitcherRenderer";

    public enum PropertyKeys {

        widgetVar,
        buttonPreText;
    }

    public ThemeSwitcherBase() {
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

    public String getButtonPreText() {
        return (String) getStateHelper().eval(PropertyKeys.buttonPreText, null);
    }

    public void setButtonPreText(String buttonPreText) {
        getStateHelper().put(PropertyKeys.buttonPreText, buttonPreText);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}