import java.util.List;
import org.primefaces.model.filter.*;
import org.primefaces.component.celleditor.CellEditor;
import javax.faces.component.UIComponent;
import org.primefaces.model.menu.MenuModel;

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
        return false;
    }

    public String getColumnKey() {
        return this.getClientId();
    }

    @Override
    public List getElements() {
        return getChildren();
    }

    @Override
    public int getElementsCount() {
        return getChildCount();
    }
