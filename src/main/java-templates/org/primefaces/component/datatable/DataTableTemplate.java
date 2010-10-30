import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.columngroup.ColumnGroup;
import java.util.List;
import java.util.ArrayList;
import javax.faces.component.UIComponent;
import javax.faces.application.NavigationHandler;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import org.primefaces.model.LazyDataModel;
import java.lang.StringBuilder;

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String DATA_CLASS = "ui-datatable-data";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header ui-corner-tl ui-corner-tr";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header ui-corner-bl ui-corner-br";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter";
    public static final String EXPANDED_ROW_CLASS = "ui-expanded-row";
    public static final String EXPANDED_ROW_CONTENT_CLASS = "ui-expanded-row-content";
    public static final String ROW_TOGGLER_CLASS = "ui-row-toggler";
    public static final String EDITABLE_CELL_CLASS = "ui-editable-cell";
    public static final String CELL_EDITOR_CLASS = "ui-cell-editor";
    public static final String ROW_EDITOR_COLUMN_CLASS = "ui-row-editor-column";
    public static final String ROW_EDITOR_CLASS = "ui-row-editor";
    public static final String SELECTION_COLUMN_CLASS = "ui-selection-column";

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

    public boolean isClearFiltersRequest(FacesContext context) {
        if(clearFiltersRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            clearFiltersRequest = params.containsKey(this.getClientId(context) + "_clearFilters");
        }

        return clearFiltersRequest;
    }

    public boolean isGlobalFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_globalFilter");
    }

    public boolean isDataManipulationRequest(FacesContext context) {
        return isPaginationRequest(context) || isSortRequest(context) || isFilterRequest(context);
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

			for(Column column : getColumns()) {
                if(column.getValueExpression("filterBy") != null) {
                    filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
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

    private boolean emptySelected = false;

    public void processUpdates(FacesContext context) {
		super.processUpdates(context);

        ValueExpression selectionVE = this.getValueExpression("selection");
        if(selectionVE != null) {
            Object value = emptySelected ? null : this.getSelection();

            selectionVE.setValue(getFacesContext().getELContext(), value);

            this.setSelection(null);
        }
	}

    public void setEmptySelected(boolean emptySelected) {
        this.emptySelected = emptySelected;
    }

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext context = FacesContext.getCurrentInstance();
        String outcome = null;
        MethodExpression me = null;
		
		if(event instanceof org.primefaces.event.SelectEvent) {
            me = getRowSelectListener();
        } else if(event instanceof org.primefaces.event.UnselectEvent) {
            me = getRowUnselectListener();
        } else if(event instanceof org.primefaces.event.RowEditEvent) {
            me = getRowEditListener();
        }

        if(me != null) {
            outcome = (String) me.invoke(context.getELContext(), new Object[] {event});
        }

        if(outcome != null) {
            NavigationHandler navHandler = context.getApplication().getNavigationHandler();

            navHandler.handleNavigation(context, null, outcome);

            context.renderResponse();
        }
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

    private Columns dynamicColumns = null;

    public Columns getDynamicColumns() {
        if(dynamicColumns == null) {
            for(UIComponent kid : getChildren()) {
                if(kid instanceof Columns) {
                    dynamicColumns = (Columns) kid;
                }
            }
        }

        return dynamicColumns;
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

    public boolean initiallyLoaded() {
        return (java.lang.Boolean) getStateHelper().eval("initiallyLoaded", false);
    }

    public void markAsLoaded() {
        getStateHelper().put("initiallyLoaded", true);
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
