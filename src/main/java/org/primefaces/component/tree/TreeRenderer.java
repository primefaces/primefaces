/*
 * Copyright 2009-2011 Prime Technology.
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.event.NodeSelectEvent;

import org.primefaces.model.TreeExplorer;
import org.primefaces.model.TreeExplorerImpl;
import org.primefaces.model.TreeModel;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class TreeRenderer extends CoreRenderer {
	
	private TreeExplorer treeExplorer;
	
	public TreeRenderer() {
		treeExplorer = new TreeExplorerImpl();
	}

    @Override
	public void decode(FacesContext context, UIComponent component) {
		Tree tree = (Tree) component;
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String clientId = tree.getClientId(context);

        //state change

        //selection
        if(tree.getSelectionMode() != null) {
            String selection = params.get(clientId + "_selection");
            String instantSelection = params.get(clientId + "_instantSelection");
            boolean isSingle = tree.getSelectionMode().equalsIgnoreCase("single");

            if(selection.equals("")) {
                if(isSingle)
                    tree.setSelection(null);
                else
                    tree.setSelection(new TreeNode[0]);
            }
            else {
                String[] selectedRowKeys = selection.split(",");
                TreeModel model = new TreeModel((TreeNode) tree.getValue());

                if(isSingle) {
                    TreeNode selectedNode = treeExplorer.findTreeNode(selectedRowKeys[0], model);
                    tree.setSelection(selectedNode);
                }
                else {
                    TreeNode[] selectedNodes = new TreeNode[selectedRowKeys.length];

                    for(int i = 0 ; i < selectedRowKeys.length; i++) {
                        selectedNodes[i] = treeExplorer.findTreeNode(selectedRowKeys[i], model);
                        model.setRowIndex(-1);  //reset
                    }

                    tree.setSelection(selectedNodes);
                }

                //Queue event to invoke nodeSelectListener
                if(instantSelection != null) {
                    model.setRowIndex(-1);  //reset
                    TreeNode selectedNode = treeExplorer.findTreeNode(instantSelection, model);

                    tree.queueEvent(new NodeSelectEvent(tree, selectedNode));
                }
            }
        }
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tree tree = (Tree) component;

        if(tree.isNodeLoadRequest(context)) {
            encodeDynamicNode(context, tree);
        }
        else {
            encodeMarkup(context, tree);
            encodeScript(context, tree);
        }
	}
	
	public void encodeDynamicNode(FacesContext context, Tree tree) throws IOException {
        TreeNode root = (TreeNode) tree.getValue();
        String rowKey = context.getExternalContext().getRequestParameterMap().get(tree.getClientId(context) + "_loadNode");
        TreeNode treeNode = treeExplorer.findTreeNode(rowKey, new TreeModel(root));

        encodeTreeNodeChildren(context, tree, treeNode, rowKey, true, false);
	}
	
	protected void encodeScript(FacesContext context, Tree tree) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        writer.write(tree.resolveWidgetVar() + " = new PrimeFaces.widget.Tree('" + clientId + "', {");

        writer.write("dynamic:" + dynamic);

        if(dynamic) {
            writer.write(",formId:'" + ComponentUtils.findParentForm(context, tree).getClientId(context) + "'");
            writer.write(",actionURL:'" + getActionURL(context) + "'");
            writer.write(",cache:" + tree.isCache());
        }

        //selection
        if(selectionMode != null) {
            writer.write(",selectionMode:'" + selectionMode + "'");

            //instant selection
            if(tree.getNodeSelectListener() != null) {
                //update is deprecated and used for backward compatibility
                String onSelectUpdate = tree.getOnSelectUpdate() != null ? tree.getOnSelectUpdate() : tree.getUpdate();

                writer.write(",instantSelect:true");

                //onselectStart and onselectComplete are deprecated but still here for backward compatibility for some time
                if(tree.getOnselectStart() != null) writer.write(",onSelectStart:function() {" + tree.getOnselectStart() + "}");
                if(tree.getOnselectStart() != null) writer.write(",onSelectComplete:function(xhr, status, args) {" + tree.getOnselectStart() + "}");
                if(tree.getOnSelectStart() != null) writer.write(",onSelectStart:function() {" + tree.getOnSelectStart() + "}");
                if(tree.getOnSelectComplete() != null) writer.write(",onSelectComplete:function(xhr, status, args) {" + tree.getOnSelectComplete() + "}");

                if(onSelectUpdate != null)
                    writer.write(",onSelectUpdate:'" + ComponentUtils.findClientIds(context, tree, onSelectUpdate) + "'");
            }
        }

        //state change listeners
        if(tree.getNodeExpandListener() != null)
            encodeStateChangeListener(context, tree, "hasExpandListener", "onExpandUpdate", tree.getOnExpandUpdate());
        if(tree.getNodeCollapseListener() != null)
            encodeStateChangeListener(context, tree, "hasCollapseListener", "onCollapseUpdate", tree.getOnCollapseUpdate());

        //collapse listener
        if(tree.getNodeCollapseListener() != null) {
            writer.write(",hasCollapseListener:true");

            String onCollapseUpdate = tree.getOnCollapseUpdate();
            if(onCollapseUpdate != null)
                writer.write(",onCollapseUpdate:'" + ComponentUtils.findClientIds(context, tree, onCollapseUpdate) + "'");
        }

        //expand/collapse icon states for specific treenodes
        encodeIconStates(context, tree);

        writer.write("});");

		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tree.getClientId(context);
        TreeNode root = (TreeNode) tree.getValue();
        int rowIndex = 0;
        boolean dynamic = tree.isDynamic();
        String styleClass = tree.getStyleClass();
        styleClass = styleClass == null ? Tree.STYLE_CLASS : Tree.STYLE_CLASS + " " + styleClass;
        
		writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
		if(tree.getStyle() != null) writer.writeAttribute("style", tree.getStyle(), null);

        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        for(TreeNode child : root.getChildren()) {
            encodeTreeNode(context, tree, child, String.valueOf(rowIndex), dynamic);
            
            rowIndex++;
        }

		writer.endElement("ul");

        if(tree.getSelectionMode() != null) {
            encodeSelectionHolder(context, tree);
        }

		writer.endElement("div");
	}

	public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node, String rowKey, boolean dynamic) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        context.getExternalContext().getRequestMap().put(tree.getVar(), node.getData());
        boolean isLeaf = node.isLeaf();
        boolean expanded = node.isExpanded();
        String iconClass = expanded ? Tree.EXPANDED_ICON_CLASS : Tree.COLLAPSED_ICON_CLASS;
        String nodeId = tree.getClientId() + "_node_" + rowKey;
        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());

        String nodeClass = isLeaf ? Tree.LEAF_CLASS : Tree.PARENT_CLASS;
        if(uiTreeNode.getStyleClass() != null) {
            nodeClass = nodeClass + " " + uiTreeNode.getStyleClass();
        }
        nodeClass = nodeClass + " " + uiTreeNode.getType();
        
        
		writer.startElement("li", null);
            writer.writeAttribute("id", nodeId, null);
            writer.writeAttribute("class", nodeClass, null);

            //label
            writer.startElement("div", null);
            writer.writeAttribute("class", Tree.NODE_CLASS, null);

                writer.startElement("span", null);
                writer.writeAttribute("class", Tree.NODE_CONTENT_CLASS, null);

                    //state icon
                    if(!isLeaf) {
                        writer.startElement("span", null);
                        writer.writeAttribute("class", iconClass, null);
                        writer.endElement("span");
                    }

                    //node icon
                    writer.startElement("span", null);
                    String icon = uiTreeNode.getIconToRender(expanded);
                    if(icon != null) {
                        writer.writeAttribute("class", icon, null);
                    }
                    writer.endElement("span");

                    //content
                    /*writer.startElement("a", null);
                    writer.writeAttribute("href", "#", null);*/
                        writer.startElement("span", null);

                        uiTreeNode.encodeAll(context);

                        writer.endElement("span");
                    //writer.endElement("a");

                writer.endElement("span");

            writer.endElement("div");

            //children
            if(!isLeaf && !dynamic) {
                encodeTreeNodeChildren(context, tree, node, rowKey, dynamic, expanded);
            }

        writer.endElement("li");
	}

    public void encodeTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String parentRowKey, boolean dynamic, boolean expanded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int rowIndex = 0;

        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.NODES_CLASS , null);

        if(!expanded) {
            writer.writeAttribute("style", "display:none", null);
        }

        for(TreeNode childNode : node.getChildren()) {
            String childRowKey = parentRowKey + "_" + rowIndex;
            encodeTreeNode(context, tree, childNode, childRowKey, dynamic);
            rowIndex++;
        }

        writer.endElement("ul");
    }

    protected void encodeIconStates(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,UITreeNode> nodes = tree.getTreeNodes();

        writer.write(",iconStates:{");

        for(Iterator<String> it = nodes.keySet().iterator(); it.hasNext();) {
            String type = it.next();
            UITreeNode node = nodes.get(type);
            String expandedIcon = node.getExpandedIcon();
            String collapsedIcon = node.getCollapsedIcon();

            if(expandedIcon != null && collapsedIcon != null) {
                writer.write("'" + node.getType() + "' : {");
                writer.write("expandedIcon:'" + expandedIcon + "'");
                writer.write(",collapsedIcon:'" + collapsedIcon + "'");
                writer.write("}");

                if(it.hasNext())
                    writer.write(",");
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
		writer.endElement("input");
    }

    protected void encodeStateChangeListener(FacesContext context, Tree tree, String configParam, String updateParam, String update) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write("," + configParam + ":true");

        if(update != null)
            writer.write("," + updateParam + ":'" + ComponentUtils.findClientIds(context, tree, update) + "'");
    }

    @Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}