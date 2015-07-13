import org.primefaces.model.TreeTableModel;
import org.primefaces.model.TreeNode;
import javax.faces.model.DataModel;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.component.column.Column;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.util.ComponentUtils;

	public final static String CONTAINER_CLASS = "ui-treetable ui-widget";
    public final static String RESIZABLE_CONTAINER_CLASS = "ui-treetable ui-treetable-resizable ui-widget";
    public final static String HEADER_CLASS = "ui-treetable-header ui-widget-header ui-corner-top";
	public final static String DATA_CLASS = "ui-treetable-data ui-widget-content";
    public final static String FOOTER_CLASS = "ui-treetable-footer ui-widget-header ui-corner-bottom";
    public final static String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String SORTABLE_COLUMN_HEADER_CLASS = "ui-state-default ui-sortable-column";
    public final static String ROW_CLASS = "ui-widget-content";
    public final static String SELECTED_ROW_CLASS = "ui-widget-content ui-state-highlight ui-selected";
    public final static String COLUMN_CONTENT_WRAPPER = "ui-tt-c";
    public final static String EXPAND_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-e ui-c";
    public final static String COLLAPSE_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-s ui-c";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-treetable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-treetable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-treetable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-treetable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-treetable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-treetable-scrollable-footer-box";
    public static final String SELECTABLE_NODE_CLASS = "ui-treetable-selectable-node";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String INDENT_CLASS = "ui-treetable-indent";
	public static final String EMPTY_MESSAGE_ROW_CLASS = "ui-widget-content ui-treetable-empty-message";
    public final static String PARTIAL_SELECTED_CLASS = "ui-treetable-partialselected";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String REFLOW_CLASS = "ui-treetable-reflow";
    
    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse", "colResize"));

    private List<String> selectedRowKeys = new ArrayList<String>();

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    public boolean isSelectionRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantSelection");
	}

    public boolean isSortRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_sorting");
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && (event instanceof AjaxBehaviorEvent)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                String nodeKey = params.get(clientId + "_expand");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } 
            else if(eventName.equals("collapse")) {
                String nodeKey = params.get(clientId + "_collapse");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();
                node.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } 
            else if(eventName.equals("select")) {
                String nodeKey = params.get(clientId + "_instantSelection");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }  
            else if(eventName.equals("unselect")) {
                String nodeKey = params.get(clientId + "_instantUnselection");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            
            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isToggleRequest(context)) {
            this.decode(context);
        } else {
            super.processDecodes(context);
        }
    }

    public UIColumn findColumn(String clientId) {
        for(UIColumn column : this.getColumns()) {
            if(column.getColumnKey().equals(clientId)) {
                return column;
            }
        }
        
        FacesContext context = this.getFacesContext();
        ColumnGroup headerGroup = this.getColumnGroup("header");
        for(UIComponent row : headerGroup.getChildren()) {
            for(UIComponent col : row.getChildren()) {
                if(col.getClientId(context).equals(clientId)) {
                    return (UIColumn) col;
                }
            }
        }
       
        throw new FacesException("Cannot find column with key: " + clientId);
    }

    public boolean hasFooterColumn() {
        for(UIComponent child : getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;

                if(column.getFacet("footer") != null || column.getFooterText() != null)
                    return true;
            }
        }

        return false;
    }

    private boolean isToggleRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_expand") != null || params.get(clientId + "_collapse") != null;
    }

    public boolean isResizeRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_colResize") != null;
    }

    private int columnsCount = -1;

    public int getColumnsCount() {
        if(columnsCount == -1) {
            columnsCount = 0;

            for(UIComponent kid : getChildren()) {
                if(kid.isRendered() && kid instanceof Column) {
                    columnsCount++;
                } 
            }
        }

        return columnsCount;
    }

    public String getScrollState() {
        Map<String,String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = this.getClientId() + "_scrollState";
        String value = params.get(name);
        
        return value == null ? "0,0" : value;
    }

    public boolean isCheckboxSelection() {
        String selectionMode = this.getSelectionMode();
        
        return selectionMode != null && selectionMode.equals("checkbox");
    }

    private UIColumn sortColumn;
    
    public void setSortColumn(UIColumn column) {
        this.sortColumn = column;
    }
    public UIColumn getSortColumn() {
        return this.sortColumn;
    }

    public void clearDefaultSorted() {
        getStateHelper().remove("defaultSorted");
    }
    public void setDefaultSorted() {
        getStateHelper().put("defaultSorted", "defaultSorted");
    }
    public boolean isDefaultSorted() {
        return getStateHelper().get("defaultSorted") != null;
    }

    public Locale resolveDataLocale() {
        FacesContext context = this.getFacesContext();
        Object userLocale = this.getDataLocale();
        
        if(userLocale != null) {
            if(userLocale instanceof String)
                return ComponentUtils.toLocale((String) userLocale);
            else if(userLocale instanceof java.util.Locale)
                return (java.util.Locale) userLocale;
            else
                throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for datatable:" + this.getClientId(context));
        } 
        else {
            return context.getViewRoot().getLocale();
        }
    }

    public ColumnGroup getColumnGroup(String target) {
        for(UIComponent child : this.getChildren()) {
            if(child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if(type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }

    private List<UIColumn> columns;
    
    public List<UIColumn> getColumns() {
        if(columns == null) {
            columns = new ArrayList<UIColumn>();
            FacesContext context = getFacesContext();
            char separator = UINamingContainer.getSeparatorChar(context);
            
            for(UIComponent child : this.getChildren()) {
                if(child instanceof Column) {
                    columns.add((UIColumn) child);
                }
                else if(child instanceof Columns) {
                    Columns uiColumns = (Columns) child;
                    String uiColumnsClientId = uiColumns.getClientId(context);
                    
                    for(int i=0; i < uiColumns.getRowCount(); i++) {
                        DynamicColumn dynaColumn = new DynamicColumn(i, uiColumns);
                        dynaColumn.setColumnKey(uiColumnsClientId + separator + i);
                        columns.add(dynaColumn);
                    }
                }
            }
        }
        
        return columns;
    }

    private Columns dynamicColumns;
    
    public void setDynamicColumns(Columns value) {
        this.dynamicColumns = value;
    }
    public Columns getDynamicColumns() {
        return dynamicColumns;
    }

    @Override
    public Object saveState(FacesContext context) {
        if(this.dynamicColumns != null) {
            dynamicColumns.setRowIndex(-1);
        }
    
        return super.saveState(context);
    } 