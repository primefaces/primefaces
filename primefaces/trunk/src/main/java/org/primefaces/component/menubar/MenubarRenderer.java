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
package org.primefaces.component.menubar;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class MenubarRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		Menubar menubar = (Menubar) component;
		
		if(menubar.shouldBuildFromModel()) {
			menubar.buildMenuFromModel();
		}
		
		encodeMarkup(context, menubar);
		encodeScript(context, menubar);
	}

	protected void encodeScript(FacesContext context, Menubar menubar) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = menubar.getClientId(context);
		String widgetVar = menubar.resolveWidgetVar();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.Menubar('" + clientId + "',{");

        writer.write("autoSubmenuDisplay:" + menubar.isAutoSubmenuDisplay());

        if(menubar.getEffectDuration() != 400) writer.write(",duration:" + menubar.getEffectDuration() + "");
		
        writer.write("});");

		writer.endElement("script");	
	}

	protected void encodeMarkup(FacesContext context, Menubar menubar) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = menubar.getClientId(context);
		
		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId, null);

        if(menubar.getStyleClass() != null) writer.writeAttribute("class", menubar.getStyleClass(), null);
        if(menubar.getStyle() != null) writer.writeAttribute("style", menubar.getStyle(), null);

		for(UIComponent child : menubar.getChildren()) {
			Submenu submenu = (Submenu) child;
			
			if(submenu.isRendered()) {
                writer.startElement("li", null);
				encodeSubmenu(context, submenu);
                writer.endElement("li");
			}
		}
		
		writer.endElement("ul");
	}
	
	protected void encodeSubmenu(FacesContext context, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		UIComponent labelFacet = submenu.getFacet("label");

        //title
		if(labelFacet == null) {			
			writer.startElement("a", null);
			writer.writeAttribute("href", "#", null);
			
			if(submenu.getLabel() != null)
                writer.write(submenu.getLabel());
			
			writer.endElement("a");
		} else {
            encodeMenuItem(context, (MenuItem) labelFacet);
		}

        //submenus and menuitems
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
			
			for(Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
                UIComponent child = (UIComponent) iterator.next();

                if(child.isRendered()) {

                    writer.startElement("li", null);
                    if(child instanceof MenuItem) {
                        encodeMenuItem(context, (MenuItem) child);
                    } else if(child instanceof Submenu) {
                        encodeSubmenu(context, (Submenu) child);
                    }
                    writer.endElement("li");
                    
                }
            }
			
			writer.endElement("ul");
		}
	}
	
	protected void encodeMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
		String clientId = menuItem.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
		
		if(menuItem.shouldRenderChildren()) {
			renderChildren(context, menuItem);
		}
        else {
            writer.startElement("a", null);
			
			if(menuItem.getUrl() != null) {
				writer.writeAttribute("href", getResourceURL(context, menuItem.getUrl()), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
			} else {
				writer.writeAttribute("href", "javascript:void(0)", null);
				
				UIComponent form = ComponentUtils.findParentForm(context, menuItem);
				if(form == null) {
					throw new FacesException("Menubar must be inside a form element");
				}
				
				String formClientId = form.getClientId(context);
				String command = menuItem.isAjax() ? buildAjaxRequest(context, menuItem, formClientId, clientId) : buildNonAjaxRequest(context, menuItem, formClientId, clientId);
				
				command = menuItem.getOnclick() == null ? command : menuItem.getOnclick() + ";" + command;
				
				writer.writeAttribute("onclick", command, null);
			}
			
			if(menuItem.getValue() != null)
                writer.write((String) menuItem.getValue());
			
            writer.endElement("a");
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