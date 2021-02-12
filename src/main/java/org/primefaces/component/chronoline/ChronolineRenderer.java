/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.chronoline;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ChronolineRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Chronoline chronoline = (Chronoline) component;

        ResponseWriter writer = context.getResponseWriter();
        String clientId = chronoline.getClientId();
        String style = chronoline.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Chronoline.STYLE_CLASS)
                .add(chronoline.getStyleClass())
                .add("left".equals(chronoline.getAlign()), Chronoline.ALIGN_LEFT_CLASS)
                .add("right".equals(chronoline.getAlign()), Chronoline.ALIGN_RIGHT_CLASS)
                .add("top".equals(chronoline.getAlign()), Chronoline.ALIGN_TOP_CLASS)
                .add("bottom".equals(chronoline.getAlign()), Chronoline.ALIGN_BOTTOM_CLASS)
                .add("alternate".equals(chronoline.getAlign()), Chronoline.ALIGN_ALTERNATE_CLASS)
                .add("horizontal".equals(chronoline.getLayout()), Chronoline.LAYOUT_HORIZONTAL_CLASS)
                .add("vertical".equals(chronoline.getLayout()), Chronoline.LAYOUT_VERTICAL_CLASS)
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeEvents(context, chronoline);

        writer.endElement("div");
    }

    protected void encodeEvents(FacesContext context, Chronoline chronoline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Collection<?> value = (Collection<?>) chronoline.getValue();

        if (value != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            String var = chronoline.getVar();

            for (Iterator<?> it = value.iterator(); it.hasNext(); ) {
                requestMap.put(var, it.next());

                writer.startElement("div", null);
                writer.writeAttribute("class", Chronoline.EVENT_CLASS, null);

                encodeOppositeContent(context, chronoline);
                encodeSeparator(context, chronoline, !it.hasNext());
                encodeContent(context, chronoline);

                writer.endElement("div");
            }

            requestMap.remove(var);
        }
    }

    protected void encodeOppositeContent(FacesContext context, Chronoline chronoline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent oppositeFacet = chronoline.getFacet("opposite");

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_OPPOSITE_CLASS, null);

        if (ComponentUtils.shouldRenderFacet(oppositeFacet)) {
            oppositeFacet.encodeAll(context);
        }
        else {
            writer.write("&nbsp;");
        }

        writer.endElement("div");
    }

    protected void encodeSeparator(FacesContext context, Chronoline chronoline, boolean isLastItem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent markerFacet = chronoline.getFacet("marker");

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_SEPARATOR_CLASS, null);

        if (ComponentUtils.shouldRenderFacet(markerFacet)) {
            markerFacet.encodeAll(context);
        }
        else {
            writer.startElement("div", null);
            writer.writeAttribute("class", Chronoline.EVENT_MARKER_CLASS, null);
            writer.endElement("div");
        }

        if (!isLastItem) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Chronoline.EVENT_CONNECTOR_CLASS, null);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Chronoline chronoline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_CONTENT_CLASS, null);
        renderChildren(context, chronoline);
        writer.endElement("div");
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
