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
package org.primefaces.component.sidebar;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Sidebar.DEFAULT_RENDERER, componentFamily = Sidebar.COMPONENT_FAMILY)
public class SidebarRenderer extends CoreRenderer<Sidebar> {

    @Override
    public void decode(FacesContext context, Sidebar component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Sidebar component) throws IOException {
        if (component.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, Sidebar component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? Sidebar.STYLE_CLASS : Sidebar.STYLE_CLASS + " " + styleClass;
        styleClass = component.isFullScreen() ? styleClass + " " + Sidebar.FULL_BAR_CLASS : styleClass;
        styleClass += " ui-sidebar-" + component.getPosition();

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (component.isShowCloseIcon()) {
            encodeCloseIcon(context);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Sidebar.CONTENT_CLASS, null);
        writer.writeAttribute("id", component.getClientId(context) + "_content", null);

        if (!component.isDynamic()) {
            renderChildren(context, component);
        }

        writer.endElement("div");
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

    private void encodeScript(FacesContext context, Sidebar component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Sidebar", component)
                .attr("visible", component.isVisible(), false)
                .attr("modal", component.isModal(), true)
                .attr("blockScroll", component.isBlockScroll(), false)
                .attr("baseZIndex", component.getBaseZIndex(), 0)
                .attr("dynamic", component.isDynamic(), false)
                .attr("showCloseIcon", component.isShowCloseIcon(), true)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .callback("onHide", "function()", component.getOnHide())
                .callback("onShow", "function()", component.getOnShow());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, Sidebar component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
