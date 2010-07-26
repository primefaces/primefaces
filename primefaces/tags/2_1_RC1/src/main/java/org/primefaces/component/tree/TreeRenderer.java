/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeExplorer;
import org.primefaces.model.TreeExplorerImpl;
import org.primefaces.model.TreeModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class TreeRenderer extends CoreRenderer implements PartialRenderer {
	
	private TreeExplorer treeExplorer;
	
	public TreeRenderer() {
		treeExplorer = new TreeExplorerImpl();
	}
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Tree tree = (Tree) component;
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		String clientId = tree.getClientId(facesContext);
		String rowKeyParam = clientId + "_rowKey";
		String actionParam = clientId + "_action";
		String selectionParam = clientId + "_selection";
		
		if(params.containsKey(clientId) && params.containsKey(rowKeyParam) && params.containsKey(actionParam)) {
			String rowKey = params.get(rowKeyParam);
			String event = params.get(actionParam);
			TreeNode root = (TreeNode) tree.getValue();
			TreeNode currentNode = treeExplorer.findTreeNode(rowKey, new TreeModel(root));
			
			switch(TreeNodeEvent.valueOf(event)) {
				case SELECT:
					tree.queueEvent(new NodeSelectEvent(tree, currentNode));
				break;
				
				case EXPAND:
					currentNode.setExpanded(true);
					tree.queueEvent(new NodeExpandEvent(tree, currentNode));
				break;

				case COLLAPSE:
					currentNode.setExpanded(false);
					tree.queueEvent(new NodeCollapseEvent(tree, currentNode));
				break;
			}
		}
		
		//Selection
		if(params.containsKey(selectionParam)) {
			String selectedNodesValue = params.get(selectionParam);
			boolean isSingle = tree.getSelectionMode().equalsIgnoreCase("single");
			
			if(selectedNodesValue.equals("")) {
				if(isSingle)
					tree.setSelection(null);
				else
					tree.setSelection(new TreeNode[0]);
			}
			else {
				String[] selectedRowKeys = selectedNodesValue.split(",");
				TreeModel model = new TreeModel((TreeNode) tree.getValue());
				
				if(isSingle) {
					TreeNode selectedNode = treeExplorer.findTreeNode(selectedRowKeys[0], model);
					tree.setSelection(selectedNode);
					
				} else {
					TreeNode[] selectedNodes = new TreeNode[selectedRowKeys.length];

					for(int i = 0 ; i < selectedRowKeys.length; i++) {
						selectedNodes[i] = treeExplorer.findTreeNode(selectedRowKeys[i], model);
						model.setRowIndex(-1);	//reset
					}
					
					tree.setSelection(selectedNodes);
				}
			}
		}
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		Tree tree = (Tree) component;
		ResponseWriter writer = facesContext.getResponseWriter();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = tree.getClientId(facesContext);
		TreeNode root = (TreeNode) tree.getValue();
		
		String rowKey = params.get(clientId + "_rowKey");
		TreeNode currentNode = treeExplorer.findTreeNode(rowKey, new TreeModel(root));
		int rowIndex = 0;
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<?xml version=\"1.0\" encoding=\"" + response.getCharacterEncoding() + "\"?>");
		writer.write("<nodes>");
		
		for(Iterator<TreeNode> iterator = currentNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode child = iterator.next();
			UITreeNode uiTreeNode = tree.getUITreeNodeByType(child.getType());
			
			facesContext.getExternalContext().getRequestMap().put(tree.getVar(), child.getData());
			writer.write("<node>");
			
				writer.write("<content>");
				writer.startCDATA();
				renderChildren(facesContext, uiTreeNode);
				writer.endCDATA();
				writer.write("</content>");
				
				writer.write("<rowKey>" + rowKey + "." + rowIndex + "</rowKey>");
				writer.write("<isLeaf>" + child.isLeaf() + "</isLeaf>");
				if(uiTreeNode.getStyleClass() != null) {
					writer.write("<contentClass>" + uiTreeNode.getStyleClass() + "</contentClass>");
				}
			writer.write("</node>");
			
			rowIndex ++;
			
			facesContext.getExternalContext().getRequestMap().remove(tree.getVar());
		}
		
		writer.write("</nodes>");
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Tree tree = (Tree) component;
		
		encodeMarkup(facesContext, tree);
		encodeScript(facesContext, tree);
	}
	
	protected void encodeScript(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tree.getClientId(facesContext);
		String formClientId = null;
		String treeVar = createUniqueWidgetVar(facesContext, tree);
		TreeNode root = (TreeNode) tree.getValue();
		
		UIComponent parentForm = ComponentUtils.findParentForm(facesContext, tree);
		if(parentForm != null)
			formClientId = parentForm.getClientId(facesContext);
		else
			throw new FacesException("Tree:" + clientId + " needs to be enclosed in a form");
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		//Nodes
		writer.write(treeVar + " = new PrimeFaces.widget.TreeView('" + clientId + "', [\n");
		if(root != null) {
			int rowIndex = 0;
			for(Iterator<TreeNode> iterator = root.getChildren().iterator(); iterator.hasNext();) {
				encodeTreeNode(facesContext, tree, iterator.next(), String.valueOf(rowIndex));
				rowIndex ++;
				
				if(iterator.hasNext())
					writer.write(",");
			}
		}
		writer.write("],{");
		
		//Config
		writer.write("dynamic:" + tree.isDynamic());
		writer.write(",actionURL:'" + getActionURL(facesContext) + "'");
		writer.write(",formId:'" + formClientId + "'");
		writer.write(",cache:" + tree.isCache());
		
		//Selection
		if(tree.getSelectionMode() != null) {
			writer.write(",selectionMode:'" + tree.getSelectionMode() + "'");
			writer.write(",propagateHighlightDown:" + tree.isPropagateSelectionDown());
			writer.write(",propagateHighlightUp:" + tree.isPropagateSelectionUp());
			
			if(tree.getUpdate() != null) writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, tree, tree.getUpdate()) + "'");
			if(tree.getOnselectStart() != null) writer.write(",onselectStart:function(xhr){" + tree.getOnselectStart() + ";}");
			if(tree.getOnselectComplete() != null) writer.write(",onselectComplete:function(xhr,status,args){" + tree.getOnselectComplete() + ";}");
		}
		
		if(tree.getNodeSelectListener() != null) writer.write(",hasSelectListener:true");
		if(tree.getNodeExpandListener() != null) writer.write(",hasExpandListener:true");
		if(tree.getNodeCollapseListener() != null) writer.write(",hasCollapseListener:true");
		
		if(tree.getOnNodeClick() != null) {
			writer.write(",onNodeClick:" + tree.getOnNodeClick());
		}
		
		writer.write("});\n");
		
		//Animations
		if(tree.getExpandAnim() != null)
			writer.write(treeVar + ".setExpandAnim(YAHOO.widget.TVAnim." + tree.getExpandAnim() + ");\n");
		if(tree.getCollapseAnim() != null)
			writer.write(treeVar + ".setCollapseAnim(YAHOO.widget.TVAnim." + tree.getCollapseAnim() + ");\n");
		
		writer.write(treeVar + ".render();\n");

		writer.endElement("script");
	}
	
	protected void encodeTreeNode(FacesContext facesContext, Tree tree, TreeNode node, String rowKey) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		int rowIndex = 0;
		UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
		
		//On initial page load, if tree is set to expanded, mark all nodes as expanded
		if(!isPostBack() && tree.isExpanded()) {
			node.setExpanded(true);
		}
		
		facesContext.getExternalContext().getRequestMap().put(tree.getVar(), node.getData());
		writer.write("{html:'");
		renderChildren(facesContext, uiTreeNode);
		facesContext.getExternalContext().getRequestMap().remove(tree.getVar());

		writer.write("',type:'html'");
		writer.write(",rowKey:'" + rowKey + "'");
		
		if(node.isLeaf()) writer.write(",isLeaf:true");
		if(uiTreeNode.getStyleClass() != null) writer.write(",contentStyle:'" + uiTreeNode.getStyleClass() + "'");
		
		if(node.isExpanded()) {
			writer.write(",expanded:true");
			if(tree.isDynamic())
				writer.write(",dynamicLoadComplete:true");
		}
		
		if(node.isExpanded() || !tree.isDynamic()) {
			writer.write(",children:[");
			
			for(Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
				String childRowKey = rowKey + "." + rowIndex;
				encodeTreeNode(facesContext, tree, iterator.next(), childRowKey);
				
				rowIndex ++;

				if(iterator.hasNext())
					writer.write(",");
			}
			
			writer.write("]");
		}
		
		writer.write("}");
	}

	protected void encodeMarkup(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tree.getClientId(facesContext);
		boolean selectionEnabled = tree.getSelectionMode() != null;
		
		writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
		if(tree.getStyle() != null) writer.writeAttribute("style", tree.getStyle(), null);
		if(tree.getStyleClass() != null) writer.writeAttribute("class", tree.getStyleClass(), null);
		
		writer.startElement("div", tree);
		writer.writeAttribute("id", clientId + "_container", null);
		if(selectionEnabled) {
			String selectionClass = tree.getSelectionMode().equalsIgnoreCase("checkbox") ? "ygtv-checkbox" : "ygtv-highlight";
			writer.writeAttribute("class", selectionClass, null);
		}
		writer.endElement("div");
		
		if(selectionEnabled) {
			writer.startElement("input", null);
			writer.writeAttribute("id", clientId + "_selection", null);
			writer.writeAttribute("name", clientId + "_selection", null);
			writer.writeAttribute("type", "hidden", null);
			writer.endElement("input");
		}
		
		writer.endElement("div");
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}