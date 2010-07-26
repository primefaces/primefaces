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
package org.primefaces.component.tabview;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;

public class TabViewRenderer extends CoreRenderer implements PartialRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		TabView tabView = (TabView) component;
		
		encodeTabViewScript(facesContext, tabView);
		encodeTabViewMarkup(facesContext, tabView);
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		TabView tabView = (TabView) component;
		String activeTabIndexParam = facesContext.getExternalContext().getRequestParameterMap().get("activeTabIndex");
		int activeTabIndex = Integer.parseInt(activeTabIndexParam);
		
		Tab activeTab = (Tab) tabView.getChildren().get(activeTabIndex);
		
		renderChildren(facesContext, activeTab);
	}

	private void encodeTabViewMarkup(FacesContext facesContext, TabView tabView) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tabView.getClientId(facesContext);
		
		int activeTabIndex = getActiveTabIndex( facesContext, tabView, clientId );
		String activeTabIndexHolder = getActiveTabIndexHolder(clientId);
		
		writer.startElement("div", tabView);
		writer.writeAttribute("id", clientId , null);
		writer.writeAttribute("class", "yui-navset", null);
		
		encodeTabHeaders(facesContext, tabView, activeTabIndex);
		encodeTabContents(facesContext, tabView, activeTabIndex);
		
		encodeActiveIndexHolder(facesContext, activeTabIndex, activeTabIndexHolder);
		
		writer.endElement("div");
	}

	private void encodeActiveIndexHolder(FacesContext facesContext, int activeTabIndex, String activeTabIndexHolder) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", activeTabIndexHolder, null);
		writer.writeAttribute("name", activeTabIndexHolder, null);
		writer.writeAttribute("value", activeTabIndex, null);
		writer.endElement("input");
	}
	
	private void encodeTabHeaders(FacesContext facesContext, TabView tabView, int activeTabIndex) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "yui-nav", null);
		
		for (int i = 0; i < tabView.getChildren().size(); i++) {
			Tab tab = (Tab) tabView.getChildren().get(i);
			
			if(tab.isRendered()) {
				writer.startElement("li", null);
				
				if( i == activeTabIndex )
					writer.writeAttribute("class", "selected", null);
				
				writer.startElement("a", null);
				writer.writeAttribute("href", "#" + tab.getClientId(facesContext), null);
				writer.startElement("em", null);
				writer.write(tab.getTitle());
				writer.endElement("em");
				writer.endElement("a");
				
				writer.endElement("li");
			}
		}
		
		writer.endElement("ul");
	}
	
	private void encodeTabContents(FacesContext facesContext, TabView tabView, int activeTabIndex) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "yui-content", null);
		
		for (int i = 0; i < tabView.getChildren().size(); i++) {
			Tab tab = (Tab) tabView.getChildren().get(i);
			
			if(tab.isRendered()) {
				writer.startElement("div", null);
				
				if(tabView.isAsyncToggling()) {
					if(i == activeTabIndex) {
						renderChildren(facesContext, tab);
					}
				} else {
					renderChildren(facesContext, tab);
				}
				
				writer.endElement("div");
			}
		}
		
		writer.endElement("div");
	}

	private int getActiveTabIndex(FacesContext facesContext, TabView tabView, String clientId) {
		String activeTabIndexHiddenFieldId = getActiveTabIndexHolder(clientId);
		String activeTabIndex = (String) facesContext.getExternalContext().getRequestParameterMap().get(activeTabIndexHiddenFieldId);
		
		if( activeTabIndex != null && activeTabIndex != "")
			tabView.setActiveIndex(Integer.parseInt(activeTabIndex));
	
		return tabView.getActiveIndex();
	}

	private void encodeTabViewScript(FacesContext facesContext, TabView tabView) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tabView.getClientId(facesContext);
		String tabViewVar = createUniqueWidgetVar(facesContext, tabView);
		String activeTabIndexHolder = getActiveTabIndexHolder(clientId);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady(\"" + clientId + "\", function() {\n");
	    writer.write(tabViewVar + " = new PrimeFaces.widget.TabView(\"" + clientId + "\",");
	    writer.write("{");
	    writer.write("clientId:\"" + clientId + "\"");
	    writer.write(",activeTabIndexHolder:\"" + activeTabIndexHolder + "\"");
	    writer.write(",orientation:\"" + tabView.getOrientation() + "\"");
	    writer.write(",toggleMode:\"" + tabView.getToggleMode() + "\"");
	    writer.write(",actionURL:\"" + getActionURL(facesContext) + "\"");
	    writer.write(",cache:" + tabView.isCache() + "");
	    writer.write(",contentTransition:" + tabView.isContentTransition() );
	 
	    writer.write("});\n");
	    
		writer.write("});\n");
	    
	    writer.endElement("script");
	}

	private String getActiveTabIndexHolder(String clientId) {
		return clientId + ":activeIndex";
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}