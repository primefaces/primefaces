/*
 * Copyright 2009-2011 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.api;

public class UIData extends javax.faces.component.UIData {

    public static final String PAGINATOR_TOP_CONTAINER_CLASS = "ui-paginator ui-paginator-top ui-widget-header"; 
    public static final String PAGINATOR_BOTTOM_CONTAINER_CLASS = "ui-paginator ui-paginator-bottom ui-widget-header"; 
    public static final String PAGINATOR_PAGES_CLASS = "ui-paginator-pages"; 
    public static final String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_CURRENT_CLASS = "ui-paginator-current"; 
    public static final String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options"; 
    public static final String PAGINATOR_JTP_CLASS = "ui-paginator-jtp-select"; 
    public static final String PAGINATOR_FIRST_PAGE_LINK_CLASS = "ui-paginator-first ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_FIRST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-first"; 
    public static final String PAGINATOR_PREV_PAGE_LINK_CLASS = "ui-paginator-prev ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_PREV_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-prev"; 
    public static final String PAGINATOR_NEXT_PAGE_LINK_CLASS = "ui-paginator-next ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_NEXT_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-next"; 
    public static final String PAGINATOR_END_PAGE_LINK_CLASS = "ui-paginator-end ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_END_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-end"; 
    
    protected enum PropertyKeys {
        paginator
		,paginatorTemplate
		,rowsPerPageTemplate
		,currentPageReportTemplate
		,pageLinks
		,paginatorPosition
		,paginatorAlwaysVisible
		,page;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {}

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
	}
    
    public boolean isPaginator() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
	}
	public void setPaginator(boolean _paginator) {
		getStateHelper().put(PropertyKeys.paginator, _paginator);
	}

	public java.lang.String getPaginatorTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorTemplate, null);
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

	public java.lang.String getCurrentPageReportTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, null);
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

	public int getPage() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.page, 1);
	}
	public void setPage(int _page) {
		getStateHelper().put(PropertyKeys.page, _page);
	}
}
