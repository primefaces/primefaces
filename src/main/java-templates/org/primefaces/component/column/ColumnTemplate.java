import org.primefaces.model.filter.*;
import org.primefaces.component.celleditor.CellEditor;
import javax.faces.component.UIComponent;
import org.primefaces.component.datatable.DataTable;

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

    private FilterConstraint filterConstraint = null;
    private final static String STARTS_WITH_MATCH_MODE = "startsWith";
    private final static String ENDS_WITH_MATCH_MODE = "endsWith";
    private final static String CONTAINS_MATCH_MODE = "contains";
    private final static String EXACT_MATCH_MODE = "exact";

    public FilterConstraint getFilterConstraint() {
        String filterMatchMode = getFilterMatchMode();

        if(filterConstraint == null) {
            if(filterMatchMode.equals(STARTS_WITH_MATCH_MODE)) {
                filterConstraint = new StartsWithFilterConstraint();
            } else if(filterMatchMode.equals(ENDS_WITH_MATCH_MODE)) {
                filterConstraint = new EndsWithFilterConstraint();
            } else if(filterMatchMode.equals(CONTAINS_MATCH_MODE)) {
                filterConstraint = new ContainsFilterConstraint();
            } else if(filterMatchMode.equals(EXACT_MATCH_MODE)) {
                filterConstraint = new ExactFilterConstraint();
            }
        }

        return filterConstraint;
    }