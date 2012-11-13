import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.DragDropEvent;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.lang.StringBuilder;
import org.primefaces.model.TreeNode;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import org.primefaces.util.Constants;
import org.primefaces.model.TreeNode;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse"));;;

    private List<String> selectedRowKeys = new ArrayList<String>();

	private Map<String,UITreeNode> nodes;

	public UITreeNode getUITreeNodeByType(String type) {
		UITreeNode node = getTreeNodes().get(type);
		
		if(node == null)
			throw new javax.faces.FacesException("Unsupported tree node type:" + type);
		else
			return node;
	}

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);
        
        String selectionMode = this.getSelectionMode();
        ValueExpression selectionVE = this.getValueExpression("selection");

        if(selectionMode != null && selectionVE != null) {

            Object selection = this.getLocalSelectedNodes();
            Object previousSelection = selectionVE.getValue(context.getELContext());

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

			selectionVE.setValue(context.getELContext(), selection);
			setSelection(null);
		}
	}
	
    public boolean isNodeExpandRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_expandNode");
	}

    public Object getLocalSelectedNodes() {
        return getStateHelper().get(PropertyKeys.selection);
    }

    public static String CONTAINER_CLASS = "ui-tree ui-widget ui-widget-content ui-corner-all";
    public static String ROOT_NODES_CLASS = "ui-tree-container";
    public static String PARENT_NODE_CLASS = "ui-treenode ui-treenode-parent";
    public static String LEAF_NODE_CLASS = "ui-treenode ui-treenode-leaf";
    public static String CHILDREN_NODES_CLASS = "ui-treenode-children";
    public static String NODE_CONTENT_CLASS = "ui-treenode-content";
    public static String SELECTABLE_NODE_CONTENT_CLASS = "ui-treenode-content ui-tree-selectable";
    public static String EXPANDED_ICON_CLASS = "ui-tree-toggler ui-icon ui-icon-triangle-1-s";
    public static String COLLAPSED_ICON_CLASS = "ui-tree-toggler ui-icon ui-icon-triangle-1-e";
    public static String LEAF_ICON_CLASS = "ui-treenode-leaf-icon";
    public static String NODE_ICON_CLASS = "ui-treenode-icon ui-icon";
    public static String NODE_LABEL_CLASS = "ui-treenode-label ui-corner-all";

    public Map<String,UITreeNode> getTreeNodes() {
        if(nodes == null) {
			nodes = new HashMap<String,UITreeNode>();
			for(UIComponent child : getChildren()) {
                UITreeNode node = (UITreeNode) child;
				nodes.put(node.getType(), node);
			}
		}

        return nodes;
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

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
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
                this.setRowKey(params.get(clientId + "_expandNode"));

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if(eventName.equals("collapse")) {
                this.setRowKey(params.get(clientId + "_collapseNode"));
                TreeNode collapsedNode = this.getRowNode();
                collapsedNode.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), collapsedNode);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if(eventName.equals("select")) {
                setRowKey(params.get(clientId + "_instantSelection"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if(eventName.equals("unselect")) {
                setRowKey(params.get(clientId + "_instantUnselection"));

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            } 
            
            super.queueEvent(wrapperEvent);
            
            this.setRowKey(null);
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isToggleRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_expandNode") != null || params.get(clientId + "_collapseNode") != null;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isToggleRequest(context)) {
            this.decode(context);
            context.renderResponse();
        } else {
            super.processDecodes(context);
        }
    }

    public boolean isCheckboxSelection() {
        String selectionMode = this.getSelectionMode();
        
        return selectionMode != null && selectionMode.equals("checkbox");
    }
