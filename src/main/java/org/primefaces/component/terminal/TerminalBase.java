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
package org.primefaces.component.terminal;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TerminalBase extends UIPanel implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TerminalRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        welcomeMessage,
        prompt,
        commandHandler,
        autoCompleteModel,
        escape;
    }

    public TerminalBase() {
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

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getWelcomeMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.welcomeMessage, null);
    }

    public void setWelcomeMessage(java.lang.String _welcomeMessage) {
        getStateHelper().put(PropertyKeys.welcomeMessage, _welcomeMessage);
    }

    public java.lang.String getPrompt() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.prompt, "prime $");
    }

    public void setPrompt(java.lang.String _prompt) {
        getStateHelper().put(PropertyKeys.prompt, _prompt);
    }

    public javax.el.MethodExpression getCommandHandler() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.commandHandler, null);
    }

    public void setCommandHandler(javax.el.MethodExpression _commandHandler) {
        getStateHelper().put(PropertyKeys.commandHandler, _commandHandler);
    }

    public org.primefaces.model.terminal.TerminalAutoCompleteModel getAutoCompleteModel() {
        return (org.primefaces.model.terminal.TerminalAutoCompleteModel) getStateHelper().eval(PropertyKeys.autoCompleteModel, null);
    }

    public void setAutoCompleteModel(org.primefaces.model.terminal.TerminalAutoCompleteModel _autoCompleteModel) {
        getStateHelper().put(PropertyKeys.autoCompleteModel, _autoCompleteModel);
    }

    public boolean isEscape() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean _escape) {
        getStateHelper().put(PropertyKeys.escape, _escape);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}