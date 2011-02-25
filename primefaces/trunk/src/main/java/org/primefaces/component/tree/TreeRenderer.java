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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.TreeExplorer;
import org.primefaces.model.TreeExplorerImpl;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;

public class TreeRenderer extends CoreRenderer {
	
	private TreeExplorer treeExplorer;
	
	public TreeRenderer() {
		treeExplorer = new TreeExplorerImpl();
	}

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Tree tree = (Tree) component;
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		String clientId = tree.getClientId(facesContext);
		
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tree tree = (Tree) component;

        if(tree.isNodeLoadRequest(context)) {
            encodeDynamicNodes(context, tree);
        } else {
            encodeMarkup(context, tree);
            encodeScript(context, tree);
        }
	}
	
	public void encodeDynamicNodes(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
	}
	
	protected void encodeScript(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tree.getClientId(facesContext);
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);


        writer.write(tree.resolveWidgetVar() + " = new PrimeFaces.widget.Tree('" + clientId + "', {");

        writer.write("});");

		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tree.getClientId(context);
        TreeNode root = (TreeNode) tree.getValue();
        String var = tree.getVar();
        String styleClass = tree.getStyleClass();
        styleClass = styleClass == null ? Tree.STYLE_CLASS : Tree.STYLE_CLASS + " " + styleClass;
        
		writer.startElement("div", tree);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
		if(tree.getStyle() != null) writer.writeAttribute("style", tree.getStyle(), null);

        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        for(TreeNode child : root.getChildren()) {
            encodeTreeNode(context, tree, child);
        }

		writer.endElement("ul");

		writer.endElement("div");
	}

	public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        context.getExternalContext().getRequestMap().put(tree.getVar(), node.getData());
        boolean isLeaf = node.isLeaf();
        boolean expanded = node.isExpanded();
        String nodeClass = isLeaf ? Tree.LEAF_CLASS : Tree.PARENT_CLASS;
        String iconClass = expanded ? Tree.EXPANDED_ICON_CLASS : Tree.COLLAPSED_ICON_CLASS;
        
		writer.startElement("li", null);
            writer.writeAttribute("class", nodeClass, null);

            //label
            writer.startElement("div", null);
            writer.writeAttribute("class", Tree.NODE_CLASS, null);

                writer.startElement("span", null);
                writer.writeAttribute("class", Tree.NODE_CONTENT_CLASS, null);

                    if(!isLeaf) {
                        writer.startElement("span", null);
                        writer.writeAttribute("class", iconClass, null);
                        writer.endElement("span");
                    }

                    writer.startElement("span", null);
                    writer.endElement("span");

                    writer.startElement("a", null);
                    writer.writeAttribute("href", "#", null);
                        writer.startElement("span", null);

                        tree.getUITreeNodeByType(node.getType()).encodeAll(context);

                        writer.endElement("span");
                    writer.endElement("a");

                writer.endElement("span");

            writer.endElement("div");

            //children
            if(!isLeaf) {
                writer.startElement("ul", null);
                writer.writeAttribute("class", Tree.NODES_CLASS , null);

                if(!expanded) {
                    writer.writeAttribute("style", "display:none", null);
                }

                for(TreeNode childNode : node.getChildren()) {
                    encodeTreeNode(context, tree, childNode);
                }

                writer.endElement("ul");
            }

        writer.endElement("li");
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