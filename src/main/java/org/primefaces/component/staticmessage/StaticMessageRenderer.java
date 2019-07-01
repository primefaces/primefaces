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
package org.primefaces.component.staticmessage;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.UINotificationRenderer;

public class StaticMessageRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        StaticMessage staticMessage = (StaticMessage) component;

        ResponseWriter writer = context.getResponseWriter();

        boolean escape = staticMessage.isEscape();
        String summary = staticMessage.getSummary();
        String detail = staticMessage.getDetail();
        String severity = staticMessage.getSeverity();
        severity = severity == null ? "info" : severity.toLowerCase();

        String styleClass = "ui-message ui-staticmessage ui-message-" + severity + " ui-widget ui-corner-all";
        String style = staticMessage.getStyle();

        writer.startElement("div", staticMessage);
        writer.writeAttribute("id", staticMessage.getClientId(context), null);
        writer.writeAttribute("aria-live", "polite", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeIcon(writer, severity, null, false);
        encodeText(writer, summary, severity + "-summary", escape);
        encodeText(writer, detail, severity + "-detail", escape);

        writer.endElement("div");
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
}
