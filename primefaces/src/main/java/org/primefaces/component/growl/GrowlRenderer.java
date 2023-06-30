/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.growl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class GrowlRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        Growl growl = (Growl) component;
        String clientId = growl.getClientId(context);
        String widgetVar = growl.resolveWidgetVar(context);

        writer.startElement("span", growl);
        writer.writeAttribute("id", clientId, "id");

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("class", "ui-growl-pl", null);
            writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);
            writer.writeAttribute("data-global", growl.isGlobalOnly(), null);
            writer.writeAttribute("data-summary", growl.isShowSummary(), null);
            writer.writeAttribute("data-detail", growl.isShowDetail(), null);
            writer.writeAttribute("data-severity", getClientSideSeverity(growl.getSeverity()), null);
            writer.writeAttribute("data-redisplay", String.valueOf(growl.isRedisplay()), null);
        }

        writer.endElement("span");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Growl", growl)
                .attr("sticky", growl.isSticky())
                .attr("life", growl.getLife())
                .attr("escape", growl.isEscape())
                .attr("keepAlive", growl.isKeepAlive());

        writer.write(",msgs:");
        encodeMessages(context, growl);

        wb.finish();
    }

    protected void encodeMessages(FacesContext context, Growl growl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean first = true;

        List<FacesMessage> messages = collectFacesMessages(growl, context);
        if (messages == null || messages.isEmpty()) {
            writer.write("[]");
            return;
        }

        writer.write("[");

        for (int i = 0; i < messages.size(); i++) {
            FacesMessage message = messages.get(i);
            String severityName = getSeverityName(message);

            if (shouldRender(growl, message, severityName)) {
                if (!first) {
                    writer.write(",");
                }
                else {
                    first = false;
                }

                String summary = EscapeUtils.forJavaScript(message.getSummary());
                String detail = EscapeUtils.forJavaScript(message.getDetail());

                writer.write("{");

                if (growl.isShowSummary() && growl.isShowDetail()) {
                    if (growl.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
                        detail = Constants.EMPTY_STRING;
                    }
                    writer.writeText("summary:\"" + summary + "\",detail:\"" + detail + "\"", null);
                }
                else if (growl.isShowSummary() && !growl.isShowDetail()) {
                    writer.writeText("summary:\"" + summary + "\",detail:\"\"", null);
                }
                else if (!growl.isShowSummary() && growl.isShowDetail()) {
                    writer.writeText("summary:\"\",detail:\"" + detail + "\"", null);
                }

                writer.write(",severity:'" + severityName + "'");
                writer.write(",severityText:'" + getSeverityText(message) + "'");

                writer.write("}");

                message.rendered();
            }
        }

        writer.write("]");
    }


    protected List<FacesMessage> collectFacesMessages(Growl growl, FacesContext context) {
        List<FacesMessage> messages = null;

        String _for = growl.getFor();
        if (!isValueBlank(_for)) {
            List<UIComponent> forComponents = SearchExpressionFacade.resolveComponents(context, growl, _for,
                    SearchExpressionUtils.SET_IGNORE_NO_RESULT);
            for (int i = 0; i < forComponents.size(); i++) {
                UIComponent forComponent = forComponents.get(i);
                String forComponentClientId = forComponent.getClientId(context);
                if (!_for.equals(forComponentClientId)) {

                    Iterator<FacesMessage> messagesIterator = context.getMessages(forComponentClientId);
                    while (messagesIterator.hasNext()) {
                        FacesMessage next = messagesIterator.next();
                        if (messages == null) {
                            messages = new ArrayList<>(5);
                        }
                        if (!messages.contains(next)) {
                            messages.add(next);
                        }
                    }
                }
            }
        }
        else if (growl.isGlobalOnly()) {
            Iterator<FacesMessage> messagesIterator = context.getMessages(null);
            while (messagesIterator.hasNext()) {
                if (messages == null) {
                    messages = new ArrayList<>(5);
                }
                messages.add(messagesIterator.next());
            }
        }
        else {
            Iterator<String> keyIterator = context.getClientIdsWithMessages();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Iterator<FacesMessage> messagesIterator = context.getMessages(key);
                while (messagesIterator.hasNext()) {
                    if (messages == null) {
                        messages = new ArrayList<>(5);
                    }
                    messages.add(messagesIterator.next());
                }
            }
        }

        return messages;
    }
}
