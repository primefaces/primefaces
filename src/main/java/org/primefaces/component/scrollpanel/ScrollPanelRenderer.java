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
package org.primefaces.component.scrollpanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ScrollPanelRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ScrollPanel panel = (ScrollPanel) component;

        encodeMarkup(context, panel);
        encodeScript(context, panel);
    }

    protected void encodeMarkup(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        boolean nativeMode = panel.getMode().equals("native");

        String defaultStyleClass = nativeMode ? ScrollPanel.SCROLL_PANEL_NATIVE_CLASS : ScrollPanel.SCROLL_PANEL_CLASS;
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        renderChildren(context, panel);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ScrollPanel panel) throws IOException {
        boolean nativeMode = panel.getMode().equals("native");
        if (!nativeMode) {
            String clientId = panel.getClientId(context);
            WidgetBuilder wb = getWidgetBuilder(context);
            wb.init("ScrollPanel", panel.resolveWidgetVar(context), clientId).finish();
        }
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
