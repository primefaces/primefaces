import org.primefaces.component.column.Column;
import java.util.List;
import java.util.ArrayList;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
/*import java.util.Iterator;
import org.primefaces.model.LazyDataModel;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.el.ValueExpression;
import org.primefaces.component.column.Column;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.lang.StringBuffer;
import java.io.Serializable;

	public void processDecodes(FacesContext facesContext) {
		if(isDynamic()) {
			super.processDecodes(facesContext);
		} else {
			int originalRows = getRows();
			setRows(getRowCount());
			super.processDecodes(facesContext);
			setRows(originalRows);
			
			//Update current page state for client side datatable
			Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
			String clientId = getClientId(facesContext);
			String pageParam = clientId + "_page";
			
			if(isPaginator() && params.containsKey(pageParam)) {
				int page = Integer.parseInt(params.get(pageParam));
				setPage(page);
			}
		}
    }
	
	public void processUpdates(FacesContext isAjaxDataRequest) {
		super.processUpdates(isAjaxDataRequest);
		
		if(!isAjaxDataRequest(isAjaxDataRequest)) {
			ValueExpression selectionVE = this.getValueExpression("selection");
			if(selectionVE != null) {
				selectionVE.setValue(isAjaxDataRequest.getELContext(), this.getSelection());
				this.setSelection(null);
			}
		}
	}

	void loadLazyData() {
		DataModel model = getDataModel();
		if(model instanceof LazyDataModel) {
			LazyDataModel lazyModel = (LazyDataModel) model;
			lazyModel.setPageSize(getRows());
			lazyModel.setWrappedData(lazyModel.fetchLazyData(getFirst(), getRows()));
		}
	}
	
	private Map<String,ValueExpression> filterMap;
	
	public Map<String,ValueExpression> getFilterMap() {
		if(filterMap == null) {
			filterMap = new HashMap<String,ValueExpression>();
			
			for(Iterator<UIComponent> children = getChildren().iterator(); children.hasNext();) {
				UIComponent kid = children.next();
					
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					
					if(column.getValueExpression("filterBy") != null) {
						filterMap.put(column.getClientId(FacesContext.getCurrentInstance()), column.getValueExpression("filterBy"));
					}
				}
			}
		}

		return filterMap;
	}
	
	public boolean hasFilter() {
		return getFilterMap().size() > 0;
	}
	
	public boolean isAjaxDataRequest(FacesContext facesContext) {
		return facesContext.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(facesContext) + "_ajaxData");
	}
	
	private List<String> selected= new ArrayList<String>();
	
	public List<String> getSelected() {
		return selected;
	}
	
	public void setSelected(List<String> selected) {
		this.selected = selected;
	}
	
	public boolean isSelectionEnabled() {
		return this.getSelectionMode() != null;
	}
	
	public boolean isSingleSelectionMode() {
		String selectionMode = this.getSelectionMode();
		
		if(selectionMode != null)
			return selectionMode.equals("single") || selectionMode.equals("singlecell");
		else
			return false;
	}
	
	public boolean isCellSelection() {
		String selectionMode = this.getSelectionMode();
		
		if(selectionMode != null)
			return selectionMode.indexOf("cell") != -1;
		else
			return false;
	}
	
	public String getSelectedAsString() {
		StringBuffer buffer = new StringBuffer();
		for(Iterator<String> iter = selected.iterator();iter.hasNext();) {
			buffer.append(iter.next());
			
			if(iter.hasNext())
				buffer.append(",");
		}
		
		return buffer.toString();
	}
	
    public UIColumn getColumnByClientId(String clientId) {
    	FacesContext facesContext = FacesContext.getCurrentInstance();
    	
    	for(UIComponent kid : getChildren()) {
    		if(kid.getClientId(facesContext).equals(clientId))
    			return (UIColumn) kid;
    	}
    	
    	return null;
    }
	
	@Override
    public void restoreState(FacesContext context, Object state) {
        super.restoreState(context, state);
        
        if(getStateHelper().get("value") == null) {
        	getStateHelper().put("value", new ArrayList());
        }
    }*/

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String DATA_CLASS = "ui-datatable-data";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header ui-corner-tl ui-corner-tr";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header ui-corner-bl ui-corner-br";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter";

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

    public boolean isAjaxRequest(FacesContext context) {
        return isPaginationRequest(context) || isSortRequest(context) || isFilterRequest(context);
    }

    private Map<String,ValueExpression> filterMap;

	public Map<String,ValueExpression> getFilterMap() {
		if(filterMap == null) {
			filterMap = new HashMap<String,ValueExpression>();

			for(Column column : getColumns()) {
                if(column.getValueExpression("filterBy") != null) {
                    filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column.getValueExpression("filterBy"));
                }
			}
		}

		return filterMap;
	}

	public boolean hasFilter() {
		return getFilterMap().size() > 0;
	}

    public boolean isSelectionEnabled() {
		return this.getSelectionMode() != null;
	}

    public boolean isSingleSelectionMode() {
		String selectionMode = this.getSelectionMode();

		if(selectionMode != null)
			return selectionMode.equals("single") || selectionMode.equals("singlecell");
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