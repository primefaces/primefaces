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
package org.primefaces.component.stack;

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

public class StackRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
        Stack stack = (Stack) menu;
        String clientId = stack.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Stack", stack.resolveWidgetVar(context), clientId)
                .attr("openSpeed", stack.getOpenSpeed())
                .attr("closeSpeed", stack.getCloseSpeed())
                .attr("expanded", stack.isExpanded(), false);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Stack stack = (Stack) menu;
        String clientId = stack.getClientId(context);

        writer.startElement("div", stack);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "ui-stack", null);

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, stack.getIcon()), null);
        writer.endElement("img");

        if (stack.getElementsCount() > 0) {
            List<MenuElement> elements = stack.getElements();

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

                    encodeMenuItem(context, stack, menuItem, "-1");
                    writer.endElement("li");
                }
            }
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    @Override
    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        if (menuitem.getValue() != null) {
            writer.write((String) menuitem.getValue());
        }

        writer.endElement("span");

        writer.startElement("img", null);
        writer.writeAttribute("src", getResourceURL(context, menuitem.getIcon()), null);
        writer.endElement("img");
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
