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
package org.primefaces.component.message;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.CompositeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.StyleClassBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.ContextCallback;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Message.DEFAULT_RENDERER, componentFamily = Message.COMPONENT_FAMILY)
public class MessageRenderer extends UINotificationRenderer<Message> {

    @Override
    public void encodeEnd(FacesContext context, Message component) throws IOException {
        UIComponent target = SearchExpressionUtils.contextlessResolveComponent(context, component, component.getFor());
        final MessageState state = new MessageState();
        state.setTargetId(target.getClientId(context));
        state.setTooltipTargetId(getTooltipTargetId(target, context));

        if (!hasDisplayableMessage(component, target, context) && CompositeUtils.isComposite(target)) {
            ContextCallback callback = (FacesContext fc, UIComponent comp) -> {
                if (hasDisplayableMessage(component, comp, fc)) {
                    state.setTargetId(comp.getClientId(fc));
                    state.setTooltipTargetId(getTooltipTargetId(comp, fc));
                }
            };
            CompositeUtils.invokeOnDeepestEditableValueHolder(context, target, callback);
        }

        encodeMarkup(context, component, state.getTargetId());
        encodeScript(context, component, state.getTooltipTargetId());
    }

    protected void encodeMarkup(FacesContext context, Message component, String targetClientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String display = component.getDisplay();
        boolean iconOnly = "icon".equals(display);
        String style = component.getStyle();
        StyleClassBuilder styleClassBuilder = getStyleClassBuilder(context)
                .add("ui-message", component.getStyleClass())
                .add("tooltip".equals(display), "ui-helper-hidden");

        Iterator<FacesMessage> msgs = context.getMessages(targetClientId);

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("role", "alert", null);
        writer.writeAttribute(HTML.ARIA_ATOMIC, "true", null);
        writer.writeAttribute(HTML.ARIA_LIVE, "polite", null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-display", display, null);
            writer.writeAttribute("data-target", targetClientId, null);
            writer.writeAttribute("data-redisplay", String.valueOf(component.isRedisplay()), null);
        }

        boolean hasMessage = false;
        while (msgs.hasNext()) {
            FacesMessage msg = msgs.next();
            String severityName = getSeverityName(msg);

            if (shouldRender(component, msg, severityName)) {

                styleClassBuilder.add("ui-message-" + severityName + " ui-widget");
                styleClassBuilder.add(iconOnly, "ui-message-icon-only ui-helper-clearfix");

                writer.writeAttribute("class", styleClassBuilder.build(), null);

                writer.startElement("div", null);

                if (!"text".equals(display)) {
                    encodeIcon(writer, severityName, msg.getDetail(), iconOnly);
                }

                if (!iconOnly) {
                    String summary = msg.getSummary();
                    String detail = msg.getDetail();
                    if (component.isSkipDetailIfEqualsSummary() && Objects.equals(summary, detail)) {
                        detail = "";
                    }

                    if (component.isShowSummary()) {
                        encodeText(context, component, summary, severityName + "-summary");
                    }
                    if (component.isShowDetail()) {
                        encodeText(context, component, detail, severityName + "-detail");
                    }
                }
                writer.endElement("div");

                msg.rendered();

                hasMessage = true;
                break;
            }
        }

        if (!hasMessage) {
            writer.writeAttribute("class", styleClassBuilder.build(), null);
        }

        writer.endElement("div");
    }

    protected void encodeText(FacesContext context, Message component, String text, String severity) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-message-" + severity, null);
        writer.writeAttribute("id", component.getClientId(context) + '_' + severity, null);

        if (component.isEscape()) {
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

    protected void encodeScript(FacesContext context, Message component, String targetClientId) throws IOException {
        boolean tooltip = "tooltip".equals(component.getDisplay());
        if (tooltip || component.isShowDetail()) {
            WidgetBuilder wb = getWidgetBuilder(context);

            wb.init("Message", component)
                    .attr("target", targetClientId)
                    .attr("tooltip", tooltip, false)
                    .finish();
        }
    }
}
