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
package org.primefaces.component.messages;

import javax.faces.component.UIMessages;

import org.primefaces.component.api.UINotification;


abstract class MessagesBase extends UIMessages implements UINotification {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MessagesRenderer";

    public enum PropertyKeys {

        escape,
        severity,
        closable,
        style,
        styleClass,
        showIcon,
        forType
    }

    public MessagesBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
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

    public boolean isShowIcon() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showIcon, true);
    }

    public void setShowIcon(boolean showIcon) {
        getStateHelper().put(PropertyKeys.showIcon, showIcon);
    }

    public String getForType() {
        return (String) getStateHelper().eval(PropertyKeys.forType, null);
    }

    public void setForType(String forType) {
        getStateHelper().put(PropertyKeys.forType, forType);
    }

}