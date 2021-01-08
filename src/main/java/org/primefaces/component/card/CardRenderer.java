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
package org.primefaces.component.card;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class CardRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Card card = (Card) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(Card.DEFAULT_STYLE_CLASS)
                .add(card.getStyleClass())
                .build();

        UIComponent headerFacet = card.getFacet("header");
        UIComponent titleFacet = card.getFacet("title");
        UIComponent subtitleFacet = card.getFacet("subtitle");
        UIComponent footerFacet = card.getFacet("footer");

        writer.startElement("div", card);
        writer.writeAttribute("id", card.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (card.getStyle() != null) {
            writer.writeAttribute("style", card.getStyle(), "style");
        }

        //header
        if (ComponentUtils.shouldRenderFacet(headerFacet)) {
            writer.startElement("div", card);
            writer.writeAttribute("class", Card.HEADER_CLASS, null);
            headerFacet.encodeAll(context);
            writer.endElement("div");
        }

        //BODY START
        writer.startElement("div", card);
        writer.writeAttribute("class", Card.BODY_CLASS, null);

        //title
        if (ComponentUtils.shouldRenderFacet(titleFacet)) {
            writer.startElement("div", card);
            writer.writeAttribute("class", Card.TITLE_CLASS, null);
            titleFacet.encodeAll(context);
            writer.endElement("div");
        }

        //subtitle
        if (ComponentUtils.shouldRenderFacet(subtitleFacet)) {
            writer.startElement("div", card);
            writer.writeAttribute("class", Card.SUBTITLE_CLASS, null);
            subtitleFacet.encodeAll(context);
            writer.endElement("div");
        }

        //content
        writer.startElement("div", card);
        writer.writeAttribute("class", Card.CONTENT_CLASS, null);
        renderChildren(context, card);
        writer.endElement("div");

        //footer
        if (ComponentUtils.shouldRenderFacet(footerFacet)) {
            writer.startElement("div", card);
            writer.writeAttribute("class", Card.FOOTER_CLASS, null);
            footerFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
        //BODY END

        writer.endElement("div");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(final FacesContext fc, final UIComponent component) {
        // nothing to do
    }

}
