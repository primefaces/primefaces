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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

public interface Pageable {

    String getClientId(FacesContext context);

    @Property(defaultValue = "bottom", description = "Position of the paginator.")
    String getPaginatorPosition();

    @Property(defaultValue = "({currentPage} of {totalPages})", description = "Template of the currentPageReport UI.")
    String getCurrentPageReportTemplate();

    @Property(defaultValue = "0", description = "Number of rows to display per page. 0 means to display all data available.")
    int getRows();

    int getRowCount();

    int getPage();

    @Property(defaultValue = "10", description = "Maximum number of page links to display.")
    int getPageLinks();

    @Property(defaultValue = "true", description = "Defines if paginator should be hidden if total data count is less than number of rows per page.")
    boolean isPaginatorAlwaysVisible();

    Object getFooter();

    Object getHeader();

    @Property(defaultValue = "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}",
            description = "Template of the paginator.")
    String getPaginatorTemplate();

    UIComponent getFacet(String element);

    int getPageCount();

    @Property(defaultValue = "0", description = "Index of the first data to display.")
    int getFirst();

    int getRowsToRender();

    @Property(description = "Template of the rowsPerPage dropdown.")
    String getRowsPerPageTemplate();

}
