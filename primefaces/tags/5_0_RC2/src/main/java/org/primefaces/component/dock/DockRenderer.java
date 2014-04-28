/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.dock;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;

import org.primefaces.util.WidgetBuilder;

public class DockRenderer extends BaseMenuRenderer {
	
    @Override
	protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
        Dock dock = (Dock) menu;
		String clientId = dock.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dock", dock.resolveWidgetVar(), clientId, "dock")
                .attr("position", dock.getPosition())
                .attr("maxWidth", dock.getMaxWidth())
                .attr("itemWidth", dock.getItemWidth())
                .attr("proximity", dock.getProximity())
                .attr("halign", dock.getHalign());

        wb.finish();
	}

    @Override
	protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Dock dock = (Dock) menu;
		String clientId = dock.getClientId(context);
		String position = dock.getPosition();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "ui-dock-" + position + " ui-widget", "styleClass");
	
		writer.startElement("div", null);
		writer.writeAttribute("class", "ui-dock-container-" + position + " ui-widget-header", null);
		
		encodeMenuItems(context, dock);
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeMenuItems(FacesContext context, Dock dock) throws IOException {		
        if(dock.getElementsCount() > 0) {
            List<MenuElement> menuElements = (List<MenuElement>) dock.getElements();
                    
            for(MenuElement element : menuElements) {
                if(element.isRendered() && element instanceof MenuItem) {                    
                    encodeMenuItem(context, dock, (MenuItem) element);
                }
            }
        }
	}
    
    @Override
    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        String position = ((Dock) menu).getPosition();
        
        if(position.equalsIgnoreCase("top")) {
            encodeItemIcon(context, menuitem);
            encodeItemLabel(context, menuitem);
        }
        else{
            encodeItemLabel(context, menuitem);
            encodeItemIcon(context, menuitem);
        }
    }
	
	protected void encodeItemIcon(FacesContext context, MenuItem menuitem) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
		writer.endElement("img");
	}
	
	protected void encodeItemLabel(FacesContext context, MenuItem menuitem) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("span", null);

		if(menuitem.getValue() != null)
            writer.write((String) menuitem.getValue());
		
		writer.endElement("span");
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}