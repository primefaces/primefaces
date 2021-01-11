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
package org.primefaces.component.menu;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.renderkit.MenuItemAwareRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public abstract class BaseMenuRenderer extends MenuItemAwareRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AbstractMenu menu = (AbstractMenu) component;
        MenuModel model = menu.getModel();
        if (model != null && menu.getElementsCount() > 0) {
            model.generateUniqueIds();
        }

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected abstract void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected abstract void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException;

    protected String getLinkStyleClass(MenuItem menuItem) {
        String styleClass = menuItem.getStyleClass();

        return (styleClass == null) ? AbstractMenu.MENUITEM_LINK_CLASS : AbstractMenu.MENUITEM_LINK_CLASS + " " + styleClass;
    }

    protected void encodeMenuItem(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        encodeMenuItem(context, menu, menuitem, "-1");
    }

    protected void encodeMenuItem(FacesContext context, AbstractMenu menu, MenuItem menuitem, String tabindex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String title = menuitem.getTitle();
        String style = menuitem.getStyle();
        boolean disabled = menuitem.isDisabled();
        String rel = menuitem.getRel();

        writer.startElement("a", null);
        writer.writeAttribute("tabindex", tabindex, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM, null);
        if (shouldRenderId(menuitem)) {
            writer.writeAttribute("id", menuitem.getClientId(), null);
        }
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        String styleClass = getLinkStyleClass(menuitem);
        if (disabled) {
            styleClass = styleClass + " ui-state-disabled";
        }

        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (rel != null) {
            writer.writeAttribute("rel", rel, null);
        }

        if (disabled) {
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("onclick", "return false;", null);
        }
        else {
            encodeOnClick(context, menu, menuitem);
        }

        encodeMenuItemContent(context, menu, menuitem);

        writer.endElement("a");
    }

    protected boolean shouldRenderId(MenuElement element) {
        if (element instanceof UIComponent) {
            return shouldWriteId((UIComponent) element);
        }
        else {
            return false;
        }
    }

    protected void encodeMenuItemContent(FacesContext context, AbstractMenu menu, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = menuitem.getIcon();
        Object value = menuitem.getValue();

        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
            writer.endElement("span");
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);

        if (menuitem.shouldRenderChildren()) {
            renderChildren(context, (UIComponent) menuitem);
        }
        else if (value != null) {
            if (menuitem.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }

        writer.endElement("span");
    }

    protected void encodeOverlayConfig(FacesContext context, OverlayMenu menu, WidgetBuilder wb) throws IOException {
        wb.attr("overlay", true)
                .attr("my", menu.getMy())
                .attr("at", menu.getAt());

        String trigger = menu.getTrigger();
        if (trigger != null) {
            wb.attr("trigger", SearchExpressionFacade.resolveClientIds(context, (UIComponent) menu, trigger))
                    .attr("triggerEvent", menu.getTriggerEvent());
        }
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeKeyboardTarget(FacesContext context, AbstractMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("tabindex", menu.getTabindex(), null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.endElement("div");
    }
}
