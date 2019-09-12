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
package org.primefaces.component.contentflow;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ContentFlowRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ContentFlow cf = (ContentFlow) component;

        encodeMarkup(context, cf);
        encodeScript(context, cf);
    }

    protected void encodeMarkup(FacesContext context, ContentFlow cf) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = cf.getStyle();
        String styleClass = cf.getStyleClass();
        String containerClass = (styleClass == null) ? ContentFlow.CONTAINER_CLASS : ContentFlow.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", cf);
        writer.writeAttribute("id", cf.getClientId(context), null);
        writer.writeAttribute("class", containerClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        //indicator
        writer.startElement("div", null);
        writer.writeAttribute("class", "loadindicator", null);
        writer.startElement("div", null);
        writer.writeAttribute("class", "indicator", null);
        writer.endElement("div");
        writer.endElement("div");

        //content
        encodeContent(context, cf);

        //caption
        writer.startElement("div", null);
        writer.writeAttribute("class", "globalCaption", null);
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ContentFlow cf) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = cf.getVar();

        writer.startElement("div", null);
        writer.writeAttribute("class", "flow", null);

        if (var == null) {
            for (UIComponent child : cf.getChildren()) {
                if (child.isRendered()) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", "item", null);
                    child.encodeAll(context);
                    writer.endElement("div");
                }
            }
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            Collection<?> value = (Collection<?>) cf.getValue();
            if (value != null) {
                for (Iterator<?> it = value.iterator(); it.hasNext(); ) {
                    requestMap.put(var, it.next());

                    writer.startElement("div", null);
                    writer.writeAttribute("class", "item", null);
                    renderChildren(context, cf);
                    writer.endElement("div");
                }
            }
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ContentFlow cf) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        if (context.isPostback()) {
            wb.init("ContentFlow", cf.resolveWidgetVar(context), cf.getClientId(context));
        }
        else {
            wb.initWithWindowLoad("ContentFlow", cf.resolveWidgetVar(context), cf.getClientId(context));
        }

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
