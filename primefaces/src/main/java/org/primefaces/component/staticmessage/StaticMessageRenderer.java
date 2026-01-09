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
package org.primefaces.component.staticmessage;

import org.primefaces.component.messages.Messages;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = StaticMessage.DEFAULT_RENDERER, componentFamily = StaticMessage.COMPONENT_FAMILY)
public class StaticMessageRenderer extends CoreRenderer<StaticMessage> {

    @Override
    public void decode(FacesContext context, StaticMessage component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, StaticMessage component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String display = component.getDisplay();
        boolean iconOnly = "icon".equals(display);
        boolean escape = component.isEscape();
        String summary = component.getSummary();
        String detail = component.getDetail();
        String severity = component.getSeverity();
        severity = severity == null ? "info" : severity.toLowerCase();

        String styleClass = getStyleClassBuilder(context)
                .add("ui-message ui-staticmessage ui-message-" + severity + " ui-widget")
                .add(iconOnly, "ui-message-icon-only ui-helper-clearfix")
                .add(component.getStyleClass())
                .build();

        String style = component.getStyle();

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("aria-live", "polite", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (component.isClosable()) {
            encodeCloseIcon(context, component);
        }

        if (!"text".equals(display)) {
            encodeIcon(writer, severity, detail, iconOnly);
        }
        if (!iconOnly) {
            encodeText(writer, summary, severity + "-summary", escape);
            encodeText(writer, detail, severity + "-detail", escape);
        }

        writer.endElement("div");

        encodeScript(context, component);
    }

    protected void encodeText(ResponseWriter writer, String text, String severity, boolean escape) throws IOException {
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-message-" + severity, null);

        if (text != null) {
            if (escape) {
                writer.writeText(text, null);
            }
            else {
                writer.write(text);
            }
        }

        writer.endElement("span");
    }

    protected void encodeIcon(ResponseWriter writer, String severity, String title, boolean iconOnly) throws IOException {
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-message-" + severity + "-icon", null);
        if (iconOnly && title != null) {
            writer.writeAttribute("title", title, null);
        }
        writer.endElement("span");
    }

    protected void encodeCloseIcon(FacesContext context, StaticMessage component) throws IOException {
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

    protected void encodeScript(FacesContext context, StaticMessage component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("StaticMessage", component);

        encodeClientBehaviors(context, component);

        wb.finish();
    }
}
