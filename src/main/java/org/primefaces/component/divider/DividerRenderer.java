/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.divider;

import org.primefaces.renderkit.CoreRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;

public class DividerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Divider divider = (Divider) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = divider.getStyleClass();
        styleClass = styleClass == null ? Divider.DEFAULT_STYLE_CLASS : Divider.DEFAULT_STYLE_CLASS + " " + styleClass;
        String layout = divider.getLayout();
        String align = divider.getAlign();

        if ("horizontal".equals(layout)) {
            styleClass += " " + Divider.HORIZONTAL_CLASS;
        }
        else if ("vertical".equals(layout)) {
            styleClass += " " + Divider.VERTICAL_CLASS;
        }

        switch (divider.getType()) {
            case "solid":
                styleClass += " " + Divider.SOLID_CLASS;
                break;
            case "dashed":
                styleClass += " " + Divider.DASHED_CLASS;
                break;
            case "dotted":
                styleClass += " " + Divider.DOTTED_CLASS;
                break;
        }

        if ("horizontal".equals(layout) && align == null) {
            styleClass += " " + Divider.ALING_LEFT_CLASS;
        }
        else if ("vertical".equals(layout) && align == null) {
            styleClass += " " + Divider.ALING_CENTER_CLASS;
        }
        else if ("horizontal".equals(layout) && "left".equals(align)) {
            styleClass += " " + Divider.ALING_LEFT_CLASS;
        }
        else if ("horizontal".equals(layout) && "center".equals(align)) {
            styleClass += " " + Divider.ALING_CENTER_CLASS;
        }
        else if ("horizontal".equals(layout) && "right".equals(align)) {
            styleClass += " " + Divider.ALING_RIGHT_CLASS;
        }
        else if ("vertical".equals(layout) && "top".equals(align)) {
            styleClass += " " + Divider.ALING_TOP_CLASS;
        }
        else if ("vertical".equals(layout) && "center".equals(align)) {
            styleClass += " " + Divider.ALING_CENTER_CLASS;
        }
        else if ("vertical".equals(layout) && "bottom".equals(align)) {
            styleClass += " " + Divider.ALING_BOTTOM_CLASS;
        }

        writer.startElement("div", divider);
        writer.writeAttribute("id", divider.getClientId(context), "id");
        writer.writeAttribute("role", "separator", "role");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (divider.getStyle() != null) {
            writer.writeAttribute("style", divider.getStyle(), "style");
        }
        if (divider.getChildCount() > 0) {
            writer.startElement("div", divider);
            writer.writeAttribute("class", Divider.CONTENT_CLASS, "styleClass");
            renderChildren(context, divider);
            writer.endElement("div");
        }
        writer.endElement("div");
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
