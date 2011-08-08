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
package org.primefaces.component.treetable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UITree;

import org.primefaces.component.column.Column;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;

public class TreeTableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		TreeTable tt = (TreeTable) component;
        String clientId = tt.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
        String nodeKey = params.get(clientId + "_expand");
        if(nodeKey != null) {
            tt.setRowKey(nodeKey);
            TreeNode node = tt.getRowNode();
            node.setExpanded(true);
            
            encodeNode(context, tt, node, clientId, nodeKey);
        } 
        else {
            encodeMarkup(context, tt);
            encodeScript(context, tt); 
        }
	}
	
	protected void encodeScript(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tt.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(tt.resolveWidgetVar() + " = new PrimeFaces.widget.TreeTable('" + clientId + "', {");
        writer.write("selection:'none'");
        
        encodeClientBehaviors(context, tt);

		writer.write("});");
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tt.getClientId(context);
		String styleClass = tt.getStyleClass() == null ? TreeTable.CONTAINER_CLASS : TreeTable.CONTAINER_CLASS + " " + tt.getStyleClass();
	
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", styleClass, null);
		if(tt.getStyle() != null) 
            writer.writeAttribute("style", tt.getStyle(), null);
        
        encodeFacet(context, tt, "header", TreeTable.HEADER_CLASS);
        
		writer.startElement("table", tt);

        encodeThead(context, tt);
		encodeTbody(context, tt);

		writer.endElement("table");
        
        encodeFacet(context, tt, "footer", TreeTable.FOOTER_CLASS);
        
        writer.endElement("div");
	}

	protected void encodeThead(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("thead", null);
		writer.startElement("tr", null);
        
		for(UIComponent kid : tt.getChildren()) {
			
			if(kid instanceof Column && kid.isRendered()) {
				Column column = (Column) kid;
                UIComponent header = column.getFacet("header");
                String headerText = column.getHeaderText();
				String columnStyleClass = column.getStyleClass() == null ? TreeTable.COLUMN_CONTENT_WRAPPER : TreeTable.COLUMN_CONTENT_WRAPPER + " " + column.getStyleClass();
                String style = column.getStyle();

				writer.startElement("th", null);
                writer.writeAttribute("class", TreeTable.COLUMN_HEADER_CLASS, null);

				writer.startElement("div", null);
                writer.writeAttribute("class", columnStyleClass, null);
                if(style != null)
                    writer.writeAttribute("style", style, null);
                
                if(header != null) 
                    header.encodeAll(context);
                else if(headerText != null)
                    writer.write(headerText);
                
                writer.endElement("div");
				
				writer.endElement("th");
			}
		}
        
		writer.endElement("tr");
		writer.endElement("thead");
	}
       
    protected void encodeTbody(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		TreeNode root = (TreeNode) tt.getValue();
        String clientId = tt.getClientId(context);
		
		writer.startElement("tbody", null);
		writer.writeAttribute("class", TreeTable.DATA_CLASS, null);

		if(root != null) {
            encodeNode(context, tt, root, clientId, null);
		}
        
        //cleanup
        tt.setRowKey(null);
		
		writer.endElement("tbody");
	}
    
    protected void encodeNode(FacesContext context, TreeTable tt, TreeNode treeNode, String clientId, String rowKey) throws IOException {
        
        if(rowKey != null) {
            ResponseWriter writer = context.getResponseWriter();
            tt.setRowKey(rowKey);
            String nodeId = clientId + "_node_" + rowKey;
            String icon = treeNode.isExpanded() ? TreeTable.COLLAPSE_ICON : TreeTable.EXPAND_ICON;
            int depth = rowKey.split(UITree.SEPARATOR).length - 1;

            writer.startElement("tr", null);
            writer.writeAttribute("id", nodeId, null);
            writer.writeAttribute("class", TreeTable.ROW_CLASS, null);

            for(int i=0; i < tt.getChildren().size(); i++) {
                UIComponent kid = (UIComponent) tt.getChildren().get(i);

                if(kid instanceof Column && kid.isRendered()) {
                    Column column = (Column) kid;
                    String styleClass = column.getStyleClass() == null ? TreeTable.COLUMN_CONTENT_WRAPPER : TreeTable.COLUMN_CONTENT_WRAPPER + " " + column.getStyleClass();
                    String style = column.getStyle();

                    writer.startElement("td", null);

                    writer.startElement("div", null);
                    writer.writeAttribute("class", styleClass, null);

                    //icon
                    if(i == 0) {
                        String padding = "padding-left:" + (depth * 15) + "px";
                        style = style == null ? padding : style + ";" + padding;

                        writer.writeAttribute("style", style, null);

                        writer.startElement("span", null);
                        writer.writeAttribute("class", icon, null);
                        if(treeNode.getChildCount() == 0) {
                            writer.writeAttribute("style", "visibility:hidden", null);
                        }
                        writer.endElement("span");
                    } 
                    else if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }

                    //content
                    column.encodeAll(context);

                    writer.endElement("div");

                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }
        
        //render child nodes if node is expanded or node itself is the root
        if(treeNode.isExpanded() || treeNode.getParent() == null) {
            int childIndex = 0;
            for(Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext();) {
                String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + UITree.SEPARATOR + childIndex;

                encodeNode(context, tt, iterator.next(), clientId, childRowKey);

                childIndex++;
            }
        }
    }
    
    protected void encodeFacet(FacesContext context, TreeTable tt, String name, String styleClass) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = tt.getFacet(name);

        if(facet != null) {
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
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