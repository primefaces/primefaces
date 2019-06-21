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
package org.primefaces.component.menu;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class MenuRenderer extends BaseMenuRenderer {

    public static final String STATIC_CONTAINER_CLASS = "ui-menu ui-widget ui-widget-content ui-corner-all ui-helper-clearfix";
    public static final String DYNAMIC_CONTAINER_CLASS = "ui-menu ui-menu-dynamic ui-widget ui-widget-content ui-corner-all ui-helper-clearfix ui-shadow";
    public static final String EXPANDED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String COLLAPSED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String TOGGLEABLE_MENU_CLASS = "ui-menu-toggleable";
    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menu menu = (Menu) abstractMenu;
        String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PlainMenu", menu.resolveWidgetVar(), clientId)
                .attr("toggleable", menu.isToggleable(), false);

        if (menu.isOverlay()) {
            encodeOverlayConfig(context, menu, wb);
        }

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? DYNAMIC_CONTAINER_CLASS : STATIC_CONTAINER_CLASS;
        if (menu.isToggleable()) {
            defaultStyleClass = defaultStyleClass + " " + TOGGLEABLE_MENU_CLASS;
        }
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);

        encodeKeyboardTarget(context, menu);

        if (menu.getElementsCount() > 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", LIST_CLASS, null);
            encodeElements(context, menu, menu.getElements(), false);
            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    protected void encodeElements(FacesContext context, Menu menu, List<MenuElement> elements, boolean isSubmenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean toggleable = menu.isToggleable();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = menuItem.getContainerStyleClass();
                    containerStyleClass = (containerStyleClass == null) ? MENUITEM_CLASS : MENUITEM_CLASS + " " + containerStyleClass;

                    if (toggleable && isSubmenu) {
                        containerStyleClass = containerStyleClass + " " + SUBMENU_CHILD_CLASS;
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("class", containerStyleClass, null);
                    writer.writeAttribute("role", "menuitem", null);
                    if (containerStyle != null) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    encodeMenuItem(context, menu, menuItem, "-1");
                    writer.endElement("li");
                }
                else if (element instanceof Submenu) {
                    encodeSubmenu(context, menu, (Submenu) element);
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeSubmenu(FacesContext context, Menu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String icon = submenu.getIcon();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? SUBMENU_TITLE_CLASS : SUBMENU_TITLE_CLASS + " " + styleClass;
        boolean toggleable = menu.isToggleable();

        //title
        writer.startElement("li", null);
        if (toggleable) {
            writer.writeAttribute("id", submenu.getClientId(), null);
        }
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("h3", null);

        if (menu.isToggleable()) {
            encodeIcon(context, label, EXPANDED_SUBMENU_HEADER_ICON_CLASS);
        }

        if (icon != null) {
            encodeIcon(context, label, "ui-submenu-icon ui-icon " + icon);
        }

        if (label != null) {
            writer.writeText(label, "value");
        }

        writer.endElement("h3");

        writer.endElement("li");

        encodeElements(context, menu, submenu.getElements(), true);
    }

    protected void encodeIcon(FacesContext context, String label, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        writer.endElement("span");
    }
}
