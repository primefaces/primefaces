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
package org.primefaces.component.divider;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Divider.DEFAULT_RENDERER, componentFamily = Divider.COMPONENT_FAMILY)
public class DividerRenderer extends CoreRenderer<Divider> {

    @Override
    public void encodeEnd(FacesContext context, Divider component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = component.getLayout();
        String align = component.getAlign();
        String type = component.getType();
        boolean isHorizontal = "horizontal".equals(layout);
        boolean isVertical = "vertical".equals(layout);
        String styleClass = getStyleClassBuilder(context)
                    .add(Divider.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add(isHorizontal, Divider.HORIZONTAL_CLASS)
                    .add(isVertical, Divider.VERTICAL_CLASS)
                    .add("solid".equals(type), Divider.SOLID_CLASS)
                    .add("dashed".equals(type), Divider.DASHED_CLASS)
                    .add("dotted".equals(type), Divider.DOTTED_CLASS)
                    .add(isHorizontal && (align == null || "left".equals(align)), Divider.ALIGN_LEFT_CLASS)
                    .add(isHorizontal && "right".equals(align), Divider.ALIGN_RIGHT_CLASS)
                    .add((isHorizontal && "center".equals(align)) || (isVertical && (align == null || "center".equals(align))), Divider.ALIGN_CENTER_CLASS)
                    .add(isVertical && "top".equals(align), Divider.ALIGN_TOP_CLASS)
                    .add(isVertical && "bottom".equals(align), Divider.ALIGN_BOTTOM_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("role", HTML.ARIA_ROLE_SEPARATOR, "role");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        if (component.getTitle() != null) {
            writer.writeAttribute("title", component.getTitle(), "title");
        }
        if (component.getChildCount() > 0) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Divider.CONTENT_CLASS, "styleClass");
            renderChildren(context, component);
            writer.endElement("div");
        }
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, Divider component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
