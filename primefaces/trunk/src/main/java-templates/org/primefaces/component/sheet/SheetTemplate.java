import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;


    public final static String CONTAINER_CLASS = "ui-sheet ui-widget";
    public static final String CAPTION_CLASS = "ui-widget-header ui-sheet-caption ui-corner-tl ui-corner-tr";
    public static final String HEADER_CLASS = "ui-widget-header ui-sheet-header";
    public static final String HEADER_BOX_CLASS = "ui-sheet-header-box";
    public static final String BODY_CLASS = "ui-sheet-body";
    public static final String CELL_CLASS = "ui-sh-c";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String CELL_DISPLAY_CLASS = "ui-sh-c-d";
    public static final String CELL_EDIT_CLASS = "ui-sh-c-e";
    public static final String INDEX_CELL_CLASS = "ui-sheet-index-cell ui-sh-c";
    public static final String EDITOR_BAR_CLASS = "ui-sheet-editor-bar ui-widget-header";
    public static final String CELL_INFO_CLASS = "ui-sheet-cell-info";
    public static final String EDITOR_CLASS = "ui-sheet-editor";
    public static final String SORTABLE_COLUMN = "ui-sortable-column"; 
    public static final String SORTABLE_COLUMN_ICON = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";    

    public static final String[] LETTERS = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public boolean isSortingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_sorting");
    }

    /*@Override
    public void processDecodes(FacesContext context) {
        if(isSortingRequest(context)) {
            this.decode(context);
            context.renderResponse();
        }
        else {
            super.processDecodes(context);
        }
	}*/

    public List<Column> columns;
    public Column findColumn(String clientId) {
        for(Column column : getColumns()) {
            if(column.getClientId().equals(clientId)) {
                return column;
            }
        }
        
        return null;
    }

    public List<Column> getColumns() {        
        if(columns == null) {
            columns = new ArrayList<Column>();

            for(UIComponent child : this.getChildren()) {
                if(child.isRendered() && child instanceof Column) {
                    columns.add((Column) child);
                }
            }
        }

        return columns;
    }

    public boolean isColResizeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_colResize");
    }

    public void syncColumnWidths() {
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String clientId = this.getClientId();
        
        String columnId = params.get(clientId + "_columnId");
        String width = params.get(clientId + "_width");
        Column column = findColumn(columnId);
        
        column.setWidth(Integer.parseInt(width));
    }