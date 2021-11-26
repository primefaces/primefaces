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
package org.primefaces.component.carousel;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.ResponsiveOption;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class CarouselRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Carousel carousel = (Carousel) component;

        encodeMarkup(context, carousel);
        encodeScript(context, carousel);
    }

    private void encodeScript(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        WidgetBuilder wb = getWidgetBuilder(context);
        List<ResponsiveOption> responsiveOptions = carousel.getResponsiveOptions();

        wb.init("Carousel", carousel);

        wb.attr("page", carousel.getPage(), 0)
                .attr("circular", carousel.isCircular(), false)
                .attr("autoplayInterval", carousel.getAutoplayInterval(), 0)
                .attr("numVisible", carousel.getNumVisible(), 1)
                .attr("numScroll", carousel.getNumScroll(), 1)
                .attr("orientation", carousel.getOrientation(), "horizontal")
                .attr("touchable", ComponentUtils.isTouchable(context, carousel), true)
                .callback("onPageChange", "function(pageValue)", carousel.getOnPageChange());

        if (responsiveOptions != null) {
            writer.write(",responsiveOptions:[");
            for (int i = 0; i < responsiveOptions.size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                responsiveOptions.get(i).encode(writer);
            }
            writer.write("]");
        }

        encodeClientBehaviors(context, carousel);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = carousel.getClientId(context);
        String style = carousel.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Carousel.STYLE_CLASS)
                .add(carousel.getStyleClass())
                .add("horizontal".equals(carousel.getOrientation()), Carousel.HORIZONTAL_CLASS)
                .add("vertical".equals(carousel.getOrientation()), Carousel.VERTICAL_CLASS)
                .build();

        //container
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeHeader(context, carousel);
        encodeContent(context, carousel);
        encodeFooter(context, carousel);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isVertical = "vertical".equals(carousel.getOrientation());

        String contentStyleClass = getStyleClassBuilder(context)
                .add(Carousel.CONTENT_CLASS)
                .add(carousel.getContentStyleClass())
                .build();
        String containerStyleClass = getStyleClassBuilder(context)
                .add(Carousel.CONTAINER_CLASS)
                .add(carousel.getContainerStyleClass())
                .build();
        String itemContentHeight = isVertical ? carousel.getVerticalViewPortHeight() : "auto";

        writer.startElement("div", null);
        writer.writeAttribute("class", contentStyleClass, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", containerStyleClass, null);

        encodePrevButton(context, carousel, isVertical);

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.ITEMS_CONTENT_CLASS, null);
        writer.writeAttribute("style", "height:" + itemContentHeight, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.ITEMS_CONTAINER_CLASS, null);

        encodeItem(context, carousel);

        writer.endElement("div");

        writer.endElement("div");

        encodeNextButton(context, carousel, isVertical);

        writer.endElement("div");

        encodeIndicators(context, carousel);

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int rowCount = carousel.getRowCount();

        if (carousel.getVar() != null) {
            for (int i = 0; i < rowCount; i++) {
                carousel.setIndex(i);

                writer.startElement("div", null);
                writer.writeAttribute("class", Carousel.ITEM_CLASS, "itemStyleClass");

                renderChildren(context, carousel);

                writer.endElement("div");
            }

            carousel.setIndex(-1);
        }
        else {
            for (UIComponent kid : carousel.getChildren()) {
                if (kid.isRendered()) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", Carousel.ITEM_CLASS, "itemStyleClass");

                    renderChild(context, kid);

                    writer.endElement("div");
                }
            }
        }
    }

    protected void encodePrevButton(FacesContext context, Carousel carousel, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String prevButtonIconStyleClass = getStyleClassBuilder(context)
                .add(Carousel.PREV_BUTTON_ICON_CLASS)
                .add(!isVertical, "pi-chevron-left")
                .add(isVertical, "pi-chevron-up")
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("class", Carousel.PREV_BUTTON_CLASS, null);
        writer.writeAttribute("type", "button", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", prevButtonIconStyleClass, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeNextButton(FacesContext context, Carousel carousel, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String nextButtonIconStyleClass = getStyleClassBuilder(context)
                .add(Carousel.NEXT_BUTTON_ICON_CLASS)
                .add(!isVertical, "pi-chevron-right")
                .add(isVertical, "pi-chevron-down")
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("class", Carousel.NEXT_BUTTON_CLASS, null);
        writer.writeAttribute("type", "button", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", nextButtonIconStyleClass, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeIndicators(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String indicatorsContentStyleClass = getStyleClassBuilder(context)
                .add(Carousel.INDICATORS_CONTENT_CLASS)
                .add(carousel.getIndicatorsContentStyleClass())
                .build();

        writer.startElement("ul", null);
        writer.writeAttribute("class", indicatorsContentStyleClass, null);

        writer.endElement("ul");
    }

    protected void encodeHeader(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String headerText = carousel.getHeaderText();
        UIComponent facet = carousel.getFacet("header");
        boolean shouldRenderFacet = ComponentUtils.shouldRenderFacet(facet);

        if (headerText == null && !shouldRenderFacet) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.HEADER_CLASS, null);

        if (shouldRenderFacet) {
            facet.encodeAll(context);
        }
        else {
            writer.writeText(headerText, "headerText");
        }

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String footerText = carousel.getFooterText();
        UIComponent facet = carousel.getFacet("footer");
        boolean shouldRenderFacet = ComponentUtils.shouldRenderFacet(facet);

        if (footerText == null && !shouldRenderFacet) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.FOOTER_CLASS, null);

        if (shouldRenderFacet) {
            facet.encodeAll(context);
        }
        else {
            writer.writeText(footerText, "footerText");
        }

        writer.endElement("div");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }
}
