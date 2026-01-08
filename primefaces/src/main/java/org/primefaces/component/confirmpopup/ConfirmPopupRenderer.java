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
package org.primefaces.component.confirmpopup;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ConfirmPopup.DEFAULT_RENDERER, componentFamily = ConfirmPopup.COMPONENT_FAMILY)
public class ConfirmPopupRenderer extends CoreRenderer<ConfirmPopup> {

    @Override
    public void encodeEnd(FacesContext context, ConfirmPopup component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, ConfirmPopup component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(ConfirmPopup.STYLE_CLASS)
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeContent(context, component);
        encodeFooter(context, component);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ConfirmPopup component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = component.getMessage();
        UIComponent messageFacet = component.getMessageFacet();
        String iconStyleClass = getStyleClassBuilder(context)
                .add(ConfirmPopup.ICON_CLASS)
                .add(component.getIcon())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmPopup.CONTENT_CLASS, null);
        writer.writeAttribute("id", component.getClientId(context) + "_content", null);

        //icon
        writer.startElement("i", null);
        writer.writeAttribute("class", iconStyleClass, null);
        writer.endElement("i");

        writer.startElement("span", null);
        writer.writeAttribute("class", ConfirmPopup.MESSAGE_CLASS, null);

        if (FacetUtils.shouldRenderFacet(messageFacet)) {
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

    protected void encodeScript(FacesContext context, ConfirmPopup component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("ConfirmPopup", component)
            .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
            .attr("global", component.isGlobal(), false)
            .attr("dismissable", component.isDismissable(), true);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, ConfirmPopup component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
