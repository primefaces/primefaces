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
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.mobile.event.SwipeEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.SortOrder;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SelectableDataModelWrapper;
import java.lang.reflect.Array;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.model.DataModel;
import javax.faces.FacesException;
import javax.faces.component.UINamingContainer;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.DynamicColumn;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostRestoreStateEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.SortMeta;
import org.primefaces.component.datatable.feature.*;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.SharedStringBuilder;

    private final static Logger logger = Logger.getLogger(DataTable.class.getName());

    private static final String SB_GET_SELECTED_ROW_KEYS_AS_STRING = DataTable.class.getName() + "#getSelectedRowKeysAsString";
            
    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String TABLE_WRAPPER_CLASS = "ui-datatable-tablewrapper";
    public static final String REFLOW_CLASS = "ui-datatable-reflow";
    public static final String RTL_CLASS = "ui-datatable-rtl";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String DYNAMIC_COLUMN_HEADER_CLASS = "ui-dynamic-column";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data ui-widget-content";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String SELECTABLE_ROW_CLASS = "ui-datatable-selectable";
    public static final String EMPTY_MESSAGE_ROW_CLASS = "ui-widget-content ui-datatable-empty-message";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header ui-corner-bottom";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String STATIC_COLUMN_CLASS = "ui-static-column";
    public static final String UNSELECTABLE_COLUMN_CLASS = "ui-column-unselectable";
    public static final String HIDDEN_COLUMN_CLASS = "ui-helper-hidden";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_TITLE_CLASS = "ui-column-title";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter ui-widget ui-state-default ui-corner-left";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String COLUMN_CUSTOM_FILTER_CLASS = "ui-column-customfilter";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String EXPANDED_ROW_CLASS = "ui-expanded-row";
    public static final String EXPANDED_ROW_CONTENT_CLASS = "ui-expanded-row-content";
    public static final String ROW_TOGGLER_CLASS = "ui-row-toggler";
    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String CELL_EDITOR_CLASS = "ui-cell-editor";
    public static final String CELL_EDITOR_INPUT_CLASS = "ui-cell-editor-input";
    public static final String CELL_EDITOR_OUTPUT_CLASS = "ui-cell-editor-output";
    public static final String ROW_EDITOR_COLUMN_CLASS = "ui-row-editor-column";
    public static final String ROW_EDITOR_CLASS = "ui-row-editor ui-helper-clearfix";
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
    public static final String SUBTABLE_HEADER = "ui-datatable-subtable-header"; 
    public static final String SUBTABLE_FOOTER = "ui-datatable-subtable-footer"; 
    public static final String SUMMARY_ROW_CLASS = "ui-datatable-summaryrow ui-widget-header";
    public static final String EDITING_ROW_CLASS = "ui-row-editing";
    public static final String STICKY_HEADER_CLASS = "ui-datatable-sticky";
    
    public static final String MOBILE_CONTAINER_CLASS = "ui-datatable ui-shadow";
    public static final String MOBILE_TABLE_CLASS = "ui-responsive ui-table table-stripe";
    public static final String MOBILE_COLUMN_HEADER_CLASS = "ui-column-header";
    public static final String MOBILE_ROW_CLASS = "ui-table-row";
    public static final String MOBILE_SORT_ICON_CLASS = "ui-sortable-column-icon ui-icon-bars ui-btn-icon-notext ui-btn-right";
    public static final String MOBILE_SORT_ICON_ASC_CLASS = "ui-sortable-column-icon ui-icon-arrow-u ui-btn-icon-notext ui-btn-right";
    public static final String MOBILE_SORT_ICON_DESC_CLASS = "ui-sortable-column-icon ui-icon-arrow-d ui-btn-icon-notext ui-btn-right";
    public static final String MOBILE_SORTED_COLUMN_CLASS = "ui-column-sorted";
    public static final String MOBILE_CELL_LABEL = "ui-table-cell-label";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page","sort","filter", "rowSelect", 
                                                        "rowUnselect", "rowEdit", "rowEditInit", "rowEditCancel", "colResize", "toggleSelect", "colReorder", "contextMenu"
                                                        ,"rowSelectRadio", "rowSelectCheckbox", "rowUnselectCheckbox", "rowDblselect", "rowToggle"
                                                        ,"cellEdit", "rowReorder", "swipeleft","swiperight","tap","taphold"));

                                                        
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
        FEATURES.put(DataTableFeatureKey.CELL_EDIT, new CellEditFeature());
        FEATURES.put(DataTableFeatureKey.ROW_EXPAND, new RowExpandFeature());
        FEATURES.put(DataTableFeatureKey.SCROLL, new ScrollFeature());
        FEATURES.put(DataTableFeatureKey.DRAGGABLE_ROWS, new DraggableRowsFeature());
    }
    
    public DataTableFeature getFeature(DataTableFeatureKey key) {
        return FEATURES.get(key);
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
    
    @Override
    public void processValidators(FacesContext context) {
        super.processValidators(context);

        if(this.isFilterRequest(context)) {
            FEATURES.get(DataTableFeatureKey.FILTER).decode(context, this);
        }
	}

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);

        ValueExpression selectionVE = this.getValueExpression("selection");

        if(selectionVE != null) {
            selectionVE.setValue(context.getELContext(), this.getLocalSelection());

            this.setSelection(null);
        }
        
        List<FilterMeta> filterMeta = this.getFilterMetadata();
        if(filterMeta != null && !filterMeta.isEmpty()) {
            ELContext eLContext = context.getELContext();
            for(FilterMeta fm : filterMeta) {
                UIColumn column = fm.getColumn();
                ValueExpression columnFilterValueVE = column.getValueExpression("filterValue");
                if(columnFilterValueVE != null) {
                    if(column.isDynamic()) { 
                        DynamicColumn dynamicColumn = (DynamicColumn) column;
                        dynamicColumn.applyStatelessModel();
                        columnFilterValueVE.setValue(eLContext, fm.getFilterValue());
                        dynamicColumn.cleanStatelessModel();
                    }
                    
                    columnFilterValueVE.setValue(eLContext, fm.getFilterValue());
                }
            }
        }
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
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
                SortOrder order;
                UIColumn sortColumn;
                int sortColumnIndex = 0;

                if(isMultiSort()) {
                    String[] sortDirs = params.get(clientId + "_sortDir").split(",");
                    String[] sortKeys = params.get(clientId + "_sortKey").split(",");
                    
                    order = SortOrder.valueOf(((SortFeature) FEATURES.get(DataTableFeatureKey.SORT)).convertSortOrderParam(sortDirs[sortDirs.length - 1]));
                    sortColumn = findColumn(sortKeys[sortKeys.length - 1]);
                    sortColumnIndex = sortKeys.length - 1;
                } 
                else {
                    order = SortOrder.valueOf(((SortFeature) FEATURES.get(DataTableFeatureKey.SORT)).convertSortOrderParam(params.get(clientId + "_sortDir")));
                    sortColumn = findColumn(params.get(clientId + "_sortKey"));
                }
                
                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, order, sortColumnIndex);
            }
            else if(eventName.equals("filter")) {
                wrapperEvent = new FilterEvent(this, behaviorEvent.getBehavior(), getFilteredValue());
            }
            else if(eventName.equals("rowEdit")||eventName.equals("rowEditCancel")||eventName.equals("rowEditInit")) {
                int rowIndex = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
                setRowIndex(rowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), this.getRowData());
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Double.valueOf(params.get(clientId + "_width")).intValue();
                int height = Double.valueOf(params.get(clientId + "_height")).intValue();;

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if(eventName.equals("toggleSelect")) {
                boolean checked = Boolean.valueOf(params.get(clientId + "_checked"));
                
                wrapperEvent = new ToggleSelectEvent(this, behaviorEvent.getBehavior(), checked);
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
            else if(eventName.equals("cellEdit")) {
                String[] cellInfo = params.get(clientId + "_cellInfo").split(",");
                int rowIndex = Integer.parseInt(cellInfo[0]);
                int cellIndex = Integer.parseInt(cellInfo[1]);
                String rowKey = null;
                if(cellInfo.length == 3) {
                    rowKey = cellInfo[2];
                }
                int i = -1;
                UIColumn column = null;
                
                for(UIColumn col : this.getColumns()) {
                    if(col.isRendered()) {
                        i++;
                        
                        if(i == cellIndex) {
                            column = col;
                            break;
                        }
                    }
                }

                wrapperEvent = new CellEditEvent(this, behaviorEvent.getBehavior(), rowIndex, column, rowKey);
            }
            else if(eventName.equals("rowReorder")) {
                int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
                int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));
                
                wrapperEvent = new ReorderEvent(this, behaviorEvent.getBehavior(), fromIndex, toIndex);
            }
            else if(eventName.equals("swipeleft")||eventName.equals("swiperight")) {
                String rowkey = params.get(clientId + "_rowkey");
                wrapperEvent = new SwipeEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowkey));
            }
            else if(eventName.equals("tap")||eventName.equals("taphold")) {
                String rowkey = params.get(clientId + "_rowkey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowkey));
            }
            
            wrapperEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
    
    public UIColumn findColumn(String clientId) {
        //body columns
        for(UIColumn column : this.getColumns()) {
            if(column.getColumnKey().equals(clientId)) {
                return column;
            }
        }
        
        //header columns        
        if(this.getFrozenColumns() > 0) {
            UIColumn column = findColumnInGroup(clientId, this.getColumnGroup("frozenHeader"));
            if(column == null) {
                column = findColumnInGroup(clientId, this.getColumnGroup("scrollableHeader"));
            }
            
            if(column != null) {
                return column;
            }
        }
        else {
            return findColumnInGroup(clientId, this.getColumnGroup("header"));
        }

        throw new FacesException("Cannot find column with key: " + clientId);
    }
    
    public UIColumn findColumnInGroup(String clientId, ColumnGroup group) {
        FacesContext context = this.getFacesContext();
        
        for(UIComponent row : group.getChildren()) {
            for(UIComponent rowChild : row.getChildren()) {
                if(rowChild instanceof Column) {
                    if(rowChild.getClientId(context).equals(clientId)) {
                        return (UIColumn) rowChild;
                    }
                }
                else if (rowChild instanceof Columns) {
                    Columns uiColumns = (Columns) rowChild;
                    List<DynamicColumn> dynaColumns = uiColumns.getDynamicColumns();
                    for(UIColumn column : dynaColumns) {
                        if(column.getColumnKey().equals(clientId)) {
                            return column;
                        }
                    }
                }
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
            
            List<?> data = null;
            
			// #7176
			calculateFirst();
			
            if(this.isMultiSort()) {
                data = lazyModel.load(getFirst(), getRows(), getMultiSortMeta(), getFilters());
            }
            else {
                data = lazyModel.load(getFirst(), getRows(),  resolveSortField(), convertSortOrder(), getFilters());
            }
            
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
    
     public void loadLazyScrollData(int offset, int rows) {
        DataModel model = getDataModel();
        
        if(model != null && model instanceof LazyDataModel) {            
            LazyDataModel lazyModel = (LazyDataModel) model;
            
            List<?> data = null;
            
            if(this.isMultiSort()) {
                data = lazyModel.load(offset, rows, getMultiSortMeta(), getFilters());
            }
            else {
                data = lazyModel.load(offset, rows, resolveSortField(), convertSortOrder(), getFilters());
            }
            
            lazyModel.setPageSize(rows);
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
        String sortField = null;
        UIColumn column = this.getSortColumn();
        ValueExpression tableSortByVE = this.getValueExpression("sortBy");
        Object tableSortByProperty = this.getSortBy();
        
        if(column == null) {
            String field = this.getSortField();
            if(field == null)
                sortField = (tableSortByVE == null) ? (String) tableSortByProperty : resolveStaticField(tableSortByVE);
            else
                sortField = field;
        }
        else {
            ValueExpression columnSortByVE = column.getValueExpression("sortBy");
            
            if(column.isDynamic()) {
                ((DynamicColumn) sortColumn).applyStatelessModel();
                Object sortByProperty = sortColumn.getSortBy();
                String field = column.getField();
                if(field == null)
                    sortField = (sortByProperty == null) ? resolveDynamicField(columnSortByVE) : sortByProperty.toString();
                else
                    sortField = field;
            }
            else {
                String field = column.getField();
                if(field == null)
                    sortField = (columnSortByVE == null) ? (String) column.getSortBy() : resolveStaticField(columnSortByVE);
                else
                    sortField = field;
            }
        }
        
        return sortField;
    }

    public SortOrder convertSortOrder() {
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
		if (getDataModel() instanceof LazyDataModel) {
			LazyDataModel model = (LazyDataModel) getDataModel();
			model.setWrappedData(null);
		}
    }

    public Map<String,Object> getFilters() {
        return (Map<String,Object>) getStateHelper().eval("filters", new HashMap<String,Object>());
    }

    public void setFilters(Map<String,Object> filters) {
        getStateHelper().put("filters", filters);
    }
    
    public int getScrollOffset() {
        return (java.lang.Integer) getStateHelper().eval("scrollOffset", 0);
    }
    
    public void setScrollOffset(int scrollOffset) {
        getStateHelper().put("scrollOffset", scrollOffset);
    }
    
    private List filterMetadata;
    public List getFilterMetadata() {
        return filterMetadata;
    }
    public void setFilterMetadata(List filterMetadata) {
        this.filterMetadata = filterMetadata;
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
        this.setValueExpression("sortBy", this.getDefaultSortByVE());
        this.setSortFunction(this.getDefaultSortFunction());
        this.setSortOrder(this.getDefaultSortOrder());
        this.setSortByVE(null);
        this.setSortColumn(null);
        this.setSortField(null);
        this.clearMultiSortMeta();
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

    public Object getLocalSelection() {
		return getStateHelper().get(PropertyKeys.selection);
	}

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isRequestSource(FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);

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
        DataModel model = getDataModel();
 
        // use rowKey if available and if != lazy
        // lazy must implement #getRowData
        if (hasRowKeyVe && !(model instanceof LazyDataModel)) {
            Map<String,Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            String var = this.getVar();
            Collection data = (Collection) getDataModel().getWrappedData();

            if(data != null) {
                for(Iterator it = data.iterator(); it.hasNext();) {
                    Object object = it.next();
                    requestMap.put(var, object);

                    if(String.valueOf(this.getRowKey()).equals(rowKey)) {
                        return object;
                    }
                }
            }

            return null;
        }
        else {
            if(!(model instanceof SelectableDataModel)) {
                throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled or you need to define rowKey attribute");
            }

            return ((SelectableDataModel) model).getRowData(rowKey);
        }
    }

    private List<Object> selectedRowKeys = new ArrayList<Object>();

    public void findSelectedRowKeys() {
        Object selection = this.getSelection();
        selectedRowKeys = new ArrayList<Object>();
        boolean hasRowKeyVe = this.getValueExpression("rowKey") != null;
        String var = this.getVar();
        Map<String,Object> requestMap = getFacesContext().getExternalContext().getRequestMap();

        if(isSelectionEnabled() && selection != null) {
            if(this.isSingleSelectionMode()) {
                addToSelectedRowKeys(selection, requestMap, var, hasRowKeyVe);
            } 
            else {
                if(selection.getClass().isArray()) {
                    for(int i = 0; i < Array.getLength(selection); i++) {
                        addToSelectedRowKeys(Array.get(selection, i), requestMap, var, hasRowKeyVe);   
                    }
                }
                else {
                    List<?> list = (List<?>) selection;
                    
                    for(Iterator<? extends Object> it = list.iterator(); it.hasNext();) {
                        addToSelectedRowKeys(it.next(), requestMap, var, hasRowKeyVe);   
                    }
                }
                
            }
            
            requestMap.remove(var);
        }
    }
    
    protected void addToSelectedRowKeys(Object object, Map<String,Object> map, String var, boolean hasRowKey) {
        if(hasRowKey) {
            map.put(var, object);
            Object rowKey = this.getRowKey();
            if (rowKey != null) {
                this.selectedRowKeys.add(rowKey);
            }
        }
        else {
            Object rowKey = this.getRowKeyFromModel(object);
            if (rowKey != null) {
                this.selectedRowKeys.add(rowKey);
            }
        }
    }

    public List<Object> getSelectedRowKeys() {
        return selectedRowKeys;
    }

    public String getSelectedRowKeysAsString() {
        StringBuilder builder = SharedStringBuilder.get(SB_GET_SELECTED_ROW_KEYS_AS_STRING);
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
                        int dynamicColumnsCount = ((Columns) kid).getRowCount();
                        if(dynamicColumnsCount > 0) {
                            columnsCount += dynamicColumnsCount;
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
    
    int columnsCountWithSpan = -1;
    
    public int getColumnsCountWithSpan() {
        if(columnsCountWithSpan == -1) {
            columnsCountWithSpan = 0;

            for(UIComponent kid : getChildren()) {
                if(kid.isRendered()) {
                    if(kid instanceof Columns) {
                        int dynamicColumnsCount = ((Columns) kid).getRowCount();
                        if(dynamicColumnsCount > 0) {
                            columnsCountWithSpan += dynamicColumnsCount;
                        }
                    }
                    else if(kid instanceof Column) {
                        columnsCountWithSpan += ((Column) kid).getColspan();
                    } 
                    else if(kid instanceof SubTable) {
                        SubTable subTable = (SubTable) kid;
                        for(UIComponent subTableKid : subTable.getChildren()) {
                            if(subTableKid.isRendered() && subTableKid instanceof Column) {
                                columnsCountWithSpan += ((Column) subTableKid).getColspan();
                            }
                        }
                    }
                } 
            }
        }

        return columnsCountWithSpan;
    }
    
    private List<UIColumn> columns;
    
    public List<UIColumn> getColumns() {
        if(columns == null) {
            columns = new ArrayList<UIColumn>();
            FacesContext context = getFacesContext();
            char separator = UINamingContainer.getSeparatorChar(context);
            
            for(UIComponent child : this.getChildren()) {
                if(child instanceof Column) {
                    columns.add((UIColumn) child);
                }
                else if(child instanceof Columns) {
                    Columns uiColumns = (Columns) child;
                    String uiColumnsClientId = uiColumns.getClientId(context);
                    
                    for(int i=0; i < uiColumns.getRowCount(); i++) {
                        DynamicColumn dynaColumn = new DynamicColumn(i, uiColumns);
                        dynaColumn.setColumnKey(uiColumnsClientId + separator + i);
                        columns.add(dynaColumn);
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
    protected boolean shouldSkipChildren(FacesContext context) {
        return this.isSkipChildren() || context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_skipChildren");
    }
    
    private UIColumn sortColumn;
    
    public void setSortColumn(UIColumn column) {
        this.sortColumn = column;
        
        if(column == null)
            getStateHelper().remove("sortColumnKey");
        else
            getStateHelper().put("sortColumnKey", column.getColumnKey());
    }
    public UIColumn getSortColumn() {
        if(sortColumn == null) {
            String sortColumnKey = (String) getStateHelper().get("sortColumnKey");
            if(sortColumnKey != null) {
                sortColumn = this.findColumn(sortColumnKey);
            }
        }
        
        return sortColumn;
    }
    
    public boolean isMultiSort() {
        String sortMode = this.getSortMode();
        
        return (sortMode != null && sortMode.equals("multiple"));
    }
    
    private List<SortMeta> multiSortMeta = null;
        
    public List<SortMeta> getMultiSortMeta() {
        if(multiSortMeta != null) {
            return multiSortMeta;
        }
        
        List<MultiSortState> multiSortStateList = (List<MultiSortState>) this.getStateHelper().get("multiSortState");
        if(multiSortStateList != null && !multiSortStateList.isEmpty()) {
            multiSortMeta = new ArrayList<SortMeta>();
            for(int i = 0; i < multiSortStateList.size(); i++) {
                MultiSortState multiSortState = multiSortStateList.get(i);
                SortMeta sortMeta = new SortMeta();
                sortMeta.setSortBy(this.findColumn(multiSortState.getSortKey()));
                sortMeta.setSortField(multiSortState.getSortField());
                sortMeta.setSortOrder(multiSortState.getSortOrder());
                sortMeta.setSortFunction(multiSortState.getSortFunction());

                multiSortMeta.add(sortMeta);
            }
        }
        else {
            ValueExpression ve = this.getValueExpression("sortBy");
            if(ve != null) {
                multiSortMeta = (List<SortMeta>) ve.getValue(getFacesContext().getELContext());
            }
        }
        
        return multiSortMeta;
    }
    
    public void setMultiSortMeta(List<SortMeta> value) {
        this.multiSortMeta = value;
        
        if(value != null && !value.isEmpty()) {
            List<MultiSortState> multiSortStateList = new ArrayList<MultiSortState>();
            for(int i = 0; i < value.size(); i++) {
                multiSortStateList.add(new MultiSortState(value.get(i)));
            }
            
            this.getStateHelper().put("multiSortState", multiSortStateList);
        }
    }
    
    private void clearMultiSortMeta() {
        this.multiSortMeta = null;
        this.getStateHelper().remove("multiSortState");
    }
    
    public boolean isRTL() {
        return this.getDir().equalsIgnoreCase("rtl");
    }
    
    public String resolveSelectionMode() {
        String tableSelectionMode = this.getSelectionMode();
        String columnSelectionMode = this.getColumnSelectionMode();
        String selectionMode = null;

        if(tableSelectionMode != null)
            selectionMode = tableSelectionMode;
        else if(columnSelectionMode != null)
            selectionMode = columnSelectionMode.equals("single") ? "radio" : "checkbox";
            
        return selectionMode;
    }
    
    @Override
    protected boolean requiresColumns() {
        return true;
    }
    
    private Columns dynamicColumns;
    
    public void setDynamicColumns(Columns value) {
        this.dynamicColumns = value;
    }
    public Columns getDynamicColumns() {
        return dynamicColumns;
    }
    
    @Override
    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {  
        if(getChildCount() > 0) {
            for(UIComponent child : this.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof UIColumn) {
                        if(child instanceof Column) {
                            for(UIComponent facet : child.getFacets().values()) {
                                process(context, facet, phaseId);
                            }
                        }
                        else if(child instanceof Columns) {
                            Columns uicolumns = (Columns) child;
                            int f = uicolumns.getFirst();
                            int r = uicolumns.getRows();
                            int l = (r == 0) ? uicolumns.getRowCount() : (f + r);

                            for(int i = f; i < l; i++) {
                                uicolumns.setRowIndex(i);

                                if(!uicolumns.isRowAvailable()) {
                                    break;
                                }

                                for(UIComponent facet : child.getFacets().values()) {
                                    process(context, facet, phaseId);
                                }
                            }

                            uicolumns.setRowIndex(-1);
                        }
                    }
                    else if(child instanceof ColumnGroup) {
                        if(child.getChildCount() > 0) {
                            for(UIComponent columnGroupChild : child.getChildren()) {
                                if(columnGroupChild instanceof Row && columnGroupChild.getChildCount() > 0) {
                                    for(UIComponent rowChild : columnGroupChild.getChildren()) {
                                        if(rowChild instanceof Column && rowChild.getFacetCount() > 0) {
                                            for(UIComponent facet : rowChild.getFacets().values()) {
                                                process(context, facet, phaseId);
                                            }
                                        }
                                        else {
                                            process(context, rowChild, phaseId);        //e.g ui:repeat
                                        }
                                    }
                                }
                                else {
                                    process(context, columnGroupChild, phaseId);        //e.g ui:repeat
                                }         
                            }
                        }
                    }
                }
            }
        }
    }
        
    private ValueExpression sortByVE;
    public void setSortByVE(ValueExpression ve) {
        this.sortByVE = ve;
    }
    public ValueExpression getSortByVE() {
        return this.sortByVE;
    }
    
    public void setDefaultSortByVE(ValueExpression ve) {
        this.setValueExpression("defaultSortBy", ve);
    }
    public ValueExpression getDefaultSortByVE() {
        return this.getValueExpression("defaultSortBy");
    }
        
    public void setDefaultSortOrder(String val) {
        this.getStateHelper().put("defaultSortOrder", val);
    }
    public String getDefaultSortOrder() {
        return (String) this.getStateHelper().get("defaultSortOrder");
    }
    
    public void setDefaultSortFunction(MethodExpression obj) {
        this.getStateHelper().put("defaultSortFunction", obj);
    }
    public MethodExpression getDefaultSortFunction() {
        return (MethodExpression) this.getStateHelper().get("defaultSortFunction");
    }
    
    public Locale resolveDataLocale() {
        FacesContext context = this.getFacesContext();
        Object userLocale = this.getDataLocale();
        
        if(userLocale != null) {
            if(userLocale instanceof String)
                return ComponentUtils.toLocale((String) userLocale);
            else if(userLocale instanceof java.util.Locale)
                return (java.util.Locale) userLocale;
            else
                throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for datatable:" + this.getClientId(context));
        } 
        else {
            return context.getViewRoot().getLocale();
        }
    }
    
    private boolean isFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_filtering");
    }
    
    private List<UIComponent> iterableChildren;
    
    @Override
    protected List<UIComponent> getIterableChildren() {
        if(iterableChildren == null) {
            iterableChildren = new ArrayList<UIComponent>();
            for(UIComponent child : this.getChildren()) {
                if(!(child instanceof ColumnGroup)) {
                    iterableChildren.add(child);
                }
            }
        }
        
        return iterableChildren;
    }
    
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        super.processEvent(event);
        if(!this.isLazy() && event instanceof PostRestoreStateEvent && (this == event.getComponent())) {
            Object filteredValue = this.getFilteredValue();
            if(filteredValue != null) {
                this.updateValue(filteredValue);
            }
        }
    }
    
    public void updateFilteredValue(FacesContext context,  List<?> value) {
        ValueExpression ve = this.getValueExpression("filteredValue");
        
        if(ve != null) {
            ve.setValue(context.getELContext(), value);
        }
        else {            
            this.setFilteredValue(value);
        }
    }
    
    public void updateValue(Object value) {
        Object originalValue = this.getValue();
        if(originalValue instanceof SelectableDataModel)
            this.setValue(new SelectableDataModelWrapper((SelectableDataModel) originalValue, value));
        else
            this.setValue(value);
    }
    
    @Override
    public Object saveState(FacesContext context) {
        if(this.isFilteringEnabled()) {
            this.setValue(null);
        }
    
        return super.saveState(context);
    }
    
    @Override
    protected void preDecode(FacesContext context) {
        resetDynamicColumns();
        super.preDecode(context);
    }
    
    @Override
    protected void preValidate(FacesContext context) {
        resetDynamicColumns();
        super.preValidate(context);
    }
    
    @Override
    protected void preUpdate(FacesContext context) {
        resetDynamicColumns();
        super.preUpdate(context);
    }
    
    @Override
    protected void preEncode(FacesContext context) {
        resetDynamicColumns();
        super.preEncode(context);
    }
    
    private void resetDynamicColumns() {
        Columns dynamicCols = this.getDynamicColumns();
        if(dynamicCols != null && isNestedWithinIterator()) {
            dynamicCols.setRowIndex(-1);
            this.setColumns(null);
        }
    }
        
    
   