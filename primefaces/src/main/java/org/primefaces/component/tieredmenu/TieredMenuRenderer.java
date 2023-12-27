/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.tieredmenu;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.badge.BadgeRenderer;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class TieredMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TieredMenu menu = (TieredMenu) abstractMenu;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TieredMenu", menu)
                .attr("autoDisplay", menu.isAutoDisplay())
                .attr("showDelay", menu.getShowDelay(), 0)
                .attr("hideDelay", menu.getHideDelay(), 0)
                .attr("toggleEvent", menu.getToggleEvent(), null);

        if (menu.isOverlay()) {
            encodeOverlayConfig(context, menu, wb);
        }

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TieredMenu menu = (TieredMenu) abstractMenu;
        String style = menu.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(menu.getStyleClass())
                .add(menu.isOverlay(), TieredMenu.DYNAMIC_CONTAINER_CLASS, TieredMenu.STATIC_CONTAINER_CLASS)
                .add(ComponentUtils.isRTL(context, abstractMenu), AbstractMenu.MENU_RTL_CLASS)
                .build();

        encodeMenu(context, menu, style, styleClass, HTML.ARIA_ROLE_MENU);
    }

    protected void encodeMenu(FacesContext context, AbstractMenu menu, String style, String styleClass, String role) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent optionsFacet = menu.getFacet("options");

        writer.startElement("div", menu);
        writer.writeAttribute("id", menu.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeKeyboardTarget(context, menu);

        writer.startElement("ul", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUBAR, null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        if (menu.getElementsCount() > 0) {
            encodeElements(context, menu, menu.getElements());
        }

        if (FacetUtils.shouldRenderFacet(optionsFacet)) {
            writer.startElement("li", null);
            writer.writeAttribute("class", Menu.OPTIONS_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
            optionsFacet.encodeAll(context);
            writer.endElement("li");
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeElements(FacesContext context, AbstractMenu menu, List<MenuElement> elements) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = getStyleClassBuilder(context)
                            .add(Menu.MENUITEM_CLASS)
                            .add(menuItem.getContainerStyleClass())
                            .add(menuItem.getBadge() != null, "ui-overlay-badge")
                            .build();

                    writer.startElement("li", null);
                    writer.writeAttribute("class", containerStyleClass, null);
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                    if (containerStyle != null) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    if (menuItem.getBadge() != null) {
                        BadgeRenderer.encode(context, menuItem.getBadge());
                    }
                    encodeMenuItem(context, menu, menuItem, "-1");
                    writer.endElement("li");
                }
                else if (element instanceof Submenu) {
                    Submenu submenu = (Submenu) element;
                    String style = submenu.getStyle();
                    String styleClass = submenu.getStyleClass();
                    styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;

                    writer.startElement("li", null);
                    if (shouldRenderId(submenu)) {
                        writer.writeAttribute("id", submenu.getClientId(), null);
                    }
                    writer.writeAttribute("class", styleClass, null);
                    if (style != null) {
                        writer.writeAttribute("style", style, null);
                    }
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                    encodeSubmenu(context, menu, submenu);
                    writer.endElement("li");
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeSubmenu(FacesContext context, AbstractMenu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean disabled = submenu.isDisabled();
        boolean isRtl = ComponentUtils.isRTL(context, menu);

        //title
        writer.startElement("a", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM, null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, "true", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("tabindex", "-1", null);

        String styleClass = Menu.SUBMENU_LINK_CLASS;
        if (disabled) {
            styleClass = styleClass + " ui-state-disabled";
        }
        writer.writeAttribute("class", styleClass, null);

        if (disabled) {
            writer.writeAttribute("onclick", "return false;", null);
        }

        if (isRtl) {
            encodeSubmenuIcon(context, submenu, isRtl);
            encodeMenuLabel(context, submenu);
            encodeMenuIcon(context, submenu);
        }
        else {
            encodeMenuIcon(context, submenu);
            encodeMenuLabel(context, submenu);
            encodeSubmenuIcon(context, submenu, isRtl);
        }

        writer.endElement("a");

        if (!disabled) {
            //submenus and menuitems
            if (submenu.getElementsCount() > 0) {
                writer.startElement("ul", null);
                writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);
                encodeElements(context, menu, submenu.getElements());
                writer.endElement("ul");
            }
        }
    }

    protected void encodeMenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();

        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
            writer.endElement("span");
        }
    }

    protected void encodeMenuLabel(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();

        if (label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(label, "value");
            writer.endElement("span");
        }
    }

    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu, boolean isRtl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = isRtl ? Menu.SUBMENU_LEFT_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        writer.endElement("span");
    }
}
