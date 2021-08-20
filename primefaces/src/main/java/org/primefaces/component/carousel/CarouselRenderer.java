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

import org.primefaces.model.carousel.CarouselResponsiveOption;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class CarouselRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Carousel carousel = (Carousel) component;

        encodeMarkup(context, carousel);
        encodeScript(context, carousel);
    }

    private void encodeScript(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        WidgetBuilder wb = getWidgetBuilder(context);
        List<CarouselResponsiveOption> responsiveOptions = carousel.getResponsiveOptions();

        wb.init("Carousel", carousel);

        wb.attr("page", carousel.getPage(), 0)
                .attr("circular", carousel.isCircular(), false)
                .attr("autoplayInterval", carousel.getAutoplayInterval(), 0)
                .attr("numVisible", carousel.getNumVisible(), 1)
                .attr("numScroll", carousel.getNumScroll(), 1)
                .attr("orientation", carousel.getOrientation(), "horizontal")
                .attr("touchable", ComponentUtils.isTouchable(context, carousel),  true);

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
        int totalIndicators = this.calculateIndicatorCount(carousel);
        boolean isCircular = carousel.isCircular();
        int rowCount = carousel.getRowCount();
        int numVisible = carousel.getNumVisible();
        int page = carousel.getPage();
        boolean backwardIsDisabled = (!isCircular || rowCount < numVisible) && page == 0;
        boolean forwardIsDisabled = (!isCircular || rowCount < numVisible) && (page == (totalIndicators - 1) || totalIndicators == 0);
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

        encodePrevButton(context, carousel, backwardIsDisabled, isVertical);

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.ITEMS_CONTENT_CLASS, null);
        writer.writeAttribute("style", "height:" + itemContentHeight, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.ITEMS_CONTAINER_CLASS, null);

        encodeItem(context, carousel, rowCount, numVisible);

        writer.endElement("div");

        writer.endElement("div");

        encodeNextButton(context, carousel, forwardIsDisabled, isVertical);

        writer.endElement("div");

        encodeIndicators(context, carousel, totalIndicators, page);

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, Carousel carousel, int rowCount, int numVisible) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int firstIndex = 0;
        int lastIndex = firstIndex + numVisible - 1;

        if (carousel.getVar() != null) {
            for (int i = 0; i < rowCount; i++) {
                carousel.setIndex(i);
                String itemStyleClass = getStyleClassBuilder(context)
                        .add(Carousel.ITEM_CLASS)
                        .add(lastIndex >= i, "ui-carousel-item-active")
                        .add(firstIndex == i, "ui-carousel-item-start")
                        .add(lastIndex == i, "ui-carousel-item-end")
                        .build();

                writer.startElement("div", null);
                writer.writeAttribute("class", itemStyleClass, "itemStyleClass");

                renderChildren(context, carousel);

                writer.endElement("div");
            }

            carousel.setIndex(-1);
        }
    }

    protected void encodePrevButton(FacesContext context, Carousel carousel, boolean backwardIsDisabled, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String prevButtonStyleClass =  getStyleClassBuilder(context)
                .add(Carousel.PREV_BUTTON_CLASS)
                .add(backwardIsDisabled, "ui-state-disabled")
                .build();
        String prevButtonIconStyleClass =  getStyleClassBuilder(context)
                .add(Carousel.PREV_BUTTON_ICON_CLASS)
                .add(!isVertical, "pi-chevron-left")
                .add(isVertical, "pi-chevron-up")
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("class", prevButtonStyleClass, null);
        writer.writeAttribute("type", "button", null);

        if (backwardIsDisabled) {
            writer.writeAttribute("disabled", true, null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", prevButtonIconStyleClass, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeNextButton(FacesContext context, Carousel carousel, boolean forwardIsDisabled, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String nextButtonStyleClass =  getStyleClassBuilder(context)
                .add(Carousel.NEXT_BUTTON_CLASS)
                .add(forwardIsDisabled, "ui-state-disabled")
                .build();
        String nextButtonIconStyleClass =  getStyleClassBuilder(context)
                .add(Carousel.NEXT_BUTTON_ICON_CLASS)
                .add(!isVertical, "pi-chevron-right")
                .add(isVertical, "pi-chevron-down")
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("class", nextButtonStyleClass, null);
        writer.writeAttribute("type", "button", null);

        if (forwardIsDisabled) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", nextButtonIconStyleClass, null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeIndicators(FacesContext context, Carousel carousel, int totalIndicators, int page) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String indicatorsContentStyleClass = getStyleClassBuilder(context)
                .add(Carousel.INDICATORS_CONTENT_CLASS)
                .add(carousel.getIndicatorsContentStyleClass())
                .build();

        if (totalIndicators >= 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", indicatorsContentStyleClass, null);
            for (int i = 0; i < totalIndicators; i++) {
                String indicatorStyleClass = getStyleClassBuilder(context)
                        .add(Carousel.INDICATOR_CLASS)
                        .add(page == i, "ui-highlight")
                        .build();

                writer.startElement("li", null);
                writer.writeAttribute("class", indicatorStyleClass, null);
                writer.writeAttribute("data-index", i, null);

                writer.startElement("button", null);
                writer.writeAttribute("class", "ui-link", null);
                writer.writeAttribute("type", "button", null);
                writer.endElement("button");

                writer.endElement("li");
            }

            writer.endElement("ul");
        }
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

    protected int calculateIndicatorCount(Carousel carousel) {
        String var = carousel.getVar();
        int childCount = carousel.getRowCount();
        int numScroll = carousel.getNumScroll();
        int numVisible = carousel.getNumVisible();
        return var != null ? (int) ((Math.ceil(childCount - numVisible)) / numScroll) + 1 : 0;
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
