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
package org.primefaces.component.speeddial;

import org.primefaces.component.badge.BadgeRenderer;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = SpeedDial.DEFAULT_RENDERER, componentFamily = SpeedDial.COMPONENT_FAMILY)
public class SpeedDialRenderer extends BaseMenuRenderer<SpeedDial> {

    @Override
    protected void encodeMarkup(FacesContext context, SpeedDial component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");

        encodeContainer(context, component);

        if (component.isMask()) {
            encodeMask(context, component);
        }

        writer.endElement("div");
    }

    protected void encodeContainer(FacesContext context, SpeedDial component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(SpeedDial.CONTAINER_CLASS)
                .add("ui-speeddial-" + component.getType())
                .add(!"circle".equals(component.getType()), "ui-speeddial-direction-" + component.getDirection())
                .add(component.isDisabled(), "ui-disabled")
                .add(component.getStyleClass())
                .build();
        String containerStyle = component.getStyle();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, "class");

        if (containerStyle != null) {
            writer.writeAttribute("style", containerStyle, "style");
        }

        BadgeRenderer.encodeOverlayed(context, component.getBadge(), this::encodeButton, component);
        encodeList(context, component);

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SpeedDial component) throws IOException {
        if (component.getElementsCount() <= 0 || component.getElements().stream().noneMatch(me -> shouldBeRendered(context, me))) {
            return;
        }
        List<MenuElement> elements = component.getElements();

        ResponseWriter writer = context.getResponseWriter();
        String listClass = getStyleClassBuilder(context)
                .add(SpeedDial.LIST_CLASS)
                .add(component.getMaskStyleClass())
                .build();

        writer.startElement("ul", null);
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
                String ariaLabel = menuItem.getAriaLabel();

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

                if (LangUtils.isNotEmpty(ariaLabel)) {
                    writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
                }

                if (disabled) {
                    writer.writeAttribute("href", "#", null);
                    writer.writeAttribute("onclick", "return false;", null);
                }
                else {
                    encodeOnClick(context, component, menuItem);
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

    protected void encodeButton(FacesContext context, SpeedDial component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String buttonStyle = component.getButtonStyle();
        String ariaLabel = component.getAriaLabel();
        String title = component.getTitle();
        boolean isDisabled = component.isDisabled();
        String buttonStyleClass = getStyleClassBuilder(context)
                .add(HTML.BUTTON_ICON_ONLY_BUTTON_CLASS)
                .add(SpeedDial.BUTTON_CLASS)
                .add(component.isRotateAnimation(), "ui-speeddial-rotate")
                .add(component.getHideIcon() != null, "ui-speeddial-dual-icon")
                .add(component.getButtonStyleClass())
                .build();

        writer.startElement("button", component);
        writer.writeAttribute("type", "button", "type");
        writer.writeAttribute("class", buttonStyleClass, "class");

        if (LangUtils.isNotEmpty(ariaLabel)) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }

        if (LangUtils.isNotEmpty(title)) {
            writer.writeAttribute("title", title, null);
        }

        if (buttonStyle != null) {
            writer.writeAttribute("style", buttonStyle, "style");
        }

        if (isDisabled) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        //show icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + component.getShowIcon(), null);
        writer.endElement("span");

        //hide icon
        if (LangUtils.isNotEmpty(component.getHideIcon())) {
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + component.getHideIcon(), null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(getIconOnlyButtonText(title, ariaLabel), null);
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

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, "class");

        if (maskStyle != null) {
            writer.writeAttribute("style", maskStyle, "style");
        }

        writer.endElement("div");
    }

    @Override
    protected void encodeScript(FacesContext context, SpeedDial component) throws IOException {
        String clientId = component.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(component);
        if (form == null) {
            throw new FacesException("SpeedDial : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SpeedDial", component)
                .attr("visible", component.isVisible(), false)
                .attr("direction", component.getDirection(), "up")
                .attr("transitionDelay", component.getTransitionDelay(), 30)
                .attr("type", component.getType(), "linear")
                .attr("radius", component.getRadius(), 0)
                .attr("mask", component.isMask(), false)
                .attr("hideOnClickOutside", component.isHideOnClickOutside(), true)
                .attr("keepOpen", component.isKeepOpen(), false)
                .callback("onVisibleChange", "function(visible)", component.getOnVisibleChange())
                .callback("onClick", "function(event)", component.getOnClick())
                .callback("onShow", "function()", component.getOnShow())
                .callback("onHide", "function()", component.getOnHide());
        wb.finish();
    }

    @Override
    protected boolean shouldBeRendered(FacesContext context, SpeedDial component) {
        return true;
    }
}
