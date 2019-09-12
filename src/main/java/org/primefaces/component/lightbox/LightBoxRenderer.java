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
package org.primefaces.component.lightbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class LightBoxRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        LightBox lb = (LightBox) component;

        encodeMarkup(context, lb);
        encodeScript(context, lb);
    }

    public void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LightBox lb = (LightBox) component;
        String clientId = lb.getClientId(context);
        UIComponent inline = lb.getFacet("inline");

        writer.startElement("div", lb);
        writer.writeAttribute("id", clientId, "id");
        if (lb.getStyle() != null) {
            writer.writeAttribute("style", lb.getStyle(), null);
        }
        if (lb.getStyleClass() != null) {
            writer.writeAttribute("class", lb.getStyleClass(), null);
        }

        renderChildren(context, lb);

        if (inline != null) {
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-lightbox-inline ui-helper-hidden", null);
            inline.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        LightBox lb = (LightBox) component;
        String clientId = lb.getClientId(context);
        String mode = "image";
        if (lb.getFacet("inline") != null) {
            mode = "inline";
        }
        else if (lb.isIframe()) {
            mode = "iframe";
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("LightBox", lb.resolveWidgetVar(context), clientId)
                .attr("mode", mode)
                .attr("width", lb.getWidth(), null)
                .attr("height", lb.getHeight(), null)
                .attr("visible", lb.isVisible(), false)
                .attr("blockScroll", lb.isBlockScroll(), false)
                .attr("iframeTitle", lb.getIframeTitle(), null)
                .callback("onShow", "function()", lb.getOnShow())
                .callback("onHide", "function()", lb.getOnHide());

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
