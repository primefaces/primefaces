/*
 * Copyright 2010 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class TreeTableRenderer extends CoreRenderer {
		
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		TreeTable treeTable = (TreeTable) component;
		
		encodeMarkup(facesContext, treeTable);
		encodeScript(facesContext, treeTable);
	}
	
	protected void encodeScript(FacesContext facesContext, TreeTable treeTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = treeTable.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, treeTable);
		String initialState = treeTable.isExpanded() ? "expanded" : "collapsed";
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.TreeTable('" + clientId + "', {");
		writer.write("initialState:'" + initialState + "'");
		
		if(treeTable.isReadOnly()) {
			writer.write(",expandable:false");
		}
		
		writer.write("});");
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext facesContext, TreeTable treeTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = treeTable.getClientId(facesContext);
		String styleClass = treeTable.getStyleClass() == null ? TreeTable.CONTAINER_CLASS : TreeTable.CONTAINER_CLASS + " " + treeTable.getStyleClass();
		
		writer.startElement("table", treeTable);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", styleClass, null);
		if(treeTable.getStyle() != null) writer.writeAttribute("style", treeTable.getStyle(), null);
		
		encodeHeaders(facesContext, treeTable);
		encodeRows(facesContext, treeTable);
		
		writer.endElement("table");
	}

	protected void encodeHeaders(FacesContext facesContext, TreeTable treeTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("thead", null);
		writer.startElement("tr", null);
		for(Iterator<UIComponent> iterator = treeTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				String columnStyleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default " + column.getStyleClass();

				writer.startElement("th", null);
				writer.writeAttribute("class", columnStyleClass, null);
					
				writer.startElement("div", null);
				writer.writeAttribute("class", TreeTable.HEADER_CLASS, null);
				
				writer.startElement("span", null);
				writer.writeAttribute("class", TreeTable.HEADER_LABEL_CLASS, null);
				
				UIComponent header = column.getFacet("header");
				if(header != null) {
					if(ComponentUtils.isLiteralText(header))
						writer.write(header.toString());
					else
						header.encodeAll(facesContext);
				}
				
				writer.endElement("span");
				writer.endElement("div");
				writer.endElement("th");
			}
		}
		writer.endElement("tr");
		writer.endElement("thead");
	}
	
	protected void encodeRows(FacesContext facesContext, TreeTable treeTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		TreeNode root = (TreeNode) treeTable.getValue();
		String clientId = treeTable.getEscapedClientId(facesContext);
		int rowKey = 0;
		
		writer.startElement("tbody", null);
		writer.writeAttribute("class", TreeTable.DATA_CLASS, null);
		
		if(root != null) {
			for(Iterator<TreeNode> iterator = root.getChildren().iterator(); iterator.hasNext();) {
				encodeTreeNode(facesContext, treeTable, iterator.next(), clientId, String.valueOf(rowKey), null);
				rowKey++;
			}
		}
		
		treeTable.setRowIndex(-1);		//cleanup
		
		writer.endElement("tbody");
	}
	
	protected void encodeTreeNode(FacesContext facesContext, TreeTable treeTable, TreeNode treeNode, String clientId, String rowKey, String parentRowKey) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		int rowIndex = 0;
		
		writer.startElement("tr", null);
		writer.writeAttribute("id", clientId + "_" + rowKey, null);
		String styleClass = "ui-widget-content";
		if(parentRowKey != null) {
			styleClass += " child-of-" + clientId + "_" + parentRowKey;	
		}
		writer.writeAttribute("class", styleClass, null);
		
		treeTable.setRowIndex(treeTable.getRowIndex() + 1);
		
		for(Iterator<UIComponent> iterator = treeTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;

				writer.startElement("td", null);
				if(column.getStyleClass() != null)
					writer.writeAttribute("class", column.getStyleClass(), null);
				
				column.encodeAll(facesContext);
				
				writer.endElement("td");
			}
		}
		
		writer.endElement("tr");
		
		for(Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext();) {
			String nodeRowKey = rowKey + "_" + rowIndex;
			encodeTreeNode(facesContext, treeTable, iterator.next(), clientId, nodeRowKey, rowKey);
			rowIndex++;
		}
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}