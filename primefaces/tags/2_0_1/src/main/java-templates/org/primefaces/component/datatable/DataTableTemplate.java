import java.util.Iterator;
import org.primefaces.model.LazyDataModel;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
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
	
	private List<Integer> selectedRowIndexes = new ArrayList<Integer>();
	
	public List<Integer> getSelectedRowIndexes() {
		return selectedRowIndexes;
	}
	
	public void setSelectedRowIndexes(List<Integer> selectedRowIndexes) {
		this.selectedRowIndexes = selectedRowIndexes;
	}
	
	public boolean isSelectionEnabled() {
		return this.getSelectionMode() != null;
	}
	
	public boolean isSingleSelectionMode() {
		return (this.getSelectionMode() != null && this.getSelectionMode().equals("single"));
	}
	
	public String getSelectedRowIndexesAsString() {
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Integer> iter = selectedRowIndexes.iterator();iter.hasNext();) {
			buffer.append(String.valueOf(iter.next()));
			
			if(iter.hasNext())
				buffer.append(",");
		}
		
		return buffer.toString();
	}
	
	@Override
    public void restoreState(FacesContext context, Object state) {
        if(state == null) {
            return;
        }
        
        super.restoreState(context, state);
        Object[] savedState = (Object[]) ((Object[]) state)[4];
       
        int length = (savedState.length - 1) / 2;
        for (int i = 0; i < length; i++) {
           Object value = savedState[i * 2 + 1];

            Serializable serializable = (Serializable) savedState[i * 2];
            if(serializable != null && serializable.toString().equals("value") && value == null) {
            	getStateHelper().put(serializable, new ArrayList());
            }
        }
    }