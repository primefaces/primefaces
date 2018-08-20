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
package org.primefaces.component.tabview;

import javax.faces.component.UIPanel;


abstract class TabBase extends UIPanel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public enum PropertyKeys {

        title,
        titleStyle,
        titleStyleClass,
        disabled,
        closable,
        titletip,
        ariaLabel
    }

    public TabBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    public String getTitleStyle() {
        return (String) getStateHelper().eval(PropertyKeys.titleStyle, null);
    }

    public void setTitleStyle(String titleStyle) {
        getStateHelper().put(PropertyKeys.titleStyle, titleStyle);
    }

    public String getTitleStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.titleStyleClass, null);
    }

    public void setTitleStyleClass(String titleStyleClass) {
        getStateHelper().put(PropertyKeys.titleStyleClass, titleStyleClass);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
    }

    public String getTitletip() {
        return (String) getStateHelper().eval(PropertyKeys.titletip, null);
    }

    public void setTitletip(String titletip) {
        getStateHelper().put(PropertyKeys.titletip, titletip);
    }

    public String getAriaLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaLabel, null);
    }

    public void setAriaLabel(String ariaLabel) {
        getStateHelper().put(PropertyKeys.ariaLabel, ariaLabel);
    }

}