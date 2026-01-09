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
package org.primefaces.component.confirmdialog;

import org.primefaces.component.dialog.Dialog;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ConfirmDialog.DEFAULT_RENDERER, componentFamily = ConfirmDialog.COMPONENT_FAMILY)
public class ConfirmDialogRenderer extends CoreRenderer<ConfirmDialog> {

    @Override
    public void encodeEnd(FacesContext context, ConfirmDialog component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, ConfirmDialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? ConfirmDialog.DIALOG_CLASS : ConfirmDialog.DIALOG_CLASS + " " + styleClass;

        if (ComponentUtils.isRTL(context, component)) {
            styleClass += " ui-dialog-rtl";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmDialog.BOX_CLASS, null);

        encodeHeader(context, component);
        encodeContent(context, component);
        encodeButtonPane(context, component);

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ConfirmDialog component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ConfirmDialog", component)
                .attr("visible", component.isVisible(), false)
                .attr("width", component.getWidth(), null)
                .attr("height", component.getHeight(), null)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("showEffect", component.getShowEffect(), null)
                .attr("hideEffect", component.getHideEffect(), null)
                .attr("closeOnEscape", component.isCloseOnEscape(), false)
                .attr("global", component.isGlobal(), false)
                .attr("responsive", component.isResponsive(), false);

        wb.finish();
    }

    protected void encodeHeader(FacesContext context, ConfirmDialog component) throws IOException {
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

            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_CLOSE_CLASS, null);
            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.CLOSE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ConfirmDialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = component.getMessage();
        UIComponent messageFacet = component.getMessageFacet();
        String defaultIcon = "ui-icon ui-icon-" + component.getSeverity();
        String severityIcon = defaultIcon + " " + ConfirmDialog.SEVERITY_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        writer.writeAttribute("id", component.getClientId(context) + "_content", null);

        //severity
        writer.startElement("span", null);
        writer.writeAttribute("class", severityIcon, null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", ConfirmDialog.MESSAGE_CLASS, null);

        if (FacetUtils.shouldRenderFacet(messageFacet)) {
            messageFacet.encodeAll(context);
        }
        else if (messageText != null) {
            writer.writeText(messageText, null);
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeButtonPane(FacesContext context, ConfirmDialog component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmDialog.BUTTONPANE_CLASS, null);

        renderChildren(context, component);

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, ConfirmDialog component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
