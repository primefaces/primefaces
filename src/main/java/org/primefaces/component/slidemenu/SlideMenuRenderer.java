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
package org.primefaces.component.slidemenu;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.util.WidgetBuilder;

public class SlideMenuRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        SlideMenu menu = (SlideMenu) abstractMenu;
        String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SlideMenu", menu.resolveWidgetVar(context), clientId);

        if (menu.isOverlay()) {
            encodeOverlayConfig(context, menu, wb);
        }

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SlideMenu menu = (SlideMenu) abstractMenu;

        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? SlideMenu.DYNAMIC_CONTAINER_CLASS : SlideMenu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", menu.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);

        //wrapper
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.WRAPPER_CLASS, "styleClass");

        //content
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.CONTENT_CLASS, "styleClass");

        //root menu
        if (menu.getElementsCount() > 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.LIST_CLASS, null);
            encodeElements(context, abstractMenu, menu.getElements());
            writer.endElement("ul");
        }

        //content
        writer.endElement("div");

        //back navigator
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.BACKWARD_CLASS, null);
        writer.startElement("span", menu);
        writer.writeAttribute("class", SlideMenu.BACKWARD_ICON_CLASS, null);
        writer.endElement("span");
        writer.writeText(menu.getBackLabel(), "backLabel");
        writer.endElement("div");

        //wrapper
        writer.endElement("div");

        writer.endElement("div");
    }
}
