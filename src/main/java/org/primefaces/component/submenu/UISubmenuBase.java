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
package org.primefaces.component.submenu;

import javax.faces.component.UIComponentBase;

import org.primefaces.model.menu.Submenu;


abstract class UISubmenuBase extends UIComponentBase implements Submenu {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        label,
        icon,
        style,
        styleClass,
        expanded
    }

    public UISubmenuBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    @Override
    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    @Override
    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    @Override
    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public boolean isExpanded() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expanded, false);
    }

    public void setExpanded(boolean expanded) {
        getStateHelper().put(PropertyKeys.expanded, expanded);
    }

}