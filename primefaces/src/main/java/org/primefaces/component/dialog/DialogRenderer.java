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
package org.primefaces.component.dialog;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Dialog.DEFAULT_RENDERER, componentFamily = Dialog.COMPONENT_FAMILY)
public class DialogRenderer extends CoreRenderer<Dialog> {

    @Override
    public void decode(FacesContext context, Dialog component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Dialog component) throws IOException {
        if (component.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeScript(FacesContext context, Dialog component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dialog", component);

        wb.attr("visible", component.isVisible(), false)
                .attr("draggable", component.isDraggable(), true)
                .attr("resizable", component.isResizable(), true)
                .attr("modal", component.isModal(), false)
                .attr("blockScroll", component.isBlockScroll(), false)
                .attr("width", component.getWidth(), null)
                .attr("height", component.getHeight(), null)
                .attr("minWidth", component.getMinWidth(), Integer.MIN_VALUE)
                .attr("minHeight", component.getMinHeight(), Integer.MIN_VALUE)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("dynamic", component.isDynamic(), false)
                .attr("showEffect", component.getShowEffect(), null)
                .attr("hideEffect", component.getHideEffect(), null)
                .attr("my", component.getMy(), null)
                .attr("position", component.getPosition(), null)
                .attr("closeOnEscape", component.isCloseOnEscape(), false)
                .attr("dismissibleMask", component.isDismissibleMask(), false)
                .attr("fitViewport", component.isFitViewport(), false)
                .attr("responsive", component.isResponsive(), true)
                .attr("cache", component.isCache(), true)
                .callback("onHide", "function()", component.getOnHide())
                .callback("onShow", "function()", component.getOnShow());

        String focusExpressions = SearchExpressionUtils.resolveOptionalClientIdsForClientSide(context, component, component.getFocus());
        if (focusExpressions != null) {
            wb.attr("focus", focusExpressions);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Dialog component) throws IOException {
        if (component.isBlockScroll() && !component.isModal()) {
            throw new IllegalArgumentException("blockScroll=true can only be used when modal=true");
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String positionType = component.getPositionType();
        String style = getStyleBuilder(context).add(component.getStyle()).add("display", "none").build();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? Dialog.DIALOG_CLASS : Dialog.DIALOG_CLASS + " " + styleClass;

        if (ComponentUtils.isRTL(context, component)) {
            styleClass += " ui-dialog-rtl";
        }

        if ("absolute".equals(positionType)) {
            if (component.isBlockScroll()) {
                throw new IllegalArgumentException("blockScroll=true cannot be used when positionType=absolute");
            }

            styleClass += " ui-dialog-absolute";
        }

        if (component.isFitViewport()) {
            styleClass += " ui-dialog-fitviewport";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.BOX_CLASS, null);

        if (component.isShowHeader()) {
            encodeHeader(context, component);
        }

        encodeContent(context, component);

        encodeFooter(context, component);

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Dialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = component.getHeader();
        UIComponent headerFacet = component.getHeaderFacet();

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.TITLE_BAR_CLASS, null);

        //title
        writer.startElement("span", null);
        writer.writeAttribute("id", component.getClientId(context) + "_title", null);
        writer.writeAttribute("class", Dialog.TITLE_CLASS, null);

        if (FacetUtils.shouldRenderFacet(headerFacet)) {
            headerFacet.encodeAll(context);
        }
        else if (header != null) {
            writer.writeText(header, null);
        }

        writer.endElement("span");

        if (component.isClosable()) {
            encodeIcon(context, Dialog.TITLE_BAR_CLOSE_CLASS, Dialog.CLOSE_ICON_CLASS, null);
        }

        if (component.isMaximizable()) {
            encodeIcon(context, Dialog.TITLE_BAR_MAXIMIZE_CLASS, Dialog.MAXIMIZE_ICON_CLASS, null);
        }

        if (component.isMinimizable()) {
            encodeIcon(context, Dialog.TITLE_BAR_MINIMIZE_CLASS, Dialog.MINIMIZE_ICON_CLASS, null);
        }

        //Actions
        UIComponent actionsFacet = component.getActionsFacet();
        if (FacetUtils.shouldRenderFacet(actionsFacet)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Dialog.DIALOG_ACTIONS_CLASS, null);
            actionsFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Dialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String footer = component.getFooter();
        UIComponent footerFacet = component.getFooterFacet();

        if (footer == null && (footerFacet == null || !footerFacet.isRendered())) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.FOOTER_CLASS, null);

        writer.startElement("div", null);
        if (FacetUtils.shouldRenderFacet(footerFacet)) {
            footerFacet.encodeAll(context);
        }
        else if (footer != null) {
            writer.writeText(footer, null);
        }
        writer.endElement("div");

        writer.endElement("div");

    }

    protected void encodeContent(FacesContext context, Dialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        writer.writeAttribute("id", component.getClientId(context) + "_content", null);

        if (!component.isDynamic()) {
            renderChildren(context, component);
        }

        writer.endElement("div");
    }

    protected void encodeIcon(FacesContext context, String anchorClass, String iconClass, String ariaLabel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", anchorClass, null);
        if (ariaLabel != null) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    @Override
    public void encodeChildren(FacesContext context, Dialog component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
