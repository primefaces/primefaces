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
package org.primefaces.component.spotlight;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Spotlight.DEFAULT_RENDERER, componentFamily = Spotlight.COMPONENT_FAMILY)
public class SpotlightRenderer extends CoreRenderer<Spotlight> {

    @Override
    public void encodeEnd(FacesContext context, Spotlight component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    private void encodeMarkup(FacesContext context, Spotlight component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.endElement("span");
    }

    private void encodeScript(FacesContext context, Spotlight component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Spotlight", component)
            .attr("target", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getTarget()))
            .attr("active", component.isActive(), false)
            .attr("blockScroll", component.isBlockScroll(), false);

        wb.finish();
    }
}
