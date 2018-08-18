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
package org.primefaces.component.linkbutton;

import javax.faces.component.html.HtmlOutcomeTargetLink;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class LinkButtonBase extends HtmlOutcomeTargetLink implements UIOutcomeTarget, Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LinkButtonRenderer";

    public enum PropertyKeys {

        widgetVar,
        fragment,
        disableClientWindow,
        href,
        escape
    }

    public LinkButtonBase() {
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
    public boolean isDisableClientWindow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableClientWindow, false);
    }

    @Override
    public void setDisableClientWindow(boolean _disableClientWindow) {
        getStateHelper().put(PropertyKeys.disableClientWindow, _disableClientWindow);
    }

    @Override
    public java.lang.String getHref() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.href, null);
    }

    public void setHref(java.lang.String _href) {
        getStateHelper().put(PropertyKeys.href, _href);
    }

    public boolean isEscape() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean _escape) {
        getStateHelper().put(PropertyKeys.escape, _escape);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}