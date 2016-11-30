import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;

    @Override
    public void processDecodes(FacesContext context) {
        if(isEditRequest(context)) {
            super.processDecodes(context);
        }
	}
    
    @Override
    public void processValidators(FacesContext context) {
        if(isEditRequest(context)) {
            super.processValidators(context);
        }
	}

    @Override
    public void processUpdates(FacesContext context) {
        if(isEditRequest(context)) {
            super.processUpdates(context);
        }
	}

    public boolean isEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context));
    }

    private UIComponent parentTable = null;

    public UIComponent getParentTable(FacesContext context) {
        if(parentTable == null) {
            UIComponent parent = this.getParent();

            while(parent != null) {
                if(parent instanceof DataTable || parent instanceof TreeTable) {
                    parentTable = parent;
                    break;
                }

                parent = parent.getParent();
            }
        }

        return parentTable;
    }

