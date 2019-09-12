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
package org.primefaces.component.dock;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.WidgetBuilder;

public class DockRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
        Dock dock = (Dock) menu;
        String clientId = dock.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dock", dock.resolveWidgetVar(context), clientId)
                .attr("position", dock.getPosition())
                .attr("maxWidth", dock.getMaxWidth())
                .attr("itemWidth", dock.getItemWidth())
                .attr("proximity", dock.getProximity())
                .attr("halign", dock.getHalign());

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
        writer.writeAttribute("class", "ui-dock-" + position + " ui-widget", "styleClass");
        renderPassThruAttributes(context, dock, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-dock-container-" + position + " ui-widget-header", null);

        encodeMenuItems(context, dock);

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeMenuItems(FacesContext context, Dock dock) throws IOException {
        if (dock.getElementsCount() > 0) {
            List<MenuElement> menuElements = dock.getElements();

            for (MenuElement element : menuElements) {
                if (element.isRendered() && element instanceof MenuItem) {
                    encodeMenuItem(context, dock, (MenuItem) element, "-1");
                }
            }
        }
    }

    @Override
    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        String position = ((Dock) menu).getPosition();

        if (position.equalsIgnoreCase("top")) {
            encodeItemIcon(context, menuitem);
            encodeItemLabel(context, menuitem);
        }
        else {
            encodeItemLabel(context, menuitem);
            encodeItemIcon(context, menuitem);
        }
    }

    protected void encodeItemIcon(FacesContext context, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.endElement("img");
    }

    protected void encodeItemLabel(FacesContext context, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

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
