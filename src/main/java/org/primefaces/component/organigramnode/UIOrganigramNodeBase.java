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
package org.primefaces.component.organigramnode;

import javax.faces.component.UIComponentBase;


abstract class UIOrganigramNodeBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        type,
        style,
        styleClass,
        icon,
        iconPos,
        expandedIcon,
        collapsedIcon,
        skipLeafHandling
    }

    public UIOrganigramNodeBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
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

    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    public String getIconPos() {
        return (String) getStateHelper().eval(PropertyKeys.iconPos, null);
    }

    public void setIconPos(String iconPos) {
        getStateHelper().put(PropertyKeys.iconPos, iconPos);
    }

    public String getExpandedIcon() {
        return (String) getStateHelper().eval(PropertyKeys.expandedIcon, null);
    }

    public void setExpandedIcon(String expandedIcon) {
        getStateHelper().put(PropertyKeys.expandedIcon, expandedIcon);
    }

    public String getCollapsedIcon() {
        return (String) getStateHelper().eval(PropertyKeys.collapsedIcon, null);
    }

    public void setCollapsedIcon(String collapsedIcon) {
        getStateHelper().put(PropertyKeys.collapsedIcon, collapsedIcon);
    }

    public boolean isSkipLeafHandling() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipLeafHandling, false);
    }

    public void setSkipLeafHandling(boolean skipLeafHandling) {
        getStateHelper().put(PropertyKeys.skipLeafHandling, skipLeafHandling);
    }

}