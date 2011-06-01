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
import org.primefaces.util.Constants;
import org.primefaces.model.TreeExplorer;
import org.primefaces.model.TreeExplorerImpl;
import org.primefaces.model.TreeModel;
import org.primefaces.model.TreeNode;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse", "dragdrop"));;;

    private List<String> selectedRowKeys = new ArrayList<String>();

	private Map<String,UITreeNode> nodes;

    private TreeExplorer treeExplorer = new TreeExplorerImpl();

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

        if(selectionMode != null) {

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
	
    public boolean isNodeExpandRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_expandNode");
	}

    public Object getLocalSelectedNodes() {
        return getStateHelper().get(PropertyKeys.selection);
    }

    public static String STYLE_CLASS = "ui-tree ui-widget ui-widget-content ui-helper-clearfix ui-corner-all";
    public static String ROOT_NODES_CLASS = "ui-tree-nodes ui-helper-reset";
    public static String PARENT_CLASS = "ui-tree-parent";
    public static String NODE_CLASS = "ui-tree-node ui-state-default";
    public static String NODES_CLASS = "ui-tree-nodes ui-helper-reset ui-tree-child";
    public static String LEAF_CLASS = "ui-tree-item";
    public static String NODE_CONTENT_CLASS = "ui-helper-clearfix ui-tree-node-content ui-corner-all";
    public static String NODE_LABEL_CLASS = "ui-tree-node-label";
    public static String EXPANDED_ICON_CLASS = "ui-tree-icon ui-icon ui-icon-triangle-1-s";
    public static String COLLAPSED_ICON_CLASS = "ui-tree-icon ui-icon ui-icon-triangle-1-e";
    public static String CHECKBOX_CLASS = "ui-tree-checkbox ui-widget";
    public static String CHECKBOX_BOX_CLASS = "ui-tree-checkbox-box ui-widget ui-corner-all ui-state-default";
    public static String CHECKBOX_ICON_CLASS = "ui-tree-checkbox-icon";
    public static String CHECKBOX_ICON_CHECKED_CLASS = "ui-tree-checkbox-icon ui-icon ui-icon-check";
    public static String CHECKBOX_ICON_MINUS_CLASS = "ui-tree-checkbox-icon ui-icon ui-icon-minus";

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
            TreeModel model = new TreeModel((TreeNode) getValue());
            model.setRowIndex(-1);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                TreeNode nodeToExpand = treeExplorer.findTreeNode(params.get(clientId + "_expandNode"), model);
                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), nodeToExpand);
            }
            else if(eventName.equals("collapse")) {
                TreeNode nodeToCollapse = treeExplorer.findTreeNode(params.get(clientId + "_collapseNode"), model);
                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), nodeToCollapse);
            }
            else if(eventName.equals("select")) {
                TreeNode nodeToCollapse = treeExplorer.findTreeNode(params.get(clientId + "_instantSelection"), model);
                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), nodeToCollapse);
            }
            else if(eventName.equals("unselect")) {
                TreeNode nodeToCollapse = treeExplorer.findTreeNode(params.get(clientId + "_instantUnselection"), model);
                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), nodeToCollapse);
            } 
            else if(eventName.equals("dragdrop")) {
                String draggedNodeId = params.get(clientId + "_draggedNode");
                String droppedNodeId = params.get(clientId + "_droppedNode");

                model.setRowIndex(-1);
                TreeNode draggedNode = treeExplorer.findTreeNode(draggedNodeId, model);

                model.setRowIndex(-1);
                TreeNode droppedNode = treeExplorer.findTreeNode(droppedNodeId, model);

                //update model
                TreeNode oldParent = draggedNode.getParent();
                oldParent.getChildren().remove(draggedNode);
                droppedNode.addChild(draggedNode);

                //fire dragdrop event
                wrapperEvent = new DragDropEvent(this, behaviorEvent.getBehavior(), draggedNodeId, droppedNodeId, draggedNode);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }