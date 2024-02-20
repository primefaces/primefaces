/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LangUtils;

public class CardRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Card card = (Card) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(Card.STYLE_CLASS)
                .add(card.getStyleClass())
                .build();

        writer.startElement("div", card);
        writer.writeAttribute("id", card.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (LangUtils.isNotBlank(card.getStyle())) {
            writer.writeAttribute("style", card.getStyle(), "style");
        }

        //header
        encodeCardTextOrFacet(context, card, card.getHeader(), "header", Card.HEADER_CLASS);

        //BODY START
        writer.startElement("div", null);
        writer.writeAttribute("class", Card.BODY_CLASS, null);

        //title
        encodeCardTextOrFacet(context, card, card.getTitle(), "title", Card.TITLE_CLASS);

        //subtitle
        encodeCardTextOrFacet(context, card, card.getSubtitle(), "subtitle", Card.SUBTITLE_CLASS);

        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", Card.CONTENT_CLASS, null);
        renderChildren(context, card);
        writer.endElement("div");

        //footer
        encodeCardTextOrFacet(context, card, card.getFooter(), "footer", Card.FOOTER_CLASS);

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

    protected void encodeCardTextOrFacet(FacesContext context, Card card, String value, String facetName, String cssClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (LangUtils.isNotBlank(value)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", cssClass, null);
            writer.writeText(value, null);
            writer.endElement("div");
        }
        else {
            UIComponent facet = card.getFacet(facetName);
            if (FacetUtils.shouldRenderFacet(facet)) {
                writer.startElement("div", null);
                writer.writeAttribute("class", cssClass, null);
                facet.encodeAll(context);
                writer.endElement("div");
            }
        }
    }

}
