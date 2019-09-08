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
package org.primefaces.component.sidebar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class SidebarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Sidebar bar = (Sidebar) component;

        encodeMarkup(context, bar);
        encodeScript(context, bar);
    }

    protected void encodeMarkup(FacesContext context, Sidebar bar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = bar.getStyle();
        String styleClass = bar.getStyleClass();
        styleClass = styleClass == null ? Sidebar.STYLE_CLASS : Sidebar.STYLE_CLASS + " " + styleClass;
        styleClass = bar.isFullScreen() ? styleClass + " " + Sidebar.FULL_BAR_CLASS : styleClass;
        styleClass += " ui-sidebar-" + bar.getPosition();

        writer.startElement("div", bar);
        writer.writeAttribute("id", bar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeCloseIcon(context);

        renderChildren(context, bar);

        writer.endElement("div");
    }

    protected void encodeCloseIcon(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Sidebar.TITLE_BAR_CLOSE_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", Sidebar.CLOSE_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    private void encodeScript(FacesContext context, Sidebar bar) throws IOException {
        String clientId = bar.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Sidebar", bar.resolveWidgetVar(context), clientId)
                .attr("visible", bar.isVisible(), false)
                .attr("blockScroll", bar.isBlockScroll(), false)
                .attr("baseZIndex", bar.getBaseZIndex(), 0)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, bar, bar.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                .callback("onHide", "function()", bar.getOnHide())
                .callback("onShow", "function()", bar.getOnShow());

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
