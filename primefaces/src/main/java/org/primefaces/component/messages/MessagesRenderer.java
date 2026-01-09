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
package org.primefaces.component.messages;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Messages.DEFAULT_RENDERER, componentFamily = Messages.COMPONENT_FAMILY)
public class MessagesRenderer extends UINotificationRenderer<Messages> {

    @Override
    public void encodeEnd(FacesContext context, Messages component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Messages component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean globalOnly = component.isGlobalOnly();
        String containerClass = component.isShowIcon() ? Messages.CONTAINER_CLASS : Messages.ICONLESS_CONTAINER_CLASS;
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? containerClass : containerClass + " " + styleClass;

        Map<String, List<FacesMessage>> messagesBySeverity = null;
        List<FacesMessage> messages = collectFacesMessages(component, context);
        if (messages != null && !messages.isEmpty()) {
            messagesBySeverity = new HashMap<>(4);

            for (int i = 0; i < messages.size(); i++) {
                FacesMessage message = messages.get(i);
                FacesMessage.Severity severity = message.getSeverity();

                if (severity.equals(FacesMessage.SEVERITY_INFO)) {
                    addMessage(component, message, messagesBySeverity, "info");
                }
                else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
                    addMessage(component, message, messagesBySeverity, "warn");
                }
                else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
                    addMessage(component, message, messagesBySeverity, "error");
                }
                else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
                    addMessage(component, message, messagesBySeverity, "fatal");
                }
            }
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-global", String.valueOf(globalOnly), null);
            writer.writeAttribute("data-summary", component.isShowSummary(), null);
            writer.writeAttribute("data-detail", component.isShowDetail(), null);
            writer.writeAttribute("data-severity", getClientSideSeverity(component.getSeverity()), null);
            writer.writeAttribute("data-redisplay", String.valueOf(component.isRedisplay()), null);
        }

        if (messagesBySeverity != null) {
            for (Map.Entry<String, List<FacesMessage>> entry : messagesBySeverity.entrySet()) {
                encodeMessages(context, component, entry.getKey(), entry.getValue());
            }
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Messages component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Messages", component)
                .attr("closable", component.isClosable())
                .finish();
    }

    protected void addMessage(Messages component, FacesMessage message, Map<String, List<FacesMessage>> messagesBySeverity, String severity) {
        if (shouldRender(component, message, severity)) {
            List<FacesMessage> severityMessages = messagesBySeverity.computeIfAbsent(severity, k -> new ArrayList<>());
            severityMessages.add(message);
        }
    }

    protected void encodeMessages(FacesContext context, Messages component, String severity, List<FacesMessage> messages) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClassPrefix = Messages.SEVERITY_PREFIX_CLASS + severity;
        boolean escape = component.isEscape();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClassPrefix, null);

        if (component.isClosable()) {
            encodeCloseIcon(context, component);
        }

        if (component.isShowIcon()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", styleClassPrefix + "-icon", null);
            writer.endElement("span");
        }

        writer.startElement("ul", null);

        for (int i = 0; i < messages.size(); i++) {
            FacesMessage message = messages.get(i);
            encodeMessage(writer, component, message, styleClassPrefix, escape);
            message.rendered();
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeMessage(ResponseWriter writer, Messages component, FacesMessage message, String styleClassPrefix, boolean escape)
            throws IOException {

        writer.startElement("li", null);

        writer.writeAttribute("role", "alert", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);

        String summary = message.getSummary() != null ? message.getSummary() : "";
        String detail = message.getDetail() != null ? message.getDetail() : summary;
        if (component.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
            detail = "";
        }

        if (component.isShowSummary()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", styleClassPrefix + "-summary", null);

            if (escape) {
                writer.writeText(summary, null);
            }
            else {
                writer.write(summary);
            }

            writer.endElement("span");
        }

        if (component.isShowDetail()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", styleClassPrefix + "-detail", null);

            if (escape) {
                writer.writeText(detail, null);
            }
            else {
                writer.write(detail);
            }

            writer.endElement("span");
        }

        writer.endElement("li");
    }

    protected void encodeCloseIcon(FacesContext context, Messages component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Messages.CLOSE_LINK_CLASS, null);
        writer.writeAttribute("onclick", "$(this).parent().slideUp();return false;", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", Messages.CLOSE_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");
    }
}
