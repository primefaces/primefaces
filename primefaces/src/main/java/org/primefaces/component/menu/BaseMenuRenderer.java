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

import org.primefaces.component.menubutton.MenuButton;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Submenu;
import org.primefaces.renderkit.MenuItemAwareRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public abstract class BaseMenuRenderer<T extends AbstractMenu> extends MenuItemAwareRenderer<T> {

    public static final SimpleEntry<String, String> ARIA_ROLE_MENUITEM = new SimpleEntry<>(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUITEM);
    public static final SimpleEntry<String, String> ARIA_CURRENT_PAGE = new SimpleEntry<>(HTML.ARIA_CURRENT, HTML.ARIA_CURRENT_PAGE);

    @Override
    public void encodeEnd(FacesContext context, T component) throws IOException {
        MenuModel model = component.getModel();
        if (model != null && component.getElementsCount() > 0) {
            model.generateUniqueIds();
        }
        if (shouldBeRendered(context, component)) {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
        else {
            encodePlaceholder(context, component);
        }
    }

    @Override
    public void encodeChildren(FacesContext facesContext, T component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodePlaceholder(FacesContext context, T menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", menu);
        writer.writeAttribute("id", menu.getClientId(context), "id");
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("div");
    }

    protected abstract void encodeMarkup(FacesContext context, T menu) throws IOException;

    protected abstract void encodeScript(FacesContext context, T menu) throws IOException;

    protected String getLinkStyleClass(MenuItem menuItem) {
        String styleClass = menuItem.getStyleClass();
        return AbstractMenu.MENUITEM_LINK_CLASS + (styleClass != null ? " " + styleClass : Constants.EMPTY_STRING);
    }

    protected void encodeMenuItem(FacesContext context, T menu, MenuItem menuitem) throws IOException {
        encodeMenuItem(context, menu, menuitem, "-1");
    }

    protected void encodeMenuItem(FacesContext context, T menu, MenuItem menuitem, String tabindex) throws IOException {
        encodeMenuItem(context, menu, menuitem, tabindex, ARIA_ROLE_MENUITEM);
    }

    protected void encodeMenuItem(FacesContext context, T menu, MenuItem menuitem, String tabindex, Entry<String, String> aria) throws IOException {
        boolean isMenuItemComponent = menuitem instanceof UIComponent;

        try {
            if (isMenuItemComponent) {
                UIComponent uiMenuItem = (UIComponent) menuitem;
                uiMenuItem.pushComponentToEL(context, uiMenuItem);

                UIComponent custom = uiMenuItem.getFacet("custom");
                if (FacetUtils.shouldRenderFacet(custom)) {
                    custom.encodeAll(context);
                    return;
                }
            }

            ResponseWriter writer = context.getResponseWriter();
            String title = menuitem.getTitle();
            String style = menuitem.getStyle();
            boolean disabled = menuitem.isDisabled();
            String rel = menuitem.getRel();
            String ariaLabel = menuitem.getAriaLabel();

            writer.startElement("a", null);
            writer.writeAttribute("tabindex", Objects.toString(tabindex, "-1"), null);
            if (aria != null) {
                writer.writeAttribute(aria.getKey(), aria.getValue(), null);
            }
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

            if (LangUtils.isNotEmpty(ariaLabel)) {
                writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
            }

            if (disabled) {
                writer.writeAttribute("href", "#", null);
                writer.writeAttribute("onclick", "return false;", null);
            }
            else {
                encodeOnClick(context, menu, menuitem);
            }

            if (isMenuItemComponent) {
                renderPassThruAttributes(context, (UIComponent) menuitem);
            }

            encodeMenuItemContent(context, menu, menuitem);

            writer.endElement("a");
        }
        finally {
            if (isMenuItemComponent) {
                UIComponent uiMenuItem = (UIComponent) menuitem;
                uiMenuItem.popComponentFromEL(context);
            }
        }
    }

    protected boolean shouldRenderId(MenuElement element) {
        if (element instanceof UIComponent) {
            UIComponent component = (UIComponent) element;
            return component.getParent() instanceof MenuButton || shouldWriteId(component);
        }
        else {
            return false;
        }
    }

    protected void encodeMenuItemContent(FacesContext context, T menu, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = menuitem.getValue();
        String iconPos = menuitem.getIconPos();
        if (LangUtils.isBlank(iconPos)) {
            iconPos = ComponentUtils.isRTL(context, menu) ? "right" : "left";
        }

        if ("left".equals(iconPos)) {
            encodeIcon(writer, menu, menuitem, iconPos);
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

        if ("right".equals(iconPos)) {
            encodeIcon(writer, menu, menuitem, iconPos);
        }
    }

    protected void encodeIcon(ResponseWriter writer, T menu, MenuItem menuitem, String iconPos) throws IOException {
        String icon = menuitem.getIcon();
        if (icon != null && LangUtils.isNotBlank(iconPos)) {
            writer.startElement("span", null);
            writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon + " ui-menuitem-icon-" + iconPos, null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
            writer.endElement("span");
        }
    }

    protected void encodeOverlayConfig(FacesContext context, OverlayMenu menu, WidgetBuilder wb) throws IOException {
        wb.attr("overlay", true)
                .attr("my", menu.getMy())
                .attr("at", menu.getAt());

        String trigger = menu.getTrigger();
        if (LangUtils.isNotBlank(trigger)) {
            wb.attr("trigger", SearchExpressionUtils.resolveClientIdsForClientSide(context, (UIComponent) menu, trigger))
                .attr("triggerEvent", menu.getTriggerEvent());
        }
    }

    protected void encodeFacet(FacesContext context, T menu, String facetName, String styleClass) throws IOException {
        UIComponent facet = menu.getFacet(facetName);
        if (FacetUtils.shouldRenderFacet(facet)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("li", null);
            writer.writeAttribute("class", styleClass, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_NONE, null);
            facet.encodeAll(context);
            writer.endElement("li");
        }
    }

    protected void encodeMenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();

        if (icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
            writer.endElement("span");
        }
    }

    protected void encodeMenuLabel(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();

        if (label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(label, "value");
            writer.endElement("span");
        }
    }

    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu, boolean isRtl, boolean isVertical) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = isRtl ? Menu.SUBMENU_LEFT_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;
        styleClass = isVertical ? styleClass : Menu.SUBMENU_DOWN_ICON_CLASS;

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        writer.endElement("span");
    }

    protected boolean shouldBeRendered(FacesContext context, T component) {
        boolean rendered = super.shouldBeRendered(context, component);
        rendered = rendered || component.getFacets().values().stream().anyMatch(FacetUtils::shouldRenderFacet);
        return rendered;
    }

}
