/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.megamenu;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.separator.UISeparator;
import org.primefaces.model.menu.MenuColumn;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class MegaMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        MegaMenu menu = (MegaMenu) abstractMenu;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("MegaMenu", menu)
                .attr("tabIndex", menu.getTabindex(), "0")
                .attr("autoDisplay", menu.isAutoDisplay())
                .attr("delay", menu.getDelay())
                .attr("activeIndex", menu.getActiveIndex(), Integer.MIN_VALUE);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MegaMenu menu = (MegaMenu) abstractMenu;
        boolean vertical = menu.getOrientation().equals("vertical");
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(MegaMenu.CONTAINER_CLASS)
                .add(menu.getStyleClass())
                .add(vertical, MegaMenu.VERTICAL_CLASS)
                .add(ComponentUtils.isRTL(context, abstractMenu), AbstractMenu.MENU_RTL_CLASS)
                .build();

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("tabindex", "-1", "tabindex");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("ul", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUBAR, null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        encodeFacet(context, menu, "start", Menu.START_CLASS);

        if (menu.getElementsCount() > 0) {
            encodeRootItems(context, menu);
        }

        encodeFacet(context, menu, "options", Menu.OPTIONS_CLASS);
        encodeFacet(context, menu, "end", Menu.END_CLASS);

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeRootItems(FacesContext context, MegaMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<MenuElement> elements = menu.getElements();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                    encodeMenuItem(context, menu, (MenuItem) element, "-1");
                    writer.endElement("li");
                }
                else if (element instanceof Submenu) {
                    encodeRootSubmenu(context, menu, (Submenu) element);
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeRootSubmenu(FacesContext context, MegaMenu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isRtl = ComponentUtils.isRTL(context, menu);
        boolean isVertical = menu.getOrientation().equals("vertical");
        String style = submenu.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Menu.TIERED_SUBMENU_CLASS)
                .add(submenu.getStyleClass())
                .build();

        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if (shouldRenderId(submenu)) {
            writer.writeAttribute("id", submenu.getClientId(), null);
        }
        if (LangUtils.isNotEmpty(style)) {
            writer.writeAttribute("style", style, null);
        }
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);

        //title
        writer.startElement("a", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM, null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, "true", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Menu.SUBMENU_LINK_CLASS, null);
        writer.writeAttribute("tabindex", "-1", null);

        if (isRtl) {
            encodeSubmenuIcon(context, submenu, isRtl, isVertical);
            encodeMenuLabel(context, submenu);
            encodeMenuIcon(context, submenu);
        }
        else {
            encodeMenuIcon(context, submenu);
            encodeMenuLabel(context, submenu);
            encodeSubmenuIcon(context, submenu, isRtl, isVertical);
        }


        writer.endElement("a");

        //submenus
        if (submenu.getElementsCount() > 0) {
            List<MenuElement> submenuElements = submenu.getElements();
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);

            writer.startElement("table", null);
            writer.writeAttribute(HTML.ARIA_ROLE, "presentation", null);

            writer.startElement("tbody", null);
            writer.startElement("tr", null);

            for (MenuElement submenuElement : submenuElements) {
                if (submenuElement.isRendered() && submenuElement instanceof MenuColumn) {
                    encodeColumn(context, menu, (MenuColumn) submenuElement);
                }
            }

            writer.endElement("tr");
            writer.endElement("tbody");
            writer.endElement("table");

            writer.endElement("ul");
        }

        writer.endElement("li");
    }

    protected void encodeColumn(FacesContext context, MegaMenu menu, MenuColumn column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("td", null);
        if (column.getStyle() != null) {
            writer.writeAttribute("style", column.getStyle(), null);
        }
        if (column.getStyleClass() != null) {
            writer.writeAttribute("class", column.getStyleClass(), null);
        }

        if (column.getElementsCount() > 0) {
            List<?> columnElements = column.getElements();
            for (Object element : columnElements) {
                if (element instanceof MenuElement) {
                    if (((MenuElement) element).isRendered()) {
                        if (element instanceof Submenu) {
                            encodeDescendantSubmenu(context, menu, (Submenu) element);
                        }
                        else if (element instanceof Separator) {
                            encodeSubmenuSeparator(context, (Separator) element);
                        }
                    }
                }
                else if (element instanceof UIComponent) {
                    ((UIComponent) element).encodeAll(context);
                }
            }
        }

        writer.endElement("td");
    }

    protected void encodeDescendantSubmenu(FacesContext context, MegaMenu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;

        writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);

        //title
        writer.startElement("li", null);
        if (shouldRenderId(submenu)) {
            writer.writeAttribute("id", submenu.getClientId(), null);
        }
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("span", null);
        if (LangUtils.isNotEmpty(label)) {
            writer.writeText(label, "value");
        }
        writer.endElement("span");

        writer.endElement("li");

        //menuitems
        if (submenu.getElementsCount() > 0) {
            List<MenuElement> submenuElements = submenu.getElements();
            for (MenuElement submenuElement : submenuElements) {
                if (submenuElement.isRendered()) {
                    if (submenuElement instanceof MenuItem) {
                        writer.startElement("li", null);
                        writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                        encodeMenuItem(context, menu, (MenuItem) submenuElement, "-1");
                        writer.endElement("li");
                    }
                    else if (submenuElement instanceof Separator) {
                        encodeSeparator(context, (Separator) submenuElement);
                    }
                }
            }
        }

        writer.endElement("ul");
    }

    protected void encodeSubmenuSeparator(FacesContext context, Separator separator) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String title = separator.getTitle();
        String style = separator.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(UISeparator.DEFAULT_STYLE_CLASS)
                .add(separator.getStyleClass())
                .build();

        writer.startElement("hr", null);
        writer.writeAttribute("class", styleClass, "styleClass");

        if (LangUtils.isNotEmpty(title)) {
            writer.writeAttribute("title", title, "title");
        }
        if (LangUtils.isNotEmpty(style)) {
            writer.writeAttribute("style", style, "style");
        }

        writer.endElement("hr");
    }
}
