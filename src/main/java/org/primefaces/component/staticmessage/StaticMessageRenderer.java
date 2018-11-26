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
        String severity = staticMessage.getSeverity().toLowerCase();
        String summary = staticMessage.getSummary();
        String detail = staticMessage.getDetail();

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
