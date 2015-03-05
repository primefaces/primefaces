import org.primefaces.component.datatable.DataTable;

    public final static String COLLAPSED_ICON = "ui-icon ui-icon-circle-triangle-e";
    public final static String EXPANDED_ICON = "ui-icon ui-icon-circle-triangle-s";

    private DataTable parentTable = null;

    public DataTable getParentTable(FacesContext context) {
        if(parentTable == null) {
            UIComponent parent = this.getParent();

            while(parent != null) {
                if(parent instanceof DataTable) {
                    parentTable = (DataTable) parent;
                    break;
                }

                parent = parent.getParent();
            }
        }

        return parentTable;
    }