import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.row.Row;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data";
    public static final String EMPTY_DATA_CLASS = "ui-datatable-data-empty";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header ui-corner-tl ui-corner-tr";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header ui-corner-bl ui-corner-br";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter";
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
    public static final String SCROLLABLE_HEADER_CLASS = "ui-datatable-scrollable-header";
    public static final String SCROLLABLE_BODY_CLASS = "ui-datatable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-datatable-scrollable-footer";
    public static final String COLUMN_RESIZER_CLASS = "ui-column-resizer";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page","sort","filter", "selectRow", "unselectRow", "rowEdit"));

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
         } else {
            //single header row
            for(Column column : getColumns()) {
               if(column.getValueExpression("filterBy") != null) {
                  filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
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
			return selectionMode.equalsIgnoreCase("single") || selectionMode.equalsIgnoreCase("singlecell");
		else if(columnSelectionMode != null)
			return columnSelectionMode.equalsIgnoreCase("single");
        else
            return false;
	}

    @Override
    public void processDecodes(FacesContext context) {
		if(isRequestSource(context)) {
            this.decode(context);
            
            if(isSelectionEnabled()) {
                this.updateSelectionModel(context);
            }

            if(isPaginator()) {
                updatePaginationMetadata(context);
            }
            
            context.renderResponse();
        } else {
            super.processDecodes(context);
        }
	}

    @Override
    public void processUpdates(FacesContext context) {
		super.processUpdates(context);

        updateSelectionModel(context);
	}

    protected void updateSelectionModel(FacesContext context) {
        ValueExpression selectionVE = this.getValueExpression("selection");
        
        if(selectionVE != null) {
            selectionVE.setValue(context.getELContext(), this.getLocalSelection());

            this.setSelection(null);
        }
    }

    protected void updatePaginationMetadata(FacesContext context) {
        ValueExpression firstVe = this.getValueExpression("first");
        ValueExpression rowsVe = this.getValueExpression("rows");
        ValueExpression pageVE = this.getValueExpression("page");

        if(firstVe != null)
            firstVe.setValue(context.getELContext(), getFirst());
        if(rowsVe != null)
            rowsVe.setValue(context.getELContext(), getRows());
        if(pageVE != null)
            pageVE.setValue(context.getELContext(), getPage());
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

            if(eventName.equals("selectRow")) {
                int selectedRowIndex = Integer.parseInt(params.get(clientId + "_instantSelectedRowIndex"));
                this.setRowIndex(selectedRowIndex);
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData());  
            }
            else if(eventName.equals("unselectRow")) {
                int unselectedRowIndex = Integer.parseInt(params.get(clientId + "_instantUnselectedRowIndex"));
                this.setRowIndex(unselectedRowIndex);
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), this.getRowData());
            }
            else if(eventName.equals("page")) {
                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), this.getPage());
            }
            else if(eventName.equals("sort")) {
                boolean asc = Boolean.valueOf(params.get(clientId + "_sortDir"));
                Column sortColumn = findColumn(params.get(clientId + "_sortKey"));

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, asc);
            }
            else if(eventName.equals("filter")) {
                wrapperEvent = event;
            }

            wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);

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
        LazyDataModel model = (LazyDataModel) getDataModel();
        model.setPageSize(getRows());

        List<?> data = model.load(getFirst(), getRows(), getSortField(), getSortOrder(), getFilters());

        model.setWrappedData(data);
    }

    public void clearLazyCache() {
        LazyDataModel model = (LazyDataModel) getDataModel();
        model.setWrappedData(null);
    }

    public String getSortField() {
        return (String) getStateHelper().eval("sortField", null);
    }

    public void setSortField(String sortField) {
        getStateHelper().put("sortField", sortField);
    }

    public boolean getSortOrder() {
        return (Boolean) getStateHelper().eval("sortOrder", true);
    }

    public void setSortOrder(boolean sortOrder) {
        getStateHelper().put("sortOrder", sortOrder);
    }

    public Map<String,String> getFilters() {
        return (Map<String,String>) getStateHelper().eval("filters", new HashMap<String,String>());
    }

    public void setFilters(Map<String,String> filters) {
        getStateHelper().put("filters", filters);
    }

    private List<Integer> selectedRowIndexes = new ArrayList<Integer>();

    public void addSelectedRowIndex(Integer rowIndex) {
        selectedRowIndexes.add(rowIndex);
    }

    public List<Integer> getSelectedRowIndexes() {
        return selectedRowIndexes;
    }

    public String getSelectedRowIndexesAsString() {
        StringBuilder sb = new StringBuilder();
        for(Iterator<Integer> iter = selectedRowIndexes.iterator(); iter.hasNext();) {
            sb.append(iter.next());

            if(iter.hasNext())
                sb.append(",");
        }

        return sb.toString();
    }

    public boolean isCellSelection() {
		String selectionMode = this.getSelectionMode();

		if(selectionMode != null)
			return selectionMode.indexOf("cell") != -1;
		else
			return false;
	}

    public void resetValue() {
        setValue(null);
    }

    public void resetPagination() {
        setFirst(0);
        setPage(1);
    }

    public void reset() {
        resetValue();
        resetPagination();
    }

    public void calculatePage() {
        int rows = this.getRows();
        int currentPage = this.getPage();
        int numberOfPages = (int) Math.ceil(this.getRowCount() * 1d / rows);

        if(currentPage > numberOfPages && numberOfPages > 0) {
            currentPage = numberOfPages;

            this.setPage(currentPage);
            this.setFirst((currentPage-1) * rows);
        }
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

    @Override
    public Object getValue() {
        return filteredData != null ? filteredData : super.getValue();
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