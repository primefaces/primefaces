import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.TreeDragDropEvent;
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
import javax.faces.context.FacesContext;
import org.primefaces.model.TreeNode;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import org.primefaces.component.tree.UITreeNode;
import org.primefaces.util.Constants;
import org.primefaces.model.TreeNode;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse", "dragdrop", "contextMenu"));

	private Map<String,UITreeNode> nodes;

	public UITreeNode getUITreeNodeByType(String type) {
		UITreeNode node = getTreeNodes().get(type);
		
		if(node == null)
			throw new javax.faces.FacesException("Unsupported tree node type:" + type);
		else
			return node;
	}

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }
		
    public boolean isNodeExpandRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_expandNode");
	}

    public boolean isSelectionRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantSelection");
	}

    public static String CONTAINER_CLASS = "ui-tree ui-widget ui-widget-content ui-corner-all";
    public static String CONTAINER_RTL_CLASS = "ui-tree ui-tree-rtl ui-widget ui-widget-content ui-corner-all";
    public static String HORIZONTAL_CONTAINER_CLASS = "ui-tree ui-tree-horizontal ui-widget ui-widget-content ui-corner-all";
    public static String ROOT_NODES_CLASS = "ui-tree-container";
    public static String PARENT_NODE_CLASS = "ui-treenode ui-treenode-parent";
    public static String LEAF_NODE_CLASS = "ui-treenode ui-treenode-leaf";
    public static String CHILDREN_NODES_CLASS = "ui-treenode-children";
    public static String NODE_CONTENT_CLASS_V = "ui-treenode-content";
    public static String SELECTABLE_NODE_CONTENT_CLASS_V = "ui-treenode-content ui-tree-selectable";
    public static String NODE_CONTENT_CLASS_H = "ui-treenode-content ui-state-default ui-corner-all";
    public static String SELECTABLE_NODE_CONTENT_CLASS_H = "ui-treenode-content ui-tree-selectable ui-state-default ui-corner-all";
    public static String EXPANDED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-s";
    public static String COLLAPSED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-e";
    public static String COLLAPSED_ICON_RTL_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-w";
    public static String EXPANDED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-minus";
    public static String COLLAPSED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-plus";
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

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                this.setRowKey(params.get(clientId + "_expandNode"));
                TreeNode expandedNode = this.getRowNode();
                expandedNode.setExpanded(true);

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), expandedNode);
            }
            else if(eventName.equals("collapse")) {
                this.setRowKey(params.get(clientId + "_collapseNode"));
                TreeNode collapsedNode = this.getRowNode();
                collapsedNode.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), collapsedNode);
            }
            else if(eventName.equals("select")) {
                setRowKey(params.get(clientId + "_instantSelection"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
            }
            else if(eventName.equals("unselect")) {
                setRowKey(params.get(clientId + "_instantUnselection"));

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
            }
            else if(eventName.equals("dragdrop")) {
                int dndIndex = Integer.parseInt(params.get(clientId + "_dndIndex"));

                wrapperEvent = new TreeDragDropEvent(this, behaviorEvent.getBehavior(), dragNode, dropNode, dndIndex);
            }
            else if(eventName.equals("contextMenu")) {
                setRowKey(params.get(clientId + "_contextMenuNode"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), this.getRowNode(), true);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            
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

    public boolean isDragDropRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);
        String source = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);

        return clientId.equals(source) && params.get(clientId + "_dragdrop") != null;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isToggleRequest(context)) {
            this.decode(context);
        } 
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isToggleRequest(context)) {
            super.processValidators(context);
        } 
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(isToggleRequest(context)) {
            this.updateSelection(context);
        }
        else {
            super.processUpdates(context);
        }  
    }

    public boolean isCheckboxSelection() {
        String selectionMode = this.getSelectionMode();
        
        return selectionMode != null && selectionMode.equals("checkbox");
    }

    public boolean isRTL() {
        return this.getDir().equalsIgnoreCase("rtl");
    }

    private TreeNode dragNode;
    private TreeNode dropNode;

    TreeNode getDragNode() {
        return dragNode;
    }
    void setDragNode(TreeNode dragNode) {
        this.dragNode = dragNode;
    }

    TreeNode getDropNode() {
        return dropNode;
    }
    void setDropNode(TreeNode dropNode) {
        this.dropNode = dropNode;
    }

    @Override
    protected boolean shouldVisitNode(TreeNode node) {
        return this.isDynamic() ? (node.isExpanded() || node.getParent() == null) : true;
    }

    @Override
    protected void processColumnChildren(FacesContext context, PhaseId phaseId, String nodeKey) {
        setRowKey(nodeKey);
        TreeNode treeNode = this.getRowNode();

        if(treeNode == null)
            return;
        
        String treeNodeType = treeNode.getType();
        
        for(UIComponent child : getChildren()) {
            if(child instanceof UITreeNode && child.isRendered()) {
                UITreeNode uiTreeNode = (UITreeNode) child;

                if(!treeNodeType.equals(uiTreeNode.getType()))
                    continue;
                
                for(UIComponent grandkid : child.getChildren()) {
                    if(!grandkid.isRendered())
                        continue;
                    
                    if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                        grandkid.processDecodes(context);
                    else if(phaseId == PhaseId.PROCESS_VALIDATIONS)
                        grandkid.processValidators(context);
                    else if(phaseId == PhaseId.UPDATE_MODEL_VALUES)
                        grandkid.processUpdates(context);
                    else
                        throw new IllegalArgumentException();
                }
            }
        }
    }
    