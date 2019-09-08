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
package org.primefaces.component.ring;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class RingRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Ring ring = (Ring) component;

        encodeMarkup(context, ring);
        encodeScript(context, ring);
    }

    public void encodeMarkup(FacesContext context, Ring ring) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = ring.getStyle();
        String styleClass = ring.getStyleClass();
        styleClass = styleClass == null ? Ring.STYLE_CLASS : Ring.STYLE_CLASS + " " + ring.getStyleClass();

        writer.startElement("ul", ring);
        writer.writeAttribute("id", ring.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        for (int rowIndex = 0; rowIndex < ring.getRowCount(); rowIndex++) {
            ring.setRowIndex(rowIndex);

            writer.startElement("li", ring);
            writer.writeAttribute("class", "ui-state-default ui-corner-all", null);

            for (UIComponent child : ring.getChildren()) {
                child.encodeAll(context);
            }

            writer.endElement("li");
        }

        ring.setRowIndex(-1);

        writer.endElement("ul");
    }

    public void encodeScript(FacesContext context, Ring ring) throws IOException {
        String clientId = ring.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Ring", ring.resolveWidgetVar(context), clientId)
                .attr("startingChild", ring.getFirst())
                .attr("easing", ring.getEasing(), null)
                .attr("autoplay", ring.isAutoplay(), false)
                .attr("autoplayDuration", ring.getAutoplayDuration(), 1000)
                .attr("autoplayPauseOnHover", ring.isAutoplayPauseOnHover(), false)
                .attr("autoplayInitialDelay", ring.getAutoplayInitialDelay(), 0);
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
