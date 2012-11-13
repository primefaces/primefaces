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
package org.primefaces.component.splitbutton;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import org.primefaces.component.commandbutton.CommandButtonRenderer;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menubutton.MenuButton;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class SplitButtonRenderer extends CommandButtonRenderer {
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
        SplitButton button = (SplitButton) component;
        if(button.isDisabled()) {
            return;
        }
        
        String clientId = button.getClientId(context);
        
        String param = button.isAjax() ? clientId : clientId + "_button";
		if(context.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		SplitButton button = (SplitButton) component;
		
		encodeMarkup(context, button);
		encodeScript(context, button);
	}
	
	protected void encodeMarkup(FacesContext context, SplitButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
        String menuId = clientId + "_menu";
        String menuButtonId = clientId + "_menuButton";
        String buttonId = clientId + "_button";
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? SplitButton.STYLE_CLASS : SplitButton.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("div", button);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
		if(button.getStyle() != null) {
            writer.writeAttribute("style", button.getStyle(), "id");
        }
        
        encodeDefaultButton(context, button, buttonId);
        encodeMenuIcon(context, button, menuButtonId);
        encodeMenu(context, button, menuId);
        
        writer.endElement("div");
	}
    
    protected void encodeDefaultButton(FacesContext context, SplitButton button, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String value = (String) button.getValue();
        String icon = button.getIcon();
        String onclick = buildOnclick(context, button);
        
        writer.startElement("button", button);
		writer.writeAttribute("id", id, "id");
		writer.writeAttribute("name", id, "name");
        writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");
        
		if(onclick.length() > 0) {
			writer.writeAttribute("onclick", onclick.toString(), "onclick");
		}
		
		renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if(button.isDisabled()) { 
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
		
        //icon
        if(icon != null) {
            String defaultIconClass = button.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS; 
            String iconClass = defaultIconClass + " " + icon;
            
            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        
        if(value == null)
            writer.write("ui-button");
        else
            writer.writeText(value, "value");
        
        writer.endElement("span");
			
		writer.endElement("button");
    }
    
    protected void encodeMenuIcon(FacesContext context, SplitButton button, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String buttonClass = SplitButton.MENU_ICON_BUTTON_CLASS;
        if(button.isDisabled()) {
            buttonClass += " ui-state-disabled";
        }
        
        writer.startElement("button", button);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass, null);

        if(button.isDisabled()) { 
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
		
        //icon    
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-icon-left ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");
       
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");
        writer.endElement("span");
			
		writer.endElement("button");
    }
	
	protected void encodeScript(FacesContext context, SplitButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		
        startScript(writer, clientId);

        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('SplitButton','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");		
		writer.write("});});");
		
		endScript(writer);
    }
    
    protected String buildOnclick(FacesContext context, SplitButton button) throws IOException {
        StringBuilder onclick = new StringBuilder();
        if(button.getOnclick() != null) {
            onclick.append(button.getOnclick()).append(";");
        }
        
        if(button.isAjax()) {
            onclick.append(buildAjaxRequest(context, button, null));
        }
        else {
            UIComponent form = ComponentUtils.findParentForm(context, button);
            if(form == null) {
                throw new FacesException("SplitButton : \"" + button.getClientId(context) + "\" must be inside a form element");
            }
        
            onclick.append(buildNonAjaxRequest(context, button, form, null, false));
        }
        
        String onclickBehaviors = getOnclickBehaviors(context, button);
        if(onclickBehaviors != null) {
            onclick.append(onclickBehaviors).append(";");
        }
        
        return onclick.toString();
    }
    
    protected void encodeMenu(FacesContext context, SplitButton button, String menuId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
            writer.writeAttribute("id", menuId, null);
            writer.writeAttribute("class", Menu.DYNAMIC_CONTAINER_CLASS, "styleClass");
            writer.writeAttribute("role", "menu", null);

            writer.startElement("ul", null);
            writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

            for(UIComponent child : button.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof MenuItem) {
                        MenuItem item = (MenuItem) child;
                        
                        writer.startElement("li", item);
                        writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                        writer.writeAttribute("role", "menuitem", null);
                        encodeMenuItem(context, item);
                        writer.endElement("li");
                    }
                    else if(child instanceof Separator) {
                        encodeSeparator(context, (Separator) child);
                    }
                }
            }

            writer.endElement("ul");
            writer.endElement("div");

    }
    
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
                String href = disabled ? "#" : getResourceURL(context, menuItem.getUrl());
				writer.writeAttribute("href", href, null);
                                
				if(menuItem.getTarget() != null) 
                    writer.writeAttribute("target", menuItem.getTarget(), null);
			}
            else {
				writer.writeAttribute("href", "#", null);

				UIComponent form = ComponentUtils.findParentForm(context, menuItem);
				if(form == null) {
					throw new FacesException("Menubar must be inside a form element");
				}

                String command = menuItem.isAjax() ? buildAjaxRequest(context, menuItem, form) : buildNonAjaxRequest(context, menuItem, form, clientId, true);

                onclick = onclick == null ? command : onclick + ";" + command;
			}

            if(onclick != null && !disabled) {
                writer.writeAttribute("onclick", onclick, null);
            }
 
            if(icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon, null);
                writer.endElement("span");
            }

			if(menuItem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);
                writer.writeText((String) menuItem.getValue(), "value");
                writer.endElement("span");
            }

            writer.endElement("a");
		}
	}
    
    protected void encodeSeparator(FacesContext context, Separator separator) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = separator.getStyle();
        String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? Menu.SEPARATOR_CLASS : Menu.SEPARATOR_CLASS + " " + styleClass;

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
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
