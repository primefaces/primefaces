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
package org.primefaces.renderkit;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.PrimeUIData;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.paginator.CurrentPageReportRenderer;
import org.primefaces.component.paginator.FirstPageLinkRenderer;
import org.primefaces.component.paginator.JumpToPageDropdownRenderer;
import org.primefaces.component.paginator.JumpToPageInputRenderer;
import org.primefaces.component.paginator.LastPageLinkRenderer;
import org.primefaces.component.paginator.NextPageLinkRenderer;
import org.primefaces.component.paginator.PageLinksRenderer;
import org.primefaces.component.paginator.PaginatorElementRenderer;
import org.primefaces.component.paginator.PrevPageLinkRenderer;
import org.primefaces.component.paginator.RowsPerPageDropdownRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class DataRenderer<T extends UIComponent & Pageable> extends CoreRenderer<T> {

    private static final Map<String, PaginatorElementRenderer> PAGINATOR_ELEMENTS = MapBuilder.<String, PaginatorElementRenderer>builder()
            .put("{CurrentPageReport}", new CurrentPageReportRenderer())
            .put("{FirstPageLink}", new FirstPageLinkRenderer())
            .put("{PreviousPageLink}", new PrevPageLinkRenderer())
            .put("{NextPageLink}", new NextPageLinkRenderer())
            .put("{LastPageLink}", new LastPageLinkRenderer())
            .put("{PageLinks}", new PageLinksRenderer())
            .put("{RowsPerPageDropdown}", new RowsPerPageDropdownRenderer())
            .put("{JumpToPageDropdown}", new JumpToPageDropdownRenderer())
            .put("{JumpToPageInput}", new JumpToPageInputRenderer())
            .build();

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>");

    public static void addPaginatorElement(String element, PaginatorElementRenderer renderer) {
        PAGINATOR_ELEMENTS.put(element, renderer);
    }

    public static PaginatorElementRenderer removePaginatorElement(String element) {
        return PAGINATOR_ELEMENTS.remove(element);
    }

    protected void encodePaginatorMarkup(FacesContext context, T pageable, String position) throws IOException {
        if (!pageable.isPaginatorAlwaysVisible() && pageable.getPageCount() <= 1) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        boolean isTop = "top".equals(position);
        UIComponent leftTopContent = pageable.getFacet("paginatorTopLeft");
        UIComponent rightTopContent = pageable.getFacet("paginatorTopRight");
        UIComponent leftBottomContent = pageable.getFacet("paginatorBottomLeft");
        UIComponent rightBottomContent = pageable.getFacet("paginatorBottomRight");

        String styleClass = isTop ? UIPageableData.PAGINATOR_TOP_CONTAINER_CLASS : UIPageableData.PAGINATOR_BOTTOM_CONTAINER_CLASS;
        String id = pageable.getClientId(context) + "_paginator_" + position;

        // start main container
        writer.startElement("div", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "navigation", null);

        // start left facet
        writer.startElement("div", null);
        String leftClass = isTop ? UIPageableData.PAGINATOR_TOP_LEFT_CONTENT_CLASS : UIPageableData.PAGINATOR_BOTTOM_LEFT_CONTENT_CLASS;
        writer.writeAttribute("class", leftClass, null);
        if (isTop && FacetUtils.shouldRenderFacet(leftTopContent)) {
            renderChild(context, leftTopContent);
        }
        if (!isTop && FacetUtils.shouldRenderFacet(leftBottomContent)) {
            renderChild(context, leftBottomContent);
        }
        // end left facet
        writer.endElement("div");

        // start center facet
        writer.startElement("div", null);
        writer.writeAttribute("class", UIPageableData.PAGINATOR_CENTER_CONTENT_CLASS, null);
        String[] elements = pageable.getPaginatorTemplate().split(" ");
        for (String element : elements) {
            PaginatorElementRenderer renderer = PAGINATOR_ELEMENTS.get(element);
            if (renderer != null) {
                renderer.render(context, pageable);
            }
            else {
                if (element.startsWith("{") && element.endsWith("}")) {
                    UIComponent elementFacet = pageable.getFacet(element);
                    if (elementFacet != null) {
                        elementFacet.encodeAll(context);
                    }
                }
                else {
                    writer.write(element + " ");
                }
            }
        }
        // end center facet
        writer.endElement("div");

        // start right facet
        writer.startElement("div", null);
        String rightClass = isTop ? UIPageableData.PAGINATOR_TOP_RIGHT_CONTENT_CLASS : UIPageableData.PAGINATOR_BOTTOM_RIGHT_CONTENT_CLASS;
        writer.writeAttribute("class", rightClass, null);
        if (isTop && FacetUtils.shouldRenderFacet(rightTopContent)) {
            writer.writeAttribute("class", UIPageableData.PAGINATOR_TOP_RIGHT_CONTENT_CLASS, null);
            renderChild(context, rightTopContent);
        }

        if (!isTop && FacetUtils.shouldRenderFacet(rightBottomContent)) {
            writer.writeAttribute("class", UIPageableData.PAGINATOR_BOTTOM_RIGHT_CONTENT_CLASS, null);
            renderChild(context, rightBottomContent);
        }
        // end right facet
        writer.endElement("div");

        // end main container
        writer.endElement("div");
    }

    protected void encodePaginatorConfig(FacesContext context, T pageable, WidgetBuilder wb) throws IOException {
        String clientId = pageable.getClientId(context);
        String paginatorPosition = pageable.getPaginatorPosition();
        String paginatorContainers = null;
        String currentPageTemplate = pageable.getCurrentPageReportTemplate();

        if ("both".equalsIgnoreCase(paginatorPosition)) {
            paginatorContainers = "'" + clientId + "_paginator_top','" + clientId + "_paginator_bottom'";
        }
        else {
            paginatorContainers = "'" + clientId + "_paginator_" + paginatorPosition.toLowerCase() + "'";
        }

        wb.append(",paginator:{")
                .append("id:[").append(paginatorContainers).append("]")
                .append(",rows:").append(pageable.getRows())
                .append(",rowCount:").append(pageable.getRowCount())
                .append(",page:").append(pageable.getPage());

        if (currentPageTemplate != null) {
            String currentPageTemplateTmp = currentPageTemplate.replace("'", "\\'");
            wb.append(",currentPageTemplate:'").append(currentPageTemplateTmp).append("'");
        }

        if (pageable.getPageLinks() != 10) {
            wb.append(",pageLinks:").append(pageable.getPageLinks());
        }

        if (!pageable.isPaginatorAlwaysVisible()) {
            wb.append(",alwaysVisible:false");
        }

        wb.append("}");
    }

    public void encodeFacet(FacesContext context, PrimeUIData data, String facet, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent component = data.getFacet(facet);

        if (FacetUtils.shouldRenderFacet(component)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, null);
            component.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected boolean isColumnAriaHeaderTextDefined(FacesContext context, UIColumn column) {
        return LangUtils.isNotBlank(column.getAriaHeaderText());
    }

    protected String resolveColumnAriaHeaderText(FacesContext context, UIColumn column) {
        if (column instanceof UIComponent) {
            UIComponent component = (UIComponent) column;
            component.pushComponentToEL(context, component);
        }

        try {
            String ariaHeaderText = column.getAriaHeaderText();

            // for headerText of column
            if (ariaHeaderText == null) {
                ariaHeaderText = column.getHeaderText();
            }

            return ariaHeaderText;
        }
        finally {
            if (column instanceof UIComponent) {
                UIComponent component = (UIComponent) column;
                component.popComponentFromEL(context);
            }
        }
    }

    protected String resolveColumnHeaderText(FacesContext context, UIColumn column) {
        if (column instanceof UIComponent) {
            UIComponent component = (UIComponent) column;
            component.pushComponentToEL(context, component);
        }

        try {
            String headerText = column.getHeaderText();

            // for ariaHeaderText of column
            if (headerText == null) {
                headerText = column.getAriaHeaderText();
            }

            return headerText;
        }
        finally {
            if (column instanceof UIComponent) {
                UIComponent component = (UIComponent) column;
                component.popComponentFromEL(context);
            }
        }
    }
}
