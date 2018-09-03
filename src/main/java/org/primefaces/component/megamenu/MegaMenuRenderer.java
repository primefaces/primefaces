/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.megamenu;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.separator.UISeparator;
import org.primefaces.model.menu.*;
import org.primefaces.util.WidgetBuilder;

public class MegaMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        MegaMenu menu = (MegaMenu) abstractMenu;
        String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("MegaMenu", menu.resolveWidgetVar(), clientId)
                .attr("autoDisplay", menu.isAutoDisplay())
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
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? MegaMenu.CONTAINER_CLASS : MegaMenu.CONTAINER_CLASS + " " + styleClass;

        if (vertical) {
            styleClass = styleClass + " " + MegaMenu.VERTICAL_CLASS;
        }

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menubar", null);

        encodeKeyboardTarget(context, menu);

        writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        if (menu.getElementsCount() > 0) {
            encodeRootItems(context, menu);
        }

        UIComponent optionsFacet = menu.getFacet("options");
        if (optionsFacet != null) {
            writer.startElement("li", null);
            writer.writeAttribute("class", Menu.OPTIONS_CLASS, null);
            writer.writeAttribute("role", "menuitem", null);
            optionsFacet.encodeAll(context);
            writer.endElement("li");
        }

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
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, menu, (MenuItem) element);
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
        boolean vertical = menu.getOrientation().equals("vertical");
        String icon = submenu.getIcon();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;

        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        writer.writeAttribute("role", "menuitem", null);
        writer.writeAttribute("aria-haspopup", "true", null);

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Menu.SUBMENU_LINK_CLASS, null);
        writer.writeAttribute("tabindex", "-1", null);

        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        if (label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(submenu.getLabel(), "value");
            writer.endElement("span");
        }

        //submenu icon
        String submenuIcon = (vertical) ? Menu.SUBMENU_RIGHT_ICON_CLASS : Menu.SUBMENU_DOWN_ICON_CLASS;
        writer.startElement("span", null);
        writer.writeAttribute("class", submenuIcon, null);
        writer.endElement("span");

        writer.endElement("a");

        //submenus
        if (submenu.getElementsCount() > 0) {
            List<MenuElement> submenuElements = submenu.getElements();
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
            writer.writeAttribute("role", "menu", null);

            writer.startElement("table", null);
            writer.writeAttribute("role", "presentation", null);

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
            List columnElements = column.getElements();
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
        writer.writeAttribute("role", "menu", null);

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("span", null);
        if (label != null) {
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
                        writer.writeAttribute("role", "menuitem", null);
                        encodeMenuItem(context, menu, (MenuItem) submenuElement);
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
        String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? UISeparator.DEFAULT_STYLE_CLASS : UISeparator.DEFAULT_STYLE_CLASS + " " + styleClass;

        writer.startElement("hr", null);
        writer.writeAttribute("class", styleClass, "styleClass");

        if (separator.getTitle() != null) {
            writer.writeAttribute("title", separator.getTitle(), "title");
        }
        if (separator.getStyle() != null) {
            writer.writeAttribute("style", separator.getStyle(), "style");
        }

        writer.endElement("hr");
    }
}
