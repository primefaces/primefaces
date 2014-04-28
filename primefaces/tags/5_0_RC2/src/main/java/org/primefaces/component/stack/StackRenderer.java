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
package org.primefaces.component.stack;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.WidgetBuilder;

public class StackRenderer extends BaseMenuRenderer {
	
    @Override
	protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
        Stack stack = (Stack) menu;
		String clientId = stack.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Stack", stack.resolveWidgetVar(), clientId, "stack")
            .attr("openSpeed", stack.getOpenSpeed())
            .attr("closeSpeed", stack.getCloseSpeed())
            .attr("expanded", stack.isExpanded(), false);

        wb.finish();
	}
	
	protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Stack stack = (Stack) menu;
		String clientId = stack.getClientId(context);
		
		writer.startElement("div", stack);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", "ui-stack", null);
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(context, stack.getIcon()), null);
		writer.endElement("img");
		
        if(stack.getElementsCount() > 0) {
            List<MenuElement> elements = stack.getElements();
            
            writer.startElement("ul", null);
            writer.writeAttribute("id", clientId + "_stack", "id");
		
            for(MenuElement element : elements) {
                if(element.isRendered() && element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = menuItem.getContainerStyleClass();
 
                    writer.startElement("li", null);
                    if(containerStyle != null) writer.writeAttribute("style", containerStyle, null);
                    if(containerStyleClass != null) writer.writeAttribute("class", containerStyleClass, null);
                    
                    encodeMenuItem(context, stack, menuItem);
                    writer.endElement("li");
                }
            }
        }
		
		writer.endElement("ul");
		
		writer.endElement("div");
	}
	    
    @Override
    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("span", null);
        if(menuitem.getValue() != null) 
            writer.write((String) menuitem.getValue());

        writer.endElement("span");

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.endElement("img");
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