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
package org.primefaces.component.tag;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class TagRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Tag tag = (Tag) component;
        ResponseWriter writer = context.getResponseWriter();
        String severity = tag.getSeverity();
        String styleClass = getStyleClassBuilder(context)
                    .add(Tag.STYLE_CLASS)
                    .add(tag.getStyleClass())
                    .add("info".equals(severity), Tag.SEVERITY_INFO_CLASS)
                    .add("success".equals(severity), Tag.SEVERITY_SUCCESS_CLASS)
                    .add("warning".equals(severity), Tag.SEVERITY_WARNING_CLASS)
                    .add("danger".equals(severity), Tag.SEVERITY_DANGER_CLASS)
                    .add(tag.isRounded(), Tag.ROUNDED_CLASS)
                    .build();

        writer.startElement("span", null);
        writer.writeAttribute("id", tag.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (tag.getStyle() != null) {
            writer.writeAttribute("style", tag.getStyle(), "style");
        }

        if (tag.getChildCount() > 0) {
            renderChildren(context, tag);
        }
        else {
            encodeDefaultContent(context, tag);
        }

        writer.endElement("span");
    }

    public void encodeDefaultContent(FacesContext context, Tag tag) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (tag.getIcon() != null) {
            String iconStyleClass = getStyleClassBuilder(context)
                    .add(Tag.ICON_CLASS)
                    .add(tag.getIcon())
                    .build();

            writer.startElement("span", tag);
            writer.writeAttribute("class", iconStyleClass, null);
            writer.endElement("span");
        }

        String value = ComponentUtils.getValueToRender(context, tag);
        if (value != null) {
            writer.startElement("span", tag);
            writer.writeAttribute("class", Tag.VALUE_CLASS, null);
            writer.writeText(value, "value");
            writer.endElement("span");
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(final FacesContext fc, final UIComponent component) {
        // nothing to do
    }
}
