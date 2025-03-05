/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.separator;

import org.primefaces.renderkit.CoreRenderer;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

@Deprecated(since = "15.0", forRemoval = true)
public class SeparatorRenderer extends CoreRenderer<UISeparator> {

    @Override
    public void encodeEnd(FacesContext context, UISeparator component) throws IOException {
        logDevelopmentWarning(context, this, "Separator is deprecated please switch to Divider.");
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? UISeparator.DEFAULT_STYLE_CLASS : UISeparator.DEFAULT_STYLE_CLASS + " " + styleClass;

        writer.startElement("hr", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (component.getTitle() != null) {
            writer.writeAttribute("title", component.getTitle(), "title");
        }
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        writer.endElement("hr");
    }
}
