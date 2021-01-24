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
package org.primefaces.component.paginator;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIPageableData;

public class CurrentPageReportRenderer implements PaginatorElementRenderer {

    private static final Pattern PATTERN_CURRENT_PAGE = Pattern.compile("\\{currentPage\\}");
    private static final Pattern PATTERN_TOTAL_PAGES = Pattern.compile("\\{totalPages\\}");
    private static final Pattern PATTERN_TOTAL_RECORDS = Pattern.compile("\\{totalRecords\\}");
    private static final Pattern PATTERN_START_RECORD = Pattern.compile("\\{startRecord\\}");
    private static final Pattern PATTERN_END_RECORD = Pattern.compile("\\{endRecord\\}");

    @Override
    public void render(FacesContext context, Pageable pageable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String template = pageable.getCurrentPageReportTemplate();
        int currentPage = pageable.getPage() + 1;
        int pageCount = pageable.getPageCount();
        if (pageCount == 0) {
            pageCount = 1;
        }

        String output = template;
        output = PATTERN_CURRENT_PAGE.matcher(output).replaceAll(
                Integer.toString(currentPage));
        output = PATTERN_TOTAL_PAGES.matcher(output).replaceAll(
                Integer.toString(pageCount));
        output = PATTERN_TOTAL_RECORDS.matcher(output).replaceAll(
                Integer.toString(pageable.getRowCount()));
        output = PATTERN_START_RECORD.matcher(output).replaceAll(
                Integer.toString(Math.min(pageable.getFirst() + 1, pageable.getRowCount())));
        output = PATTERN_END_RECORD.matcher(output).replaceAll(
                Integer.toString(Math.min(pageable.getFirst() + pageable.getRowsToRender(), pageable.getRowCount())));

        writer.startElement("span", null);
        writer.writeAttribute("class", UIPageableData.PAGINATOR_CURRENT_CLASS, null);
        writer.writeText(output, null);
        writer.endElement("span");
    }
}
