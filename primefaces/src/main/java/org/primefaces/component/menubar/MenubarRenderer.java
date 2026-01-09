/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Menubar.DEFAULT_RENDERER, componentFamily = Menubar.COMPONENT_FAMILY)
public class MenubarRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menubar menubar = (Menubar) abstractMenu;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Menubar", menubar)
                .attr("autoDisplay", menubar.isAutoDisplay())
                .attr("showDelay", menubar.getShowDelay(), 0)
                .attr("hideDelay", menubar.getHideDelay(), 0)
                .attr("tabIndex", menubar.getTabindex(), "0")
                .attr("toggleEvent", menubar.getToggleEvent(), null);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menubar menubar = (Menubar) abstractMenu;
        String style = menubar.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(menubar.getStyleClass())
                .add(Menubar.CONTAINER_CLASS)
                .add(ComponentUtils.isRTL(context, abstractMenu), AbstractMenu.MENU_RTL_CLASS)
                .build();

        encodeMenu(context, menubar, style, styleClass, HTML.ARIA_ORIENTATION_HORIZONTAL);
    }

    @Override
    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu, boolean isRtl, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object parent = submenu.getParent();
        String icon = null;
        String rtlStyleClass = isRtl ? Menu.SUBMENU_LEFT_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;

        if (parent == null) {
            icon = (submenu.getId().startsWith("_")) ? Menu.SUBMENU_DOWN_ICON_CLASS : rtlStyleClass;
        }
        else {
            icon = (parent instanceof Menubar) ? Menu.SUBMENU_DOWN_ICON_CLASS : rtlStyleClass;
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        writer.endElement("span");
    }
}
