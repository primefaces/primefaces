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

import org.primefaces.util.ComponentUtils;


abstract class ButtonBase extends HtmlOutcomeTargetButton implements org.primefaces.component.api.Widget, org.primefaces.component.api.UIOutcomeTarget {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    @Override
    public java.lang.String getFragment() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.fragment, null);
    }

    public void setFragment(java.lang.String _fragment) {
        getStateHelper().put(PropertyKeys.fragment, _fragment);
    }

    @Override
    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    @Override
    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public java.lang.String getIcon() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(java.lang.String _icon) {
        getStateHelper().put(PropertyKeys.icon, _icon);
    }

    public java.lang.String getIconPos() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.iconPos, "left");
    }

    public void setIconPos(java.lang.String _iconPos) {
        getStateHelper().put(PropertyKeys.iconPos, _iconPos);
    }

    @Override
    public java.lang.String getHref() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.href, null);
    }

    public void setHref(java.lang.String _href) {
        getStateHelper().put(PropertyKeys.href, _href);
    }

    public java.lang.String getTarget() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.target, "_self");
    }

    public void setTarget(java.lang.String _target) {
        getStateHelper().put(PropertyKeys.target, _target);
    }

    public boolean isEscape() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean _escape) {
        getStateHelper().put(PropertyKeys.escape, _escape);
    }

    public boolean isInline() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.inline, false);
    }

    public void setInline(boolean _inline) {
        getStateHelper().put(PropertyKeys.inline, _inline);
    }

    @Override
    public boolean isDisableClientWindow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableClientWindow, false);
    }

    @Override
    public void setDisableClientWindow(boolean _disableClientWindow) {
        getStateHelper().put(PropertyKeys.disableClientWindow, _disableClientWindow);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}