/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.model.terminal.TerminalAutoCompleteMatches;
import org.primefaces.model.terminal.TerminalAutoCompleteModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Arrays;

import jakarta.el.MethodExpression;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Terminal.DEFAULT_RENDERER, componentFamily = Terminal.COMPONENT_FAMILY)
public class TerminalRenderer extends CoreRenderer<Terminal> {

    @Override
    public void decode(FacesContext context, Terminal component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Terminal component) throws IOException {
        if (component.isCommandRequest()) {
            handleCommand(context, component);
        }
        else if (component.isAutoCompleteRequest()) {
            autoCompleteCommand(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, Terminal component) throws IOException {
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? Terminal.CONTAINER_CLASS : Terminal.CONTAINER_CLASS + " " + styleClass;
        String welcomeMessage = component.getWelcomeMessage();
        String prompt = component.getPrompt();
        String inputId = clientId + "_input";

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if (welcomeMessage != null) {
            writer.startElement("div", null);
            if (component.isEscape()) {
                writer.writeText(welcomeMessage, null);
            }
            else {
                writer.write(welcomeMessage);
            }
            writer.endElement("div");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Terminal.CONTENT_CLASS, null);
        writer.endElement("div");

        writer.startElement("div", null);
        writer.startElement("span", null);
        writer.writeAttribute("class", Terminal.PROMPT_CLASS, null);
        if (component.isEscape()) {
            writer.writeText(prompt, null);
        }
        else {
            writer.write(prompt);
        }
        writer.endElement("span");

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("class", Terminal.INPUT_CLASS, null);
        writer.endElement("input");

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Terminal component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Terminal", component);
        encodeClientBehaviors(context, component);
        wb.finish();
    }

    protected void handleCommand(FacesContext context, Terminal component) throws IOException {
        String[] tokens = getValueTokens(context, component);
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        MethodExpression commandHandler = component.getCommandHandler();
        String result = (String) commandHandler.invoke(context.getELContext(), new Object[]{command, args});

        ResponseWriter writer = context.getResponseWriter();
        writer.writeText(result, null);
    }

    protected void autoCompleteCommand(FacesContext context, Terminal component) throws IOException {
        String[] tokens = getValueTokens(context, component);
        String command = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        TerminalAutoCompleteModel autoCompleteModel = component.getAutoCompleteModel();
        ResponseWriter writer = context.getResponseWriter();
        if (autoCompleteModel == null) {
            writer.write("null");
        }
        else {
            TerminalAutoCompleteMatches matches = component.traverseAutoCompleteModel(autoCompleteModel, command, args);
            writer.writeText(matches.toString(), null);
        }
    }

    private String[] getValueTokens(FacesContext context, Terminal component) {
        String clientId = component.getClientId(context);
        String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        String[] tokens = value.trim().split(" ");

        return tokens;
    }

}

