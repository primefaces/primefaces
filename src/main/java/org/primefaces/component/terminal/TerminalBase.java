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
        escape
    }

    public TerminalBase() {
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

    public String getWelcomeMessage() {
        return (String) getStateHelper().eval(PropertyKeys.welcomeMessage, null);
    }

    public void setWelcomeMessage(String welcomeMessage) {
        getStateHelper().put(PropertyKeys.welcomeMessage, welcomeMessage);
    }

    public String getPrompt() {
        return (String) getStateHelper().eval(PropertyKeys.prompt, "prime $");
    }

    public void setPrompt(String prompt) {
        getStateHelper().put(PropertyKeys.prompt, prompt);
    }

    public javax.el.MethodExpression getCommandHandler() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.commandHandler, null);
    }

    public void setCommandHandler(javax.el.MethodExpression commandHandler) {
        getStateHelper().put(PropertyKeys.commandHandler, commandHandler);
    }

    public org.primefaces.model.terminal.TerminalAutoCompleteModel getAutoCompleteModel() {
        return (org.primefaces.model.terminal.TerminalAutoCompleteModel) getStateHelper().eval(PropertyKeys.autoCompleteModel, null);
    }

    public void setAutoCompleteModel(org.primefaces.model.terminal.TerminalAutoCompleteModel autoCompleteModel) {
        getStateHelper().put(PropertyKeys.autoCompleteModel, autoCompleteModel);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}