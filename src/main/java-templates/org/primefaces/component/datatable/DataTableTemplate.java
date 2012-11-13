import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.contextmenu.ContextMenu;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.context.RequestContext;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.application.NavigationHandler;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import org.primefaces.model.LazyDataModel;
import java.lang.StringBuilder;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import org.primefaces.util.Constants;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.SortOrder;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SelectableDataModelWrapper;
import java.lang.reflect.Array;
import javax.el.ELContext;
import javax.faces.model.DataModel;
import javax.faces.FacesException;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.DynamicColumn;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.feature.*;

    private final static Logger logger = Logger.getLogger(DataTable.class.getName());

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String DYNAMIC_COLUMN_HEADER_CLASS = "ui-dynamic-column";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data ui-widget-content";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String EMPTY_MESSAGE_ROW_CLASS = "ui-widget-content ui-datatable-empty-message";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter ui-widget ui-state-default ui-corner-left";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String EXPANDED_ROW_CLASS = "ui-expanded-row";
    public static final String EXPANDED_ROW_CONTENT_CLASS = "ui-expanded-row-content";
    public static final String ROW_TOGGLER_CLASS = "ui-row-toggler";
    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String CELL_EDITOR_CLASS = "ui-cell-editor";
    public static final String CELL_EDITOR_INPUT_CLASS = "ui-cell-editor-input";
    public static final String CELL_EDITOR_OUTPUT_CLASS = "ui-cell-editor-output";
    public static final String ROW_EDITOR_COLUMN_CLASS = "ui-row-editor-column";
    public static final String ROW_EDITOR_CLASS = "ui-row-editor";
    public static final String SELECTION_COLUMN_CLASS = "ui-selection-column";
    public static final String EVEN_ROW_CLASS = "ui-datatable-even";
    public static final String ODD_ROW_CLASS = "ui-datatable-odd";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-datatable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-datatable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-datatable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-datatable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-datatable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-datatable-scrollable-footer-box";
    public static final String COLUMN_RESIZER_CLASS = "ui-column-resizer";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-datatable-resizable"; 
    public static final String COLUMN_CONTENT_WRAPPER = "ui-dt-c"; 
    public static final String SUBTABLE_HEADER = "ui-datatable-subtable-header"; 
    public static final String SUBTABLE_FOOTER = "ui-datatable-subtable-footer"; 
    public static final String SUMMARY_ROW_CLASS = "ui-datatable-summaryrow ui-widget-header";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page","sort","filter", "rowSelect", 
                                                        "rowUnselect", "rowEdit", "rowEditCancel", "colResize", "toggleSelect", "colReorder", "contextMenu"
                                                        ,"rowSelectRadio", "rowSelectCheckbox", "rowUnselectCheckbox", "rowDblselect", "rowToggle"));

                                                        
    static Map<DataTableFeatureKey,DataTableFeature> FEATURES;
    
    static {
        FEATURES = new HashMap<DataTableFeatureKey,DataTableFeature>();
        FEATURES.put(DataTableFeatureKey.DRAGGABLE_COLUMNS, new DraggableColumnsFeature());
        FEATURES.put(DataTableFeatureKey.FILTER, new FilterFeature());
        FEATURES.put(DataTableFeatureKey.PAGE, new PageFeature());
        FEATURES.put(DataTableFeatureKey.SORT, new SortFeature());
        FEATURES.put(DataTableFeatureKey.RESIZABLE_COLUMNS, new ResizableColumnsFeature());
        FEATURES.put(DataTableFeatureKey.SELECT, new SelectionFeature());
        FEATURES.put(DataTableFeatureKey.ROW_EDIT, new RowEditFeature());
        FEATURES.put(DataTableFeatureKey.ROW_EXPAND, new RowExpandFeature());
        FEATURES.put(DataTableFeatureKey.SCROLL, new ScrollFeature());
    }
    
    public boolean shouldEncodeFeature(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_encodeFeature");
    }
    
    public boolean isRowEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_rowEditAction");
    }
    
    public boolean isRowEditCancelRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(this.getClientId(context) + "_rowEditAction");
        
        return value != null && value.equals("cancel");
    }

    public boolean isRowSelectionEnabled() {
        return this.getSelectionMode() != null;
	}

    public boolean isColumnSelectionEnabled() {
        return getColumnSelectionMode() != null;
	}

    public String getColumnSelectionMode() {
        for(UIComponent child : getChildren()) {
            if(child.isRendered() && (child instanceof Column)) {
                String selectionMode = ((Column) child).getSelectionMode();
                
                if(selectionMode != null) {
                    return selectionMode;
                }
            }
        }

		return null;
	}

    public boolean isSelectionEnabled() {
        return this.isRowSelectionEnabled() || isColumnSelectionEnabled();
	}

    public boolean isSingleSelectionMode() {
		String selectionMode = this.getSelectionMode();
        String columnSelectionMode = this.getColumnSelectionMode();

		if(selectionMode != null)
			return selectionMode.equalsIgnoreCase("single");
		else if(columnSelectionMode != null)
			return columnSelectionMode.equalsIgnoreCase("single");
        else
            return false;
	}

    public void processUpdates(FacesContext context) {
        super.processUpdates(context);

        ValueExpression selectionVE = this.getValueExpression("selection");

        if(selectionVE != null) {
            selectionVE.setValue(context.getELContext(), this.getLocalSelection());

            this.setSelection(null);
        }
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("rowSelect")||eventName.equals("rowSelectRadio")||eventName.equals("contextMenu")
                    ||eventName.equals("rowSelectCheckbox")||eventName.equals("rowDblselect")) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey)); 
            }
            else if(eventName.equals("rowUnselect")||eventName.equals("rowUnselectCheckbox")) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey));
            }
            else if(eventName.equals("page")) {
                int rows = this.getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;
        
                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
            }
            else if(eventName.equals("sort")) {
                SortOrder order = SortOrder.valueOf(params.get(clientId + "_sortDir"));
                Column sortColumn = findColumn(params.get(clientId + "_sortKey"));

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, order);
            }
            else if(eventName.equals("filter")) {
                wrapperEvent = new FilterEvent(this, behaviorEvent.getBehavior(), getFilteredValue(), getFilters());
            }
            else if(eventName.equals("rowEdit")||eventName.equals("rowEditCancel")) {
                int rowIndex = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
                setRowIndex(rowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), this.getRowData());
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if(eventName.equals("toggleSelect")) {
                wrapperEvent = behaviorEvent;
            }
            else if(eventName.equals("colReorder")) {
                wrapperEvent = behaviorEvent;
            }
            else if(eventName.equals("rowToggle")) {
                boolean expansion = params.containsKey(clientId + "_rowExpansion");
                Visibility visibility = expansion ? Visibility.VISIBLE : Visibility.HIDDEN;
                String rowIndex = expansion ? params.get(clientId + "_expandedRowIndex") : params.get(clientId + "_collapsedRowIndex");
                setRowIndex(Integer.parseInt(rowIndex));
                
                wrapperEvent = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility, getRowData());
            }
            
            wrapperEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    public Column findColumn(String clientId) {
        for(UIComponent column : getChildren()) {
            if(column.getClientId().equals(clientId)) {
                return (Column) column;
            }
        }
        
        return null;
    }

    public ColumnGroup getColumnGroup(String target) {
        for(UIComponent child : this.getChildren()) {
            if(child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if(type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }

    public boolean hasFooterColumn() {
        for(UIComponent child : getChildren()) {
            if(child.isRendered() && (child instanceof UIColumn)) {
                UIColumn column = (UIColumn) child;
                
                if(column.getFacet("footer") != null || column.getFooterText() != null)
                    return true; 
            }
            
        }

        return false;
    }

    public void loadLazyData() {
        DataModel model = getDataModel();
        
        if(model != null && model instanceof LazyDataModel) {            
            LazyDataModel lazyModel = (LazyDataModel) model;
            
            List<?> data = lazyModel.load(getFirst(), getRows(), resolveSortField() , convertSortOrder(), getFilters());
            
            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator for callback
            if(this.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();

                if(requestContext != null) {
                    requestContext.addCallbackParam("totalRecords", lazyModel.getRowCount());
                }
            }
        }
    }
    
    protected String resolveSortField() {
        UIColumn column = this.getSortColumn();
        String sortField = null;
        ValueExpression sortVE = this.getValueExpression("sortBy");
        
        if(column == null) {
            sortField = resolveStaticField(sortVE);
        }
        else {
            sortField = (column instanceof DynamicColumn) ? resolveDynamicField(sortVE) : resolveStaticField(sortVE);
        }
        
        return sortField;
    }

    protected SortOrder convertSortOrder() {
        String sortOrder = getSortOrder();
        
        if(sortOrder == null)
            return SortOrder.UNSORTED;
        else
            return SortOrder.valueOf(sortOrder.toUpperCase(Locale.ENGLISH));
    }

    public String resolveStaticField(ValueExpression expression) {
        if(expression != null) {
            String expressionString = expression.getExpressionString();
            expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}

            return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
        }
        else {
            return null;
        }
    }
    
    public String resolveDynamicField(ValueExpression expression) {
        if(expression != null) {
            String expressionString = expression.getExpressionString();
            expressionString = expressionString.substring(expressionString.indexOf("[") + 1, expressionString.indexOf("]"));            
            expressionString = "#{" + expressionString + "}";
            
            FacesContext context = getFacesContext();
            ELContext eLContext = context.getELContext();
            ValueExpression dynaVE = context.getApplication()
                                    .getExpressionFactory().createValueExpression(eLContext, expressionString, String.class);

            return (String) dynaVE.getValue(eLContext);
        }
        else {
            return null;
        }
    }

    public void clearLazyCache() {
        LazyDataModel model = (LazyDataModel) getDataModel();
        model.setWrappedData(null);
    }

    public Map<String,String> getFilters() {
        return (Map<String,String>) getStateHelper().eval("filters", new HashMap<String,String>());
    }

    public void setFilters(Map<String,String> filters) {
        getStateHelper().put("filters", filters);
    }
    
    private boolean reset = false;
    
    public boolean isReset() {
        return reset;
    }

    public void resetValue() {
        setValue(null);
        setFilteredValue(null);
        setFilters(null);
    }

    public void reset() {
        resetValue();
        setFirst(0);
        this.reset = true;
    }

    public boolean isFilteringEnabled() {
        Object value = getStateHelper().get("filtering");

        return value == null ? false : true;
	}
	public void enableFiltering() {
		getStateHelper().put("filtering", true);
	}

    public RowExpansion getRowExpansion() {
        for(UIComponent kid : getChildren()) {
            if(kid instanceof RowExpansion)
                return (RowExpansion) kid;
        }

        return null;
    }

    private SelectableDataModelWrapper selectableDataModelWrapper = null;

    /**
    * Override to support filtering, we return the filtered subset in getValue instead of actual data.
    * In case selectableDataModel is bound, we wrap it with filtered data.
    * 
    */ 
    @Override
    public Object getValue() {
        Object value = super.getValue();
        List<?> filteredValue = this.getFilteredValue();
        
        if(filteredValue != null) {
            if(value instanceof SelectableDataModel) {
                return selectableDataModelWrapper == null 
                                ? (selectableDataModelWrapper = new SelectableDataModelWrapper((SelectableDataModel) value, filteredValue))
                                : selectableDataModelWrapper;
            } 
            else {
                return filteredValue;
            }
        }
        else {
            return value;
        }
    }
    
    public void setSelectableDataModelWrapper(SelectableDataModelWrapper wrapper) {
        this.selectableDataModelWrapper = wrapper;
    }

    public Object getLocalSelection() {
		return getStateHelper().get(PropertyKeys.selection);
	}

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isRequestSource(FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM);

        return partialSource != null && this.getClientId(context).equals(partialSource);
    }

    public boolean isBodyUpdate(FacesContext context) {
        String clientId = this.getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_updateBody");
    }

    public SubTable getSubTable() {
        for(UIComponent kid : getChildren()) {
            if(kid instanceof SubTable)
                return (SubTable) kid;
        }
        
        return null;
    }

    public Object getRowKeyFromModel(Object object) {
        DataModel model = getDataModel();
        if(!(model instanceof SelectableDataModel)) {
            throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled.");
        }
        
        return ((SelectableDataModel) getDataModel()).getRowKey(object);
    }

    public Object getRowData(String rowKey) {
        boolean hasRowKeyVe = this.getValueExpression("rowKey") != null;
        
        if(hasRowKeyVe) {
            Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
            String var = this.getVar();
            Collection data = (Collection) getDataModel().getWrappedData();

            for(Iterator it = data.iterator(); it.hasNext();) {
                Object object = it.next();
                requestMap.put(var, object);

                if(String.valueOf(this.getRowKey()).equals(rowKey)) {
                    return object;
                }
            }
            
            return null;
        } 
        else {
            DataModel model = getDataModel();
            if(!(model instanceof SelectableDataModel)) {
                throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled or you need to define rowKey attribute");
            }

            return ((SelectableDataModel) getDataModel()).getRowData(rowKey);
        }
    }

    private List<Object> selectedRowKeys = new ArrayList<Object>();

    void findSelectedRowKeys() {
        Object selection = this.getSelection();
        selectedRowKeys = new ArrayList<Object>();
        boolean hasRowKeyVe = this.getValueExpression("rowKey") != null;
        String var = this.getVar();
        Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if(isSelectionEnabled() && selection != null) {
            if(this.isSingleSelectionMode()) {
                if(hasRowKeyVe) {
                    requestMap.put(var, selection);
                    selectedRowKeys.add(this.getRowKey());
                }
                else {
                    selectedRowKeys.add(this.getRowKeyFromModel(selection));
                }
            } 
            else {
                for(int i = 0; i < Array.getLength(selection); i++) {
                    if(hasRowKeyVe) {
                        requestMap.put(var, Array.get(selection, i));
                        selectedRowKeys.add(this.getRowKey());
                    }
                    else {
                        selectedRowKeys.add(this.getRowKeyFromModel(Array.get(selection, i)));
                    }    
                }
            }
        }
    }

    List<Object> getSelectedRowKeys() {
        return selectedRowKeys;
    }

    String getSelectedRowKeysAsString() {
        StringBuilder builder = new StringBuilder();
        for(Iterator<Object> iter = getSelectedRowKeys().iterator(); iter.hasNext();) {
            builder.append(iter.next());

            if(iter.hasNext()) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public SummaryRow getSummaryRow() {
        for(UIComponent kid : getChildren()) {
            if(kid.isRendered() && kid instanceof SummaryRow) {
                return (SummaryRow) kid;
            }
        }

        return null;
    }

    private int columnsCount = -1;
    
    public int getColumnsCount() {
        if(columnsCount == -1) {
            columnsCount = 0;

            for(UIComponent kid : getChildren()) {
                if(kid.isRendered()) {
                    if(kid instanceof Columns) {
                        Columns uicolumns = (Columns) kid;
                        Collection collection = (Collection) uicolumns.getValue();
                        if(collection != null) {
                            columnsCount += collection.size();
                        }
                    }
                    else if(kid instanceof Column) {
                        columnsCount++;
                    } 
                    else if(kid instanceof SubTable) {
                        SubTable subTable = (SubTable) kid;
                        for(UIComponent subTableKid : subTable.getChildren()) {
                            if(subTableKid.isRendered() && subTableKid instanceof Column) {
                                columnsCount++;
                            }
                        }
                    }
                } 
            }
        }

        return columnsCount;
    }
    
    private List<UIColumn> columns;
    
    public List<UIColumn> getColumns() {
        if(columns == null) {
            columns = new ArrayList<UIColumn>();
            
            for(UIComponent child : this.getChildren()) {
                if(child instanceof Column) {
                    columns.add((UIColumn) child);
                }
                else if(child instanceof Columns) {
                    Columns uiColumns = (Columns) child;
                    
                    for(int j=0; j < uiColumns.getRowCount(); j++) {
                        columns.add(new DynamicColumn(j, uiColumns));
                    }
                }
            }
        }
        
        return columns;
    }
    
    public void setColumns(List<UIColumn> columns) {
        this.columns = columns;
    }
        
    public String getScrollState() {
        Map<String,String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = this.getClientId() + "_scrollState";
        String value = params.get(name);
        
        return value == null ? "0,0" : value;
    }
    
    @Override
    protected boolean shouldProcessChildren(FacesContext context) {
        return !context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_skipChildren");
    }
    
    private UIColumn sortColumn;
    
    public void setSortColumn(UIColumn column) {
        this.sortColumn = column;
    }
    public UIColumn getSortColumn() {
        return this.sortColumn;
    }