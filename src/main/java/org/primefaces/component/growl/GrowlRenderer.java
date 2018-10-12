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
package org.primefaces.component.growl;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class GrowlRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Growl growl = (Growl) component;
        String clientId = growl.getClientId(context);
        String widgetVar = growl.resolveWidgetVar();

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
        wb.init("Growl", growl.resolveWidgetVar(), clientId)
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
        String _for = growl.getFor();
        boolean first = true;
        Iterator<FacesMessage> messages;
        if (_for != null) {
            messages = context.getMessages(_for);
        }
        else {
            messages = growl.isGlobalOnly() ? context.getMessages(null) : context.getMessages();
        }

        writer.write("[");

        while (messages.hasNext()) {
            FacesMessage message = messages.next();
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
                    writer.writeText("summary:\"" + summary + "\",detail:\"" + detail + "\"", null);
                }
                else if (growl.isShowSummary() && !growl.isShowDetail()) {
                    writer.writeText("summary:\"" + summary + "\",detail:\"\"", null);
                }
                else if (!growl.isShowSummary() && growl.isShowDetail()) {
                    writer.writeText("summary:\"\",detail:\"" + detail + "\"", null);
                }

                writer.write(",severity:'" + severityName + "'");

                writer.write("}");

                message.rendered();
            }
        }

        writer.write("]");
    }
}
