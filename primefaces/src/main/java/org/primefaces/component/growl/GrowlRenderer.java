/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

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

        writer.write("[");

        if (messages != null && !messages.isEmpty()) {
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

                    writer.write("}");

                    message.rendered();
                }
            }
        }

        writer.write("]");
    }
}
