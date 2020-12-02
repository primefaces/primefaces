/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import org.primefaces.component.inputtext.InputText;

import javax.faces.context.FacesContext;
import java.util.Map;

public class UIPageableData extends UIData implements Pageable {

    public static final String PAGINATOR_TOP_CONTAINER_CLASS = "ui-paginator ui-paginator-top ui-widget-header";
    public static final String PAGINATOR_BOTTOM_CONTAINER_CLASS = "ui-paginator ui-paginator-bottom ui-widget-header";
    public static final String PAGINATOR_PAGES_CLASS = "ui-paginator-pages";
    public static final String PAGINATOR_TOP_LEFT_CONTENT_CLASS = "ui-paginator-top-left-content";
    public static final String PAGINATOR_TOP_RIGHT_CONTENT_CLASS = "ui-paginator-top-right-content";
    public static final String PAGINATOR_BOTTOM_LEFT_CONTENT_CLASS = "ui-paginator-bottom-left-content";
    public static final String PAGINATOR_BOTTOM_RIGHT_CONTENT_CLASS = "ui-paginator-bottom-right-content";
    public static final String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-state-default ui-corner-all";
    public static final String PAGINATOR_ACTIVE_PAGE_CLASS = "ui-paginator-page ui-state-default ui-state-active ui-corner-all";
    public static final String PAGINATOR_CURRENT_CLASS = "ui-paginator-current";
    public static final String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left";
    public static final String PAGINATOR_RPP_LABEL_CLASS = "ui-paginator-rpp-label ui-helper-hidden";
    public static final String PAGINATOR_JTP_SELECT_CLASS = "ui-paginator-jtp-select ui-widget ui-state-default ui-corner-left";
    public static final String PAGINATOR_JTP_INPUT_CLASS = "ui-paginator-jtp-input " + InputText.STYLE_CLASS;
    public static final String PAGINATOR_FIRST_PAGE_LINK_CLASS = "ui-paginator-first ui-state-default ui-corner-all";
    public static final String PAGINATOR_FIRST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-first";
    public static final String PAGINATOR_PREV_PAGE_LINK_CLASS = "ui-paginator-prev ui-state-default ui-corner-all";
    public static final String PAGINATOR_PREV_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-prev";
    public static final String PAGINATOR_NEXT_PAGE_LINK_CLASS = "ui-paginator-next ui-state-default ui-corner-all";
    public static final String PAGINATOR_NEXT_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-next";
    public static final String PAGINATOR_LAST_PAGE_LINK_CLASS = "ui-paginator-last ui-state-default ui-corner-all";
    public static final String PAGINATOR_LAST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-end";
    public static final String ARIA_HEADER_LABEL = "primefaces.paginator.aria.HEADER";
    public static final String ARIA_FIRST_PAGE_LABEL = "primefaces.paginator.aria.FIRST_PAGE";
    public static final String ARIA_PREVIOUS_PAGE_LABEL = "primefaces.paginator.aria.PREVIOUS_PAGE";
    public static final String ARIA_NEXT_PAGE_LABEL = "primefaces.paginator.aria.NEXT_PAGE";
    public static final String ARIA_LAST_PAGE_LABEL = "primefaces.paginator.aria.LAST_PAGE";
    public static final String ROWS_PER_PAGE_LABEL = "primefaces.paginator.aria.ROWS_PER_PAGE";

    public enum PropertyKeys {
        paginator,
        paginatorTemplate,
        rowsPerPageTemplate,
        rowsPerPageLabel,
        currentPageReportTemplate,
        pageLinks,
        paginatorPosition,
        paginatorAlwaysVisible,
    }

    public boolean isPaginator() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
    }

    public void setPaginator(boolean _paginator) {
        getStateHelper().put(PropertyKeys.paginator, _paginator);
    }

    public java.lang.String getPaginatorTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorTemplate,
                "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
    }

    public void setPaginatorTemplate(java.lang.String _paginatorTemplate) {
        getStateHelper().put(PropertyKeys.paginatorTemplate, _paginatorTemplate);
    }

    public java.lang.String getRowsPerPageTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageTemplate, null);
    }

    public void setRowsPerPageTemplate(java.lang.String _rowsPerPageTemplate) {
        getStateHelper().put(PropertyKeys.rowsPerPageTemplate, _rowsPerPageTemplate);
    }

    public java.lang.String getRowsPerPageLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageLabel, null);
    }

    public void setRowsPerPageLabel(java.lang.String _rowsPerPageLabel) {
        getStateHelper().put(PropertyKeys.rowsPerPageLabel, _rowsPerPageLabel);
    }

    public java.lang.String getCurrentPageReportTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, "({currentPage} of {totalPages})");
    }

    public void setCurrentPageReportTemplate(java.lang.String _currentPageReportTemplate) {
        getStateHelper().put(PropertyKeys.currentPageReportTemplate, _currentPageReportTemplate);
    }

    public int getPageLinks() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.pageLinks, 10);
    }

    public void setPageLinks(int _pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, _pageLinks);
    }

    public java.lang.String getPaginatorPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorPosition, "both");
    }

    public void setPaginatorPosition(java.lang.String _paginatorPosition) {
        getStateHelper().put(PropertyKeys.paginatorPosition, _paginatorPosition);
    }

    public boolean isPaginatorAlwaysVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginatorAlwaysVisible, true);
    }

    public void setPaginatorAlwaysVisible(boolean _paginatorAlwaysVisible) {
        getStateHelper().put(PropertyKeys.paginatorAlwaysVisible, _paginatorAlwaysVisible);
    }

    public void calculateFirst() {
        int rows = getRows();

        if (rows > 0) {
            int first = getFirst();
            int rowCount = getRowCount();

            if (rowCount > 0 && first >= rowCount) {
                int numberOfPages = (int) Math.ceil(rowCount * 1d / rows);

                setFirst(Math.max((numberOfPages - 1) * rows, 0));
            }
        }
    }

    public int getPage() {
        if (getRowCount() > 0) {
            int rows = getRowsToRender();

            if (rows > 0) {
                int first = getFirst();

                return first / rows;
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public int getPageCount() {
        return (int) Math.ceil(getRowCount() * 1d / getRowsToRender());
    }

    public int getRowsToRender() {
        int rows = getRows();

        return rows == 0 ? getRowCount() : rows;
    }

    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }

    private boolean isRowsPerPageValid(String rowsParam) {

        if (rowsParam == null) {
            return true;
        }

        String rowsPerPageTemplate = getRowsPerPageTemplate();

        if (rowsPerPageTemplate != null) {
            String[] options = rowsPerPageTemplate.split("[,]+");

            for (String option : options) {
                String opt = option.trim();

                if (opt.equals(rowsParam) || (opt.startsWith("{ShowAll|") && "*".equals(rowsParam))) {
                    return true;
                }
            }

            return false;
        }

        int rows = getRows();

        if (rows > 0) {
            return Integer.toString(rows).equals(rowsParam);
        }

        return true;
    }

    public void updatePaginationData(FacesContext context) {
        setRowIndex(-1);
        String componentClientId = getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String firstParam = params.get(componentClientId + "_first");
        String rowsParam = params.get(componentClientId + "_rows");

        if (!isRowsPerPageValid(rowsParam)) {
            throw new IllegalArgumentException("Unsupported rows per page value: " + rowsParam);
        }

        setFirst(Integer.parseInt(firstParam));
        int newRowsValue = "*".equals(rowsParam) ? getRowCount() : Integer.parseInt(rowsParam);
        setRows(newRowsValue);
    }
}
