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
        String layout = divider.getLayout();
        String align = divider.getAlign();
        String type = divider.getType();
        boolean isHorizontal = "horizontal".equals(layout);
        boolean isVertical = "vertical".equals(layout);
        String styleClass = getStyleClassBuilder(context)
                    .add(Divider.DEFAULT_STYLE_CLASS)
                    .add(divider.getStyleClass())
                    .add(isHorizontal, Divider.HORIZONTAL_CLASS)
                    .add(isVertical, Divider.VERTICAL_CLASS)
                    .add("solid".equals(type), Divider.SOLID_CLASS)
                    .add("dashed".equals(type), Divider.DASHED_CLASS)
                    .add("dotted".equals(type), Divider.DOTTED_CLASS)
                    .add((isHorizontal && align == null) || "left".equals(align), Divider.ALING_LEFT_CLASS)
                    .add((isVertical && align == null) || "center".equals(align), Divider.ALING_CENTER_CLASS)
                    .add(isHorizontal && "right".equals(align), Divider.ALING_RIGHT_CLASS)
                    .add(isVertical && "top".equals(align), Divider.ALING_TOP_CLASS)
                    .add(isVertical && "bottom".equals(align), Divider.ALING_BOTTOM_CLASS)
                    .build();

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
