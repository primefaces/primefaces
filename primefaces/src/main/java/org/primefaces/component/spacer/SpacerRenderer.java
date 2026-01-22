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
package org.primefaces.component.spacer;

import org.primefaces.renderkit.CoreRenderer;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Spacer.DEFAULT_RENDERER, componentFamily = Spacer.COMPONENT_FAMILY)
public class SpacerRenderer extends CoreRenderer<Spacer> {

    @Override
    public void encodeEnd(FacesContext context, Spacer component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        // <svg xmlns="http://www.w3.org/2000/svg" width="1" height="1"/>
        writer.startElement("svg", component);
        writer.writeAttribute("xmlns", "http://www.w3.org/2000/svg", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("width", component.getWidth(), "width");
        writer.writeAttribute("height", component.getHeight(), "height");

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), "styleClass");
        }
        if (component.getTitle() != null) {
            writer.writeAttribute("title", component.getTitle(), "title");
        }

        writer.endElement("svg");
    }
}
