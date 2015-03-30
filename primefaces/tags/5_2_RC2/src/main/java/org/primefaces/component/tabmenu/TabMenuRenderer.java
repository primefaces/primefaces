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
package org.primefaces.component.tabmenu;

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

public class TabMenuRenderer extends BaseMenuRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TabMenu menu = (TabMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TabMenu", menu.resolveWidgetVar(), clientId);
        wb.finish();
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        TabMenu menu = (TabMenu) component;
		String clientId = menu.getClientId(context);
		String styleClass = menu.getStyleClass();
		styleClass = styleClass == null ? TabMenu.CONTAINER_CLASS : TabMenu.CONTAINER_CLASS + " " + styleClass;
        int activeIndex = menu.getActiveIndex();
        List<MenuElement> elements = menu.getElements();

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if(menu.getStyle() != null) {
            writer.writeAttribute("style", menu.getStyle(), "style");
        }        
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", TabMenu.NAVIGATOR_CLASS, null);
        writer.writeAttribute("role", "tablist", null);

        int i = 0;
        if(elements != null && !elements.isEmpty()) {
            for(MenuElement element : elements) {
                if(element.isRendered() && (element instanceof MenuItem)) {
                    encodeItem(context, menu, (MenuItem) element, (i == activeIndex));
                    i++;
                }
            }
        }
        
        writer.endElement("ul");
        
        writer.endElement("div");
    }
    
    protected void encodeItem(FacesContext context, TabMenu menu, MenuItem item, boolean active) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String containerClass = active ? TabMenu.ACTIVE_TAB_HEADER_CLASS : TabMenu.INACTIVE_TAB_HEADER_CLASS;
        if(item.getIcon() != null) {
            containerClass += " ui-tabmenuitem-hasicon";
        }
        
        //header container
        writer.startElement("li", null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tab", null);
        writer.writeAttribute("aria-expanded", String.valueOf(active), null);

        encodeMenuItem(context, menu, item);
        
        writer.endElement("li");
    }

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}
