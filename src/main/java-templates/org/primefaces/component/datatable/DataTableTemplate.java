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
import org.primefaces.model.SortOrder;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SelectableDataModelWrapper;
import java.lang.reflect.Array;
import javax.faces.model.DataModel;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

    private final static Logger logger = Logger.getLogger(DataTable.class.getName());

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data ui-widget-content";
    public static final String EMPTY_DATA_CLASS = "ui-datatable-data-empty";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter";
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
                                                        "rowUnselect", "rowEdit", "colResize", "toggleCheckAll", "columnReorder"));

    public List<Column> columns;

    public List<Column> getColumns() {        
        if(columns == null) {
            columns = new ArrayList<Column>();

            for(UIComponent child : this.getChildren()) {
                if(child.isRendered() && child instanceof Column) {
                    columns.add((Column) child);
                }
            }
        }

        return columns;
    }

    private Boolean pageRequest = null;
    private Boolean sortRequest = null;
    private Boolean filterRequest = null;
    private Boolean clearFiltersRequest = null;
    
    public boolean isPaginationRequest(FacesContext context) {
        if(pageRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            pageRequest = params.containsKey(this.getClientId(context) + "_paging");
        }

        return pageRequest;
    }

    public boolean isSortRequest(FacesContext context) {
        if(sortRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            sortRequest = params.containsKey(this.getClientId(context) + "_sorting");
        }

        return sortRequest;
    }

    public boolean isFilterRequest(FacesContext context) {
        if(filterRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            filterRequest = params.containsKey(this.getClientId(context) + "_filtering");
        }

        return filterRequest;
    }

    public boolean isGlobalFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_globalFilter");
    }

    public boolean isInstantSelectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantSelectedRowIndex");
    }

    public boolean isInstantUnselectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantUnselectedRowIndex");
    }

    public boolean isRowExpansionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_rowExpansion");
    }

    public boolean isRowEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_rowEdit");
    }

    public boolean isScrollingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_scrolling");
    }

    private Map<String,Column> filterMap;

    public Map<String,Column> getFilterMap() {
      if(filterMap == null) {
         filterMap = new HashMap<String,Column>();

         ColumnGroup group = getColumnGroup("header");
         if(group != null) {
            //column group
            for(UIComponent child : group.getChildren()) {
               Row headerRow = (Row) child;
               
               if(headerRow.isRendered()) {
                   for(UIComponent headerRowChild : headerRow.getChildren()) {
                      Column column= (Column) headerRowChild;

                      if(column.isRendered() && column.getValueExpression("filterBy") != null) {
                         filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
                      }
                   }
               }
            }
         } 
         else {

            for(UIComponent child : getChildren()) {
                if(child instanceof Columns) {
                    Columns columns = (Columns) child;
                    List<?> columnModel = (List<?>) columns.getValue();
                    
                    if(columnModel != null && columns.getValueExpression("filterBy") != null) {
                        for(int i = 0; i < columnModel.size(); i++) {
                            columns.setColIndex(i);
                            
                            filterMap.put(columns.getClientId(FacesContext.getCurrentInstance()) + "_filter", columns);
                        }
                        
                        columns.setColIndex(-1);    //reset
                    }
                }
                else if(child instanceof Column) {
                    Column column = (Column) child;
                    if(column.getValueExpression("filterBy") != null) {
                        filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
                    }
                }
            }
            
         }
      }

      return filterMap;
   }

	public boolean hasFilter() {
		return getFilterMap().size() > 0;
	}

    public boolean isRowSelectionEnabled() {
        return this.getSelectionMode() != null;
	}

    public boolean isColumnSelectionEnabled() {
        return getColumnSelectionMode() != null;
	}

    public String getColumnSelectionMode() {
        for(Column column : getColumns()) {
            String selectionMode = column.getSelectionMode();
            if(selectionMode != null) {
                return selectionMode;
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
    public void processDecodes(FacesContext context) {
		String clientId = getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        if(isBodyUpdate(context)) {
            this.decode(context);
        }
        else if(params.containsKey(clientId + "_rowEditCancel")) {
            context.renderResponse();
        } else {
            super.processDecodes(context);
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

            if(eventName.equals("rowSelect")) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey)); 
            }
            else if(eventName.equals("rowUnselect")) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey));
            }
            else if(eventName.equals("page")) {
                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), this.getPage());
            }
            else if(eventName.equals("sort")) {
                SortOrder order = SortOrder.valueOf(params.get(clientId + "_sortDir"));
                Column sortColumn = findColumn(params.get(clientId + "_sortKey"));

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, order);
            }
            else if(eventName.equals("filter")) {
                wrapperEvent = new FilterEvent(this, behaviorEvent.getBehavior(), getFilteredData(), getFilters());
            }
            else if(eventName.equals("rowEdit")) {
                int editedRowIndex = Integer.parseInt(params.get(clientId + "_editedRowIndex"));
                setRowIndex(editedRowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), this.getRowData());
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if(eventName.equals("toggleCheckAll")) {
                wrapperEvent = behaviorEvent;
            }
            else if(eventName.equals("columnReorder")) {
                wrapperEvent = behaviorEvent;
            }
            
            wrapperEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    private Column findColumn(String clientId) {
        for(Column column : getColumns()) {
            if(column.getClientId().equals(clientId)) {
                return column;
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
        for(Column column : getColumns()) {
            if(column.getFacet("footer") != null || column.getFooterText() != null)
                return true;
        }

        return false;
    }

    public void loadLazyData() {
        DataModel model = getDataModel();
        
        if(model != null && model instanceof LazyDataModel) {            
            LazyDataModel lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(getFirst(), getRows(), resolveSortField(this.getValueExpression("sortBy")), convertSortOrder(), getFilters());
            
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

    protected SortOrder convertSortOrder() {
        String sortOrder = getSortOrder();
        
        if(sortOrder == null)
            return SortOrder.UNSORTED;
        else
            return SortOrder.valueOf(sortOrder.toUpperCase(Locale.ENGLISH));
    }

    protected String resolveSortField(ValueExpression expression) {
        if(expression != null) {
            String expressionString = expression.getExpressionString();
            expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}

            return expressionString.substring(expressionString.indexOf(".") + 1);                //Remove var
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

    public void resetValue() {
        setValue(null);
    }

    public void reset() {
        resetValue();
        setFirst(0);
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

    private List filteredData;

    public void setFilteredData(List list) {
        this.filteredData = list;
    }

    public List getFilteredData() {
        return this.filteredData;
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
        
        if(filteredData != null) {
            if(value instanceof SelectableDataModel) {
                return selectableDataModelWrapper == null 
                                ? (selectableDataModelWrapper = new SelectableDataModelWrapper((SelectableDataModel) value, filteredData))
                                : selectableDataModelWrapper;
            } 
            else {
                return filteredData;
            }
        }
        else {
            return value;
        }
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

        return partialSource != null && this.getClientId(context).startsWith(partialSource);
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
                    if(kid instanceof Column) {
                        columnsCount++;
                    } 
                    else if(kid instanceof Columns) {
                        Columns columns = (Columns) kid;
                        Collection collection = (Collection) columns.getValue();
                        if(collection != null) {
                            columnsCount += collection.size();
                        }
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

    public void setLazy(boolean value) {
        logger.info("Lazy attribute has been removed from datatable api, please also remove it from your page definition. See issue #2993.");
    }
    
    public void syncColumnOrder() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<Column> actualColumns = getColumns();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String[] order = params.get(getClientId(context) + "_columnOrder").split(",");
        UIComponent firstChild = actualColumns.get(0);
        
        if(firstChild instanceof Columns) {
            Columns uicolumns = (Columns) firstChild;
            List<?> model = (List<?>) uicolumns.getValue();
            List orderedModel = new ArrayList();
            
            for(String columnId : order) {
                int colIndex = Integer.parseInt(columnId.split("_colIndex_")[1]);
                
                orderedModel.add(model.get(colIndex));
            }
            
            uicolumns.getValueExpression("value").setValue(context.getELContext(), orderedModel);
        }
        else {
            List<Column> orderedColumns = new ArrayList<Column>();
            for(String columnId : order) {
                for(Column column : actualColumns) {
                    if(columnId.equals(column.getClientId(context))) {
                        orderedColumns.add(column);
                        break;                    
                    }

                }
            }
            
            this.columns = orderedColumns;
        }
    }
    
    public String getColumnIds() {
        StringBuilder builder = new StringBuilder();
        List<Column> cols = getColumns();
        
        for(Iterator<Column> iter = cols.iterator(); iter.hasNext();) {
            Column column = iter.next();
            
            if(column instanceof Columns) {
                Columns uicolumns = (Columns) column;
                List<?> model = (List<?>) uicolumns.getValue();
                int size = model.size();
                
                if(model != null) {
                    for(int i = 0; i < size; i++) {
                        uicolumns.setColIndex(i);
                        
                        builder.append(uicolumns.getClientId());
                        
                        if(i != (size - 1)) {
                            builder.append(",");
                        }
                    }
                }
            }
            else {
                builder.append(iter.next().getClientId());

                if(iter.hasNext()) {
                    builder.append(",");
                }
            }
        }

        return builder.toString();
    }