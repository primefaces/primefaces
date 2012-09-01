import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.primefaces.component.api.UIData;
import org.primefaces.component.celleditor.CellEditor;

    public String getSelectionMode() {
        return null;
    }

    public boolean isDisabledSelection() {
        return false;
    }

    public CellEditor getCellEditor() {
        return null;
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
    
