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
        decodeSelection(context, component);
            
        decodeBehaviors(context, component);
    }

    protected void decodeSelection(FacesContext context, UIComponent component) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        TreeTable tt = (TreeTable) component;
        String selectionMode = tt.getSelectionMode();
        
        //decode selection
        if(selectionMode != null) {
            String selectionValue = params.get(tt.getClientId(context) + "_selection");
            
            if(!isValueBlank(selectionValue)) {
                if(selectionMode.equals("single")) {
                    tt.setRowKey(selectionValue);

                    tt.setSelection(tt.getRowNode());
                } 
                else {
                    String[] rowKeys = selectionValue.split(",");
                    TreeNode[] selection = new TreeNode[rowKeys.length];

                    for(int i = 0; i < rowKeys.length; i++) {
                       tt.setRowKey(rowKeys[i]);

                       selection[i] = tt.getRowNode();
                    }

                    tt.setSelection(selection);
                }

                tt.setRowKey(null);     //cleanup
            }
        }
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
        String selectionMode = tt.getSelectionMode();
		
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('TreeTable','" + tt.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(selectionMode != null) 
            writer.write(",selectionMode:'" + selectionMode + "'");
                
        encodeClientBehaviors(context, tt);
        
		writer.write("});");
		
		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = tt.getClientId(context);
        boolean scrollable = tt.isScrollable();
        
        //Style class for container
        String containerClass = TreeTable.CONTAINER_CLASS;
        containerClass = scrollable ? containerClass + " " + TreeTable.SCROLLABLE_CONTAINER_CLASS : containerClass;
		containerClass = tt.getStyleClass() == null ? containerClass : containerClass + " " + tt.getStyleClass();
	
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", containerClass, null);
		if(tt.getStyle() != null) 
            writer.writeAttribute("style", tt.getStyle(), null);
        
        if(scrollable) {
            encodeScrollableMarkup(context, tt);
        } else {
            encodeRegularMarkup(context, tt);
        }
        
        if(tt.getSelectionMode() != null) {
            encodeSelectionHolder(context, tt);
        }
        
        writer.endElement("div");
	}
    
    protected void encodeScrollableMarkup(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int scrollHeight = tt.getScrollHeight();    
        int scrollWidth = tt.getScrollWidth();
        boolean hasScrollHeight = scrollHeight != Integer.MIN_VALUE;
        boolean hasScrollWidth = scrollWidth != Integer.MIN_VALUE;
        StringBuilder style = new StringBuilder();
        
        if(hasScrollHeight)
            style.append("height:").append(scrollHeight).append("px;");
        if(hasScrollWidth)
            style.append("width:").append(scrollWidth).append("px;");
        
        //header
        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_HEADER_CLASS, null);
        if(hasScrollWidth) {
            writer.writeAttribute("style", "width:" + scrollWidth + "px", null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_HEADER_BOX_CLASS, null);
        
        writer.startElement("table", null);
        writer.writeAttribute("role", "treegrid", null);
        if(tt.getTableStyle() != null) writer.writeAttribute("style", tt.getTableStyle(), null);
        if(tt.getTableStyleClass() != null) writer.writeAttribute("class", tt.getTableStyleClass(), null);
        
        encodeThead(context, tt);
        writer.endElement("table");
        
        writer.endElement("div");
        writer.endElement("div");

        //body
        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_BODY_CLASS, null);
        if(style.length() > 0) {
            writer.writeAttribute("style", style.toString(), null);
        }
        writer.startElement("table", null);
        writer.writeAttribute("role", "treegrid", null);
        if(tt.getTableStyle() != null) writer.writeAttribute("style", tt.getTableStyle(), null);
        if(tt.getTableStyleClass() != null) writer.writeAttribute("class", tt.getTableStyleClass(), null);
        
        encodeTbody(context, tt);
        writer.endElement("table");
        writer.endElement("div");
        
        //footer
        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_FOOTER_CLASS, null);
        if(hasScrollWidth) {
            writer.writeAttribute("style", "width:" + scrollWidth + "px", null);
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_FOOTER_BOX_CLASS, null);
        
        writer.startElement("table", null);
        writer.writeAttribute("role", "treegrid", null);
        if(tt.getTableStyle() != null) writer.writeAttribute("style", tt.getTableStyle(), null);
        if(tt.getTableStyleClass() != null) writer.writeAttribute("class", tt.getTableStyleClass(), null);
        
        encodeTfoot(context, tt);
        writer.endElement("table");
        
        writer.endElement("div");
        writer.endElement("div");
    }
    
    protected void encodeRegularMarkup(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("table", tt);
        writer.writeAttribute("role", "treegrid", null);
        if(tt.getTableStyle() != null) writer.writeAttribute("style", tt.getTableStyle(), null);
        if(tt.getTableStyleClass() != null) writer.writeAttribute("class", tt.getTableStyleClass(), null);

        encodeThead(context, tt);
        encodeTfoot(context, tt);
		encodeTbody(context, tt);
        
		writer.endElement("table");
    }

	protected void encodeThead(FacesContext context, TreeTable tt) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("thead", null);
        
        encodeFacet(context, tt, "header", TreeTable.HEADER_CLASS, "th");
        
		writer.startElement("tr", null);
        writer.writeAttribute("role", "row", null);
		
        for(int i = 0; i < tt.getChildCount(); i++) {
            UIComponent kid = tt.getChildren().get(i);
			
			if(kid instanceof Column && kid.isRendered()) {
				Column column = (Column) kid;
                UIComponent header = column.getFacet("header");
                String headerText = column.getHeaderText();
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
                styleClass = styleClass == null ? TreeTable.COLUMN_HEADER_CLASS  : TreeTable.COLUMN_HEADER_CLASS  + " " + styleClass;

				writer.startElement("th", null);
                writer.writeAttribute("id", column.getClientId(context), null);
                writer.writeAttribute("class", styleClass, null);
                writer.writeAttribute("role", "columnheader", null);
                if(style != null) {
                    writer.writeAttribute("style", style, null);
                }
                
				writer.startElement("div", null);
                writer.writeAttribute("class", TreeTable.COLUMN_CONTENT_WRAPPER, null);
                if(column.getWidth() != -1) {
                    writer.writeAttribute("style", "width:" + column.getWidth() + "px", null);
                }
                
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
            boolean scrollable = tt.isScrollable();
            ResponseWriter writer = context.getResponseWriter();
            tt.setRowKey(rowKey);
            String nodeId = clientId + "_node_" + rowKey;
            String icon = treeNode.isExpanded() ? TreeTable.COLLAPSE_ICON : TreeTable.EXPAND_ICON;
            int depth = rowKey.split(UITree.SEPARATOR).length - 1;
            boolean selectionEnabled = tt.getSelectionMode() != null;
            boolean selectable = treeNode.isSelectable() && selectionEnabled;
            boolean selected = treeNode.isSelected();
            
            String rowStyleClass = selected ? TreeTable.SELECTED_ROW_CLASS : TreeTable.ROW_CLASS;
            rowStyleClass = selectable ? rowStyleClass + " " + TreeTable.SELECTABLE_NODE_CLASS : rowStyleClass;
            rowStyleClass = rowStyleClass + " " + treeNode.getType();
            
            if(selected) {
                tt.getSelectedRowKeys().add(rowKey);
            }

            writer.startElement("tr", null);
            writer.writeAttribute("id", nodeId, null);
            writer.writeAttribute("class", rowStyleClass, null);
            writer.writeAttribute("role", "row", null);
            writer.writeAttribute("aria-expanded", String.valueOf(treeNode.isExpanded()), null);
            if(selectionEnabled) {
                writer.writeAttribute("aria-selected", String.valueOf(selected), null);
            }

            for(int i=0; i < tt.getChildren().size(); i++) {
                UIComponent kid = (UIComponent) tt.getChildren().get(i);

                if(kid instanceof Column && kid.isRendered()) {
                    Column column = (Column) kid;
                    int width = column.getWidth();

                    writer.startElement("td", null);
                    writer.writeAttribute("role", "gridcell", null);
                    if(column.getStyleClass() != null) writer.writeAttribute("class", column.getStyleClass(), null);
                    if(column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);

                    writer.startElement("div", null);
                    writer.writeAttribute("class", TreeTable.COLUMN_CONTENT_WRAPPER, null);

                    if(i == 0) {
                        int paddingLeft = (depth * 15);
                        String cellStyle = "padding-left:" + paddingLeft + "px";
                        if(scrollable) {
                            width = width - (paddingLeft - 10);
                            cellStyle = cellStyle + ";width:" + width + "px";
                        }
                        writer.writeAttribute("style", cellStyle, null);

                        writer.startElement("span", null);
                        writer.writeAttribute("class", icon, null);
                        if(treeNode.getChildCount() == 0) {
                            writer.writeAttribute("style", "visibility:hidden", null);
                        }
                        writer.endElement("span");
                    }
                    else if(scrollable) {
                        writer.writeAttribute("style", "width:" + width + "px", null);
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
    
    protected void encodeFacet(FacesContext context, TreeTable tt, String name, String styleClass, String tag) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = tt.getFacet(name);

        if(facet != null) {
            writer.startElement("tr", null);
            writer.startElement(tag, null);
            writer.writeAttribute("colspan", tt.getColumnsCount(), null);
            writer.writeAttribute("class", styleClass, null);
            facet.encodeAll(context);
            writer.endElement(tag);
            writer.endElement("tr");
        }
	}
    
    protected void encodeTfoot(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("tfoot", null);

        encodeFacet(context, tt, "footer", TreeTable.FOOTER_CLASS, "td");

        if(tt.hasFooterColumn()) {
            writer.startElement("tr", null);

            for(int i = 0; i < tt.getChildCount(); i++) {
                UIComponent kid = tt.getChildren().get(i);

                if(kid instanceof Column && kid.isRendered()) {
                    Column column = (Column) kid;
                    UIComponent footer = column.getFacet("footer");
                    String footerText = column.getHeaderText();

                    String columnStyleClass = column.getStyleClass() == null ? TreeTable.COLUMN_HEADER_CLASS : TreeTable.COLUMN_HEADER_CLASS + " " + column.getStyleClass();
                    String style = column.getStyle();

                    writer.startElement("td", null);
                    writer.writeAttribute("id", column.getClientId(context), null);
                    writer.writeAttribute("class", columnStyleClass, null);
                    if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }

                    writer.startElement("div", null);
                    writer.writeAttribute("class", TreeTable.COLUMN_CONTENT_WRAPPER, null);
                    if(column.getWidth() != -1) {
                        writer.writeAttribute("style", "width:" + column.getWidth() + "px", null);
                    }

                    if(footer != null) 
                        footer.encodeAll(context);
                    else if(footerText != null)
                        writer.write(footerText);

                    writer.endElement("div");

                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }
        
        writer.endElement("tfoot");
    }
	
    @Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
    @Override
	public boolean getRendersChildren() {
		return true;
	}

    private void encodeSelectionHolder(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String name = tt.getClientId(context) + "_selection";
        
        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("value", tt.getSelectedRowKeysAsString(), null);
        writer.endElement("input");
    }
}