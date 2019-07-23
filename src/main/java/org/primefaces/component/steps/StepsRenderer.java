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
package org.primefaces.component.steps;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.List;

public class StepsRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Steps steps = (Steps) abstractMenu;
        String clientId = steps.getClientId(context);
        String styleClass = steps.getStyleClass();
        String containerClass = steps.isReadonly() ? Steps.READONLY_CONTAINER_CLASS : Steps.CONTAINER_CLASS;
        styleClass = styleClass == null ? containerClass : containerClass + " " + styleClass;
        int activeIndex = steps.getActiveIndex();
        List<MenuElement> elements = steps.getElements();

        writer.startElement("div", steps);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (steps.getStyle() != null) {
            writer.writeAttribute("style", steps.getStyle(), "style");
        }

        writer.startElement("ul", null);
        writer.writeAttribute("role", "tablist", null);

        int i = 0;
        if (elements != null && !elements.isEmpty()) {
            for (MenuElement element : elements) {
                if (element.isRendered() && (element instanceof MenuItem)) {
                    encodeItem(context, steps, (MenuItem) element, activeIndex, i);
                    i++;
                }
            }
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, Steps steps, MenuItem item, int activeIndex, int index) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemClass;

        if (steps.isReadonly()) {
            itemClass = (index == activeIndex) ? Steps.ACTIVE_ITEM_CLASS : Steps.INACTIVE_ITEM_CLASS;
        }
        else {
            if (index == activeIndex) {
                itemClass = Steps.ACTIVE_ITEM_CLASS;
            }
            else if (index < activeIndex) {
                itemClass = Steps.VISITED_ITEM_CLASS;
            }
            else {
                itemClass = Steps.INACTIVE_ITEM_CLASS;
            }
        }

        String containerStyle = item.getContainerStyle();
        String containerStyleClass = item.getContainerStyleClass();

        if (containerStyleClass != null) {
            itemClass = itemClass + " " + containerStyleClass;
        }

        //header container
        writer.startElement("li", null);
        writer.writeAttribute("class", itemClass, null);
        writer.writeAttribute("role", "tab", null);
        if (containerStyle != null) {
            writer.writeAttribute("style", containerStyle, null);
        }

        encodeMenuItem(context, steps, item, activeIndex, index);

        writer.endElement("li");
    }

    protected void encodeMenuItem(FacesContext context, Steps steps, MenuItem menuitem, int activeIndex, int index) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String title = menuitem.getTitle();
        String style = menuitem.getStyle();
        String styleClass = getLinkStyleClass(menuitem);

        writer.startElement("a", null);
        writer.writeAttribute("tabindex", "-1", null);
        if (shouldRenderId(menuitem)) {
            writer.writeAttribute("id", menuitem.getClientId(), null);
        }
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (steps.isReadonly() || menuitem.isDisabled() || (activeIndex <= index)) {
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("onclick", "return false;", null);
        }
        else {
            encodeOnClick(context, steps, menuitem);
        }

        writer.startElement("span", steps);
        writer.writeAttribute("class", Steps.STEP_NUMBER_CLASS, null);
        writer.writeText((index + 1), null);
        writer.endElement("span");

        Object value = menuitem.getValue();
        if (value != null) {
            writer.startElement("span", steps);
            writer.writeAttribute("class", Steps.STEP_TITLE_CLASS, null);
            writer.writeText(value, null);
            writer.endElement("span");
        }

        writer.endElement("a");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        //no widget
    }

}
