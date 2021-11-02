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
package org.primefaces.component.speeddial;

import org.primefaces.component.badge.BadgeRenderer;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.List;
import org.primefaces.util.LangUtils;

public class SpeedDialRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SpeedDial speedDial = (SpeedDial) abstractMenu;
        String clientId = speedDial.getClientId(context);

        writer.startElement("div", speedDial);
        writer.writeAttribute("id", clientId, "id");

        encodeContainer(context, speedDial);

        if (speedDial.isMask()) {
            encodeMask(context, speedDial);
        }

        writer.endElement("div");
    }

    protected void encodeContainer(FacesContext context, SpeedDial speedDial) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(SpeedDial.CONTAINER_CLASS)
                .add("ui-speeddial-" + speedDial.getType())
                .add(!"circle".equals(speedDial.getType()), "ui-speeddial-direction-" + speedDial.getDirection())
                .add(speedDial.isDisabled(), "ui-disabled")
                .add(speedDial.getStyleClass())
                .build();
        String containerStyle = speedDial.getStyle();

        writer.startElement("div", speedDial);
        writer.writeAttribute("class", styleClass, "class");

        if (containerStyle != null) {
            writer.writeAttribute("style", containerStyle, "style");
        }

        BadgeRenderer.encode(context, speedDial.getBadge(), this::encodeButton, speedDial);
        encodeList(context, speedDial);

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SpeedDial speedDial) throws IOException {
        if (speedDial.getElementsCount() <= 0) {
            return;
        }
        List<MenuElement> elements = speedDial.getElements();

        ResponseWriter writer = context.getResponseWriter();
        String listClass = getStyleClassBuilder(context)
                .add(SpeedDial.LIST_CLASS)
                .add(speedDial.getMaskStyleClass())
                .build();

        writer.startElement("ul", speedDial);
        writer.writeAttribute("class", listClass, "class");
        writer.writeAttribute("role", "menu", "role");

        for (MenuElement element : elements) {
            if (element.isRendered() && element instanceof MenuItem) {
                MenuItem menuItem = (MenuItem) element;
                String icon = menuItem.getIcon();
                Object value = menuItem.getValue();
                boolean disabled = menuItem.isDisabled();
                String title = menuItem.getTitle();
                String style = menuItem.getStyle();
                String rel = menuItem.getRel();

                writer.startElement("li", null);
                writer.writeAttribute("role", "none", "role");
                writer.writeAttribute("class", SpeedDial.ITEM_CLASS, "class");

                writer.startElement("a", null);
                writer.writeAttribute("tabindex", -1, null);
                writer.writeAttribute("role", "menuitem", "role");

                if (shouldRenderId(menuItem)) {
                    writer.writeAttribute("id", menuItem.getClientId(), null);
                }

                String styleClass = getStyleClassBuilder(context)
                        .add(SpeedDial.ITEM_BUTTON_CLASS)
                        .add(disabled, "ui-disabled")
                        .build();

                writer.writeAttribute("class", styleClass, null);

                if (style != null) {
                    writer.writeAttribute("style", style, null);
                }

                if (title != null) {
                    writer.writeAttribute("data-tooltip", title, null);
                }

                if (rel != null) {
                    writer.writeAttribute("rel", rel, null);
                }

                if (disabled) {
                    writer.writeAttribute("href", "#", null);
                    writer.writeAttribute("onclick", "return false;", null);
                }
                else {
                    encodeOnClick(context, speedDial, menuItem);
                }

                if (icon != null) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", SpeedDial.ITEM_ICON_CLASS + " " + icon, null);
                    writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
                    writer.endElement("span");
                }

                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);

                if (menuItem.shouldRenderChildren()) {
                    renderChildren(context, (UIComponent) menuItem);
                }
                else if (value != null) {
                    if (menuItem.isEscape()) {
                        writer.writeText(value, "value");
                    }
                    else {
                        writer.write(value.toString());
                    }
                }

                writer.endElement("span");

                writer.endElement("a");

                writer.endElement("li");
            }
        }

        writer.endElement("ul");
    }

    protected void encodeButton(FacesContext context, SpeedDial speedDial) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String buttonStyle = speedDial.getButtonStyle();
        boolean isDisabled = speedDial.isDisabled();
        String buttonStyleClass = getStyleClassBuilder(context)
                .add(HTML.BUTTON_ICON_ONLY_BUTTON_CLASS)
                .add(SpeedDial.BUTTON_CLASS)
                .add(speedDial.isRotateAnimation(), "ui-speeddial-rotate")
                .add(speedDial.getHideIcon() != null, "ui-speeddial-dual-icon")
                .add(speedDial.getButtonStyleClass())
                .build();

        writer.startElement("button", speedDial);
        writer.writeAttribute("type", "button", "type");
        writer.writeAttribute("class", buttonStyleClass, "class");

        if (buttonStyle != null) {
            writer.writeAttribute("style", buttonStyle, "style");
        }

        if (isDisabled) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        //show icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + speedDial.getShowIcon(), null);
        writer.endElement("span");

        //hide icon
        if (LangUtils.isNotEmpty(speedDial.getHideIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + speedDial.getHideIcon(), null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText("ui-button", null);
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeMask(FacesContext context, SpeedDial speedDial) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(SpeedDial.MASK_CLASS)
                .add(speedDial.getMaskStyleClass())
                .build();
        String maskStyle = speedDial.getMaskStyle();

        writer.startElement("div", speedDial);
        writer.writeAttribute("class", styleClass, "class");

        if (maskStyle != null) {
            writer.writeAttribute("style", maskStyle, "style");
        }

        writer.endElement("div");
    }

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        SpeedDial speedDial = (SpeedDial) abstractMenu;
        String clientId = speedDial.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(context, speedDial);
        if (form == null) {
            throw new FacesException("SpeedDial : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SpeedDial", speedDial)
                .attr("visible", speedDial.isVisible(), false)
                .attr("direction", speedDial.getDirection(), "up")
                .attr("transitionDelay", speedDial.getTransitionDelay(), 30)
                .attr("type", speedDial.getType(), "linear")
                .attr("radius", speedDial.getRadius(), 0)
                .attr("mask", speedDial.isMask(), false)
                .attr("hideOnClickOutside", speedDial.isHideOnClickOutside(), true)
                .attr("keepOpen", speedDial.isKeepOpen(), false)
                .callback("onVisibleChange", "function(visible)", speedDial.getOnVisibleChange())
                .callback("onClick", "function(event)", speedDial.getOnClick())
                .callback("onShow", "function()", speedDial.getOnShow())
                .callback("onHide", "function()", speedDial.getOnHide());
        wb.finish();
    }
}
