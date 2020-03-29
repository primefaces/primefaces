/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.terminal;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;

public abstract class TerminalBase extends UIPanel implements Widget {

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
}