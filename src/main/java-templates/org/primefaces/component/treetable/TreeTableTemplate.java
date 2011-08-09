import org.primefaces.model.TreeTableModel;
import org.primefaces.model.TreeNode;
import javax.faces.model.DataModel;
import javax.faces.event.FacesEvent;
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

	public final static String CONTAINER_CLASS = "ui-treetable ui-widget";
    public final static String HEADER_CLASS = "ui-treetable-header ui-widget-header ui-corner-top";
	public final static String DATA_CLASS = "ui-treetable-data";
    public final static String FOOTER_CLASS = "ui-treetable-footer ui-widget-header";
    public final static String COLUMN_HEADER_CLASS = "ui-state-default";
    public final static String ROW_CLASS = "ui-widget-content";
    public final static String SELECTED_ROW_CLASS = "ui-widget-content ui-state-highlight ui-selected";
    public final static String COLUMN_CONTENT_WRAPPER = "ui-tt-c";
    public final static String EXPAND_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-e";
    public final static String COLLAPSE_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-s";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-treetable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-treetable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-treetable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-treetable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-treetable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-treetable-scrollable-footer-box";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-treetable-resizable";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse", "colResize"));

    private List<String> selectedRowKeys = new ArrayList<String>();

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                String nodeKey = params.get(clientId + "_expand");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
            } 
            else if(eventName.equals("collapse")) {
                String nodeKey = params.get(clientId + "_collapse");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
            } 
            else if(eventName.equals("select")) {
                String nodeKey = params.get(clientId + "_instantSelect");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node);
            }  
            else if(eventName.equals("unselect")) {
                String nodeKey = params.get(clientId + "_instantUnselect");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
 
            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    public boolean isSelectionEnabled() {
        return (!this.getSelectionMode().equals("none"));
    }

    @Override
    public void processUpdates(FacesContext context) {
		super.processUpdates(context);

        if(isSelectionEnabled()) {
            String selectionMode = getSelectionMode();
            Object selection = this.getLocalSelectedNodes();
            Object previousSelection = this.getValueExpression("selection").getValue(context.getELContext());

            if(selectionMode.equals("single")) {
                if(previousSelection != null)
                    ((TreeNode) previousSelection).setSelected(false);
                if(selection != null)
                    ((TreeNode) selection).setSelected(true);
            } 
            else {
                TreeNode[] previousSelections = (TreeNode[]) previousSelection;
                TreeNode[] selections = (TreeNode[]) selection;

                if(previousSelections != null) {
                    for(TreeNode node : previousSelections)
                        node.setSelected(false);
                }

                if(selections != null) {
                    for(TreeNode node : selections)
                        node.setSelected(true);
                }
            }

			this.getValueExpression("selection").setValue(context.getELContext(), selection);
			setSelection(null);
		}
	}

    public Object getLocalSelectedNodes() {
        return getStateHelper().get(PropertyKeys.selection);
    }

    public List<String> getSelectedRowKeys() {
        return this.selectedRowKeys;
    }

    public String getSelectedRowKeysAsString() {
        StringBuilder builder = new StringBuilder();

        for(Iterator<String> iter = this.selectedRowKeys.iterator();iter.hasNext();) {
            builder.append(iter.next());

            if(iter.hasNext()) {
                builder.append(',');
            }
        }

        return builder.toString();
    }

    private Column findColumn(String clientId) {
        for(UIComponent child : getChildren()) {
            if(child instanceof Column && child.getClientId().equals(clientId)) {
                return (Column) child;
            }
        }
        
        return null;
    }