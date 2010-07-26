import java.util.Iterator;
import org.primefaces.model.LazyDataModel;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.model.DataModel;
import javax.el.ValueExpression;
import org.primefaces.component.column.Column;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

	public void processDecodes(FacesContext context) {
		if(isDynamic()) {
			super.processDecodes(context);
		} else {
			int originalRows = getRows();
			setRows(getRowCount());
			super.processDecodes(context);
			setRows(originalRows);
		}
		
		//Set current page and first
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String currentPageParam = getClientId(context) + "_currentPage";
		if(isPaginator() && params.containsKey(currentPageParam)) {
			int currentPage = Integer.parseInt(params.get(currentPageParam));
			setPage(currentPage);
			setFirst((currentPage - 1) * getRows());
		}
    }
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);	
		
		ValueExpression selectionVE = this.getValueExpression("selection");
		if(selectionVE != null) {
			selectionVE.setValue(context.getELContext(), this.getSelection());
			this.setSelection(null);
		}
	}
	
	private String columnSelectionMode = null;
	
	public String getColumnSelectionMode() {
		if(columnSelectionMode == null) { 
			for(Iterator<javax.faces.component.UIComponent> children = getChildren().iterator(); children.hasNext();) {
				javax.faces.component.UIComponent kid = children.next();
				
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					
					if(column.getSelectionMode() != null) {
						columnSelectionMode = column.getSelectionMode();
					}
				}
			}
		}
		
		return columnSelectionMode;
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
	
	public void assignDataModel(javax.faces.model.DataModel model) {
		setDataModel(model);
	}
	
	private List<Integer> selectedRowIndexes = new ArrayList<Integer>();
	
	public List<Integer> getSelectedRowIndexes() {
		return selectedRowIndexes;
	}
	
	public void setSelectedRowIndexes(List<Integer> selectedRowIndexes) {
		this.selectedRowIndexes = selectedRowIndexes;
	}
	
	public boolean isSelectionEnabled() {
		return this.getSelectionMode() != null || this.getColumnSelectionMode() != null;
	}
	
	public boolean isSingleSelectionMode() {
		return (this.getSelectionMode() != null && this.getSelectionMode().equals("single")) || (this.getColumnSelectionMode() != null && this.getColumnSelectionMode().equals("single"));
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