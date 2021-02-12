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
package org.primefaces.component.confirmpopup;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class ConfirmPopupRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ConfirmPopup popup = (ConfirmPopup) component;

        encodeMarkup(context, popup);
        encodeScript(context, popup);
    }

    protected void encodeMarkup(FacesContext context, ConfirmPopup popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = popup.getClientId(context);
        String style = popup.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(ConfirmPopup.STYLE_CLASS)
                .add(popup.getStyleClass())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeContent(context, popup);
        encodeFooter(context, popup);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ConfirmPopup popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = popup.getMessage();
        UIComponent messageFacet = popup.getFacet("message");
        String iconStyleClass = getStyleClassBuilder(context)
                .add(ConfirmPopup.ICON_CLASS)
                .add(popup.getIcon())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmPopup.CONTENT_CLASS, null);
        writer.writeAttribute("id", popup.getClientId(context) + "_content", null);

        //icon
        writer.startElement("i", null);
        writer.writeAttribute("class", iconStyleClass, null);
        writer.endElement("i");

        writer.startElement("span", null);
        writer.writeAttribute("class", ConfirmPopup.MESSAGE_CLASS, null);

        if (ComponentUtils.shouldRenderFacet(messageFacet)) {
            messageFacet.encodeAll(context);
        }
        else if (messageText != null) {
            writer.writeText(messageText, null);
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, ConfirmPopup popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmPopup.FOOTER_CLASS, null);

        renderChildren(context, popup);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ConfirmPopup popup) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ConfirmPopup", popup)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, popup, popup.getAppendTo(),
                        SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE), null)
                .attr("global", popup.isGlobal(), false)
                .attr("dismissable", popup.isDismissable(), true);

        wb.finish();
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
