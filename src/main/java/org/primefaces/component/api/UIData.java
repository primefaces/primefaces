/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;

public class UIData extends javax.faces.component.UIData {

    public static final String PAGINATOR_TOP_CONTAINER_CLASS = "ui-paginator ui-paginator-top ui-widget-header"; 
    public static final String PAGINATOR_BOTTOM_CONTAINER_CLASS = "ui-paginator ui-paginator-bottom ui-widget-header"; 
    public static final String PAGINATOR_PAGES_CLASS = "ui-paginator-pages"; 
    public static final String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_ACTIVE_PAGE_CLASS = "ui-paginator-page ui-state-default ui-state-active ui-corner-all"; 
    public static final String PAGINATOR_CURRENT_CLASS = "ui-paginator-current"; 
    public static final String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left"; 
    public static final String PAGINATOR_JTP_CLASS = "ui-paginator-jtp-select ui-widget ui-state-default ui-corner-left"; 
    public static final String PAGINATOR_FIRST_PAGE_LINK_CLASS = "ui-paginator-first ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_FIRST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-first"; 
    public static final String PAGINATOR_PREV_PAGE_LINK_CLASS = "ui-paginator-prev ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_PREV_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-prev"; 
    public static final String PAGINATOR_NEXT_PAGE_LINK_CLASS = "ui-paginator-next ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_NEXT_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-next"; 
    public static final String PAGINATOR_LAST_PAGE_LINK_CLASS = "ui-paginator-last ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_LAST_PAGE_ICON_CLASS = "ui-icon ui-icon-seek-end"; 
    
    protected enum PropertyKeys {
        paginator
		,paginatorTemplate
		,rowsPerPageTemplate
		,currentPageReportTemplate
		,pageLinks
		,paginatorPosition
		,paginatorAlwaysVisible;

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
		return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorTemplate, "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
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
    
    public void calculatePage() {
        int rows = this.getRowsToRender();
        
        if(rows > 0) {
            int rowCount = this.getRowCount();
            int first = this.getFirst();
            int currentPage = (int) (first / rows);
            int numberOfPages = (int) Math.ceil(rowCount * 1d / rows);

            if(currentPage > numberOfPages && numberOfPages > 0) {
                currentPage = numberOfPages;

                this.setFirst((currentPage-1) * rows);
            }
        }
    }
    
    public int getPage() {
        int rows = this.getRowsToRender();
        
        if(rows > 0) {
            int first = this.getFirst();
         
            return (int) (first / rows);
        } else {
            return 0;
        }
    }
    
    public int getPageCount() {
        return (int) Math.ceil(this.getRowCount() * 1d / this.getRowsToRender());
    }
    
    public int getRowsToRender() {
        int rows = this.getRows();
        
        return rows == 0 ? this.getRowCount() : rows;
    }
    
    public boolean isLazy() {
        ValueExpression ve = getValueExpression("value");

        if(ve != null) {
            Class type = ve.getType(FacesContext.getCurrentInstance().getELContext());
            if(type != null && LazyDataModel.class.isAssignableFrom(type)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }
    
    public void updatePaginationData(FacesContext context, UIData data) {
        data.setRowIndex(-1);
        String clientId = data.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ELContext elContext = context.getELContext();
        
		String firstParam = params.get(clientId + "_first");
		String rowsParam = params.get(clientId + "_rows");

		data.setFirst(Integer.valueOf(firstParam));
		data.setRows(Integer.valueOf(rowsParam));
        
        ValueExpression firstVe = data.getValueExpression("first");
        ValueExpression rowsVe = data.getValueExpression("rows");

        if(firstVe != null && !firstVe.isReadOnly(elContext))
            firstVe.setValue(context.getELContext(), data.getFirst());
        if(rowsVe != null && !rowsVe.isReadOnly(elContext))
            rowsVe.setValue(context.getELContext(), data.getRows());
    }
}
