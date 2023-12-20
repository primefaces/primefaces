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
package org.primefaces.component.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.InputHolder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class MessageRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Message uiMessage = (Message) component;

        UIComponent target = SearchExpressionUtils.contextlessResolveComponent(context, uiMessage, uiMessage.getFor());
        String targetClientId = target.getClientId(context);

        encodeMarkup(context, uiMessage, targetClientId);
        encodeScript(context, uiMessage, target);
    }

    protected void encodeMarkup(FacesContext context, Message uiMessage, String targetClientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String display = uiMessage.getDisplay();
        boolean iconOnly = "icon".equals(display);
        String style = uiMessage.getStyle();
        String containerClass = "tooltip".equals(display) ? "ui-message ui-helper-hidden" : "ui-message";
        String styleClass = uiMessage.getStyleClass();
        styleClass = styleClass == null ? containerClass : styleClass + " " + containerClass;

        Iterator<FacesMessage> msgs = context.getMessages(targetClientId);

        writer.startElement("div", uiMessage);
        writer.writeAttribute("id", uiMessage.getClientId(context), null);
        writer.writeAttribute("role", "alert", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);
        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-display", display, null);
            writer.writeAttribute("data-target", targetClientId, null);
            writer.writeAttribute("data-redisplay", String.valueOf(uiMessage.isRedisplay()), null);
        }

        boolean hasMessage = false;
        while (msgs.hasNext()) {
            FacesMessage msg = msgs.next();
            String severityName = getSeverityName(msg);

            if (shouldRender(uiMessage, msg, severityName)) {

                styleClass += " ui-message-" + severityName + " ui-widget ui-corner-all";

                if (iconOnly) {
                    styleClass += " ui-message-icon-only ui-helper-clearfix";
                }

                writer.writeAttribute("class", styleClass, null);

                writer.startElement("div", null);

                if (!"text".equals(display)) {
                    encodeIcon(writer, severityName, msg.getDetail(), iconOnly);
                }

                if (!iconOnly) {
                    String summary = msg.getSummary();
                    String detail = msg.getDetail();
                    if (uiMessage.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
                        detail = "";
                    }

                    if (uiMessage.isShowSummary()) {
                        encodeText(context, uiMessage, summary, severityName + "-summary");
                    }
                    if (uiMessage.isShowDetail()) {
                        encodeText(context, uiMessage, detail, severityName + "-detail");
                    }
                }
                writer.endElement("div");

                msg.rendered();

                hasMessage = true;
                break;
            }
        }

        if (!hasMessage) {
            writer.writeAttribute("class", styleClass, null);
        }

        writer.endElement("div");
    }

    protected void encodeText(FacesContext context, Message uiMessage, String text, String severity) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-message-" + severity, null);
        writer.writeAttribute("id", uiMessage.getClientId(context) + '_' + severity, null);

        if (uiMessage.isEscape()) {
            writer.writeText(text, null);
        }
        else {
            writer.write(text);
        }

        writer.endElement("span");
    }

    protected void encodeIcon(ResponseWriter writer, String severity, String title, boolean iconOnly) throws IOException {
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-message-" + severity + "-icon", null);
        if (iconOnly) {
            writer.writeAttribute("title", title, null);
        }
        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, Message uiMessage, UIComponent target) throws IOException {
        boolean tooltip = "tooltip".equals(uiMessage.getDisplay());
        if (tooltip || uiMessage.isShowDetail()) {
            String targetClientId = (target instanceof InputHolder) ? ((InputHolder) target).getInputClientId() : target.getClientId(context);
            WidgetBuilder wb = getWidgetBuilder(context);

            wb.init("Message", uiMessage)
                    .attr("target", targetClientId)
                    .attr("tooltip", tooltip, false)
                    .finish();
        }
    }
}
