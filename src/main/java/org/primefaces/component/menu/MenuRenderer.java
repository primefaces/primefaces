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
package org.primefaces.component.menu;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.renderkit.CoreRenderer;

public class MenuRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Menu menu = (Menu) component;
		
		encodeScript(facesContext, menu);
		encodeMarkup(facesContext, menu);
	}

	private void encodeScript(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menu.getClientId(facesContext);
		String menuVar = createUniqueWidgetVar(facesContext, menu);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(menuVar + " = new YAHOO.widget.Menu('" + clientId + "', {");
		
		if(menu.getPosition().equalsIgnoreCase("static")) {
			writer.write("position:'static'");
		}
		else if(menu.getPosition().equalsIgnoreCase("dynamic")) {
			writer.write("position:'dynamic'");
			if(menu.isVisible()) writer.write(",visible:true");
			if(!menu.isClickToHide()) writer.write(",clicktohide:false");
			if(menu.isKeepOpen()) writer.write(",keepopen:true");
			if(menu.getX() != -1) writer.write(",x:" + menu.getX());
			if(menu.getY() != -1) writer.write(",y:" + menu.getY());
			if(menu.isFixedCenter()) writer.write(",fixedcenter:true");
			if(!menu.isConstraintToViewport()) writer.write(",constrainttoviewport:false");
			if(menu.getShowDelay() != 250) writer.write(",showdelay:" + menu.getShowDelay());
			if(menu.getHideDelay() != 0) writer.write(",hidedelay:" + menu.getHideDelay());
			if(menu.getSubmenuHideDelay() != 250) writer.write(",submenuhidedelay:" + menu.getSubmenuHideDelay());
			if(menu.getContext() != null) writer.write(",context:[" + menu.getContext() + "]");
		}
		
		if(menu.isAutoSubmenuDisplay() == false) writer.write(",autosubmenudisplay:false");
		
		if(!menu.getEffect().equals("NONE")) {
			writer.write(",effect: {effect: YAHOO.widget.ContainerEffect." + menu.getEffect());
			
			if(menu.getEffectDuration() != 0.25)
				writer.write(",duration:" + menu.getEffectDuration() + "}");
			else
				writer.write(",duration: 0.25}");
		}
		
		writer.write("});\n");
		
		writer.write(menuVar + ".render();\n");
		
		writer.write("});\n");
		
		writer.endElement("script");	
	}

	private void encodeMarkup(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menu.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "yuimenu", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		if(menu.isTiered())
			renderTieredMenu(facesContext, menu);
		else
			renderRegularMenu(facesContext, menu);
		
		writer.endElement("div");
				
		writer.endElement("div");
	}
	
	private void renderTieredMenu(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		List<UIComponent> submenus = menu.getChildren();
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		for (int i=0; i < submenus.size(); i++) {
			Submenu submenu = (Submenu) submenus.get(i);
			
			encodeTieredSubmenu(facesContext, submenu);
		}
		
		writer.endElement("ul");
	}

	private void renderRegularMenu(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		List<UIComponent> submenus = menu.getChildren();
		
		for (int i=0; i < submenus.size(); i++) {
			Submenu submenu = (Submenu) submenus.get(i);
			
			if(submenu.isRendered()) {
				writer.startElement("h6", null);
				String defaultTitleStyleClass = (i == 0) ? "first-of-type" : null;
				
				if(submenu.getStyleClass() != null) {
					if(defaultTitleStyleClass != null) 
						writer.writeAttribute("class", defaultTitleStyleClass + " " + submenu.getStyleClass(), null);
					else 
						writer.writeAttribute("class", submenu.getStyleClass(), null);
				} else if(defaultTitleStyleClass != null) {
					writer.writeAttribute("class", defaultTitleStyleClass, null);
				}
				
				if(submenu.getStyle() != null) writer.writeAttribute("style", submenu.getStyle(), null);
		
				if(submenu.getLabel() != null)
					writer.write(submenu.getLabel());
					
				writer.endElement("h6");
				
				writer.startElement("ul", null);
				if(i == 0)
					writer.writeAttribute("class", "first-of-type", null);
				
				encodeSubmenuItems(facesContext, submenu);
				
				writer.endElement("ul");
			}		
		}	
	}
	
	private void encodeTieredSubmenu(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String styleClass = submenu.getStyleClass() == null ? "yuimenuitemlabel" : "yuimenuitemlabel " + submenu.getStyleClass();
		
		writer.startElement("li", null);
		writer.writeAttribute("class", "yuimenuitem", null);
		
		writer.startElement("a", null);
		writer.writeAttribute("class", styleClass, null);
		
		if(submenu.getLabel() != null)
			writer.write(submenu.getLabel());
		
		writer.endElement("a");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "yuimenu", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		encodeSubmenuItems(facesContext, submenu);
		
		writer.endElement("ul");
		
		writer.endElement("li");
	}

	private void encodeSubmenuItems(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for (Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem menuItem = (MenuItem) child;
				String styleClass = menuItem.getStyleClass() == null ? "yuimenuitem" : "yuimenuitem " + menuItem.getStyleClass();
				
				writer.startElement("li", null);
				writer.writeAttribute("class", "yuimenuitem", null);
				
				writer.startElement("a", null);
				writer.writeAttribute("href", menuItem.getUrl(), null);
				writer.writeAttribute("class", styleClass, null);
				
				if(menuItem.getStyle() != null) writer.writeAttribute("style", menuItem.getStyle(), null);
				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getLabel() != null) writer.write(menuItem.getLabel());
				
				writer.endElement("a");
				
				writer.endElement("li");
				
			} else if(child instanceof Submenu && child.isRendered()) {				
				encodeTieredSubmenu(facesContext, (Submenu) child);
			}
		}
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}