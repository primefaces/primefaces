/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.api.IterationStatus;
import org.primefaces.model.ResponsiveOption;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Galleria.DEFAULT_RENDERER, componentFamily = Galleria.COMPONENT_FAMILY)
public class GalleriaRenderer extends CoreRenderer<Galleria> {

    @Override
    public void encodeEnd(FacesContext context, Galleria component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    public void encodeMarkup(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? Galleria.CONTAINER_CLASS : Galleria.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("tabindex", component.getTabindex(), null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeHeader(context, component);
        encodeContent(context, component);
        encodeFooter(context, component);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = component.getHeaderFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (shouldRenderFacet) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.HEADER_CLASS, null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    public void encodeContent(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Galleria.CONTENT_CLASS, null);

        encodeItems(context, component);
        encodeCaptions(context, component);
        encodeIndicators(context, component);
        encodeThumbnails(context, component);

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = component.getFooterFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (shouldRenderFacet) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.FOOTER_CLASS, null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    public void encodeItems(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);
        writer.writeAttribute("class", Galleria.ITEMS_CLASS, null);

        if (component.isRepeating()) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            int rowCount = component.getRowCount();
            String varStatus = component.getVarStatus();
            Object varStatusBackup = varStatus == null ? null : requestMap.get(varStatus);

            for (int i = 0; i < rowCount; i++) {
                component.setIndex(i);

                IterationStatus status = new IterationStatus((i == 0), (i == (rowCount - 1)), i, i, 0, rowCount - 1, 1);
                if (varStatus != null) {
                    requestMap.put(varStatus, status);
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", Galleria.ITEM_CLASS, null);
                renderChildren(context, component);
                writer.endElement("li");
            }

            component.setIndex(-1);

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
            for (UIComponent kid : component.getChildren()) {
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

    public void encodeCaptions(FacesContext context, Galleria component) throws IOException {
        UIComponent facet = component.getCaptionFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (component.isShowCaption() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("ul", null);
            writer.writeAttribute("class", Galleria.CAPTION_ITEMS_CLASS, null);
            writer.writeAttribute("style", "display: none", null); // default to hidden

            if (component.isRepeating()) {
                for (int i = 0; i < component.getRowCount(); i++) {
                    component.setIndex(i);

                    encodeCaption(context, component, component.getCaptionFacet(), true);
                }

                component.setIndex(-1);
            }
            else {
                if (facet instanceof UIPanel) {
                    for (UIComponent kid : facet.getChildren()) {
                        if (kid.isRendered()) {
                            encodeCaption(context, component, kid, false);
                        }
                    }
                }
                else {
                    encodeCaption(context, component, facet, false);
                }
            }

            writer.endElement("ul");
        }
    }

    public void encodeCaption(FacesContext context, Galleria component, UIComponent child, boolean hasVar) throws IOException {
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

    public void encodeIndicators(FacesContext context, Galleria component) throws IOException {
        UIComponent facet = component.getIndicatorFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (component.isShowIndicators() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("ul", null);
            writer.writeAttribute("class", Galleria.INDICATORS_CLASS, null);

            if (component.isRepeating()) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                int rowCount = component.getRowCount();
                String varStatus = component.getVarStatus();
                Object varStatusBackup = varStatus == null ? null : requestMap.get(varStatus);

                for (int i = 0; i < rowCount; i++) {
                    component.setIndex(i);

                    IterationStatus status = new IterationStatus((i == 0), (i == (rowCount - 1)), i, i, 0, rowCount - 1, 1);
                    if (varStatus != null) {
                        requestMap.put(varStatus, status);
                    }

                    encodeIndicator(context, component, component.getIndicatorFacet(), true);
                }

                component.setIndex(-1);

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
                            encodeIndicator(context, component, kid, false);
                        }
                    }
                }
                else {
                    encodeIndicator(context, component, facet, false);
                }
            }

            writer.endElement("ul");
        }
    }

    public void encodeIndicator(FacesContext context, Galleria component, UIComponent child, boolean hasVar) throws IOException {
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

    public void encodeThumbnails(FacesContext context, Galleria component) throws IOException {
        UIComponent facet = component.getThumbnailFacet();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(facet);

        if (component.isShowThumbnails() && shouldRenderFacet) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("div", null);
            writer.writeAttribute("class", Galleria.THUMBNAIL_ITEMS_CLASS, null);

            if (component.isRepeating()) {
                for (int i = 0; i < component.getRowCount(); i++) {
                    component.setIndex(i);

                    encodeThumbnail(context, component, component.getThumbnailFacet(), true);
                }

                component.setIndex(-1);
            }
            else {
                if (facet instanceof UIPanel) {
                    for (UIComponent kid : facet.getChildren()) {
                        if (kid.isRendered()) {
                            encodeThumbnail(context, component, kid, false);
                        }
                    }
                }
                else {
                    encodeThumbnail(context, component, facet, false);
                }
            }

            writer.endElement("div");
        }
    }

    public void encodeThumbnail(FacesContext context, Galleria component, UIComponent child, boolean hasVar) throws IOException {
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

    public void encodeScript(FacesContext context, Galleria component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        WidgetBuilder wb = getWidgetBuilder(context);
        List<ResponsiveOption> responsiveOptions = component.getResponsiveOptions();

        wb.init("Galleria", component);

        wb.attr("activeIndex", component.getActiveIndex(), 0)
                .attr("fullScreen", component.isFullScreen(), false)
                .attr("closeIcon", component.getCloseIcon(), null)
                .attr("numVisible", component.getNumVisible(), 3)
                .attr("showThumbnails", component.isShowThumbnails(), true)
                .attr("showIndicators", component.isShowIndicators(), false)
                .attr("showIndicatorsOnItem", component.isShowIndicatorsOnItem(), false)
                .attr("showCaption", component.isShowCaption(), false)
                .attr("showItemNavigators", component.isShowItemNavigators(), false)
                .attr("showThumbnailNavigators", component.isShowThumbnailNavigators(), true)
                .attr("showItemNavigatorsOnHover", component.isShowItemNavigatorsOnHover(), false)
                .attr("changeItemOnIndicatorHover", component.isChangeItemOnIndicatorHover(), false)
                .attr("circular", component.isCircular(), false)
                .attr("autoPlay", component.isAutoPlay(), false)
                .attr("transitionInterval", component.getTransitionInterval(), 4000)
                .attr("thumbnailsPosition", component.getThumbnailsPosition(), "bottom")
                .attr("verticalViewPortHeight", component.getVerticalViewPortHeight(), "450px")
                .attr("indicatorsPosition", component.getIndicatorsPosition(), "bottom");

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
    public void encodeChildren(FacesContext context, Galleria component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
