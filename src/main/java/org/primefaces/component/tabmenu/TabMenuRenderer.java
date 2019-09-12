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
package org.primefaces.component.tabmenu;

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
import org.primefaces.util.WidgetBuilder;

public class TabMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TabMenu menu = (TabMenu) abstractMenu;
        String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TabMenu", menu.resolveWidgetVar(context), clientId);
        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TabMenu menu = (TabMenu) component;
        String clientId = menu.getClientId(context);
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? TabMenu.CONTAINER_CLASS : TabMenu.CONTAINER_CLASS + " " + styleClass;
        int activeIndex = menu.getActiveIndex();
        List<?> elements = menu.getElements();

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (menu.getStyle() != null) {
            writer.writeAttribute("style", menu.getStyle(), "style");
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", TabMenu.NAVIGATOR_CLASS, null);
        writer.writeAttribute("role", "tablist", null);

        int i = 0;
        if (elements != null && !elements.isEmpty()) {
            for (Object element : elements) {
                if (element instanceof MenuElement) {
                    if (((MenuElement) element).isRendered() && (element instanceof MenuItem)) {
                        encodeItem(context, menu, (MenuItem) element, (i == activeIndex));
                        i++;
                    }
                }
            }
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, TabMenu menu, MenuItem item, boolean active) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String containerStyle = item.getContainerStyle();
        String containerStyleClass = item.getContainerStyleClass();
        String containerClass = active ? TabMenu.ACTIVE_TAB_HEADER_CLASS : TabMenu.INACTIVE_TAB_HEADER_CLASS;
        if (item.getIcon() != null) {
            containerClass += " ui-tabmenuitem-hasicon";
        }

        if (containerStyleClass != null) {
            containerClass = containerClass + " " + containerStyleClass;
        }

        //header container
        writer.startElement("li", null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tab", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(active), null);

        if (containerStyle != null) {
            writer.writeAttribute("style", containerStyle, null);
        }

        encodeMenuItem(context, menu, item, "-1");

        writer.endElement("li");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
