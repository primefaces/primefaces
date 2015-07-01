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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.tabmenu.TabMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.WidgetBuilder;

public class TabMenuRenderer extends BaseMenuRenderer {
        
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TabMenu tabMenu = (TabMenu) component;
        List<MenuElement> elements = tabMenu.getElements();
        String style = tabMenu.getStyle();
        String styleClass = tabMenu.getStyleClass();
        styleClass = (styleClass == null) ? TabMenu.MOBILE_CONTAINER_CLASS : TabMenu.MOBILE_CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", tabMenu);
        writer.writeAttribute("id", tabMenu.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "navigation", null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("ul", null);
        if(tabMenu.getElementsCount() > 0) {
            for(MenuElement element : elements) {
                if(element.isRendered() && element instanceof MenuItem) {
                    writer.startElement("li", null);
                    encodeMenuItem(context, tabMenu, (MenuItem) element);
                    writer.endElement("li");
                }
            }
        }
        writer.endElement("ul");
        
        renderDynamicPassThruAttributes(context, tabMenu);
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TabMenu menu = (TabMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TabMenu", menu.resolveWidgetVar(), clientId).attr("activeIndex", menu.getActiveIndex());
        wb.finish();
    }
    
    @Override
    protected String getLinkStyleClass(MenuItem menuitem) {
        String icon = menuitem.getIcon();
        String iconPos = menuitem.getIconPos();
        iconPos = (iconPos == null) ? "ui-btn-icon-top": "ui-btn-icon-" + iconPos;
        String styleClass = (icon == null) ? AbstractMenu.MOBILE_MENUITEM_LINK_CLASS: AbstractMenu.MOBILE_MENUITEM_LINK_CLASS + " " + icon + " " + iconPos;
        String userStyleClass = menuitem.getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
        
        return styleClass;
    }
}
