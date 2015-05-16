/*
 * Copyright 2009-2014 PrimeTek.
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
/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.primefaces.org/elite/license.xhtml
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.api;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.StateManager;
import javax.faces.component.*;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.faces.render.Renderer;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.model.CollectionDataModel;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;

public class UIData extends javax.faces.component.UIData {

    public static final String PAGINATOR_TOP_CONTAINER_CLASS = "ui-paginator ui-paginator-top ui-widget-header"; 
    public static final String PAGINATOR_BOTTOM_CONTAINER_CLASS = "ui-paginator ui-paginator-bottom ui-widget-header"; 
    public static final String PAGINATOR_PAGES_CLASS = "ui-paginator-pages"; 
    public static final String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-state-default ui-corner-all"; 
    public static final String PAGINATOR_ACTIVE_PAGE_CLASS = "ui-paginator-page ui-state-default ui-state-active ui-corner-all"; 
    public static final String PAGINATOR_CURRENT_CLASS = "ui-paginator-current"; 
    public static final String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left"; 
    public static final String PAGINATOR_RPP_LABEL_CLASS = "ui-paginator-rpp-label"; 
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
    private Object oldVar = null;
    private Map<String, Object> _rowDeltaStates = new HashMap<String, Object>();
    private Map<String, Object> _rowTransientStates = new HashMap<String, Object>();
    private Object _initialDescendantFullComponentState = null;
    private Boolean isNested = null;
    
    protected enum PropertyKeys {
        paginator
		,paginatorTemplate
		,rowsPerPageTemplate
        ,rowsPerPageLabel
		,currentPageReportTemplate
		,pageLinks
		,paginatorPosition
		,paginatorAlwaysVisible
        ,rowIndex
        ,rowIndexVar
        ,saved
        ,lazy
        ,rowStatePreserved;

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
    
    public boolean isRowStatePreserved() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.rowStatePreserved, false);
	}
	public void setRowStatePreserved(boolean _paginator) {
		getStateHelper().put(PropertyKeys.rowStatePreserved, _paginator);
	}
    
    public void calculateFirst() {
        int rows = this.getRows();
        
        if(rows > 0) {
            int first = this.getFirst();
            int rowCount = this.getRowCount();
            
            if(rowCount > 0 && first >= rowCount) {
                int numberOfPages = (int) Math.ceil(rowCount * 1d / rows);
                
                this.setFirst(Math.max((numberOfPages-1) * rows, 0));
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
        preDecode(context);
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
        preValidate(context);
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
        preUpdate(context);
        processPhase(context, PhaseId.UPDATE_MODEL_VALUES);
        popComponentFromEL(context);
    }
    
    protected void processPhase(FacesContext context, PhaseId phaseId) {
        if(shouldSkipChildren(context)) {
            return;
        }
        
        setRowIndex(-1);
        processFacets(context, phaseId);
        if(requiresColumns()) {
            processColumnFacets(context, phaseId);
        }
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
    
    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
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
            
            for(UIComponent child : this.getIterableChildren()) {
                if(child.isRendered()) {
                    if(child instanceof Column) {
                        for(UIComponent grandkid : child.getChildren()) {
                            process(context, grandkid, phaseId);
                        }
                    }
                    else {
                        process(context, child, phaseId);
                    }
                }
            }            
        }
    }
    
    protected void process(FacesContext context, UIComponent component, PhaseId phaseId) {
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
            UniqueIdVendor parentUniqueIdVendor = ComponentTraversalUtils.closestUniqueIdVendor(this);
            
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

        UIComponent namingContainer = ComponentTraversalUtils.closestNamingContainer(this);
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
        if(isRowStatePreserved()) {
            setRowIndexRowStatePreserved(rowIndex);
        }
        else {
            setRowIndexWithoutRowStatePreserved(rowIndex);
        }
    }
    
    //Row State preserved implementation is taken from Mojarra
    private void setRowIndexRowStatePreserved(int rowIndex) {
        if(rowIndex < -1) {
            throw new IllegalArgumentException("rowIndex is less than -1");
        }

        if(getRowIndex() == rowIndex) {
            return;
        }

        FacesContext facesContext = getFacesContext();

        if(_initialDescendantFullComponentState != null) {
            //Just save the row
            Map<String, Object> sm = saveFullDescendantComponentStates(facesContext, null, getChildren().iterator(), false);
            if(sm != null && !sm.isEmpty()) {
                _rowDeltaStates.put(getContainerClientId(facesContext), sm);
            }
            
            if(getRowIndex() != -1) {
                _rowTransientStates.put(getContainerClientId(facesContext), saveTransientDescendantComponentStates(facesContext, null, getChildren().iterator(), false));
            }
        }

        // Update to the new row index        
        //this.rowIndex = rowIndex;
        getStateHelper().put(PropertyKeys.rowIndex, rowIndex);
        DataModel localModel = getDataModel();
        localModel.setRowIndex(rowIndex);
        
        // if rowIndex is -1, clear the cache
        if (rowIndex == -1) {
            setDataModel(null);
        }
        
        // Clear or expose the current row data as a request scope attribute
        String var = this.getVar();
        if (var != null) {
            Map<String, Object> requestMap =
                  getFacesContext().getExternalContext().getRequestMap();
            if (rowIndex == -1) {
                oldVar = requestMap.remove(var);
            } else if (isRowAvailable()) {
                requestMap.put(var, getRowData());
            } else {
                requestMap.remove(var);
                if (null != oldVar) {
                    requestMap.put(var, oldVar);
                    oldVar = null;
                }
            }
        }

        if (_initialDescendantFullComponentState != null)
        {
            Object rowState = _rowDeltaStates.get(getContainerClientId(facesContext));
            if (rowState == null)
            {
                //Restore as original
                restoreFullDescendantComponentStates(facesContext, getChildren().iterator(), _initialDescendantFullComponentState, false);
            }
            else
            {
                //Restore first original and then delta
                restoreFullDescendantComponentDeltaStates(facesContext, getChildren().iterator(), rowState, _initialDescendantFullComponentState, false);
            }
            if (getRowIndex() == -1)
            {
                restoreTransientDescendantComponentStates(facesContext, getChildren().iterator(), null, false);
            }
            else
            {
                rowState = _rowTransientStates.get(getContainerClientId(facesContext));
                if (rowState == null)
                {
                    restoreTransientDescendantComponentStates(facesContext, getChildren().iterator(), null, false);
                }
                else
                {
                    restoreTransientDescendantComponentStates(facesContext, getChildren().iterator(), (Map<String, Object>) rowState, false);
                }
            }
        }
    }
    
    private void setRowIndexWithoutRowStatePreserved(int rowIndex) {
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
        if(var != null) {
            String rowIndexVar = this.getRowIndexVar();
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            
            if(rowIndex == -1) {
                oldVar = requestMap.remove(var);
                
                if(rowIndexVar != null)
                    requestMap.remove(rowIndexVar);
            }
            else if(isRowAvailable()) {
                requestMap.put(var, getRowData());
                
                if(rowIndexVar != null)
                    requestMap.put(rowIndexVar, rowIndex);
            }
            else {
                requestMap.remove(var);
                
                if(rowIndexVar != null)
                    requestMap.put(rowIndexVar, rowIndex);
                
                if(oldVar != null) {
                    requestMap.put(var, oldVar);
                    oldVar = null;
                }
            }
        }
    }
    
    @Override
    public int getRowIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.rowIndex, -1);
    }
    
    protected void saveDescendantState() {
        FacesContext context = getFacesContext();
        
        if(this.getChildCount() > 0) {
            for(UIComponent kid : getChildren()) {
                saveDescendantState(kid, context);
            }
        }
        
        if(this.getFacetCount() > 0) {
            for(UIComponent facet : this.getFacets().values()) {
                saveDescendantState(facet, context);
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
        
        if(this.getFacetCount() > 0) {
            for(UIComponent facet : this.getFacets().values()) {
                restoreDescendantState(facet, context);
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
        else if (current instanceof Collection) {
            setDataModel(new CollectionDataModel((Collection) current));
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
    
    protected boolean shouldSkipChildren(FacesContext context) {
        return false;
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

                if(requiresColumns() && visitColumnsAndColumnFacets(context, callback, visitRows)) {
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
            for(UIComponent child : getChildren()) {
                VisitResult result = context.invokeVisitCallback(child, callback); // visit the column directly
                if (result == VisitResult.COMPLETE) {
                    return true;
                }
                    
                if(child instanceof UIColumn) {
                    if(child.getFacetCount() > 0) {
                        if(child instanceof Columns) {
                            Columns columns = (Columns) child;
                            for(int i = 0; i < columns.getRowCount(); i++) {
                                columns.setRowIndex(i);
                                boolean value = visitColumnFacets(context, callback, child);
                                if(value) {
                                    return true;
                                }
                            }
                            columns.setRowIndex(-1);
                        }
                        else {
                            boolean value = visitColumnFacets(context, callback, child);
                            if(value) {
                                return true;
                            }
                        }

                    }
                }
                else if(child instanceof ColumnGroup) {
                    visitColumnGroup(context, callback, (ColumnGroup) child);
                }                
            }
        }

        return false;
    }
    
    protected boolean visitColumnGroup(VisitContext context, VisitCallback callback, ColumnGroup group) {                
        if(group.getChildCount() > 0) {
            for(UIComponent row : group.getChildren()) {
                if(row.getChildCount() > 0) {
                    for(UIComponent col : row.getChildren()) {
                        if(col instanceof Column && col.getFacetCount() > 0) {
                            boolean value = visitColumnFacets(context, callback, (Column) col);
                            if(value) {
                                return true;
                            }
                        }
                    }
                }            
            }
        }
        
        return false;
    }
    
    protected boolean visitColumnFacets(VisitContext context, VisitCallback callback, UIComponent component) {
        for(UIComponent columnFacet : component.getFacets().values()) {
            if(columnFacet.visitTree(context, callback)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        boolean requiresColumns = this.requiresColumns();
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

                    if(requiresColumns) {
                        if(kid instanceof Columns) {
                            Columns uicolumns = (Columns) kid;
                            for(int i = 0; i < uicolumns.getRowCount(); i++) {
                                uicolumns.setRowIndex(i);

                                boolean value = visitColumnContent(context, callback, uicolumns);
                                if(value) {
                                    uicolumns.setRowIndex(-1);
                                    return true;
                                }
                            }

                            uicolumns.setRowIndex(-1);
                        }
                        else {
                            boolean value = visitColumnContent(context, callback, kid);
                            if(value) {
                                return true;
                            }
                        } 
                    }
                    else {
                        if(kid.visitTree(context, callback)) {
                            return true;
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
    
    protected boolean visitColumnContent(VisitContext context, VisitCallback callback, UIComponent component) {
        if(component.getChildCount() > 0) {
            for(UIComponent grandkid : component.getChildren()) {
                if(grandkid.visitTree(context, callback)) {
                    return true;
                }
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
    
    protected boolean requiresColumns() {
        return false;
    }
    
    protected List<UIComponent> getIterableChildren() {
        return this.getChildren();
    }
    
    @Override
    public void markInitialState()
    {
        if (isRowStatePreserved())
        {
            if (getFacesContext().getAttributes().containsKey(StateManager.IS_BUILDING_INITIAL_STATE))
            {
                _initialDescendantFullComponentState = saveDescendantInitialComponentStates(getFacesContext(), getChildren().iterator(), false);
            }
        }
        super.markInitialState();
    }

    private void restoreFullDescendantComponentStates(FacesContext facesContext,
            Iterator<UIComponent> childIterator, Object state,
            boolean restoreChildFacets)
    {
        Iterator<? extends Object[]> descendantStateIterator = null;
        while (childIterator.hasNext())
        {
            if (descendantStateIterator == null && state != null)
            {
                descendantStateIterator = ((Collection<? extends Object[]>) state)
                        .iterator();
            }
            UIComponent component = childIterator.next();

            // reset the client id (see spec 3.1.6)
            component.setId(component.getId());
            if (!component.isTransient())
            {
                Object childState = null;
                Object descendantState = null;
                if (descendantStateIterator != null
                        && descendantStateIterator.hasNext())
                {
                    Object[] object = descendantStateIterator.next();
                    childState = object[0];
                    descendantState = object[1];
                }
                
                component.clearInitialState();
                component.restoreState(facesContext, childState);
                component.markInitialState();
                
                Iterator<UIComponent> childsIterator;
                if (restoreChildFacets)
                {
                    childsIterator = component.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = component.getChildren().iterator();
                }
                restoreFullDescendantComponentStates(facesContext, childsIterator,
                        descendantState, true);
            }
        }
    }

    private Collection<Object[]> saveDescendantInitialComponentStates(FacesContext facesContext,
            Iterator<UIComponent> childIterator, boolean saveChildFacets)
    {
        Collection<Object[]> childStates = null;
        while (childIterator.hasNext())
        {
            if (childStates == null)
            {
                childStates = new ArrayList<Object[]>();
            }

            UIComponent child = childIterator.next();
            if (!child.isTransient())
            {
                // Add an entry to the collection, being an array of two
                // elements. The first element is the state of the children
                // of this component; the second is the state of the current
                // child itself.

                Iterator<UIComponent> childsIterator;
                if (saveChildFacets)
                {
                    childsIterator = child.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = child.getChildren().iterator();
                }
                Object descendantState = saveDescendantInitialComponentStates(
                        facesContext, childsIterator, true);
                Object state = child.saveState(facesContext);
                childStates.add(new Object[] { state, descendantState });
            }
        }
        return childStates;
    }
    
    private Map<String,Object> saveFullDescendantComponentStates(FacesContext facesContext, Map<String,Object> stateMap,
            Iterator<UIComponent> childIterator, boolean saveChildFacets)
    {
        while (childIterator.hasNext())
        {
            UIComponent child = childIterator.next();
            if (!child.isTransient())
            {
                Iterator<UIComponent> childsIterator;
                if (saveChildFacets)
                {
                    childsIterator = child.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = child.getChildren().iterator();
                }
                stateMap = saveFullDescendantComponentStates(facesContext, stateMap,
                        childsIterator, true);
                Object state = child.saveState(facesContext);
                if (state != null)
                {
                    if (stateMap == null)
                    {
                        stateMap = new HashMap<String,Object>();
                    }
                    stateMap.put(child.getClientId(facesContext), state);
                }
            }
        }
        return stateMap;
    }
    
    private void restoreFullDescendantComponentDeltaStates(FacesContext facesContext,
            Iterator<UIComponent> childIterator, Object state, Object initialState,
            boolean restoreChildFacets)
    {
        Map<String,Object> descendantStateIterator = null;
        Iterator<? extends Object[]> descendantFullStateIterator = null;
        while (childIterator.hasNext())
        {
            if (descendantStateIterator == null && state != null)
            {
                descendantStateIterator = (Map<String,Object>) state;
            }
            if (descendantFullStateIterator == null && initialState != null)
            {
                descendantFullStateIterator = ((Collection<? extends Object[]>) initialState).iterator();
            }
            UIComponent component = childIterator.next();

            // reset the client id (see spec 3.1.6)
            component.setId(component.getId());
            if (!component.isTransient())
            {
                Object childInitialState = null;
                Object descendantInitialState = null;
                Object childState = null;
                if (descendantStateIterator != null
                        && descendantStateIterator.containsKey(component.getClientId(facesContext)))
                {
                    //Object[] object = (Object[]) descendantStateIterator.get(component.getClientId(facesContext));
                    //childState = object[0];
                    childState = descendantStateIterator.get(component.getClientId(facesContext));
                }
                if (descendantFullStateIterator != null
                        && descendantFullStateIterator.hasNext())
                {
                    Object[] object = (Object[]) descendantFullStateIterator.next();
                    childInitialState = object[0];
                    descendantInitialState = object[1];
                }
                
                component.clearInitialState();
                if (childInitialState != null)
                {
                    component.restoreState(facesContext, childInitialState);
                    component.markInitialState();
                    component.restoreState(facesContext, childState);
                }
                else
                {
                    component.restoreState(facesContext, childState);
                    component.markInitialState();
                }
                
                Iterator<UIComponent> childsIterator;
                if (restoreChildFacets)
                {
                    childsIterator = component.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = component.getChildren().iterator();
                }
                restoreFullDescendantComponentDeltaStates(facesContext, childsIterator,
                        state, descendantInitialState , true);
            }
        }
    }

    private void restoreTransientDescendantComponentStates(FacesContext facesContext, Iterator<UIComponent> childIterator, Map<String, Object> state,
            boolean restoreChildFacets)
    {
        while (childIterator.hasNext())
        {
            UIComponent component = childIterator.next();

            // reset the client id (see spec 3.1.6)
            component.setId(component.getId());
            if (!component.isTransient())
            {
                component.restoreTransientState(facesContext, (state == null) ? null : state.get(component.getClientId(facesContext)));                    
                
                Iterator<UIComponent> childsIterator;
                if (restoreChildFacets)
                {
                    childsIterator = component.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = component.getChildren().iterator();
                }
                restoreTransientDescendantComponentStates(facesContext, childsIterator, state, true);
            }
        }

    }

    private Map<String, Object> saveTransientDescendantComponentStates(FacesContext facesContext, Map<String, Object> childStates, Iterator<UIComponent> childIterator,
            boolean saveChildFacets)
    {
        while (childIterator.hasNext())
        {
            UIComponent child = childIterator.next();
            if (!child.isTransient())
            {
                Iterator<UIComponent> childsIterator;
                if (saveChildFacets)
                {
                    childsIterator = child.getFacetsAndChildren();
                }
                else
                {
                    childsIterator = child.getChildren().iterator();
                }
                childStates = saveTransientDescendantComponentStates(facesContext, childStates, childsIterator, true);
                Object state = child.saveTransientState(facesContext);
                if (state != null)
                {
                    if (childStates == null)
                    {
                        childStates = new HashMap<String, Object>();
                    }
                    childStates.put(child.getClientId(facesContext), state);
                }
            }
        }
        return childStates;
    }

    @Override
    public void restoreState(FacesContext context, Object state)
    {
        if (state == null)
        {
            return;
        }
        
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        Object restoredRowStates = UIComponentBase.restoreAttachedState(context, values[1]);
        if (restoredRowStates == null)
        {
            if (!_rowDeltaStates.isEmpty())
            {
                _rowDeltaStates.clear();
            }
        }
        else
        {
            _rowDeltaStates = (Map<String, Object>) restoredRowStates;
        } 
    }

    @Override
    public Object saveState(FacesContext context)
    {
        if (initialStateMarked()) {
            Object superState = super.saveState(context);
            
            if (superState == null && _rowDeltaStates.isEmpty()) {
                return null;
            }
            else {
                Object values[] = null;
                Object attachedState = UIComponentBase.saveAttachedState(context, _rowDeltaStates);
                if (superState != null || attachedState != null) {
                    values = new Object[] { superState, attachedState };
                }
                return values; 
            }
        } else {
            Object values[] = new Object[2];
            values[0] = super.saveState(context);
            values[1] = UIComponentBase.saveAttachedState(context, _rowDeltaStates);
            return values;
        }
    }
    
    protected Boolean isNestedWithinIterator() {
        if (isNested == null) {
            UIComponent parent = this;
            while (null != (parent = parent.getParent())) {
                if (parent instanceof javax.faces.component.UIData || parent.getClass().getName().endsWith("UIRepeat")) {
                    isNested = Boolean.TRUE;
                    break;
                }
            }
            if (isNested == null) {
                isNested = Boolean.FALSE;
            }
            return isNested;
        } else {
            return isNested;
        }
    }
    
    protected void preDecode(FacesContext context) {
        setDataModel(null);
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);
        if (null == saved || !keepSaved(context)) {
            getStateHelper().remove(PropertyKeys.saved);
        }
    }
    
    protected void preValidate(FacesContext context) {
        if (isNestedWithinIterator()) {
            setDataModel(null);
        }
    }

    protected void preUpdate(FacesContext context) {
        if (isNestedWithinIterator()) {
            setDataModel(null);
        }
    }
    
    protected void preEncode(FacesContext context) {
        setDataModel(null);
        if (!keepSaved(context)) {
 
            getStateHelper().remove(PropertyKeys.saved);
        }
    }
    
    private boolean keepSaved(FacesContext context) {
        return (contextHasErrorMessages(context) || isNestedWithinIterator());
    }
    
    private boolean contextHasErrorMessages(FacesContext context) {
        FacesMessage.Severity sev = context.getMaximumSeverity();
        return (sev != null && (FacesMessage.SEVERITY_ERROR.compareTo(sev) >= 0));
    }
    
    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        preEncode(context);
        
        if (context == null) {
            throw new NullPointerException();
        }

        pushComponentToEL(context, null);

        if (!isRendered()) {
            return;
        }

        context.getApplication().publishEvent(context, PreRenderComponentEvent.class, this);

        String rendererType = getRendererType();
        if (rendererType != null) {
            Renderer renderer = this.getRenderer(context);
            if (renderer != null) {
                renderer.encodeBegin(context, this);
            }
        }
    }
}
