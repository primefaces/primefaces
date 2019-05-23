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
package org.primefaces.renderkit;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.util.HTML;

public class RendererUtils {

    private RendererUtils() {
        // Hide constructor
    }

    public static void encodeCheckbox(FacesContext context, boolean checked) throws IOException {
        encodeCheckbox(context, checked, false, false, null);
    }

    public static void encodeCheckbox(FacesContext context, boolean checked, boolean partialSelected, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon;
        String boxClass = disabled ? HTML.CHECKBOX_BOX_CLASS + " ui-state-disabled" : HTML.CHECKBOX_BOX_CLASS;
        String containerClass = (styleClass == null) ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        if (checked) {
            icon = HTML.CHECKBOX_CHECKED_ICON_CLASS;
        }
        else if (partialSelected) {
            icon = HTML.CHECKBOX_PARTIAL_CHECKED_ICON_CLASS;
        }
        else {
            icon = HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");

        writer.endElement("div");

        writer.endElement("div");
    }

}
