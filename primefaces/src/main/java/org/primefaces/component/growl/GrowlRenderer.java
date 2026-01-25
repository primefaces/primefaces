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
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Growl.DEFAULT_RENDERER, componentFamily = Growl.COMPONENT_FAMILY)
public class GrowlRenderer extends UINotificationRenderer<Growl> {

    @Override
    public void encodeEnd(FacesContext context, Growl component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, "id");

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("class", "ui-growl-pl", null);
            writer.writeAttribute("data-global", component.isGlobalOnly(), null);
            writer.writeAttribute("data-summary", component.isShowSummary(), null);
            writer.writeAttribute("data-detail", component.isShowDetail(), null);
            writer.writeAttribute("data-severity", getClientSideSeverity(component.getSeverity()), null);
            writer.writeAttribute("data-redisplay", String.valueOf(component.isRedisplay()), null);
        }

        writer.endElement("span");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Growl", component)
                .attr("sticky", component.isSticky())
                .attr("life", component.getLife())
                .attr("escape", component.isEscape())
                .attr("keepAlive", component.isKeepAlive());

        writer.write(",msgs:");
        encodeMessages(context, component);

        wb.finish();
    }

    protected void encodeMessages(FacesContext context, Growl component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean first = true;
        List<FacesMessage> messages = collectFacesMessages(component, context);

        writer.write("[");

        if (messages != null && !messages.isEmpty()) {
            for (int i = 0; i < messages.size(); i++) {
                FacesMessage message = messages.get(i);
                String severityName = getSeverityName(message);

                if (shouldRender(component, message, severityName)) {
                    if (!first) {
                        writer.write(",");
                    }
                    else {
                        first = false;
                    }

                    String summary = EscapeUtils.forJavaScript(message.getSummary());
                    String detail = EscapeUtils.forJavaScript(message.getDetail());

                    writer.write("{");

                    if (component.isShowSummary() && component.isShowDetail()) {
                        if (component.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
                            detail = Constants.EMPTY_STRING;
                        }
                        writer.writeText("summary:\"" + summary + "\",detail:\"" + detail + "\"", null);
                    }
                    else if (component.isShowSummary() && !component.isShowDetail()) {
                        writer.writeText("summary:\"" + summary + "\",detail:\"\"", null);
                    }
                    else if (!component.isShowSummary() && component.isShowDetail()) {
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
