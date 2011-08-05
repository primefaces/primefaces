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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;

public class ContextMenuRenderer extends BaseMenuRenderer {

    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(context);
		String trigger = findTrigger(context, menu);
		
		writer.startElement("script", menu);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");
        
		writer.write(widgetVar + " = new PrimeFaces.widget.ContextMenu('" + clientId + "',");
        
        writer.write("{zindex:" + menu.getZindex());
        writer.write(",animation:{animated:'" + menu.getEffect() + "', duration:" + menu.getEffectDuration() + "}");

        if(trigger != null) writer.write(",target:'" + trigger + "'");
        
        if(menu.getStyleClass() != null) writer.write(",styleClass:'" + menu.getStyleClass() + "'");
        if(menu.getStyle() != null) writer.write(",style:'" + menu.getStyle() + "'");

        writer.write("});});");
		
		writer.endElement("script");
	}
	
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String clientId = menu.getClientId(context);

        writer.startElement("span", menu);
		writer.writeAttribute("id", clientId, "id");

		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId + "_menu", null);

		for(UIComponent child : menu.getChildren()) {
			MenuItem item = (MenuItem) child;

			if(item.isRendered()) {
                writer.startElement("li", null);
                encodeMenuItem(context, item);
                writer.endElement("li");
			}
		}

		writer.endElement("ul");

        writer.endElement("span");
	}

    protected String findTrigger(FacesContext context, ContextMenu menu) {
		String trigger = null;
		String _for = menu.getFor();

		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);

			if(forComponent == null)
				throw new FacesException("Cannot find component '" + _for + "' in view.");
			else 
            {
                if(forComponent instanceof DataTable) {
                    ((DataTable) forComponent).enableContextMenu();
                }
                
                return forComponent.getClientId(context);
			}
		}

		return trigger;
	}
}