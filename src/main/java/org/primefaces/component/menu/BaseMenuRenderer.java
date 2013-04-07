/*
 * Copyright 2009-2013 PrimeTek.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.separator.Separator;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.Menuitem;
import org.primefaces.model.menu.Submenu;
import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public abstract class BaseMenuRenderer extends OutcomeTargetRenderer {
    
    public final static String SEPARATOR = "_";
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		AbstractMenu menu = (AbstractMenu) component;
        String clientId = menu.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
        if(params.containsKey(clientId)) {
            String menuid = params.get(clientId + "_menuid");
            Menuitem menuitem = findMenuitem(menu.getElements(), menuid);
            MenuActionEvent event = new MenuActionEvent(menu, menuitem);
            
            menu.queueEvent(event);
        }
	}
    
    protected Menuitem findMenuitem(List<MenuElement> elements, String id) {        
        if(elements == null || elements.isEmpty()) {
            return null;
        }
        else {
            String[] paths = id.split(SEPARATOR);
            
            if(paths.length == 0)
                return null;
            
            int childIndex = Integer.parseInt(paths[0]);
            if(childIndex >= elements.size()) 
                return null;
            
            MenuElement childElement = elements.get(childIndex);

            if(paths.length == 1) {
                return (Menuitem) childElement;
            } 
            else {
                String relativeIndex = id.substring(id.indexOf(SEPARATOR) + 1);

                return findMenuitem(((Submenu) childElement).getElements(), relativeIndex);
            }
        }
    }
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AbstractMenu menu = (AbstractMenu) component;

		encodeMarkup(context, menu);
		encodeScript(context, menu);
	}

    protected abstract void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected abstract void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected void encodeMenuItem(FacesContext context, AbstractMenu menu, Menuitem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = menuitem.getIcon();
        String title = menuitem.getTitle();

		if(menuitem.shouldRenderChildren()) {
			renderChildren(context, (UIComponent) menuitem);
		}
        else {
            boolean disabled = menuitem.isDisabled();
            String onclick = menuitem.getOnclick();
            
            writer.startElement("a", null);
            if(title != null) {
                writer.writeAttribute("title", title, null);
            }
            
            String styleClass = menuitem.getStyleClass();
            styleClass = styleClass == null ? AbstractMenu.MENUITEM_LINK_CLASS : AbstractMenu.MENUITEM_LINK_CLASS + " " + styleClass;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;
            
            writer.writeAttribute("class", styleClass, null);
            
            if(menuitem.getStyle() != null) {
                writer.writeAttribute("style", menuitem.getStyle(), null);
            }
                  
            //GET
			if(menuitem.getUrl() != null || menuitem.getOutcome() != null) {                
                String targetURL = getTargetURL(context, (UIOutcomeTarget) menuitem);
                String href = disabled ? "javascript:void(0)" : targetURL;
				writer.writeAttribute("href", href, null);
                                
				if(menuitem.getTarget() != null) {
                    writer.writeAttribute("target", menuitem.getTarget(), null);
                }
			}
            //POST
            else {
				writer.writeAttribute("href", "javascript:void(0)", null);
                
                UIComponent form = ComponentUtils.findParentForm(context, menu);
                if(form == null) {
                    throw new FacesException("MenuItem must be inside a form element");
                }

                String command = null;
                if(!menuitem.isAjax()) {
                    if(menuitem.isDynamic()) {
                        String menuClientId = menu.getClientId(context);
                        Map<String,String> params = new HashMap<String, String>();
                        params.put(menuClientId + "_menuid", menuitem.getId());
                        
                        command = buildNonAjaxRequest(context, menu, form, menuClientId, params, true);
                    } 
                    else {
                        command = buildNonAjaxRequest(context, ((UIComponent) menuitem), form, ((UIComponent) menuitem).getClientId(context), null, true);
                    } 
                }
                //= menuItem.isAjax() ? buildAjaxRequest(context, menuItem, form) : buildNonAjaxRequest(context, menuItem, form, clientId, true);

                onclick = (onclick == null) ? command : onclick + ";" + command;
			}

            if(onclick != null && !disabled) {
                writer.writeAttribute("onclick", onclick, null);
            }
 
            if(icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon, null);
                writer.endElement("span");
            }

			if(menuitem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);
                writer.writeText((String) menuitem.getValue(), "value");
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
    
    protected void encodeOverlayConfig(FacesContext context, OverlayMenu menu, WidgetBuilder wb) throws IOException {

        wb.attr("overlay", true)
            .attr("my", menu.getMy())
            .attr("at", menu.getAt());
        
        UIComponent trigger = ((UIComponent) menu).findComponent(menu.getTrigger());
        String triggerClientId = trigger == null ? menu.getTrigger() : trigger.getClientId(context);

        wb.attr("trigger", triggerClientId)
            .attr("triggerEvent", menu.getTriggerEvent());
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
