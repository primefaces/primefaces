/*import javax.faces.component.UIComponent;
import org.primefaces.component.datatable.DataTable;

    public void processDecodes(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processDecodes(context);
        }
    }

    public void processValidators(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processValidators(context);
        }
    }

    public void processUpdates(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processUpdates(context);
        }
    }

    private DataTable table;

    private DataTable getTable(FacesContext context) {
        if(table == null) {
            table = findParentTable(context);
        }

        return table;
    }

    private DataTable findParentTable(FacesContext context) {
		UIComponent parent = this.getParent();

		while(parent != null) {
			if(parent instanceof DataTable) {
				return (DataTable) parent;
            }

			parent = parent.getParent();
		}

		return null;
	}*/