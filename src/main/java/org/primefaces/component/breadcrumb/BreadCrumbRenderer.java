/**
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
package org.primefaces.component.breadcrumb;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;

public class BreadCrumbRenderer extends BaseMenuRenderer {

	protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        BreadCrumb breadCrumb = (BreadCrumb) menu;
		String clientId = breadCrumb.getClientId(context);
		String styleClass = breadCrumb.getStyleClass();
		styleClass = styleClass == null ? BreadCrumb.CONTAINER_CLASS : BreadCrumb.CONTAINER_CLASS + " " + styleClass;
        int elementCount = menu.getElementsCount();
        List<MenuElement> menuElements = (List<MenuElement>) menu.getElements();
        boolean isIconHome = breadCrumb.getHomeDisplay().equals("icon");
        
        //home icon for first item
        if(isIconHome && elementCount > 0) {
            ((MenuItem) menuElements.get(0)).setStyleClass("ui-icon ui-icon-home");
        }

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "menu", null);
		if(breadCrumb.getStyle() != null) {
            writer.writeAttribute("style", breadCrumb.getStyle(), null);
        }

        if(elementCount > 0) {            
            writer.startElement("ul", null);
        
            for(int i = 0; i < elementCount; i++) {
                MenuElement element = menuElements.get(i);

                if(element.isRendered() && element instanceof MenuItem) {
                    MenuItem item = (MenuItem) element;

                    //dont render chevron before home icon
                    if(i != 0) {
                        writer.startElement("li", null);
                        writer.writeAttribute("class", BreadCrumb.CHEVRON_CLASS, null);
                        writer.endElement("li");
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("role", "menuitem", null);

                    if(item.isDisabled())
                        encodeDisabledMenuItem(context, item);
                    else
                        encodeMenuItem(context, menu, item);

                    writer.endElement("li");                
                }
            }
            
            UIComponent optionsFacet = menu.getFacet("options");
            if(optionsFacet != null) {
                writer.startElement("li", null);
                writer.writeAttribute("class", BreadCrumb.OPTIONS_CLASS, null);
                writer.writeAttribute("role", "menuitem", null);
                optionsFacet.encodeAll(context);
                writer.endElement("li");
            }
            
            writer.endElement("ul");
        }
        		
		writer.endElement("div");
	}
	
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        // Do nothing
    }

    private void encodeDisabledMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        String style = menuItem.getStyle();
        String styleClass = menuItem.getStyleClass();
        styleClass = styleClass == null ? BreadCrumb.MENUITEM_LINK_CLASS : BreadCrumb.MENUITEM_LINK_CLASS + " " + styleClass;
        styleClass += " ui-state-disabled";
        
        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("span", null);
        writer.writeAttribute("class", BreadCrumb.MENUITEM_TEXT_CLASS, null);
        writer.writeText((String) menuItem.getValue(), "value");
        writer.endElement("span");
        
        writer.endElement("span");
    }
}