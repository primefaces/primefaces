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

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

@Deprecated(since = "15.0", forRemoval = true)
public class SeparatorRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        logDevelopmentWarning(context, this, "Separator is deprecated please switch to Divider.");
        UISeparator separator = (UISeparator) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? UISeparator.DEFAULT_STYLE_CLASS : UISeparator.DEFAULT_STYLE_CLASS + " " + styleClass;

        writer.startElement("hr", separator);
        writer.writeAttribute("id", separator.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (separator.getTitle() != null) {
            writer.writeAttribute("title", separator.getTitle(), "title");
        }
        if (separator.getStyle() != null) {
            writer.writeAttribute("style", separator.getStyle(), "style");
        }

        writer.endElement("hr");
    }
}
