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
package org.primefaces.component.breadcrumb;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.HTML;

public class BreadCrumbRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BreadCrumb breadCrumb = (BreadCrumb) menu;
        String clientId = breadCrumb.getClientId(context);
        String styleClass = breadCrumb.getStyleClass();
        styleClass = styleClass == null ? BreadCrumb.CONTAINER_CLASS : BreadCrumb.CONTAINER_CLASS + " " + styleClass;
        int elementCount = menu.getElementsCount();
        List<MenuElement> menuElements = menu.getElements();
        boolean isIconHome = breadCrumb.getHomeDisplay().equals("icon");

        //home icon for first item
        if (isIconHome && elementCount > 0) {
            ((MenuItem) menuElements.get(0)).setStyleClass("ui-icon ui-icon-home");
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "menu", null);
        if (breadCrumb.getStyle() != null) {
            writer.writeAttribute("style", breadCrumb.getStyle(), null);
        }

        if (elementCount > 0) {
            writer.startElement("ul", null);

            for (int i = 0; i < elementCount; i++) {
                MenuElement element = menuElements.get(i);

                if (element.isRendered() && element instanceof MenuItem) {
                    MenuItem item = (MenuItem) element;

                    //dont render chevron before home icon
                    if (i != 0) {
                        writer.startElement("li", null);
                        writer.writeAttribute("class", BreadCrumb.CHEVRON_CLASS, null);
                        writer.endElement("li");
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("role", "menuitem", null);

                    if (item.isDisabled() || (breadCrumb.isLastItemDisabled() && i + 1 == elementCount)) {
                        encodeDisabledMenuItem(context, item);
                    }
                    else {
                        encodeMenuItem(context, menu, item, menu.getTabindex());
                    }

                    writer.endElement("li");
                }
            }

            UIComponent optionsFacet = menu.getFacet("options");
            if (optionsFacet != null) {
                writer.startElement("li", null);
                writer.writeAttribute("class", BreadCrumb.OPTIONS_CLASS, null);
                writer.writeAttribute("role", "menuitem", null);
                optionsFacet.encodeAll(context);
                writer.endElement("li");
            }

            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        // Do nothing
    }

    private void encodeDisabledMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String style = menuItem.getStyle();
        String styleClass = menuItem.getStyleClass();
        styleClass = styleClass == null ? BreadCrumb.MENUITEM_LINK_CLASS : BreadCrumb.MENUITEM_LINK_CLASS + " " + styleClass;
        styleClass += " ui-state-disabled";

        writer.startElement("span", null); // outer span
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        String icon = menuItem.getIcon();
        Object value = menuItem.getValue();

        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", BreadCrumb.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
            writer.endElement("span");
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", BreadCrumb.MENUITEM_TEXT_CLASS, null);

        if (value != null) {
            if (menuItem.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }
        writer.endElement("span"); // text span
        writer.endElement("span"); // outer span
    }
}
