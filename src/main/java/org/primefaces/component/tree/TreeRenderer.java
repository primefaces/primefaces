/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UITree;

import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class TreeRenderer extends CoreRenderer {
    
    protected enum NodeOrder {
        FIRST,
        MIDDLE,
        LAST,
        NONE
    }

    @Override
	public void decode(FacesContext context, UIComponent component) {
        Tree tree = (Tree) component;
        
        decodeSelection(context, tree);
        
        decodeBehaviors(context, tree);
	}
    
    public void decodeSelection(FacesContext context, Tree tree) {
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String clientId = tree.getClientId(context);
        
        if(tree.getSelectionMode() != null) {
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
        }
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tree tree = (Tree) component;
        boolean vertical = tree.getOrientation().equals("vertical");

        if(tree.isNodeExpandRequest(context)) {
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
                    encodeTreeNodeChildren(context, tree, node, clientId, rowKey, tree.isDynamic(), tree.isCheckboxSelection());
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
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        String widget = tree.getOrientation().equals("vertical") ? "VerticalTree" : "HorizontalTree";
			
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('" + widget + "','" + tree.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",dynamic:" + dynamic);
        
        if(dynamic) {
            writer.write(",cache:" + tree.isCache());
        }

        //selection
        if(selectionMode != null) {
            writer.write(",selectionMode:'" + selectionMode + "'");
            writer.write(",highlight:" + tree.isHighlight());
            writer.write(",propagateUp:" + tree.isPropagateSelectionUp());
            writer.write(",propagateDown:" + tree.isPropagateSelectionDown());
        }

        if(tree.getOnNodeClick() != null) {
            writer.write(",onNodeClick:function(node) {" + tree.getOnNodeClick() + "}");
        }
        
        if(tree.isAnimate()) {
            writer.write(",animate:true");
        }

        encodeIconStates(context, tree);
        
        encodeClientBehaviors(context, tree);

        writer.write("});");

		endScript(writer);
	}
	
	protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
		boolean vertical = tree.getOrientation().equals("vertical");
		
        if(vertical) {
            encodeVerticalTree(context, tree);
        }
        else {
            encodeHorizontalTree(context, tree);
        }
	}
    
    public void encodeVerticalTree(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        TreeNode root = (TreeNode) tree.getValue();
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean selectable = selectionMode != null;
        boolean multiselectable = selectable && selectionMode.equals("single");
        boolean checkbox = selectable && selectionMode.equals("checkbox");
        String containerClass = tree.getStyleClass() == null ? Tree.CONTAINER_CLASS : Tree.CONTAINER_CLASS + " " + tree.getStyleClass();
        
        writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);
        writer.writeAttribute("aria-multiselectable", String.valueOf(multiselectable), null);
		if(tree.getStyle() != null) {
            writer.writeAttribute("style", tree.getStyle(), null);
        }
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        if(root != null) {
            root.setExpanded(true);
            encodeTreeNode(context, tree, root, clientId, null, dynamic, checkbox);
        }

		writer.endElement("ul");

        if(selectable) {
            encodeSelectionHolder(context, tree);
        }

		writer.endElement("div");
    }
    
    protected void encodeHorizontalTree(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        TreeNode root = (TreeNode) tree.getValue();
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean checkbox = (selectionMode != null) && selectionMode.equals("checkbox");
        
        String containerClass = tree.getStyleClass() == null ? Tree.HORIZONTAL_CONTAINER_CLASS : Tree.HORIZONTAL_CONTAINER_CLASS + " " + tree.getStyleClass();
        
        writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);
        
        if(root != null) {
            encodeHorizontalTreeNode(context, tree, root, clientId, null, NodeOrder.NONE, dynamic, checkbox);
        }
        
        if(tree.getSelectionMode() != null) {
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
        
        //preselection
        boolean selected = node.isSelected();
        if(selected) {
            tree.getSelectedRowKeys().add(rowKey);
        }
        
        String nodeClass;
        if(leaf) {
            nodeClass = Tree.LEAF_NODE_CLASS;
        }
        else {
            nodeClass = Tree.PARENT_NODE_CLASS;
            nodeClass = expanded ? nodeClass + " ui-treenode-expanded" : nodeClass + " ui-treenode-collapsed";
        }
        
        nodeClass = selected ? nodeClass + " ui-treenode-selected" : nodeClass + " ui-treenode-unselected";
        
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
        if(checkbox && selectable) {
            encodeCheckbox(context, tree, node, selected);
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
 
	public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey, boolean dynamic, boolean checkbox) throws IOException {
        
        if(rowKey != null) {
            ResponseWriter writer = context.getResponseWriter();
            tree.setRowKey(rowKey);
            boolean isLeaf = node.isLeaf();
            boolean expanded = node.isExpanded();
            boolean selectable = node.isSelectable();
            String toggleIcon = expanded ? Tree.EXPANDED_ICON_CLASS_V : Tree.COLLAPSED_ICON_CLASS_V;
            String stateIcon = isLeaf ? Tree.LEAF_ICON_CLASS : toggleIcon;
            UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
            Object datakey = tree.getDatakey();
            String nodeId = clientId + UINamingContainer.getSeparatorChar(context) + rowKey;

            //preselection
            boolean selected = node.isSelected();
            if(selected) {
                tree.getSelectedRowKeys().add(rowKey);
            }

            //style class of node
            String containerClass = isLeaf ? Tree.LEAF_NODE_CLASS : Tree.PARENT_NODE_CLASS;
            containerClass = selected ? containerClass + " ui-treenode-selected" : containerClass + " ui-treenode-unselected";
            containerClass = uiTreeNode.getStyleClass() == null ? containerClass : containerClass + " " + uiTreeNode.getStyleClass();

            writer.startElement("li", null);
                writer.writeAttribute("id", nodeId, null);
                writer.writeAttribute("data-rowkey", rowKey, null);
                writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);
                writer.writeAttribute("class", containerClass, null);
                writer.writeAttribute("role", "treeitem", null);

                if(datakey != null) {
                    writer.writeAttribute("data-datakey", datakey, null);
                }
                                
                //content
                String contentClass = selectable ? Tree.SELECTABLE_NODE_CONTENT_CLASS_V : Tree.NODE_CONTENT_CLASS_V;
                
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
                    if(checkbox && selectable) {
                        encodeCheckbox(context, tree, node, selected);
                    }

                    //node icon
                    encodeIcon(context, uiTreeNode, expanded);

                    //label
                    String nodeLabelClass = (selected && !checkbox) ? Tree.NODE_LABEL_CLASS + " ui-state-highlight" : Tree.NODE_LABEL_CLASS;
                        
                    writer.startElement("span", null);
                    writer.writeAttribute("class", nodeLabelClass, null);
                    uiTreeNode.encodeAll(context);
                    writer.endElement("span");

                writer.endElement("span");

                //children nodes                
                writer.startElement("ul", null);
                writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS , null);
                
                if(!expanded) {
                    writer.writeAttribute("style", "display:none", null);
                }
                
                if((dynamic && expanded) || !dynamic) {
                    encodeTreeNodeChildren(context, tree, node, clientId, rowKey, dynamic, checkbox);
                }
                
                writer.endElement("ul");

            writer.endElement("li");
        } 
        else {
            encodeTreeNodeChildren(context, tree, node, clientId, rowKey, dynamic, checkbox);
        }
	}
    
    public void encodeTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey, boolean dynamic, boolean checkbox) throws IOException {     

        int childIndex = 0;
        for(Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
            String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + UITree.SEPARATOR + childIndex;

            encodeTreeNode(context, tree, iterator.next(), clientId, childRowKey, dynamic, checkbox);

            childIndex++;
        }
    }

    protected void encodeIconStates(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,UITreeNode> nodes = tree.getTreeNodes();

        writer.write(",iconStates:{");
        
        boolean firstWritten = false;
        for(Iterator<String> it = nodes.keySet().iterator(); it.hasNext();) {
            String type = it.next();
            UITreeNode node = nodes.get(type);
            String expandedIcon = node.getExpandedIcon();
            String collapsedIcon = node.getCollapsedIcon();

            if(expandedIcon != null && collapsedIcon != null) {
                if(firstWritten) {
                    writer.write(",");
                }
                
                writer.write("'" + node.getType() + "' : {");
                writer.write("expandedIcon:'" + expandedIcon + "'");
                writer.write(",collapsedIcon:'" + collapsedIcon + "'");
                writer.write("}");
                
                firstWritten = true;
            }
        }

        writer.write("}");
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
		writer.endElement("input");
    }

	protected void encodeCheckbox(FacesContext context, Tree tree, TreeNode node, boolean selected) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String iconClass = selected ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_ICON_CLASS;

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