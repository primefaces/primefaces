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
package org.primefaces.component.paginator;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.util.HTML;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class RowsPerPageDropdownRenderer implements PaginatorElementRenderer {

    private static final Pattern PATTERN = Pattern.compile("[,]+");

    @Override
    public void render(FacesContext context, Pageable pageable) throws IOException {
        String template = pageable.getRowsPerPageTemplate();
        UIViewRoot viewroot = context.getViewRoot();
        char separator = UINamingContainer.getSeparatorChar(context);

        if (template != null) {
            ResponseWriter writer = context.getResponseWriter();
            int actualRows = pageable.getRows();
            String[] options = PATTERN.split(pageable.getRowsPerPageTemplate());
            String clientId = pageable.getClientId(context);
            String ddId = clientId + separator + viewroot.createUniqueId();
            String ddName = clientId + "_rppDD";
            String labelId = null;

            labelId = ddId + "_rppLabel";

            writer.startElement("label", null);
            writer.writeAttribute("id", labelId, null);
            writer.writeAttribute("for", ddId, null);
            writer.writeAttribute("class", UIPageableData.PAGINATOR_RPP_LABEL_CLASS, null);
            writer.endElement("label");

            writer.startElement("select", null);
            writer.writeAttribute("id", ddId, null);
            writer.writeAttribute("name", ddName, null);
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, labelId, null);
            writer.writeAttribute("class", UIPageableData.PAGINATOR_RPP_OPTIONS_CLASS, null);
            writer.writeAttribute("autocomplete", "off", null);

            for (String option : options) {
                writer.startElement("option", null);

                int rows;
                String optionText;
                if (option.trim().startsWith("{ShowAll|")) {
                    optionText = option.substring(option.indexOf('\'') + 1, option.lastIndexOf('\''));
                    rows = pageable.getRowCount();

                    writer.writeAttribute("value", "*", null);
                }
                else {
                    optionText = option.trim();
                    rows = Integer.parseInt(optionText);

                    writer.writeAttribute("value", rows, null);
                }

                if (actualRows == rows) {
                    writer.writeAttribute("selected", "selected", null);
                }

                writer.writeText(optionText, null);
                writer.endElement("option");
            }

            writer.endElement("select");
        }
    }
}
