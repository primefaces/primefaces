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

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.InputHolder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class MessageRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Message uiMessage = (Message) component;

        UIComponent target = SearchExpressionFacade.resolveComponent(context, uiMessage, uiMessage.getFor());
        String targetClientId = target.getClientId(context);

        encodeMarkup(context, uiMessage, targetClientId);
        encodeScript(context, uiMessage, target);
    }

    protected void encodeMarkup(FacesContext context, Message uiMessage, String targetClientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String display = uiMessage.getDisplay();
        boolean iconOnly = display.equals("icon");
        String style = uiMessage.getStyle();
        String containerClass = display.equals("tooltip") ? "ui-message ui-helper-hidden" : "ui-message";
        String styleClass = uiMessage.getStyleClass();
        styleClass = styleClass == null ? containerClass : styleClass + " " + containerClass;

        Iterator<FacesMessage> msgs = context.getMessages(targetClientId);

        writer.startElement("div", uiMessage);
        writer.writeAttribute("id", uiMessage.getClientId(context), null);
        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-display", display, null);
            writer.writeAttribute("data-target", targetClientId, null);
            writer.writeAttribute("data-redisplay", String.valueOf(uiMessage.isRedisplay()), null);
        }

        if (msgs.hasNext()) {
            FacesMessage msg = msgs.next();
            String severityName = getSeverityName(msg);

            if (!shouldRender(uiMessage, msg, severityName)) {
                writer.writeAttribute("class", styleClass, null);
                writer.endElement("div");

                return;
            }
            else {
                Severity severity = msg.getSeverity();
                String severityKey = null;

                if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
                    severityKey = "error";
                }
                else if (severity.equals(FacesMessage.SEVERITY_INFO)) {
                    severityKey = "info";
                }
                else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
                    severityKey = "warn";
                }
                else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
                    severityKey = "fatal";
                }

                styleClass += " ui-message-" + severityKey + " ui-widget ui-corner-all";

                if (iconOnly) {
                    styleClass += " ui-message-icon-only ui-helper-clearfix";
                }

                writer.writeAttribute("class", styleClass, null);
                writer.writeAttribute("role", "alert", null);
                writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);

                if (!display.equals("text")) {
                    encodeIcon(writer, severityKey, msg.getDetail(), iconOnly);
                }

                if (!iconOnly) {
                    if (uiMessage.isShowSummary()) {
                        encodeText(context, uiMessage, msg.getSummary(), severityKey + "-summary");
                    }
                    if (uiMessage.isShowDetail()) {
                        encodeText(context, uiMessage, msg.getDetail(), severityKey + "-detail");
                    }
                }

                msg.rendered();
            }
        }
        else {
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
            String clientId = uiMessage.getClientId(context);
            String targetClientId = (target instanceof InputHolder) ? ((InputHolder) target).getInputClientId() : target.getClientId(context);
            WidgetBuilder wb = getWidgetBuilder(context);

            wb.init("Message", uiMessage.resolveWidgetVar(), clientId)
                    .attr("target", targetClientId)
                    .attr("tooltip", tooltip, false)
                    .finish();
        }
    }
}
