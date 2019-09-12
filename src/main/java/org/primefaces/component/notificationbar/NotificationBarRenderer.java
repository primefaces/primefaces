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
package org.primefaces.component.notificationbar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class NotificationBarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        NotificationBar bar = (NotificationBar) component;

        encodeMarkup(context, bar);
        encodeScript(context, bar);
    }

    protected void encodeMarkup(FacesContext context, NotificationBar bar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = bar.getStyleClass();
        styleClass = styleClass == null ? NotificationBar.STYLE_CLASS : NotificationBar.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", bar);
        writer.writeAttribute("id", bar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (bar.getStyle() != null) {
            writer.writeAttribute("style", bar.getStyle(), null);
        }

        renderChildren(context, bar);

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, NotificationBar bar) throws IOException {
        String clientId = bar.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("NotificationBar", bar.resolveWidgetVar(context), clientId)
                .attr("position", bar.getPosition())
                .attr("effect", bar.getEffect())
                .attr("effectSpeed", bar.getEffectSpeed())
                .attr("autoDisplay", bar.isAutoDisplay(), false);
        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
