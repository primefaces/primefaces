import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.HashMap;
import org.primefaces.model.TreeNode;

	private Map<String,UITreeNode> nodes;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = null;

		if(event instanceof NodeSelectEvent) {
			me = getNodeSelectListener();
		} else if(event instanceof NodeExpandEvent) {
			me = getNodeExpandListener();
		} else if(event instanceof NodeCollapseEvent) {
			me = getNodeCollapseListener();
		}
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
	
	public UITreeNode getUITreeNodeByType(String type) {
		UITreeNode node = getTreeNodes().get(type);
		
		if(node == null)
			throw new javax.faces.FacesException("Unsupported tree node type:" + type);
		else
			return node;
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
	
	public boolean hasAjaxListener() {
		return getNodeSelectListener() != null || getNodeExpandListener() != null || getNodeCollapseListener() != null;
	}

    public boolean isNodeExpandRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_expandNode");
	}

    public boolean isNodeCollapseRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_collapseNode");
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
    public static String EXPANDED_ICON_CLASS = "ui-tree-icon ui-icon ui-icon-triangle-1-s";
    public static String COLLAPSED_ICON_CLASS = "ui-tree-icon ui-icon ui-icon-triangle-1-e";

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


