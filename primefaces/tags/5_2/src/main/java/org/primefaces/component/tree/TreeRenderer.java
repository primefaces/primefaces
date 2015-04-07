/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UITree;
import org.primefaces.context.RequestContext;

import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

public class TreeRenderer extends CoreRenderer {
        
    private static final String SB_DECODE_SELECTION = TreeRenderer.class.getName() + "#decodeSelection";
    
    protected enum NodeOrder {
        FIRST,
        MIDDLE,
        LAST,
        NONE
    }

    @Override
	public void decode(FacesContext context, UIComponent component) {
        Tree tree = (Tree) component;

        if(tree.isDragDropRequest(context)) {
            decodeDragDrop(context, tree);
        }
                        
        if(tree.getSelectionMode() != null) {
            decodeSelection(context, tree);
        }
        
        decodeBehaviors(context, tree);
	}
    
    public void decodeSelection(FacesContext context, Tree tree) {        
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tree.getClientId(context);
        String selection = params.get(clientId + "_selection");

        boolean isSingle = tree.getSelectionMode().equalsIgnoreCase("single");

        if(isValueBlank(selection)) {
            if(isSingle)
                tree.setSelection(null);
            else
                tree.setSelection(new TreeNode[0]);
        }
        else {
            String[] selectedRowKeys = selection.split(",");

            if(isSingle) {
                tree.setRowKey(selectedRowKeys[0]);
                tree.setSelection(tree.getRowNode());
            }
            else {
                TreeNode[] selectedNodes = new TreeNode[selectedRowKeys.length];

                for(int i = 0 ; i < selectedRowKeys.length; i++) {
                    tree.setRowKey(selectedRowKeys[i]);
                    selectedNodes[i] = tree.getRowNode();
                }

                tree.setSelection(selectedNodes);
            }

            tree.setRowKey(null);
        }
        
        if(tree.isCheckboxSelection() && tree.isDynamic() && tree.isSelectionRequest(context)) {
            String selectedNodeRowKey = params.get(clientId + "_instantSelection");
            tree.setRowKey(selectedNodeRowKey);
            TreeNode selectedNode = tree.getRowNode();
            List<String> descendantRowKeys = new ArrayList<String>();
            tree.populateRowKeys(selectedNode, descendantRowKeys);
            int size = descendantRowKeys.size();
            StringBuilder sb = SharedStringBuilder.get(context, SB_DECODE_SELECTION);
            
            for(int i = 0; i < size; i++) {
                sb.append(descendantRowKeys.get(i));
                if(i != (size - 1)) {
                    sb.append(",");
                }
            }
            
            RequestContext.getCurrentInstance().addCallbackParam("descendantRowKeys", sb.toString());
            sb.setLength(0);
            descendantRowKeys = null;
        }
    }
    
    public void decodeDragDrop(FacesContext context, Tree tree) {        
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tree.getClientId(context);
        String dragNodeRowKey = params.get(clientId + "_dragNode");
        String dropNodeRowKey = params.get(clientId + "_dropNode");
        String dragSource = params.get(clientId + "_dragSource");
        int dndIndex = Integer.parseInt(params.get(clientId + "_dndIndex"));
        TreeNode dragNode;
        TreeNode dropNode;
        
        if(dragSource.equals(clientId)) {
            tree.setRowKey(dragNodeRowKey);
            dragNode = tree.getRowNode();
        }
        else {
            Tree otherTree = (Tree) tree.findComponent(":" + dragSource);
            otherTree.setRowKey(dragNodeRowKey);
            dragNode = otherTree.getRowNode();
        }
        
        if(isValueBlank(dropNodeRowKey)) {
            dropNode = tree.getValue();
        }
        else {
            tree.setRowKey(dropNodeRowKey);
            dropNode = tree.getRowNode();
        }
        
        tree.setDragNode(dragNode);
        tree.setDropNode(dropNode);
        
        if(dndIndex >= 0 && dndIndex < dropNode.getChildCount())
            dropNode.getChildren().add(dndIndex, dragNode);
        else
            dropNode.getChildren().add(dragNode);
    }
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tree tree = (Tree) component;

        if(tree.isNodeExpandRequest(context)) {
        	boolean vertical = tree.getOrientation().equals("vertical");
            String clientId = tree.getClientId(context);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String rowKey = params.get(clientId + "_expandNode");
            
            if(!vertical && rowKey.equals("root")) {
                encodeHorizontalTreeNodeChildren(context, tree, tree.getValue(), tree.getClientId(context), null, tree.isDynamic(), tree.isCheckboxSelection());
            }
            else {
                tree.setRowKey(rowKey);
                TreeNode node = tree.getRowNode();
                node.setExpanded(true);
                
                if(vertical) {
                    encodeTreeNodeChildren(context, tree, node, clientId, tree.isDynamic(), tree.isCheckboxSelection(), tree.isDroppable());
                }
                else {
                    encodeHorizontalTreeNodeChildren(context, tree, node, tree.getClientId(context), rowKey, tree.isDynamic(), tree.isCheckboxSelection());
                }
                
                
                tree.setRowKey(null);
            }            
        }
        else {
            encodeMarkup(context, tree);
            encodeScript(context, tree);
        }
	}
		
	protected void encodeScript(FacesContext context, Tree tree) throws IOException {
		String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        String widget = tree.getOrientation().equals("vertical") ? "VerticalTree" : "HorizontalTree";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady(widget, tree.resolveWidgetVar(), clientId);

        wb.attr("dynamic", dynamic)
        	.attr("highlight", tree.isHighlight(), true)
    		.attr("animate", tree.isAnimate(), false)
    		.attr("droppable", tree.isDroppable(), false)
    		.attr("cache", tree.isCache() && dynamic)
    		.attr("dragdropScope", tree.getDragdropScope(), null)
    		.callback("onNodeClick", "function(node, event)", tree.getOnNodeClick());

        //selection
        if(selectionMode != null) {
        	wb.attr("selectionMode", selectionMode);
        	wb.attr("propagateUp", tree.isPropagateSelectionUp());
        	wb.attr("propagateDown", tree.isPropagateSelectionDown());
        }

        if(tree.isDraggable()) {
        	wb.attr("draggable", true)
        		.attr("dragMode", tree.getDragMode())
        		.attr("dropRestrict", tree.getDropRestrict());
        }

        encodeIconStates(context, tree, wb);
        encodeClientBehaviors(context, tree);

        wb.finish();
	}
	
	protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
		boolean vertical = tree.getOrientation().equals("vertical");
        TreeNode root = (TreeNode) tree.getValue();
        
        if(root != null && root.getRowKey() == null) {
            root.setRowKey("root");
            tree.buildRowKeys(root);
            tree.initPreselection();
        }
		
        if(vertical)
            encodeVerticalTree(context, tree, root);
        else
            encodeHorizontalTree(context, tree, root);
	}
    
    public void encodeVerticalTree(FacesContext context, Tree tree, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean selectable = selectionMode != null;
        boolean multiselectable = selectable && selectionMode.equals("single");
        boolean checkbox = selectable && selectionMode.equals("checkbox");
        boolean droppable = tree.isDroppable();     
        
        if(root != null && root.getRowKey() == null) {
            root.setRowKey("root");
            tree.buildRowKeys(root);
            tree.initPreselection();
        }
        
        //enable RTL
        if(ComponentUtils.isRTL(context, tree)) {
            tree.setRTLRendering(true);
        }
        
        //container class
        String containerClass = tree.isRTLRendering() ? Tree.CONTAINER_RTL_CLASS : Tree.CONTAINER_CLASS;
        if(tree.getStyleClass() != null) {
            containerClass = containerClass + " " + tree.getStyleClass();
        }
        if(tree.isShowUnselectableCheckbox()) {
            containerClass = containerClass + " ui-tree-checkbox-all";
        }
        
        writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);
        writer.writeAttribute("tabindex", tree.getTabindex(), null);
        writer.writeAttribute("aria-multiselectable", String.valueOf(multiselectable), null);
		if(tree.getStyle() != null) {
            writer.writeAttribute("style", tree.getStyle(), null);
        }
                
        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        if(root != null) {
            encodeTreeNodeChildren(context, tree, root, clientId, dynamic, checkbox, droppable);
        }

		writer.endElement("ul");

        if(selectable) {
            encodeSelectionHolder(context, tree);
        }

		writer.endElement("div");
    }
    
    protected void encodeHorizontalTree(FacesContext context, Tree tree, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean checkbox = (selectionMode != null) && selectionMode.equals("checkbox");
        
        String containerClass = tree.getStyleClass() == null ? Tree.HORIZONTAL_CONTAINER_CLASS : Tree.HORIZONTAL_CONTAINER_CLASS + " " + tree.getStyleClass();
        if(tree.isShowUnselectableCheckbox()) {
            containerClass = containerClass + " ui-tree-checkbox-all";
        }
        
        writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);
        
        if(root != null) {
            encodeHorizontalTreeNode(context, tree, root, clientId, null, NodeOrder.NONE, dynamic, checkbox);
        }
        
        if(selectionMode != null) {
            encodeSelectionHolder(context, tree);
        }
               
        writer.endElement("div");
    }
    
    protected void encodeHorizontalTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey, NodeOrder nodeOrder, boolean dynamic, boolean checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        boolean expanded = node.isExpanded();
        boolean leaf = node.isLeaf();
        boolean selectable = node.isSelectable();
        boolean partialSelected = node.isPartialSelected();
        boolean selected = node.isSelected();
        
        String nodeClass;
        if(leaf) {
            nodeClass = Tree.LEAF_NODE_CLASS;
        }
        else {
            nodeClass = Tree.PARENT_NODE_CLASS;
            nodeClass = expanded ? nodeClass + " ui-treenode-expanded" : nodeClass + " ui-treenode-collapsed";
        }
        
        if(selected)
            nodeClass += " ui-treenode-selected";
        else if(partialSelected)
            nodeClass += " ui-treenode-hasselected";
        else
            nodeClass += " ui-treenode-unselected";
      
        writer.startElement("table", tree);        
        writer.startElement("tbody", null);
        writer.startElement("tr", null);
        
        //connector
        if(nodeOrder != NodeOrder.NONE) {
            encodeConnector(context, tree, nodeOrder);
        }
        
        //node
        writer.startElement("td", null); 
        writer.writeAttribute("class", nodeClass, null);
        writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);
        
        if(rowKey != null) {
            tree.setRowKey(rowKey);
            writer.writeAttribute("data-rowkey", rowKey, null);
        }
        else {
            context.getExternalContext().getRequestMap().put(tree.getVar(), tree.getValue().getData());
            writer.writeAttribute("data-rowkey", "root", null);
        }
        
        String nodeContentClass = node.isSelectable() ? Tree.SELECTABLE_NODE_CONTENT_CLASS_H : Tree.NODE_CONTENT_CLASS_H;
        if(selected) {
            nodeContentClass += " ui-state-highlight";
        }
        writer.startElement("div", null);
        writer.writeAttribute("class", nodeContentClass, null);
        
        //toggler
        if(!leaf) {
            String toggleIcon = expanded ? Tree.EXPANDED_ICON_CLASS_H : Tree.COLLAPSED_ICON_CLASS_H;
            writer.startElement("span", null); 
            writer.writeAttribute("class", toggleIcon, null);
            writer.endElement("span");
        }
        
        //checkbox
        if(checkbox) {
            RendererUtils.encodeCheckbox(context, selected, partialSelected, !selectable, Tree.CHECKBOX_CLASS);
        }
        
        //icon
        encodeIcon(context, uiTreeNode, expanded);
        
        uiTreeNode.encodeAll(context);
        writer.endElement("div");
        writer.endElement("td");
                
        //children
        if(!leaf) {
            writer.startElement("td", null);
            writer.writeAttribute("class", "ui-treenode-children-container", null);
            
            if(!expanded) {
                writer.writeAttribute("style", "display:none", null);
            }
                                    
            writer.startElement("div", null);
            writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS, null);

            if((dynamic && expanded) || !dynamic) {
                encodeHorizontalTreeNodeChildren(context, tree, node, clientId, rowKey, dynamic, checkbox);
            }
            
            writer.endElement("div");
            writer.endElement("td");
        }
        
        writer.endElement("tr");
        writer.endElement("tbody");
        writer.endElement("table");
    }
    
    protected void encodeHorizontalTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey, boolean dynamic, boolean checkbox) throws IOException {
        int childIndex = 0;
        for(Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
            String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + UITree.SEPARATOR + childIndex;

            NodeOrder no = null;
            if(node.getChildCount() == 1) {
                no = NodeOrder.NONE;
            } else if(childIndex == 0) {
                no = NodeOrder.FIRST;
            } else if(childIndex == (node.getChildCount() - 1)) {
                no = NodeOrder.LAST;
            } else {
                no = NodeOrder.MIDDLE;
            }

            encodeHorizontalTreeNode(context, tree, iterator.next(), clientId, childRowKey, no, dynamic, checkbox);

            childIndex++;
        }
    }
    
    protected void encodeConnector(FacesContext context, Tree tree, NodeOrder nodeOrder) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
		writer.startElement("td", null); 
        writer.writeAttribute("class", "ui-treenode-connector", null);

        writer.startElement("table", null); 
        writer.writeAttribute("class", "ui-treenode-connector-table", null);
        writer.startElement("tbody", null);
        
        writer.startElement("tr", null); 
        writer.startElement("td", null);
        if(!nodeOrder.equals(NodeOrder.FIRST)) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");
        
        writer.startElement("tr", null); 
        writer.startElement("td", null); 
        if(!nodeOrder.equals(NodeOrder.LAST)) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");
        
        writer.endElement("tbody");
        writer.endElement("table");
        
        writer.endElement("td");
	}
 
	public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, boolean dynamic, boolean checkbox, boolean dragdrop) throws IOException {
        //preselection
        String rowKey = node.getRowKey();
        boolean selected = node.isSelected();
        boolean partialSelected = node.isPartialSelected();

        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        if(!uiTreeNode.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        tree.setRowKey(rowKey);
        boolean isLeaf = node.isLeaf();
        boolean expanded = node.isExpanded();
        boolean selectable = node.isSelectable();
        String toggleIcon = expanded ? Tree.EXPANDED_ICON_CLASS_V : (tree.isRTLRendering() ? Tree.COLLAPSED_ICON_RTL_CLASS_V : Tree.COLLAPSED_ICON_CLASS_V);
        String stateIcon = isLeaf ? Tree.LEAF_ICON_CLASS : toggleIcon;
        Object datakey = tree.getDatakey();
        String nodeId = clientId + UINamingContainer.getSeparatorChar(context) + rowKey;

        //style class of node
        String containerClass = isLeaf ? Tree.LEAF_NODE_CLASS : Tree.PARENT_NODE_CLASS;

        if(selected)
            containerClass += " ui-treenode-selected";
        else if(partialSelected)
            containerClass += " ui-treenode-hasselected";
        else
            containerClass += " ui-treenode-unselected";

        containerClass = uiTreeNode.getStyleClass() == null ? containerClass : containerClass + " " + uiTreeNode.getStyleClass();

        writer.startElement("li", null);
            writer.writeAttribute("id", nodeId, null);
            writer.writeAttribute("data-rowkey", rowKey, null);
            writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);
            writer.writeAttribute("class", containerClass, null);

            if(datakey != null) {
                writer.writeAttribute("data-datakey", datakey, null);
            }

            //content
            String contentClass = selectable ? Tree.SELECTABLE_NODE_CONTENT_CLASS_V : Tree.NODE_CONTENT_CLASS_V;
            if(dragdrop) {
                contentClass += " ui-treenode-droppable";
            }

            writer.startElement("span", null);
            writer.writeAttribute("class", contentClass, null);
            writer.writeAttribute("aria-expanded", String.valueOf(expanded), null);
            writer.writeAttribute("aria-selected", String.valueOf(selected), null);
            if(checkbox) {
                writer.writeAttribute("aria-checked", String.valueOf(selected), null);
            }

                //state icon
                writer.startElement("span", null);
                writer.writeAttribute("class", stateIcon, null);
                writer.endElement("span");

                //checkbox
                if(checkbox) {
                    RendererUtils.encodeCheckbox(context, selected, partialSelected, !selectable, Tree.CHECKBOX_CLASS);
                }

                //node icon
                encodeIcon(context, uiTreeNode, expanded);

                //label
                String nodeLabelClass = selected? Tree.NODE_LABEL_CLASS + " ui-state-highlight" : Tree.NODE_LABEL_CLASS;

                writer.startElement("span", null);
                writer.writeAttribute("class", nodeLabelClass, null);
                writer.writeAttribute("tabindex", "-1", null);
                writer.writeAttribute("role", "treeitem", null);
                writer.writeAttribute("aria-label", uiTreeNode.getAriaLabel(), null);
                uiTreeNode.encodeAll(context);
                writer.endElement("span");

            writer.endElement("span");

            //children nodes                
            writer.startElement("ul", null);
            writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS , null);
            writer.writeAttribute("role", "group", null);
            
            if(!expanded) {
                writer.writeAttribute("style", "display:none", null);
            }

            if((dynamic && expanded) || !dynamic) {
                encodeTreeNodeChildren(context, tree, node, clientId, dynamic, checkbox, dragdrop);
            }

            writer.endElement("ul");

        writer.endElement("li");

        if(dragdrop) {
            encodeDropTarget(context, tree);
        }
	}
    
    public void encodeTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String clientId, boolean dynamic, boolean checkbox, boolean droppable) throws IOException {     
        int childCount = node.getChildCount();        
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                if(i == 0 && droppable) {
                    encodeDropTarget(context, tree);
                }
                
                encodeTreeNode(context, tree, node.getChildren().get(i), clientId, dynamic, checkbox, droppable);
            }
        }
    }
    
    protected void encodeDropTarget(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("li", null);
        writer.writeAttribute("class", "ui-tree-droppoint", null);
        writer.endElement("li");
    }

    protected void encodeIconStates(FacesContext context, Tree tree, WidgetBuilder wb) throws IOException {
        Map<String,UITreeNode> nodes = tree.getTreeNodes();

        wb.append(",iconStates:{");
        
        boolean firstWritten = false;
        for(Iterator<String> it = nodes.keySet().iterator(); it.hasNext();) {
            String type = it.next();
            UITreeNode node = nodes.get(type);
            String expandedIcon = node.getExpandedIcon();
            String collapsedIcon = node.getCollapsedIcon();

            if(expandedIcon != null && collapsedIcon != null) {
                if(firstWritten) {
                	wb.append(",");
                }
                
                wb.append("'" + node.getType() + "' : {");
                wb.append("expandedIcon:'" + expandedIcon + "'");
                wb.append(",collapsedIcon:'" + collapsedIcon + "'");
                wb.append("}");
                
                firstWritten = true;
            }
        }

        wb.append("}");
    }
    
    protected void encodeIcon(FacesContext context, UITreeNode uiTreeNode, boolean expanded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", null);
        String icon = uiTreeNode.getIconToRender(expanded);
        if(icon != null) {
            writer.writeAttribute("class", Tree.NODE_ICON_CLASS + " " + icon, null);
        }
        writer.endElement("span");
    }

    protected void encodeSelectionHolder(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String id = tree.getClientId(context) + "_selection";

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
        writer.writeAttribute("value", tree.getSelectedRowKeysAsString(), null);
        writer.writeAttribute("autocomplete", "off", null);
		writer.endElement("input");
    }

	protected void encodeCheckbox(FacesContext context, Tree tree, TreeNode node, boolean selected) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String iconClass = selected ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_BOX_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");

        writer.endElement("div");
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}