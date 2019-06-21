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

import org.primefaces.component.api.Pageable;
import org.primefaces.component.inputtext.InputTextRenderer;

public interface PaginatorElementRenderer {

    String PAGINATOR_PAGES_CLASS = "ui-paginator-pages";
    String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-state-default ui-corner-all";
    String PAGINATOR_ACTIVE_PAGE_CLASS = "ui-paginator-page ui-state-default ui-state-active ui-corner-all";
    String PAGINATOR_CURRENT_CLASS = "ui-paginator-current";
    String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left";
    String PAGINATOR_RPP_LABEL_CLASS = "ui-paginator-rpp-label ui-helper-hidden";
    String PAGINATOR_JTP_SELECT_CLASS = "ui-paginator-jtp-select ui-widget ui-state-default ui-corner-left";
    String PAGINATOR_JTP_INPUT_CLASS = "ui-paginator-jtp-input " + InputTextRenderer.STYLE_CLASS;
    String PAGINATOR_FIRST_PAGE_LINK_CLASS = "ui-paginator-first ui-state-default ui-corner-all";
    String PAGINATOR_FIRST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-first";
    String PAGINATOR_PREV_PAGE_LINK_CLASS = "ui-paginator-prev ui-state-default ui-corner-all";
    String PAGINATOR_PREV_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-prev";
    String PAGINATOR_NEXT_PAGE_LINK_CLASS = "ui-paginator-next ui-state-default ui-corner-all";
    String PAGINATOR_NEXT_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-next";
    String PAGINATOR_LAST_PAGE_LINK_CLASS = "ui-paginator-last ui-state-default ui-corner-all";
    String PAGINATOR_LAST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-end";

    String ARIA_FIRST_PAGE_LABEL = "primefaces.paginator.aria.FIRST_PAGE";
    String ARIA_PREVIOUS_PAGE_LABEL = "primefaces.paginator.aria.PREVIOUS_PAGE";
    String ARIA_NEXT_PAGE_LABEL = "primefaces.paginator.aria.NEXT_PAGE";
    String ARIA_LAST_PAGE_LABEL = "primefaces.paginator.aria.LAST_PAGE";
    String ROWS_PER_PAGE_LABEL = "primefaces.paginator.aria.ROWS_PER_PAGE";

    void render(FacesContext context, Pageable pageable) throws IOException;
}
