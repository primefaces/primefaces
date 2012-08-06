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

        if(tree.isNodeExpandRequest(context)) {
            String clientId = tree.getClientId(context);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String rowKey = params.get(clientId + "_expandNode");
            tree.setRowKey(rowKey);
            TreeNode node = tree.getRowNode();
            node.setExpanded(true);
            
            encodeTreeNodeChildren(context, tree, node, clientId, rowKey, tree.isDynamic(), tree.isCheckboxSelection());
            tree.setRowKey(null);
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
			
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Tree','" + tree.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",dynamic:" + dynamic);
        
        if(dynamic) {
            writer.write(",cache:" + tree.isCache());
        }

        //selection
        if(selectionMode != null) {
            writer.write(",selectionMode:'" + selectionMode + "'");
            writer.write(",highlight:" + tree.isHighlight());
        }

        if(tree.getOnNodeClick() != null) {
            writer.write(",onNodeClick:function(node) {" + tree.getOnNodeClick() + "}");
        }
        
        if(tree.isAnimate()) {
            writer.write(",animate:true");
        }

        //expand-collapse icon states for specific treenodes
        encodeIconStates(context, tree);
        
        encodeClientBehaviors(context, tree);

        writer.write("});");

		endScript(writer);
	}
	
	protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tree.getClientId(context);
        TreeNode root = (TreeNode) tree.getValue();
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean selectable = selectionMode != null;
        boolean multiselectable = selectable && selectionMode.equals("single");
        boolean checkbox = selectable && selectionMode.equals("checkbox");
        
        //container class
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

	public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey, boolean dynamic, boolean checkbox) throws IOException {
        
        if(rowKey != null) {
            ResponseWriter writer = context.getResponseWriter();
            tree.setRowKey(rowKey);
            boolean isLeaf = node.isLeaf();
            boolean expanded = node.isExpanded();
            boolean selectable = node.isSelectable();
            String toggleIcon = expanded ? Tree.EXPANDED_ICON_CLASS : Tree.COLLAPSED_ICON_CLASS;
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
            containerClass = uiTreeNode.getStyleClass() == null ? containerClass : containerClass + " " + uiTreeNode.getStyleClass();
            containerClass = containerClass + " " + uiTreeNode.getType();

            writer.startElement("li", null);
                writer.writeAttribute("id", nodeId, null);
                writer.writeAttribute("data-rowkey", rowKey, null);
                writer.writeAttribute("class", containerClass, null);
                writer.writeAttribute("role", "treeitem", null);

                if(datakey != null) {
                    writer.writeAttribute("data-datakey", datakey, null);
                }
                                
                //content
                String contentClass = selectable ? Tree.SELECTABLE_NODE_CONTENT_CLASS : Tree.NODE_CONTENT_CLASS;
                
                
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
                    writer.startElement("span", null);
                    String icon = uiTreeNode.getIconToRender(expanded);
                    if(icon != null) {
                        writer.writeAttribute("class", Tree.NODE_ICON_CLASS + " " + icon, null);
                    }
                    writer.endElement("span");

                    //label
                    String nodeLabelClass = (selected && !checkbox) ? Tree.NODE_LABEL_CLASS + " ui-state-highlight" : Tree.NODE_LABEL_CLASS;
                        
                    writer.startElement("span", null);
                    writer.writeAttribute("class", nodeLabelClass, null);
                    uiTreeNode.encodeAll(context);
                    writer.endElement("span");

                writer.endElement("span");

                //children nodes
                boolean shouldRender = (dynamic && expanded) || !dynamic;
                
                writer.startElement("ul", null);
                writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS , null);
                
                if(!expanded)
                    writer.writeAttribute("style", "display:none", null);
                
                if(shouldRender)
                    encodeTreeNodeChildren(context, tree, node, clientId, rowKey, dynamic, checkbox);
                
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