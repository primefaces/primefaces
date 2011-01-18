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

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class ContextMenuRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ContextMenu menu = (ContextMenu) component;
		
		encodeMarkup(facesContext, menu);
		encodeScript(facesContext, menu);
	}

	protected void encodeScript(FacesContext facesContext, ContextMenu menu) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(facesContext);
		String trigger = findTrigger(facesContext, menu);
		
		writer.startElement("script", menu);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function() {");
        
		writer.write(widgetVar + " = new PrimeFaces.widget.ContextMenu('" + clientId + "',");
		writer.write("{target:" + trigger);
        writer.write(",zindex:" + menu.getZindex());

        writer.write(",animated:'" + menu.getEffect() + "'");

        if(menu.getEffectDuration() != 400) {
            writer.write(",showDuration:" + menu.getEffectDuration());
            writer.write(",hideDuration:" + menu.getEffectDuration());
        }

        if(menu.getStyleClass() != null) writer.write(",styleClass:'" + menu.getStyleClass() + "'");
        if(menu.getStyle() != null) writer.write(",style:'" + menu.getStyle() + "'");

        writer.write("});});");
		
		writer.endElement("script");
	}
	
	protected String findTrigger(FacesContext facesContext, ContextMenu menu) {
		String trigger = null;
		String _for = menu.getFor();
		
		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);
			
			if(forComponent == null)
				throw new FacesException("Cannot find component '" + _for + "' in view.");
			else {
                return "'" +  forComponent.getClientId(facesContext) + "'";
			}
		}
		else {
			trigger = "document";
		}
		
		return trigger;
	}
		
    protected void encodeMarkup(FacesContext context, ContextMenu menu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = menu.getClientId(context);

		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId, null);

		for(UIComponent child : menu.getChildren()) {
			MenuItem item = (MenuItem) child;

			if(item.isRendered()) {
                writer.startElement("li", null);
                encodeMenuItem(context, item);
                writer.endElement("li");
			}
		}

		writer.endElement("ul");
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
					throw new FacesException("Menu must be inside a form element");
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
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}