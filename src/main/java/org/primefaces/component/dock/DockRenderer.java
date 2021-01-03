/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.dock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class DockRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
        Dock dock = (Dock) menu;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dock", dock)
                .attr("position", dock.getPosition())
                .attr("halign", dock.getHalign())
                .attr("blockScroll", dock.isBlockScroll(), false)
                .attr("animate", dock.isAnimate())
                .attr("animationDuration", dock.getAnimationDuration());

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Dock dock = (Dock) menu;
        String clientId = dock.getClientId(context);
        String position = dock.getPosition();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "ui-dock " + position + " ui-widget", "styleClass");
        renderPassThruAttributes(context, dock);

        writer.startElement("ul", null);
        writer.writeAttribute("class", "ui-dock-container " + dock.getHalign() + " " + position, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENU, null);

        encodeMenuItems(context, dock);

        writer.endElement("ul");
        writer.endElement("div");
    }

    protected void encodeMenuItems(FacesContext context, Dock dock) throws IOException {
        if (dock.getElementsCount() > 0) {
            ResponseWriter writer = context.getResponseWriter();
            List<MenuElement> menuElements = new ArrayList<>(dock.getElements());

            if (ComponentUtils.isRTL(context, dock)) {
                Collections.reverse(menuElements);
            }

            for (MenuElement element : menuElements) {
                if (element.isRendered() && element instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", "ui-dock-item", "styleClass");
                    writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
                    encodeMenuItem(context, dock, (MenuItem) element, "-1");
                    writer.endElement("li");
                }
            }
        }
    }

    @Override
    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        encodeItemLabel(context, menuitem);
        encodeItemIcon(context, menuitem);
    }

    protected void encodeItemIcon(FacesContext context, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.writeAttribute("class", "ui-dock-image", "styleClass");
        writer.endElement("img");
    }

    protected void encodeItemLabel(FacesContext context, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("em", null);
        writer.startElement("span", null);

        if (menuitem.getValue() != null) {
            if (menuitem.isEscape()) {
                writer.writeText(menuitem.getValue(), "value");
            }
            else {
                writer.write((String) menuitem.getValue());
            }
        }

        writer.endElement("span");
        writer.endElement("em");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
