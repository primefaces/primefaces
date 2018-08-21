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
package org.primefaces.component.button;

import javax.faces.component.html.HtmlOutcomeTargetButton;

import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ButtonBase extends HtmlOutcomeTargetButton implements Widget, UIOutcomeTarget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ButtonRenderer";

    public enum PropertyKeys {

        widgetVar,
        fragment,
        disabled,
        icon,
        iconPos,
        href,
        target,
        escape,
        inline,
        disableClientWindow
    }

    public ButtonBase() {
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

    @Override
    public String getFragment() {
        return (String) getStateHelper().eval(PropertyKeys.fragment, null);
    }

    public void setFragment(String fragment) {
        getStateHelper().put(PropertyKeys.fragment, fragment);
    }

    @Override
    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    @Override
    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    public String getIconPos() {
        return (String) getStateHelper().eval(PropertyKeys.iconPos, "left");
    }

    public void setIconPos(String iconPos) {
        getStateHelper().put(PropertyKeys.iconPos, iconPos);
    }

    @Override
    public String getHref() {
        return (String) getStateHelper().eval(PropertyKeys.href, null);
    }

    public void setHref(String href) {
        getStateHelper().put(PropertyKeys.href, href);
    }

    public String getTarget() {
        return (String) getStateHelper().eval(PropertyKeys.target, "_self");
    }

    public void setTarget(String target) {
        getStateHelper().put(PropertyKeys.target, target);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    public boolean isInline() {
        return (Boolean) getStateHelper().eval(PropertyKeys.inline, false);
    }

    public void setInline(boolean inline) {
        getStateHelper().put(PropertyKeys.inline, inline);
    }

    @Override
    public boolean isDisableClientWindow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableClientWindow, false);
    }

    @Override
    public void setDisableClientWindow(boolean disableClientWindow) {
        getStateHelper().put(PropertyKeys.disableClientWindow, disableClientWindow);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}