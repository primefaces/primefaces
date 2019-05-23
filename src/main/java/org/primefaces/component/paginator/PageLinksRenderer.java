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
package org.primefaces.component.paginator;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIData;

public class PageLinksRenderer implements PaginatorElementRenderer {

    @Override
    public void render(FacesContext context, Pageable pageable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int currentPage = pageable.getPage();
        int pageLinks = pageable.getPageLinks();
        int pageCount = pageable.getPageCount();
        int visiblePages = Math.min(pageLinks, pageCount);

        //calculate range, keep current in middle if necessary
        int start = Math.max(0, (int) Math.ceil(currentPage - ((visiblePages) / 2)));
        int end = Math.min(pageCount - 1, start + visiblePages - 1);

        //check when approaching to last page
        int delta = pageLinks - (end - start + 1);
        start = Math.max(0, start - delta);

        writer.startElement("span", null);
        writer.writeAttribute("class", UIData.PAGINATOR_PAGES_CLASS, null);

        for (int i = start; i <= end; i++) {
            String styleClass = currentPage == i ? UIData.PAGINATOR_ACTIVE_PAGE_CLASS : UIData.PAGINATOR_PAGE_CLASS;

            writer.startElement("a", null);
            writer.writeAttribute("class", styleClass, null);
            writer.writeAttribute("tabindex", 0, null);
            writer.writeAttribute("href", "#", null);
            writer.writeText((i + 1), null);
            writer.endElement("a");
        }

        writer.endElement("span");
    }
}
