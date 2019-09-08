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
package org.primefaces.component.outputpanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class OutputPanelRenderer extends CoreRenderer {

    private static final String BLOCK = "div";
    private static final String INLINE = "span";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OutputPanel panel = (OutputPanel) component;

        if (panel.isContentLoadRequest(context)) {
            renderChildren(context, panel);
        }
        else {
            encodeMarkup(context, panel);
            if (isDeferredNecessary(context, panel)) {
                encodeScript(context, panel);
            }
        }
    }

    public void encodeMarkup(FacesContext context, OutputPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tag = panel.getLayout().equals("block") ? BLOCK : INLINE;
        String clientId = panel.getClientId(context);
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = (styleClass == null) ? OutputPanel.CONTAINER_CLASS : OutputPanel.CONTAINER_CLASS + " " + styleClass;

        writer.startElement(tag, panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", panel.getStyle(), "style");
        }

        if (isDeferredNecessary(context, panel)) {
            renderLoading(context, panel);
        }
        else {
            renderChildren(context, panel);
        }

        writer.endElement(tag);
    }

    protected void encodeScript(FacesContext context, OutputPanel panel) throws IOException {
        String clientId = panel.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OutputPanel", panel.resolveWidgetVar(context), clientId);

        wb.attr("deferred", true)
                .attr("deferredMode", panel.getDeferredMode());

        encodeClientBehaviors(context, panel);

        wb.finish();
    }

    protected void renderLoading(FacesContext context, OutputPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", OutputPanel.LOADING_CLASS, null);
        writer.endElement("div");
    }

    protected boolean isDeferredNecessary(FacesContext context, OutputPanel panel) {
        return !context.getPartialViewContext().isAjaxRequest() && panel.isDeferred();
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
