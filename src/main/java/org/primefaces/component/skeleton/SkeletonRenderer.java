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
package org.primefaces.component.skeleton;

import org.primefaces.renderkit.CoreRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;

public class SkeletonRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Skeleton skeleton = (Skeleton) component;
        ResponseWriter writer = context.getResponseWriter();
        String size = skeleton.getSize();
        String borderRadius = skeleton.getBorderRadius();
        String styleClass = getStyleClassBuilder(context)
                    .add(Skeleton.STYLE_CLASS)
                    .add(skeleton.getStyleClass())
                    .add("circle".equals(skeleton.getShape()), Skeleton.CIRCLE_CLASS)
                    .add("none".equals(skeleton.getAnimation()), Skeleton.NONE_ANIMATION_CLASS)
                    .build();
        String style = skeleton.getStyle();
        style = style == null ? "" : style;

        if (size != null) {
            style += " width: " + size + "; height: " + size + ";";
        }
        else {
            style += " width: " + skeleton.getWidth() + "; height: " + skeleton.getHeight() + ";";
        }

        if (borderRadius != null) style += " borderRadius: " + borderRadius + ";";

        writer.startElement("div", null);
        writer.writeAttribute("id", skeleton.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("style", style, "style");

        writer.endElement("div");
    }
}
