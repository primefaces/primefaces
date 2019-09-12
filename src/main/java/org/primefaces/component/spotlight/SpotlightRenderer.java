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
package org.primefaces.component.spotlight;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class SpotlightRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Spotlight spotlight = (Spotlight) component;

        encodeMarkup(context, spotlight);
        encodeScript(context, spotlight);
    }

    private void encodeMarkup(FacesContext context, Spotlight spotlight) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", spotlight);
        writer.writeAttribute("id", spotlight.getClientId(context), null);
        writer.endElement("span");
    }

    private void encodeScript(FacesContext context, Spotlight spotlight) throws IOException {
        String clientId = spotlight.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Spotlight", spotlight.resolveWidgetVar(context), clientId)
                .attr("target", SearchExpressionFacade.resolveClientIds(context, spotlight, spotlight.getTarget(), SearchExpressionHint.RESOLVE_CLIENT_SIDE))
                .attr("active", spotlight.isActive(), false)
                .attr("blockScroll", spotlight.isBlockScroll(), false);

        wb.finish();
    }
}
