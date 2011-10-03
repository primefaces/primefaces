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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.util.ComponentUtils;

public class ContextMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(context);
		String target = findTarget(context, menu);
		
		writer.startElement("script", menu);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");
        
		writer.write(widgetVar + " = new PrimeFaces.widget.ContextMenu('" + clientId + "',{");
        
        writer.write("target:" + target);
 
        if(!menu.getEffect().equals("none")) {
            writer.write(",effect:'" + menu.getEffect() + "'");
            writer.write(",effectDuration:" + menu.getEffectDuration());
        }
        
        writer.write("});});");
		
		writer.endElement("script");
	}
	
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? ContextMenu.CONTAINER_CLASS : ContextMenu.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

		writer.startElement("ul", null);
        writer.writeAttribute("class", ContextMenu.LIST_CLASS, null);

		for(UIComponent child : menu.getChildren()) {
			MenuItem item = (MenuItem) child;

			if(item.isRendered()) {
                writer.startElement("li", null);
                writer.writeAttribute("class", ContextMenu.MENUITEM_CLASS, null);
                encodeMenuItem(context, item);
                writer.endElement("li");
			}
		}

		writer.endElement("ul");
        
        writer.endElement("div");
	}

    protected String findTarget(FacesContext context, ContextMenu menu) {
		String trigger = null;
		String _for = menu.getFor();

		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);

			if(forComponent == null)
				throw new FacesException("Cannot find component '" + _for + "' in view.");
			else 
                return "'" + forComponent.getClientId(context) + "'";
		}
        else {
            return "document";
        }
	}
    
    @Override
    protected void encodeMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
		String clientId = menuItem.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String icon = menuItem.getIcon();

		if(menuItem.shouldRenderChildren()) {
			renderChildren(context, menuItem);
		}
        else {
            boolean disabled = menuItem.isDisabled();
            String onclick = menuItem.getOnclick();
            
            writer.startElement("a", null);
            String styleClass = menuItem.getStyleClass();
            styleClass = styleClass == null ? ContextMenu.MENUITEM_LINK_CLASS : ContextMenu.MENUITEM_LINK_CLASS + " " + styleClass;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
            
            writer.writeAttribute("class", styleClass, null);
            
            if(menuItem.getStyle() != null) 
                writer.writeAttribute("style", menuItem.getStyle(), null);
                        
			if(menuItem.getUrl() != null) {
                String href = disabled ? "javascript:void(0)" : getResourceURL(context, menuItem.getUrl());
				writer.writeAttribute("href", href, null);
                                
				if(menuItem.getTarget() != null) 
                    writer.writeAttribute("target", menuItem.getTarget(), null);
			}
            else {
				writer.writeAttribute("href", "javascript:void(0)", null);

				UIComponent form = ComponentUtils.findParentForm(context, menuItem);
				if(form == null) {
					throw new FacesException("Menubar must be inside a form element");
				}

                String command = menuItem.isAjax() ? buildAjaxRequest(context, menuItem) : buildNonAjaxRequest(context, menuItem, form.getClientId(context), clientId);

                onclick = onclick == null ? command : onclick + ";" + command;
			}

            if(onclick != null && !disabled) {
                writer.writeAttribute("onclick", onclick, null);
            }
 
            if(icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", icon + " " + ContextMenu.MENUITEM_ICON_CLASS, null);
                writer.endElement("span");
            }

			if(menuItem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", ContextMenu.MENUITEM_TEXT_CLASS, null);
                writer.write((String) menuItem.getValue());
                writer.endElement("span");
            }

            writer.endElement("a");
		}
	}
}