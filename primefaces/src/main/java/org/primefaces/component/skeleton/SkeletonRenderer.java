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
package org.primefaces.component.skeleton;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.LangUtils;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Skeleton.DEFAULT_RENDERER, componentFamily = Skeleton.COMPONENT_FAMILY)
public class SkeletonRenderer extends CoreRenderer<Skeleton> {

    @Override
    public void encodeEnd(FacesContext context, Skeleton component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String size = component.getSize();
        String styleClass = getStyleClassBuilder(context)
                    .add(Skeleton.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add("circle".equals(component.getShape()), Skeleton.CIRCLE_CLASS)
                    .add("none".equals(component.getAnimation()), Skeleton.NONE_ANIMATION_CLASS)
                    .build();

        String style = getStyleBuilder(context)
                         .add(component.getStyle())
                         .add(size != null, "width", size, component.getWidth())
                         .add(size != null, "height", size, component.getHeight())
                         .add("border-radius", component.getBorderRadius())
                         .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, "style");
        }

        writer.endElement("div");
    }
}
