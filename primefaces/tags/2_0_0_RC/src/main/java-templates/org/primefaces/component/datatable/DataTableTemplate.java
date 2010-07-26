import java.util.Iterator;
import org.primefaces.model.LazyDataModel;
import javax.faces.component.UIComponent;
import javax.faces.model.DataModel;
import org.primefaces.component.column.Column;

	public void processDecodes(FacesContext context) {
		if(isDynamic()) {
			super.processDecodes(context);
		} else {
			int originalRows = getRows();
			setRows(getRowCount());
			super.processDecodes(context);
			setRows(originalRows);
		}
		
    }
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);
		Object selection = this.getSelection();
		
		if(selection != null) {
			this.getValueExpression("selection").setValue(context.getELContext(), selection);
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