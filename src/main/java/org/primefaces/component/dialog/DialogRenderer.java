/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

public class DialogRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Dialog dialog = (Dialog) component;

        if (dialog.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, dialog);
            encodeScript(context, dialog);
        }
    }

    protected void encodeScript(FacesContext context, Dialog dialog) throws IOException {
        String clientId = dialog.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dialog", dialog.resolveWidgetVar(context), clientId);

        wb.attr("visible", dialog.isVisible(), false)
                .attr("draggable", dialog.isDraggable(), true)
                .attr("resizable", dialog.isResizable(), true)
                .attr("modal", dialog.isModal(), false)
                .attr("blockScroll", dialog.isBlockScroll(), false)
                .attr("width", dialog.getWidth(), null)
                .attr("height", dialog.getHeight(), null)
                .attr("minWidth", dialog.getMinWidth(), Integer.MIN_VALUE)
                .attr("minHeight", dialog.getMinHeight(), Integer.MIN_VALUE)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, dialog, dialog.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                .attr("dynamic", dialog.isDynamic(), false)
                .attr("showEffect", dialog.getShowEffect(), null)
                .attr("hideEffect", dialog.getHideEffect(), null)
                .attr("my", dialog.getMy(), null)
                .attr("position", dialog.getPosition(), null)
                .attr("closeOnEscape", dialog.isCloseOnEscape(), false)
                .attr("fitViewport", dialog.isFitViewport(), false)
                .attr("responsive", dialog.isResponsive(), false)
                .callback("onHide", "function()", dialog.getOnHide())
                .callback("onShow", "function()", dialog.getOnShow());

        String focusExpressions = SearchExpressionFacade.resolveClientIds(
                context, dialog, dialog.getFocus(), SearchExpressionHint.RESOLVE_CLIENT_SIDE);
        if (focusExpressions != null) {
            wb.attr("focus", focusExpressions);
        }

        encodeClientBehaviors(context, dialog);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String positionType = dialog.getPositionType();
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = styleClass == null ? Dialog.CONTAINER_CLASS : Dialog.CONTAINER_CLASS + " " + styleClass;

        if (ComponentUtils.isRTL(context, dialog)) {
            styleClass += " ui-dialog-rtl";
        }

        if (positionType.equals("absolute")) {
            styleClass += " ui-dialog-absolute";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (dialog.isShowHeader()) {
            encodeHeader(context, dialog);
        }

        encodeContent(context, dialog);

        encodeFooter(context, dialog);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = dialog.getHeader();
        UIComponent headerFacet = dialog.getFacet("header");

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.TITLE_BAR_CLASS, null);

        //title
        writer.startElement("span", null);
        writer.writeAttribute("id", dialog.getClientId(context) + "_title", null);
        writer.writeAttribute("class", Dialog.TITLE_CLASS, null);

        if (headerFacet != null) {
            headerFacet.encodeAll(context);
        }
        else if (header != null) {
            writer.writeText(header, null);
        }

        writer.endElement("span");

        if (dialog.isClosable()) {
            encodeIcon(context, Dialog.TITLE_BAR_CLOSE_CLASS, Dialog.CLOSE_ICON_CLASS, MessageFactory.getMessage(Dialog.ARIA_CLOSE, null));
        }

        if (dialog.isMaximizable()) {
            encodeIcon(context, Dialog.TITLE_BAR_MAXIMIZE_CLASS, Dialog.MAXIMIZE_ICON_CLASS, null);
        }

        if (dialog.isMinimizable()) {
            encodeIcon(context, Dialog.TITLE_BAR_MINIMIZE_CLASS, Dialog.MINIMIZE_ICON_CLASS, null);
        }

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String footer = dialog.getFooter();
        UIComponent footerFacet = dialog.getFacet("footer");

        if (footer == null && (footerFacet == null || !footerFacet.isRendered())) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.FOOTER_CLASS, null);

        writer.startElement("span", null);
        if (footerFacet != null) {
            footerFacet.encodeAll(context);
        }
        else if (footer != null) {
            writer.writeText(footer, null);
        }
        writer.endElement("span");

        writer.endElement("div");

    }

    protected void encodeContent(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        writer.writeAttribute("id", dialog.getClientId(context) + "_content", null);

        if (!dialog.isDynamic()) {
            renderChildren(context, dialog);
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
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
