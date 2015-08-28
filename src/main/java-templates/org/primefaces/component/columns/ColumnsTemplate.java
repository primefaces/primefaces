import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIData;
import org.primefaces.component.celleditor.CellEditor;

    public String getSelectionMode() {
        return null;
    }

    private CellEditor cellEditor = null;

    public CellEditor getCellEditor() {
        if(cellEditor == null) {
            for(UIComponent child : getChildren()) {
                if(child instanceof CellEditor)
                    cellEditor = (CellEditor) child;
            }
        }

        return cellEditor;
    }

    public boolean isDynamic() {
        return true;
    }

    public java.lang.String getColumnIndexVar() {
		return super.getRowIndexVar();
	}
	public void setColumnIndexVar(String _columnIndexVar) {
		super.setRowIndexVar(_columnIndexVar);
	}

    public String getColumnKey() {
        return this.getClientId();
    }

    public void renderChildren(FacesContext context) throws IOException {
        this.encodeChildren(context);
    }

    private List<DynamicColumn> dynamicColumns;

    public List<DynamicColumn> getDynamicColumns() {
        if(dynamicColumns == null) {
            FacesContext context = this.getFacesContext();
            this.setRowIndex(-1);
            char separator = UINamingContainer.getSeparatorChar(context);
            dynamicColumns = new ArrayList<DynamicColumn>();
            String clientId = this.getClientId(context);

            for(int i=0; i < this.getRowCount(); i++) {
                DynamicColumn dynaColumn = new DynamicColumn(i, this);
                dynaColumn.setColumnKey(clientId + separator + i);

                dynamicColumns.add(dynaColumn);
            }
        }

        return dynamicColumns;
    }

    public void setDynamicColumns(List<DynamicColumn> dynamicColumns) {
        this.dynamicColumns = dynamicColumns;
    }
    
