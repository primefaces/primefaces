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
package org.primefaces.component.megamenu;

import org.primefaces.component.divider.Divider;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
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

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = MegaMenu.DEFAULT_RENDERER, componentFamily = MegaMenu.COMPONENT_FAMILY)
public class MegaMenuRenderer extends BaseMenuRenderer<MegaMenu> {

    @Override
    protected void encodeScript(FacesContext context, MegaMenu component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("MegaMenu", component)
                .attr("tabIndex", component.getTabindex(), "0")
                .attr("autoDisplay", component.isAutoDisplay())
                .attr("delay", component.getDelay())
                .attr("activeIndex", component.getActiveIndex(), Integer.MIN_VALUE);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, MegaMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean vertical = component.getOrientation().equals("vertical");
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(MegaMenu.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .add(vertical, MegaMenu.VERTICAL_CLASS)
                .add(ComponentUtils.isRTL(context, component), AbstractMenu.MENU_RTL_CLASS)
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("tabindex", "-1", "tabindex");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("ul", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUBAR, null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        encodeFacet(context, component, "start", Menu.START_CLASS);

        if (component.getElementsCount() > 0) {
            encodeRootItems(context, component);
        }

        encodeFacet(context, component, "options", Menu.OPTIONS_CLASS);
        encodeFacet(context, component, "end", Menu.END_CLASS);

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeRootItems(FacesContext context, MegaMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<MenuElement> elements = component.getElements();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                    encodeMenuItem(context, component, (MenuItem) element, "-1");
                    writer.endElement("li");
                }
                else if (element instanceof Submenu) {
                    encodeRootSubmenu(context, component, (Submenu) element);
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeRootSubmenu(FacesContext context, MegaMenu component, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isRtl = ComponentUtils.isRTL(context, component);
        boolean isVertical = component.getOrientation().equals("vertical");
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
                    encodeColumn(context, component, (MenuColumn) submenuElement);
                }
            }

            writer.endElement("tr");
            writer.endElement("tbody");
            writer.endElement("table");

            writer.endElement("ul");
        }

        writer.endElement("li");
    }

    protected void encodeColumn(FacesContext context, MegaMenu component, MenuColumn column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("td", null);
        if (column.getStyle() != null) {
            writer.writeAttribute("style", column.getStyle(), null);
        }
        if (column.getStyleClass() != null) {
            writer.writeAttribute("class", column.getStyleClass(), null);
        }

        // Render the elements inside the column, which can include MenuElements (like Submenu or Separator) and UIComponents
        if (column.getElementsCount() > 0) {
            // Use List<?> as columns may contain various types.
            List<?> columnElements = column.getElements();
            for (Object element : columnElements) {
                if (element instanceof MenuElement) {
                    MenuElement menuElement = (MenuElement) element;
                    if (menuElement.isRendered()) {
                        if (element instanceof Submenu) {
                            encodeDescendantSubmenu(context, component, (Submenu) element);
                        }
                        else if (element instanceof Separator) {
                            encodeSubmenuSeparator(context, (Separator) element);
                        }
                    }
                }
                // UIComponent support allows for arbitrary component placement in columns.
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
                .add(Divider.STYLE_CLASS)
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
