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
package org.primefaces.component.menu;

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

public abstract class BaseMenuRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AbstractMenu menu = (AbstractMenu) component;

        if(menu.isDynamic()) {
            menu.buildMenuFromModel();
        }

		encodeMarkup(context, menu);
		encodeScript(context, menu);
	}

    protected abstract void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected abstract void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException;

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
            writer.writeAttribute("id", menuItem.getClientId(context), null);
            
            String styleClass = menuItem.getStyleClass();
            styleClass = styleClass == null ? AbstractMenu.MENUITEM_LINK_CLASS : AbstractMenu.MENUITEM_LINK_CLASS + " " + styleClass;
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
                writer.writeAttribute("class", icon + " " + AbstractMenu.MENUITEM_ICON_CLASS, null);
                writer.endElement("span");
            }

			if(menuItem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);
                writer.write((String) menuItem.getValue());
                writer.endElement("span");
            }

            writer.endElement("a");
		}
	}
    
    protected void encodeTieredMenuContent(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for(Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {

                writer.startElement("li", null);
                
                if(child instanceof MenuItem) {
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    
                    encodeMenuItem(context, (MenuItem) child);
                } 
                else if(child instanceof Submenu) {
                    Submenu submenu = (Submenu) child;
                    String style = submenu.getStyle();
                    String styleClass = submenu.getStyleClass();
                    styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;
        
                    writer.writeAttribute("class", styleClass, null);
                    if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }
                    
                    encodeTieredSubmenu(context, (Submenu) child);
                }

                writer.endElement("li");
            }
        }
    }
    
    protected void encodeTieredSubmenu(FacesContext context, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String label = submenu.getLabel();

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Menu.MENUITEM_LINK_CLASS, null);

        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", icon + " " + Menu.MENUITEM_ICON_CLASS, null);
            writer.endElement("span");
        }

        if(label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.write(submenu.getLabel());
            writer.endElement("span");
        }
        
        writer.startElement("span", null);
        writer.writeAttribute("class", Menu.SUBMENU_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");

        //submenus and menuitems
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);

			encodeTieredMenuContent(context, submenu);

			writer.endElement("ul");
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
