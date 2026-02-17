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
package org.primefaces.component.chronoline;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;

import java.io.IOException;
import java.util.Collection;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Chronoline.DEFAULT_RENDERER, componentFamily = Chronoline.COMPONENT_FAMILY)
public class ChronolineRenderer extends CoreRenderer<Chronoline> {

    @Override
    public void encodeEnd(FacesContext context, Chronoline component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId();
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Chronoline.STYLE_CLASS)
                .add(component.getStyleClass())
                .add("left".equals(component.getAlign()), Chronoline.ALIGN_LEFT_CLASS)
                .add("right".equals(component.getAlign()), Chronoline.ALIGN_RIGHT_CLASS)
                .add("top".equals(component.getAlign()), Chronoline.ALIGN_TOP_CLASS)
                .add("bottom".equals(component.getAlign()), Chronoline.ALIGN_BOTTOM_CLASS)
                .add("alternate".equals(component.getAlign()), Chronoline.ALIGN_ALTERNATE_CLASS)
                .add("horizontal".equals(component.getLayout()), Chronoline.LAYOUT_HORIZONTAL_CLASS)
                .add("vertical".equals(component.getLayout()), Chronoline.LAYOUT_VERTICAL_CLASS)
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeEvents(context, component);

        writer.endElement("div");
    }

    protected void encodeEvents(FacesContext context, Chronoline component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Collection<?> value = (Collection<?>) component.getValue();

        if (value != null) {
            int rowCount = component.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                component.setRowIndex(i);

                writer.startElement("div", null);
                writer.writeAttribute("class", Chronoline.EVENT_CLASS, null);

                encodeOppositeContent(context, component);
                encodeSeparator(context, component, i == rowCount - 1);
                encodeContent(context, component);

                writer.endElement("div");
            }

            component.setRowIndex(-1);
        }
    }

    protected void encodeOppositeContent(FacesContext context, Chronoline component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent oppositeFacet = component.getOppositeFacet();

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_OPPOSITE_CLASS, null);

        if (FacetUtils.shouldRenderFacet(oppositeFacet)) {
            oppositeFacet.encodeAll(context);
        }
        else {
            writer.write("&nbsp;");
        }

        writer.endElement("div");
    }

    protected void encodeSeparator(FacesContext context, Chronoline component, boolean isLastItem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent markerFacet = component.getMarkerFacet();

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_SEPARATOR_CLASS, null);

        if (FacetUtils.shouldRenderFacet(markerFacet)) {
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

    protected void encodeContent(FacesContext context, Chronoline component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Chronoline.EVENT_CONTENT_CLASS, null);
        renderChildren(context, component);
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, Chronoline component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
