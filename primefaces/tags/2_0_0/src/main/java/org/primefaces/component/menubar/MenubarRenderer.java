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
package org.primefaces.component.menubar;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.renderkit.CoreRenderer;

public class MenubarRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		Menubar menubar = (Menubar) component;
		
		encodeScript(facesContext, menubar);
		encodeMarkup(facesContext, menubar);
	}

	private void encodeScript(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		String menubarVar = createUniqueWidgetVar(facesContext, menubar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(menubarVar + " = new YAHOO.widget.MenuBar('" + clientId + "',{");
		
		writer.write("autosubmenudisplay:" + menubar.isAutoSubmenuDisplay());
		
		if(!menubar.getEffect().equals("NONE")) {
			writer.write(",effect: {effect: YAHOO.widget.ContainerEffect." + menubar.getEffect());
			
			if(menubar.getEffectDuration() != 0.25)
				writer.write(",duration:" + menubar.getEffectDuration() + "}");
			else
				writer.write(",duration: 0.25}");
		}
		
		writer.write("})\n;");
	
		writer.write(menubarVar + ".render();\n");
		
		writer.write("});\n");
		
		writer.endElement("script");	
	}

	private void encodeMarkup(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "yuimenubar", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		List<UIComponent> children = menubar.getChildren();
		for (int i=0; i < children.size(); i++) {
			Submenu submenu = (Submenu) children.get(i);
			
			if(submenu.isRendered()) {
				writer.startElement("li", null);
				if(i == 0)
					writer.writeAttribute("class", "yuimenubaritem first-of-type", null);
				else
					writer.writeAttribute("class", "yuimenubaritem", null);
				
				encodeSubmenu(facesContext, submenu);
				
				writer.endElement("li");
			}
		}
		
		writer.endElement("ul");
		writer.endElement("div");
		writer.endElement("div");
	}
	
	private void encodeSubmenu(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(hasCustomContent(submenu)) {
			renderChildren(facesContext, submenu);
		}
		else {
			String labelClass = submenu.getParent() instanceof Menubar ? "yuimenubaritemlabel" : "yuimenuitemlabel";
			if(submenu.getStyleClass() != null)
				labelClass = labelClass + " " + submenu.getStyleClass();
			
			writer.startElement("a", null);
			writer.writeAttribute("class", labelClass, null);
			if(submenu.getUrl() != null)  writer.writeAttribute("href", submenu.getUrl(), null);
			if(submenu.getStyle() != null)  writer.writeAttribute("style", submenu.getStyle(), null);
			if(submenu.getOnclick() != null)  writer.writeAttribute("onclick", submenu.getOnclick(), null);
			if(submenu.getLabel() != null)  writer.write(submenu.getLabel());
				
			writer.endElement("a");
			
			if(submenu.getChildCount() > 0) {
				writer.startElement("div", null);
				writer.writeAttribute("class", "yuimenu", null);
				
				writer.startElement("div", null);
				writer.writeAttribute("class", "bd", null);
				writer.startElement("ul", null);
				
				encodeSubmenuItems(facesContext, submenu);
				
				writer.endElement("ul");
				writer.endElement("div");
				writer.endElement("div");
			}
		}
	}
	
	private boolean hasCustomContent(Submenu submenu) {
		for(Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(!(kid instanceof Submenu || kid instanceof MenuItem))
				return true;
		}
		
		return false;
	}
	
	private void encodeSubmenuItems(FacesContext facesContext, Submenu submenu) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for (Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			if(child.isRendered()) {
			
				writer.startElement("li", null);
				writer.writeAttribute("class", "yuimenuitem", null);
				
				if(child instanceof MenuItem) {
					MenuItem menuItem = (MenuItem) child;
					encodeMenuItem(facesContext, menuItem);
				} else if(child instanceof Submenu) {
					Submenu childSubmenu = (Submenu) child;
					encodeSubmenu(facesContext, childSubmenu);
				}
				
				writer.endElement("li");
			}
		}
	}
	
	private void encodeMenuItem(FacesContext facesContext, MenuItem menuItem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(menuItem.getChildCount() > 0) {
			renderChildren(facesContext, menuItem);
		}
		else {
			String menuitemStyleClass = menuItem.getStyleClass() == null ? "yuimenuitemlabel" : "yuimenuitemlabel " + menuItem.getStyleClass(); 
			
			writer.startElement("a", null);
			writer.writeAttribute("class", menuitemStyleClass, null);
			writer.writeAttribute("href", menuItem.getUrl(), null);
			
			if(menuItem.getStyle() != null)  writer.writeAttribute("style", menuItem.getStyle(), null);
			if(menuItem.getTarget() != null)  writer.writeAttribute("target", menuItem.getTarget(), null);
			if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
			if(menuItem.getLabel() != null) writer.write(menuItem.getLabel());
	
			if(menuItem.getHelpText() != null) {
				writer.startElement("em", null);
				writer.writeAttribute("class", "helptext", null);
				writer.write(menuItem.getHelpText());
				writer.endElement("em");
			}
			
			writer.endElement("a");
		}
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}

}