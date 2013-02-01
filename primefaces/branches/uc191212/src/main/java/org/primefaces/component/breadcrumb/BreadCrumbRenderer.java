/**
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
package org.primefaces.component.breadcrumb;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;

public class BreadCrumbRenderer extends BaseMenuRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		BreadCrumb breadCrumb = (BreadCrumb) component;
		
		if(breadCrumb.isDynamic()) {
			breadCrumb.buildMenuFromModel();
		}

		encodeMarkup(context, breadCrumb);
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        BreadCrumb breadCrumb = (BreadCrumb) menu;
		String clientId = breadCrumb.getClientId(context);
		String styleClass = breadCrumb.getStyleClass();
		styleClass = styleClass == null ? BreadCrumb.CONTAINER_CLASS : BreadCrumb.CONTAINER_CLASS + " " + styleClass;
        
        //home icon for first item
        if(breadCrumb.getChildCount() > 0) {
            ((MenuItem) breadCrumb.getChildren().get(0)).setStyleClass("ui-icon ui-icon-home");
        }

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "menu", null);
		if(breadCrumb.getStyle() != null) {
            writer.writeAttribute("style", breadCrumb.getStyle(), null);
        }

		writer.startElement("ul", null);
        
        for(int i = 0; i < breadCrumb.getChildCount(); i++) {
            UIComponent child = breadCrumb.getChildren().get(i);
            
            if(child.isRendered() && child instanceof MenuItem) {
                MenuItem item = (MenuItem) child;
                        
                //dont render chevron before home icon
                if(i != 0) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", BreadCrumb.CHEVRON_CLASS, null);
                    writer.endElement("li");
                }
                
				writer.startElement("li", null);
                writer.writeAttribute("role", "menuitem", null);

                if(item.isDisabled())
                    encodeDisabledMenuItem(context, (MenuItem) child);
                else
                    encodeMenuItem(context, (MenuItem) child);

				writer.endElement("li");                
			}
        }

		writer.endElement("ul");
		
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
        if(menuItem.getStyle() != null) {
            writer.writeAttribute("style", menuItem.getStyle(), null);
        }
        
        writer.startElement("span", null);
        writer.writeAttribute("class", BreadCrumb.MENUITEM_TEXT_CLASS, null);
        writer.writeText((String) menuItem.getValue(), "value");
        writer.endElement("span");
        
        writer.endElement("span");
    }
}