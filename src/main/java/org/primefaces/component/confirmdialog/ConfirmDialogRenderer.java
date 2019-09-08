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
package org.primefaces.component.confirmdialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.dialog.Dialog;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

public class ConfirmDialogRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ConfirmDialog dialog = (ConfirmDialog) component;

        encodeMarkup(context, dialog);
        encodeScript(context, dialog);
    }

    protected void encodeMarkup(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = styleClass == null ? ConfirmDialog.CONTAINER_CLASS : ConfirmDialog.CONTAINER_CLASS + " " + styleClass;

        if (ComponentUtils.isRTL(context, dialog)) {
            styleClass += " ui-dialog-rtl";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeHeader(context, dialog);
        encodeContent(context, dialog);
        encodeButtonPane(context, dialog);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ConfirmDialog dialog) throws IOException {
        String clientId = dialog.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ConfirmDialog", dialog.resolveWidgetVar(context), clientId)
                .attr("visible", dialog.isVisible(), false)
                .attr("width", dialog.getWidth(), null)
                .attr("height", dialog.getHeight(), null)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, dialog, dialog.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                .attr("showEffect", dialog.getShowEffect(), null)
                .attr("hideEffect", dialog.getHideEffect(), null)
                .attr("closeOnEscape", dialog.isCloseOnEscape(), false)
                .attr("global", dialog.isGlobal(), false)
                .attr("responsive", dialog.isResponsive(), false);

        wb.finish();
    }

    protected void encodeHeader(FacesContext context, ConfirmDialog dialog) throws IOException {
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
            String ariaLabel = MessageFactory.getMessage(Dialog.ARIA_CLOSE, null);

            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_CLOSE_CLASS, null);
            if (ariaLabel != null) {
                writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
            }

            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.CLOSE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = dialog.getMessage();
        UIComponent messageFacet = dialog.getFacet("message");
        String defaultIcon = dialog.isGlobal() ? "ui-icon" : "ui-icon ui-icon-" + dialog.getSeverity();
        String severityIcon = defaultIcon + " " + ConfirmDialog.SEVERITY_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        writer.writeAttribute("id", dialog.getClientId(context) + "_content", null);

        //severity
        writer.startElement("span", null);
        writer.writeAttribute("class", severityIcon, null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", ConfirmDialog.MESSAGE_CLASS, null);

        if (messageFacet != null) {
            messageFacet.encodeAll(context);
        }
        else if (messageText != null) {
            writer.writeText(messageText, null);
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeButtonPane(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmDialog.BUTTONPANE_CLASS, null);

        renderChildren(context, dialog);

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
