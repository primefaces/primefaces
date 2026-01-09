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
package org.primefaces.component.menu;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Menu.DEFAULT_RENDERER, componentFamily = Menu.COMPONENT_FAMILY)
public class MenuRenderer extends BaseMenuRenderer<Menu> {

    @Override
    protected void encodeScript(FacesContext context, Menu component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PlainMenu", component)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("toggleable", component.isToggleable(), false)
                .attr("tabIndex", component.getTabindex(), "0")
                .attr("statefulGlobal", component.isStatefulGlobal(), false);

        if (component.isOverlay()) {
            encodeOverlayConfig(context, component, wb);
            wb.attr("collision", component.getCollision());
        }

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, Menu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String clientId = component.getClientId(context);
        boolean isOverlay = component.isOverlay();
        String maxHeight = component.getMaxHeight();
        boolean hasMaxHeight = LangUtils.isNotEmpty(maxHeight);
        String styleClass = getStyleClassBuilder(context)
                .add(component.getStyleClass())
                .add(isOverlay, Menu.DYNAMIC_CONTAINER_CLASS)
                .add(!isOverlay, Menu.STATIC_CONTAINER_CLASS)
                .add(component.isToggleable(), Menu.TOGGLEABLE_MENU_CLASS)
                .add(hasMaxHeight, Menu.CONTAINER_MAXHEIGHT_CLASS)
                .build();

        String style = getStyleBuilder(context)
                .add(component.getStyle())
                .add(hasMaxHeight, "max-height", maxHeight)
                .build();

        if (hasMaxHeight) {
            // If maxHeight is a number, add the unit "px", otherwise use it as is
            char lastChar = maxHeight.charAt(maxHeight.length() - 1);
            if (Character.isDigit(lastChar)) {
                style += "px";
            }
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);

        if (component.getElementsCount() > 0) {
            writer.writeAttribute("tabindex", "-1", "tabindex");
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.LIST_CLASS, null);
            encodeElements(context, component, component.getElements(), false, true);
            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    protected void encodeElements(FacesContext context, Menu component, List<MenuElement> elements, boolean isSubmenu, boolean visible)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean toggleable = component.isToggleable();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;

                    String containerStyle = getStyleBuilder(context)
                            .add(menuItem.getContainerStyle())
                            .add(!visible, "display", "none")
                            .build();

                    String containerStyleClass = getStyleClassBuilder(context)
                            .add(Menu.MENUITEM_CLASS)
                            .add(menuItem.getContainerStyleClass())
                            .build();

                    if (toggleable && isSubmenu) {
                        containerStyleClass = containerStyleClass + " " + Menu.SUBMENU_CHILD_CLASS;
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("class", containerStyleClass, null);
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM, null);
                    if (LangUtils.isNotBlank(containerStyle)) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    encodeMenuItem(context, component, menuItem, "-1");
                    writer.endElement("li");
                }
                else if (element instanceof Submenu) {
                    encodeSubmenu(context, component, (Submenu) element);
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeSubmenu(FacesContext context, Menu component, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String icon = submenu.getIcon();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;
        boolean toggleable = component.isToggleable();
        boolean expanded = !toggleable || submenu.isExpanded();

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

        if (component.isToggleable()) {
            encodeIcon(context, label, expanded ? Menu.EXPANDED_SUBMENU_HEADER_ICON_CLASS : Menu.COLLAPSED_SUBMENU_HEADER_ICON_CLASS);
        }

        if (icon != null) {
            encodeIcon(context, label, "ui-submenu-icon ui-icon " + icon);
        }

        if (label != null) {
            writer.writeText(label, "value");
        }

        writer.endElement("h3");

        writer.endElement("li");

        encodeElements(context, component, submenu.getElements(), true, expanded);
    }

    protected void encodeIcon(FacesContext context, String label, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        writer.endElement("span");
    }
}
