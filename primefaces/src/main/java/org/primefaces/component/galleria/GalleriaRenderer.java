/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.galleria;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.IterationStatus;
import org.primefaces.model.ResponsiveOption;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

public class GalleriaRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Galleria galleria = (Galleria) component;

        encodeMarkup(context, galleria);
        encodeScript(context, galleria);
    }

    public void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        String style = galleria.getStyle();
        String styleClass = galleria.getStyleClass();
        styleClass = (styleClass == null) ? Galleria.CONTAINER_CLASS : Galleria.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", galleria.getClientId(context), "id");
        writer.writeAttribute("tabindex", galleria.getTabindex(), null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeHeader(context, galleria);
        encodeContent(context, galleria);
        encodeFooter(context, galleria);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Galleria galleria) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = galleria.getFacet("header");
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (shouldRenderFacet) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.HEADER_CLASS, null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    public void encodeContent(FacesContext context, Galleria galleria) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Galleria.CONTENT_CLASS, null);

        encodeItems(context, galleria);
        encodeCaptions(context, galleria);
        encodeIndicators(context, galleria);
        encodeThumbnails(context, galleria);

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Galleria galleria) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = galleria.getFacet("footer");
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (shouldRenderFacet) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.FOOTER_CLASS, null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    public void encodeItems(FacesContext context, Galleria galleria) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);
        writer.writeAttribute("class", Galleria.ITEMS_CLASS, null);

        if (galleria.isRepeating()) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            int rowCount = galleria.getRowCount();
            String varStatus = galleria.getVarStatus();
            Object varStatusBackup = varStatus == null ? null : requestMap.get(varStatus);

            for (int i = 0; i < rowCount; i++) {
                galleria.setIndex(i);

                IterationStatus status = new IterationStatus((i == 0), (i == (rowCount - 1)), i, i, 0, rowCount - 1, 1);
                if (varStatus != null) {
                    requestMap.put(varStatus, status);
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", Galleria.ITEM_CLASS, null);
                renderChildren(context, galleria);
                writer.endElement("li");
            }

            galleria.setIndex(-1);

            if (varStatus != null) {
                if (varStatusBackup == null) {
                    requestMap.remove(varStatus);
                }
                else {
                    requestMap.put(varStatus, varStatusBackup);
                }
            }
        }
        else {
            for (UIComponent kid : galleria.getChildren()) {
                if (kid.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Galleria.ITEM_CLASS, null);
                    renderChild(context, kid);
                    writer.endElement("li");
                }
            }
        }

        writer.endElement("ul");
    }

    public void encodeCaptions(FacesContext context, Galleria galleria) throws IOException {
        UIComponent facet = galleria.getFacet("caption");
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (galleria.isShowCaption() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("ul", null);
            writer.writeAttribute("class", Galleria.CAPTION_ITEMS_CLASS, null);
            writer.writeAttribute("style", "display: none", null); // default to hidden

            if (galleria.isRepeating()) {
                for (int i = 0; i < galleria.getRowCount(); i++) {
                    galleria.setIndex(i);

                    encodeCaption(context, galleria, galleria.getFacet("caption"), true);
                }

                galleria.setIndex(-1);
            }
            else {
                if (facet instanceof UIPanel) {
                    for (UIComponent kid : facet.getChildren()) {
                        if (kid.isRendered()) {
                            encodeCaption(context, galleria, kid, false);
                        }
                    }
                }
                else {
                    encodeCaption(context, galleria, facet, false);
                }
            }

            writer.endElement("ul");
        }
    }

    public void encodeCaption(FacesContext context, Galleria galleria, UIComponent child, boolean hasVar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("li", null);
        writer.writeAttribute("class", Galleria.CAPTION_ITEM_CLASS, null);

        if (hasVar) {
            child.encodeAll(context);
        }
        else {
            renderChild(context, child);
        }

        writer.endElement("li");
    }

    public void encodeIndicators(FacesContext context, Galleria galleria) throws IOException {
        UIComponent facet = galleria.getFacet("indicator");
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (galleria.isShowIndicators() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("ul", null);
            writer.writeAttribute("class", Galleria.INDICATORS_CLASS, null);

            if (galleria.isRepeating()) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                int rowCount = galleria.getRowCount();
                String varStatus = galleria.getVarStatus();
                Object varStatusBackup = varStatus == null ? null : requestMap.get(varStatus);

                for (int i = 0; i < rowCount; i++) {
                    galleria.setIndex(i);

                    IterationStatus status = new IterationStatus((i == 0), (i == (rowCount - 1)), i, i, 0, rowCount - 1, 1);
                    if (varStatus != null) {
                        requestMap.put(varStatus, status);
                    }

                    encodeIndicator(context, galleria, galleria.getFacet("indicator"), true);
                }

                galleria.setIndex(-1);

                if (varStatus != null) {
                    if (varStatusBackup == null) {
                        requestMap.remove(varStatus);
                    }
                    else {
                        requestMap.put(varStatus, varStatusBackup);
                    }
                }
            }
            else {
                if (facet instanceof UIPanel) {
                    for (UIComponent kid : facet.getChildren()) {
                        if (kid.isRendered()) {
                            encodeIndicator(context, galleria, kid, false);
                        }
                    }
                }
                else {
                    encodeIndicator(context, galleria, facet, false);
                }
            }

            writer.endElement("ul");
        }
    }

    public void encodeIndicator(FacesContext context, Galleria galleria, UIComponent child, boolean hasVar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("li", null);
        writer.writeAttribute("class", Galleria.INDICATOR_CLASS, null);

        if (hasVar) {
            child.encodeAll(context);
        }
        else {
            renderChild(context, child);
        }

        writer.endElement("li");
    }

    public void encodeThumbnails(FacesContext context, Galleria galleria) throws IOException {
        UIComponent facet = galleria.getFacet("thumbnail");
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (galleria.isShowThumbnails() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.THUMBNAIL_ITEMS_CLASS, null);

            if (galleria.isRepeating()) {
                for (int i = 0; i < galleria.getRowCount(); i++) {
                    galleria.setIndex(i);

                    encodeThumbnail(context, galleria, galleria.getFacet("thumbnail"), true);
                }

                galleria.setIndex(-1);
            }
            else {
                if (facet instanceof UIPanel) {
                    for (UIComponent kid : facet.getChildren()) {
                        if (kid.isRendered()) {
                            encodeThumbnail(context, galleria, kid, false);
                        }
                    }
                }
                else {
                    encodeThumbnail(context, galleria, facet, false);
                }
            }

            writer.endElement("div");
        }
    }

    public void encodeThumbnail(FacesContext context, Galleria galleria, UIComponent child, boolean hasVar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Galleria.THUMBNAIL_ITEM_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", Galleria.THUMBNAIL_ITEM_CONTENT_CLASS, null);

        if (hasVar) {
            child.encodeAll(context);
        }
        else {
            renderChild(context, child);
        }

        writer.endElement("div");

        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        WidgetBuilder wb = getWidgetBuilder(context);
        List<ResponsiveOption> responsiveOptions = galleria.getResponsiveOptions();

        wb.init("Galleria", galleria);

        wb.attr("activeIndex", galleria.getActiveIndex(), 0)
                .attr("fullScreen", galleria.isFullScreen(), false)
                .attr("closeIcon", galleria.getCloseIcon(), null)
                .attr("numVisible", galleria.getNumVisible(), 3)
                .attr("showThumbnails", galleria.isShowThumbnails(), true)
                .attr("showIndicators", galleria.isShowIndicators(), false)
                .attr("showIndicatorsOnItem", galleria.isShowIndicatorsOnItem(), false)
                .attr("showCaption", galleria.isShowCaption(), false)
                .attr("showItemNavigators", galleria.isShowItemNavigators(), false)
                .attr("showThumbnailNavigators", galleria.isShowThumbnailNavigators(), true)
                .attr("showItemNavigatorsOnHover", galleria.isShowItemNavigatorsOnHover(), false)
                .attr("changeItemOnIndicatorHover", galleria.isChangeItemOnIndicatorHover(), false)
                .attr("circular", galleria.isCircular(), false)
                .attr("autoPlay", galleria.isAutoPlay(), false)
                .attr("transitionInterval", galleria.getTransitionInterval(), 4000)
                .attr("thumbnailsPosition", galleria.getThumbnailsPosition(), "bottom")
                .attr("verticalViewPortHeight", galleria.getVerticalViewPortHeight(), "450px")
                .attr("indicatorsPosition", galleria.getIndicatorsPosition(), "bottom");

        if (responsiveOptions != null) {
            writer.write(",responsiveOptions:[");

            for (Iterator<ResponsiveOption> it = responsiveOptions.iterator(); it.hasNext();) {
                it.next().encode(writer);

                if (it.hasNext()) {
                    writer.write(",");
                }
            }

            writer.write("]");
        }

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
