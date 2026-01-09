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
package org.primefaces.component.stack;

import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Stack.DEFAULT_RENDERER, componentFamily = Stack.COMPONENT_FAMILY)
public class StackRenderer extends BaseMenuRenderer<Stack> {

    @Override
    protected void encodeScript(FacesContext context, Stack component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Stack", component)
                .attr("openSpeed", component.getOpenSpeed())
                .attr("closeSpeed", component.getCloseSpeed())
                .attr("expanded", component.isExpanded(), false);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, Stack component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "ui-stack", null);

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, component.getIcon()), null);
        writer.endElement("img");

        if (component.getElementsCount() > 0) {
            List<MenuElement> elements = component.getElements();

            writer.startElement("ul", null);
            writer.writeAttribute("id", clientId + "_stack", "id");

            for (MenuElement element : elements) {
                if (element.isRendered() && element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = menuItem.getContainerStyleClass();

                    writer.startElement("li", null);
                    if (containerStyle != null) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    if (containerStyleClass != null) {
                        writer.writeAttribute("class", containerStyleClass, null);
                    }

                    encodeMenuItem(context, component, menuItem, "-1");
                    writer.endElement("li");
                }
            }
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    @Override
    protected void encodeMenuItemContent(FacesContext context, Stack component, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        if (menuitem.getValue() != null) {
            writer.writeText(menuitem.getValue(), "value");
        }

        writer.endElement("span");

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.endElement("img");
    }

    @Override
    public void encodeChildren(FacesContext context, Stack component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected boolean shouldBeRendered(FacesContext context, Stack component) {
        return true;
    }
}
