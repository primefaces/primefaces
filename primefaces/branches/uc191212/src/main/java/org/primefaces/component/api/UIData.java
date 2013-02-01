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

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.*;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.model.*;
import javax.faces.render.Renderer;
import org.primefaces.util.ComponentUtils;

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
    
    private String clientId = null;
    private StringBuilder idBuilder = new StringBuilder();
    private DataModel model = null;
    
    protected enum PropertyKeys {
        paginator
		,paginatorTemplate
		,rowsPerPageTemplate
		,currentPageReportTemplate
		,pageLinks
		,paginatorPosition
		,paginatorAlwaysVisible
        ,rowIndex
        ,rowIndexVar
        ,saved
        ,lazy;

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
    
    public boolean isLazy() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.lazy, false);
	}
	public void setLazy(boolean _lazy) {
		getStateHelper().put(PropertyKeys.lazy, _lazy);
	}
    
    public java.lang.String getRowIndexVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowIndexVar, null);
	}
	public void setRowIndexVar(java.lang.String _rowIndexVar) {
		getStateHelper().put(PropertyKeys.rowIndexVar, _rowIndexVar);
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
        if(this.getRowCount() > 0) {
            int rows = this.getRowsToRender();
        
            if(rows > 0) {
                int first = this.getFirst();

                return (int) (first / rows);
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
        return (int) Math.ceil(this.getRowCount() * 1d / this.getRowsToRender());
    }
    
    public int getRowsToRender() {
        int rows = this.getRows();
        
        return rows == 0 ? this.getRowCount() : rows;
    }
        
    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }
    
    public void updatePaginationData(FacesContext context, UIData data) {
        data.setRowIndex(-1);
        String componentClientId = data.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ELContext elContext = context.getELContext();
        
		String firstParam = params.get(componentClientId + "_first");
		String rowsParam = params.get(componentClientId + "_rows");

		data.setFirst(Integer.valueOf(firstParam));
		data.setRows(Integer.valueOf(rowsParam));
        
        ValueExpression firstVe = data.getValueExpression("first");
        ValueExpression rowsVe = data.getValueExpression("rows");

        if(firstVe != null && !firstVe.isReadOnly(elContext))
            firstVe.setValue(context.getELContext(), data.getFirst());
        if(rowsVe != null && !rowsVe.isReadOnly(elContext))
            rowsVe.setValue(context.getELContext(), data.getRows());
    }
    
    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
        pushComponentToEL(context, this);
        processPhase(context, PhaseId.APPLY_REQUEST_VALUES);
        decode(context);
        popComponentFromEL(context);
    }
    
    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
        pushComponentToEL(context, this);
        Application app = context.getApplication();
        app.publishEvent(context, PreValidateEvent.class, this);
        processPhase(context, PhaseId.PROCESS_VALIDATIONS);
        app.publishEvent(context, PostValidateEvent.class, this);
        popComponentFromEL(context);
    }
    
    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
        pushComponentToEL(context, this);
        processPhase(context, PhaseId.UPDATE_MODEL_VALUES);
        popComponentFromEL(context);
    }
    
    protected void processPhase(FacesContext context, PhaseId phaseId) {
        setRowIndex(-1);
        processFacets(context, phaseId);
        processChildrenFacets(context, phaseId);
        processChildren(context, phaseId);
        setRowIndex(-1);
    }
    
    protected void processFacets(FacesContext context, PhaseId phaseId) {
        if(this.getFacetCount() > 0) {
            for(UIComponent facet : getFacets().values()) {
                process(context, facet, phaseId);
            }
        }
    }
    
    protected void processChildrenFacets(FacesContext context, PhaseId phaseId) {
        for(UIComponent child : this.getChildren()) {
            if(child.isRendered() && (child.getFacetCount() > 0)) {
                for(UIComponent facet : child.getFacets().values()) {
                    process(context, facet, phaseId);
                }
            }
        }
    }
    
    protected void processChildren(FacesContext context, PhaseId phaseId) {
        int first = getFirst();
        int rows = getRows();
        int last = rows == 0 ? getRowCount() : (first + rows);
        
        for(int rowIndex = first; rowIndex < last; rowIndex++) {
            setRowIndex(rowIndex);

            if(!isRowAvailable()) {
                break;
            }
            
            for(UIComponent child : this.getChildren()) {
                if(child.isRendered()) {
                    process(context, child, phaseId);
                }
            }            
        }
    }
    
    protected void process(FacesContext context, UIComponent component, PhaseId phaseId) {
        if(!shouldProcessChildren(context)) {
            return;
        }
        
        if(phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            component.processDecodes(context);
        }
        else if(phaseId == PhaseId.PROCESS_VALIDATIONS) {
            component.processValidators(context);
        }
        else if(phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            component.processUpdates(context);
        }
        
    }
    
    @Override
    public String getClientId(FacesContext context) {
        if(this.clientId != null) {
            return this.clientId;
        }

        String id = getId();
        if(id == null) {
            UniqueIdVendor parentUniqueIdVendor = ComponentUtils.findParentUniqueIdVendor(this);
            
            if(parentUniqueIdVendor == null) {
                UIViewRoot viewRoot = context.getViewRoot();
                
                if(viewRoot != null) {
                    id = viewRoot.createUniqueId();
                }
                else {
                    throw new FacesException("Cannot create clientId for " + this.getClass().getCanonicalName());
                }
            }
            else {
                id = parentUniqueIdVendor.createUniqueId(context, null);
            }
            
            this.setId(id);
        }

        UIComponent namingContainer = ComponentUtils.findParentNamingContainer(this);
        if(namingContainer != null) {
            String containerClientId = namingContainer.getContainerClientId(context);
            
            if(containerClientId != null) {                
                this.clientId = this.idBuilder.append(containerClientId).append(UINamingContainer.getSeparatorChar(context)).append(id).toString();
                this.idBuilder.setLength(0);
            }
            else
            {
                this.clientId = id;
            }
        }
        else
        {
            this.clientId = id;
        }

        Renderer renderer = getRenderer(context);
        if(renderer != null) {
            this.clientId = renderer.convertClientId(context, this.clientId);
        }

        return this.clientId;
    }
    
    @Override
    public String getContainerClientId(FacesContext context) {
        //clientId is without rowIndex
        String componentClientId = this.getClientId(context);
        
        int rowIndex = getRowIndex();
        if(rowIndex == -1) {
            return componentClientId;
        }

        String containerClientId = idBuilder.append(componentClientId).append(UINamingContainer.getSeparatorChar(context)).append(rowIndex).toString();
        idBuilder.setLength(0);
        
        return containerClientId;
    }
    
    @Override
    public void setId(String id) {
        super.setId(id);

        //clear
        this.clientId = null;
    }
    
    @Override
    public void setRowIndex(int rowIndex) {
        saveDescendantState();

        setRowModel(rowIndex);

        restoreDescendantState();
    }
        
    public void setRowModel(int rowIndex) {
        //update rowIndex
        getStateHelper().put(PropertyKeys.rowIndex, rowIndex);
        getDataModel().setRowIndex(rowIndex);
        
        //clear datamodel
        if(rowIndex == -1) {
            setDataModel(null);
        }
        
        //update var
        String var = (String) this.getVar();
        String rowIndexVar = this.getRowIndexVar();
        if(var != null) {
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            
            if(isRowAvailable()) {
                requestMap.put(var, getRowData());
                
                if(rowIndexVar != null)
                    requestMap.put(rowIndexVar, rowIndex);
            }
            else {
                requestMap.remove(var);
                
                if(rowIndexVar != null)
                    requestMap.remove(rowIndexVar);
            }
        }
    }
    
    @Override
    public int getRowIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.rowIndex, -1);
    }
    
    protected void saveDescendantState() {
        FacesContext context = getFacesContext();
        
        if(getChildCount() > 0) {
            for(UIComponent kid : getChildren()) {
                saveDescendantState(kid, context);
            }
        }
    }
    
    protected void saveDescendantState(UIComponent component, FacesContext context) {
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);
        
        if(component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            SavedState state = null;
            String componentClientId = component.getClientId(context);
            
            if(saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, componentClientId, state);
            }
            
            if(state == null) {
                state = saved.get(componentClientId);
                
                if(state == null) {
                    state = new SavedState();
                    getStateHelper().put(PropertyKeys.saved, componentClientId, state);
                }
            }
            
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        } 
        else if (component instanceof UIForm) {
            UIForm form = (UIForm) component;
            String componentClientId = component.getClientId(context);
            SavedState state = null;
            if (saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, componentClientId, state);
            }
            
            if (state == null) {
                state = saved.get(componentClientId);
                if (state == null) {
                    state = new SavedState();
                    //saved.put(clientId, state);
                    getStateHelper().put(PropertyKeys.saved, componentClientId, state);
                }
            }
            state.setSubmitted(form.isSubmitted());
        }

        //save state for children
        if(component.getChildCount() > 0) {
            for(UIComponent kid : component.getChildren()) {
                saveDescendantState(kid, context);
            }
        }

        //save state for facets
        if(component.getFacetCount() > 0) {
            for(UIComponent facet : component.getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }

    }
    
    protected void restoreDescendantState() {
        FacesContext context = getFacesContext();
        
        if(getChildCount() > 0) {
            for(UIComponent kid : getChildren()) {
                restoreDescendantState(kid, context);
            }
        }
    }


    protected void restoreDescendantState(UIComponent component, FacesContext context) {
        String id = component.getId();
        component.setId(id); //reset the client id
        Map<String, SavedState> saved = (Map<String,SavedState>) getStateHelper().get(PropertyKeys.saved);
        
        if(component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String componentClientId = component.getClientId(context);

            SavedState state = saved.get(componentClientId);
            if(state == null) {
                state = new SavedState();
            }
            
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        } 
        else if (component instanceof UIForm) {
            UIForm form = (UIForm) component;
            String componentClientId = component.getClientId(context);
            SavedState state = saved.get(componentClientId);
            if(state == null) {
                state = new SavedState();
            }
            
            form.setSubmitted(state.getSubmitted());
            state.setSubmitted(form.isSubmitted());
        }

        //restore state of children
        if(component.getChildCount() > 0) {
            for(UIComponent kid : component.getChildren()) {
                restoreDescendantState(kid, context);
            }
        }

        //restore state of facets
        if(component.getFacetCount() > 0) {
            for(UIComponent facet : component.getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }

    }
    
    @Override
    protected DataModel getDataModel() {
        if(this.model != null) {
            return (model);
        }

        Object current = getValue();
        if(current == null) {
            setDataModel(new ListDataModel(Collections.EMPTY_LIST));
        } 
        else if (current instanceof DataModel) {
            setDataModel((DataModel) current);
        } 
        else if (current instanceof List) {
            setDataModel(new ListDataModel((List) current));
        } 
        else if (Object[].class.isAssignableFrom(current.getClass())) {
            setDataModel(new ArrayDataModel((Object[]) current));
        } 
        else if (current instanceof ResultSet) {
            setDataModel(new ResultSetDataModel((ResultSet) current));
        } 
        else {
            setDataModel(new ScalarDataModel(current));
        }
        
        return model;
    }

    @Override
    protected void setDataModel(DataModel dataModel) {
        this.model = dataModel;
    }
    
    protected boolean shouldProcessChildren(FacesContext context) {
        return true;
    }
    
    protected boolean shouldVisitChildren(VisitContext context, boolean visitRows) {
        if(visitRows) {
            setRowIndex(-1);
        }
        
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);

        return (!idsToVisit.isEmpty());
    }
    
    @Override
    public boolean visitTree(VisitContext context,  VisitCallback callback) {
        if(!isVisitable(context)) {
            return false;
        }

        FacesContext facesContext = context.getFacesContext();
        boolean visitRows = shouldVisitRows(facesContext, context);

        int rowIndex = -1;
        if(visitRows) {
            rowIndex = getRowIndex();
            setRowIndex(-1);
        }

        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if(result == VisitResult.COMPLETE) {
                return true;
            }

            if((result == VisitResult.ACCEPT) && shouldVisitChildren(context, visitRows)) {
                if(visitFacets(context, callback, visitRows)) {
                    return true;
                }

                if(visitColumnsAndColumnFacets(context, callback, visitRows)) {
                    return true;
                }

                if(visitRows(context, callback, visitRows)) {
                    return true;
                }
                
            }
        }
        finally {
            popComponentFromEL(facesContext);
            
            if(visitRows) {
                setRowIndex(rowIndex);
            }
        }

        return false;
    }
    
    protected boolean visitFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if(visitRows) {
            setRowIndex(-1);
        }
        
        if(getFacetCount() > 0) {
            for(UIComponent facet : getFacets().values()) {
                if(facet.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    protected boolean visitColumnsAndColumnFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if(visitRows) {
            setRowIndex(-1);
        }
        
        if(getChildCount() > 0) {
            for(UIComponent column : getChildren()) {
                VisitResult result = context.invokeVisitCallback(column, callback); // visit the column directly
                if (result == VisitResult.COMPLETE) {
                    return true;
                }
                    
                if(column.getFacetCount() > 0) {
                    for(UIComponent columnFacet : column.getFacets().values()) {
                        if(columnFacet.visitTree(context, callback)) {
                            return true;
                        }
                    }
                }
                
            }
        }

        return false;
    }
    
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        int processed = 0;
        int rowIndex = 0;
        int rows = 0;
        if (visitRows) {
            rowIndex = getFirst() - 1;
            rows = getRows();
        }

        while(true) {
            if(visitRows) {
                if((rows > 0) && (++processed > rows)) {
                    break;
                }

                setRowIndex(++rowIndex);
                if(!isRowAvailable()) {
                    break;
                }
            }

            if(getChildCount() > 0) {
                for(UIComponent kid : getChildren()) {

                    if(kid.getChildCount() > 0) {
                        for(UIComponent grandkid : kid.getChildren()) {
                            if(grandkid.visitTree(context, callback)) {
                                return true;
                            }
                        }
                    }
                }
            }

            if(!visitRows) {
                break;
            }

        }

        return false;
    }
    
    protected boolean shouldVisitRows(FacesContext context, VisitContext visitContext) {
        try {
            //JSF 2.1
            VisitHint skipHint = VisitHint.valueOf("SKIP_ITERATION");
            return !visitContext.getHints().contains(skipHint);
        }
        catch(IllegalArgumentException e) {
            //JSF 2.0
            Object skipHint = context.getAttributes().get("javax.faces.visit.SKIP_ITERATION");
            return !Boolean.TRUE.equals(skipHint);
        }
    }
}

