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
package org.primefaces.component.notificationbar;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = NotificationBar.DEFAULT_RENDERER, componentFamily = NotificationBar.COMPONENT_FAMILY)
public class NotificationBarRenderer extends CoreRenderer<NotificationBar> {

    @Override
    public void encodeEnd(FacesContext context, NotificationBar component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, NotificationBar component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? NotificationBar.STYLE_CLASS : NotificationBar.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        renderChildren(context, component);

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, NotificationBar component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("NotificationBar", component)
                .attr("position", component.getPosition())
                .attr("effect", component.getEffect())
                .attr("effectSpeed", component.getEffectSpeed())
                .attr("autoDisplay", component.isAutoDisplay(), false);
        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, NotificationBar component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
