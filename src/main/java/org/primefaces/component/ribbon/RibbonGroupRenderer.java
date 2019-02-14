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
package org.primefaces.component.ribbon;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class RibbonGroupRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RibbonGroup group = (RibbonGroup) component;
        String label = group.getLabel();
        String groupClass = group.getStyleClass();
        groupClass = (groupClass == null) ? Ribbon.GROUP_CLASS : Ribbon.GROUP_CLASS + " " + groupClass;
        String style = group.getStyle();

        writer.startElement("li", null);
        writer.writeAttribute("class", groupClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Ribbon.GROUP_CONTENT_CLASS, null);
        renderChildren(context, group);
        writer.endElement("div");

        writer.startElement("div", null);
        writer.writeAttribute("class", Ribbon.GROUP_LABEL_CLASS, null);
        if (label != null) {
            writer.writeText(label, null);
        }
        writer.endElement("div");

        writer.endElement("li");
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
