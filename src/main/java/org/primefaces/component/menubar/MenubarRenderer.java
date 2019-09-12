/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.menubar;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.model.menu.BaseMenuModel;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class MenubarRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menubar menubar = (Menubar) abstractMenu;
        String clientId = menubar.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Menubar", menubar.resolveWidgetVar(context), clientId)
                .attr("autoDisplay", menubar.isAutoDisplay())
                .attr("toggleEvent", menubar.getToggleEvent(), null);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menubar menubar = (Menubar) abstractMenu;
        String style = menubar.getStyle();
        String styleClass = menubar.getStyleClass();
        styleClass = styleClass == null ? Menubar.CONTAINER_CLASS : Menubar.CONTAINER_CLASS + " " + styleClass;

        encodeMenu(context, menubar, style, styleClass, "menubar");
    }

    @Override
    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object parent = submenu.getParent();
        String icon = null;

        if (parent == null) {
            icon = (submenu.getId().indexOf(BaseMenuModel.ID_SEPARATOR) == -1) ? Menu.SUBMENU_DOWN_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;
        }
        else {
            icon = (parent instanceof Menubar) ? Menu.SUBMENU_DOWN_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");
    }
}
