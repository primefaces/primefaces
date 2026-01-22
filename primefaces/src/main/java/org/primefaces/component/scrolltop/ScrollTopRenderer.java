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
package org.primefaces.component.scrolltop;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ScrollTop.DEFAULT_RENDERER, componentFamily = ScrollTop.COMPONENT_FAMILY)
public class ScrollTopRenderer extends CoreRenderer<ScrollTop> {
    @Override
    public void encodeEnd(FacesContext context, ScrollTop component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, ScrollTop component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                    .add(ScrollTop.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add("parent".equals(component.getTarget()), ScrollTop.STICKY_CLASS)
                    .build();
        String style = component.getStyle() != null ? "display: none;" + component.getStyle() : "display: none;";

        writer.startElement("a", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("tabindex", "0", "tabindex");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("style", style, "style");

        writer.startElement("span", component);
        writer.writeAttribute("class", ScrollTop.ICON_CLASS + " " + component.getIcon(), null);
        writer.endElement("span");
        writer.endElement("a");
    }

    protected void encodeScript(FacesContext context, ScrollTop component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ScrollTop", component)
                .attr("target", component.getTarget())
                .attr("threshold", component.getThreshold())
                .attr("behavior", component.getBehavior());
        wb.finish();
    }
}
