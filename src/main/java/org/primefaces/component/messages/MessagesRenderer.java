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
package org.primefaces.component.messages;

import java.io.IOException;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

public class MessagesRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Messages uiMessages = (Messages) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = uiMessages.getClientId(context);
        boolean globalOnly = uiMessages.isGlobalOnly();
        String containerClass = uiMessages.isShowIcon() ? Messages.CONTAINER_CLASS : Messages.ICONLESS_CONTAINER_CLASS;
        String style = uiMessages.getStyle();
        String styleClass = uiMessages.getStyleClass();
        styleClass = (styleClass == null) ? containerClass : containerClass + " " + styleClass;

        Map<String, List<FacesMessage>> messagesBySeverity = null;
        List<FacesMessage> messages = collectFacesMessages(uiMessages, context);
        if (messages != null && !messages.isEmpty()) {
            messagesBySeverity = new HashMap<>(4);

            for (int i = 0; i < messages.size(); i++) {
                FacesMessage message = messages.get(i);
                FacesMessage.Severity severity = message.getSeverity();

                if (severity.equals(FacesMessage.SEVERITY_INFO)) {
                    addMessage(uiMessages, message, messagesBySeverity, "info");
                }
                else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
                    addMessage(uiMessages, message, messagesBySeverity, "warn");
                }
                else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
                    addMessage(uiMessages, message, messagesBySeverity, "error");
                }
                else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
                    addMessage(uiMessages, message, messagesBySeverity, "fatal");
                }
            }
        }

        writer.startElement("div", uiMessages);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-global", String.valueOf(globalOnly), null);
            writer.writeAttribute("data-summary", uiMessages.isShowSummary(), null);
            writer.writeAttribute("data-detail", uiMessages.isShowDetail(), null);
            writer.writeAttribute("data-severity", getClientSideSeverity(uiMessages.getSeverity()), null);
            writer.writeAttribute("data-redisplay", String.valueOf(uiMessages.isRedisplay()), null);
        }

        if (messagesBySeverity != null) {
            for (Map.Entry<String, List<FacesMessage>> entry : messagesBySeverity.entrySet()) {
                encodeMessages(context, uiMessages, entry.getKey(), entry.getValue());
            }
        }

        writer.endElement("div");
    }

    protected void addMessage(Messages uiMessages, FacesMessage message, Map<String, List<FacesMessage>> messagesBySeverity, String severity) {
        if (shouldRender(uiMessages, message, severity)) {
            List<FacesMessage> severityMessages = messagesBySeverity.get(severity);

            if (severityMessages == null) {
                severityMessages = new ArrayList<>();
                messagesBySeverity.put(severity, severityMessages);
            }

            severityMessages.add(message);
        }
    }

    protected void encodeMessages(FacesContext context, Messages uiMessages, String severity, List<FacesMessage> messages) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClassPrefix = Messages.SEVERITY_PREFIX_CLASS + severity;
        boolean escape = uiMessages.isEscape();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClassPrefix + " ui-corner-all", null);

        if (uiMessages.isClosable()) {
            encodeCloseIcon(context, uiMessages);
        }

        if (uiMessages.isShowIcon()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", styleClassPrefix + "-icon", null);
            writer.endElement("span");
        }

        writer.startElement("ul", null);

        for (int i = 0; i < messages.size(); i++) {
            FacesMessage message = messages.get(i);
            encodeMessage(writer, uiMessages, message, styleClassPrefix, escape);
            message.rendered();
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeMessage(ResponseWriter writer, Messages uiMessages, FacesMessage message, String styleClassPrefix, boolean escape)
            throws IOException {

        writer.startElement("li", null);

        writer.writeAttribute("role", "alert", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);

        String summary = message.getSummary() != null ? message.getSummary() : "";
        String detail = message.getDetail() != null ? message.getDetail() : summary;
        if (uiMessages.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
            detail = "";
        }

        if (uiMessages.isShowSummary()) {
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

        if (uiMessages.isShowDetail()) {
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

    protected void encodeCloseIcon(FacesContext context, Messages uiMessages) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Messages.CLOSE_LINK_CLASS, null);
        writer.writeAttribute("onclick", "$(this).parent().slideUp();return false;", null);
        writer.writeAttribute(HTML.ARIA_LABEL, MessageFactory.getMessage(Messages.ARIA_CLOSE, null), null);

        writer.startElement("span", null);
        writer.writeAttribute("class", Messages.CLOSE_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    protected List<FacesMessage> collectFacesMessages(Messages uiMessages, FacesContext context) {
        List<FacesMessage> messages = null;

        String _for = uiMessages.getFor();
        if (!isValueBlank(_for)) {
            String forType = uiMessages.getForType();

            // key case
            if (forType == null || forType.equals("key")) {
                Iterator<FacesMessage> messagesIterator = context.getMessages(_for);
                while (messagesIterator.hasNext()) {
                    if (messages == null) {
                        messages = new ArrayList<>();
                    }
                    messages.add(messagesIterator.next());
                }
            }

            // clientId / SearchExpression case
            if (forType == null || forType.equals("expression")) {
                UIComponent forComponent = SearchExpressionFacade.resolveComponent(context, uiMessages, _for, SearchExpressionHint.IGNORE_NO_RESULT);
                if (forComponent != null) {

                    String forComponentClientId = forComponent.getClientId(context);
                    if (!_for.equals(forComponentClientId)) {

                        Iterator<FacesMessage> messagesIterator = context.getMessages(forComponentClientId);
                        while (messagesIterator.hasNext()) {
                            FacesMessage next = messagesIterator.next();
                            if (messages == null) {
                                messages = new ArrayList<>();
                            }
                            if (!messages.contains(next)) {
                                messages.add(next);
                            }
                        }
                    }
                }
            }
        }
        else if (uiMessages.isGlobalOnly()) {
            Iterator<FacesMessage> messagesIterator = context.getMessages(null);
            while (messagesIterator.hasNext()) {
                if (messages == null) {
                    messages = new ArrayList<>();
                }
                messages.add(messagesIterator.next());
            }
        }
        else {
            String[] ignores = uiMessages.getForIgnores() == null
                    ? null
                    : SearchExpressionFacade.split(context, uiMessages.getForIgnores(), SearchExpressionFacade.EXPRESSION_SEPARATORS);
            Iterator<String> keyIterator = context.getClientIdsWithMessages();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                if (ignores == null || !LangUtils.contains(ignores, key)) {
                    Iterator<FacesMessage> messagesIterator = context.getMessages(key);
                    while (messagesIterator.hasNext()) {
                        if (messages == null) {
                            messages = new ArrayList<>();
                        }
                        messages.add(messagesIterator.next());
                    }
                }
            }
        }

        return messages;
    }
}
