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
package org.primefaces.component.message;

import javax.faces.component.UIMessage;

import org.primefaces.component.api.UINotification;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class MessageBase extends UIMessage implements UINotification, Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MessageRenderer";

    public enum PropertyKeys {

        display,
        escape,
        severity,
        style,
        styleClass
    }

    public MessageBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getDisplay() {
        return (String) getStateHelper().eval(PropertyKeys.display, "both");
    }

    public void setDisplay(String display) {
        getStateHelper().put(PropertyKeys.display, display);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, null);
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}