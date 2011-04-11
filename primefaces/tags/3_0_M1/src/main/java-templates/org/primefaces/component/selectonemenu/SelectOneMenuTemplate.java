import org.primefaces.component.column.Column;
import java.util.List;
import java.util.ArrayList;
import javax.faces.component.UIComponent;

    public final static String STYLE_CLASS = "ui-selectonemenu ui-widget ui-state-default ui-corner-all ui-helper-clearfix";
    public final static String LABEL_CLASS = "ui-selectonemenu-label ui-corner-all";
    public final static String TRIGGER_CLASS = "ui-selectonemenu-trigger ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-selectonemenu-panel ui-helper-hidden-accessible";
    public final static String LIST_CLASS = "ui-selectonemenu-items ui-selectonemenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String TABLE_CLASS = "ui-selectonemenu-items ui-selectonemenu-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-selectonemenu-item ui-selectonemenu-list-item ui-corner-all";
    public final static String ROW_CLASS = "ui-selectonemenu-item ui-selectonemenu-row ui-widget-content";

    public List<Column> getColums() {
        List<Column> columns = new ArrayList<Column>();
        
        for(UIComponent kid : this.getChildren()) {
            if(kid instanceof Column)
                columns.add((Column) kid);
        }

        return columns;
    }

    