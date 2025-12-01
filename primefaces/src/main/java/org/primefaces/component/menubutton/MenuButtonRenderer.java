/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.menubutton;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIForm;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;
@FacesRenderer(rendererType = MenuButton.DEFAULT_RENDERER, componentFamily = MenuButton.COMPONENT_FAMILY)
public class MenuButtonRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MenuButton button = (MenuButton) abstractMenu;
        String clientId = button.getClientId(context);
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? MenuButton.CONTAINER_CLASS : MenuButton.CONTAINER_CLASS + " " + styleClass;
        boolean disabled = button.isDisabled();

        writer.startElement("span", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "class");

        if (button.getStyle() != null) {
            writer.writeAttribute("style", button.getStyle(), "style");
        }
        if (button.getTitle() != null) {
            writer.writeAttribute("title", button.getTitle(), "title");
        }
        String menuId = clientId + "_menu";
        encodeButton(context, button, clientId + "_button", menuId, disabled);
        encodeMenu(context, button, menuId);

        writer.endElement("span");
    }

    protected void encodeButton(FacesContext context, MenuButton button, String buttonId, String menuId, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isIconLeft = button.getIconPos().equals("left");
        String value = button.getValue();
        String buttonIcon = button.getButtonIcon();
        String buttonClass = getStyleClassBuilder(context)
                .add(button.getButtonStyleClass())
                .add(isIconLeft, HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS, HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS)
                .add(isValueBlank(value) && isValueBlank(buttonIcon), HTML.BUTTON_ICON_ONLY_BUTTON_CLASS)
                .add(disabled, "ui-state-disabled")
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("id", buttonId, null);
        writer.writeAttribute("name", buttonId, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass, null);
        writer.writeAttribute(HTML.ARIA_LABEL, button.getAriaLabel(), "ariaLabel");
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, "true", null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, menuId, null);

        if (LangUtils.isNotEmpty(button.getButtonStyle())) {
            writer.writeAttribute("style", button.getButtonStyle(), null);
        }
        if (button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        // button icon
        String iconClass = isValueBlank(button.getIcon()) ? MenuButton.ICON_CLASS : button.getIcon();

        //button icon pos
        String iconPosClass = isIconLeft ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
        iconClass = iconPosClass + " " + iconClass;

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        if (!isValueBlank(buttonIcon)) {
            // Render buttonIcon instead of label
            writer.startElement("span", null);
            writer.writeAttribute("class", buttonIcon, null);
            writer.endElement("span");
        }
        else {
            renderButtonValue(writer, true, button.getValue(), button.getTitle(), button.getAriaLabel());
        }

        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeMenu(FacesContext context, MenuButton button, String menuId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String menuStyleClass = button.getMenuStyleClass();
        menuStyleClass = (menuStyleClass == null) ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.DYNAMIC_CONTAINER_CLASS + " " + menuStyleClass;

        writer.startElement("div", null);
        if (!LangUtils.isEmpty(button.getMaxHeight())) {
            menuStyleClass = menuStyleClass + " " + Menu.CONTAINER_MAXHEIGHT_CLASS;
            // If maxHeight is a number, add the unit "px", otherwise use it as is
            char lastChar = button.getMaxHeight().charAt(button.getMaxHeight().length() - 1);
            String style = Character.isDigit(lastChar) ? button.getMaxHeight() + "px" : button.getMaxHeight();
            writer.writeAttribute("style", "max-height:" + style, null);
        }
        writer.writeAttribute("id", menuId, null);
        writer.writeAttribute("class", menuStyleClass, "styleClass");

        writer.startElement("ul", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_MENUBAR, null);
        writer.writeAttribute(HTML.ARIA_ORIENTATION, HTML.ARIA_ORIENTATION_VERTICAL, null);
        writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

        if (button.getElementsCount() > 0) {
            List<MenuElement> elements = button.getElements();
            encodeElements(context, button, elements, true);
        }

        writer.endElement("ul");
        writer.endElement("div");

    }

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        MenuButton button = (MenuButton) abstractMenu;
        String clientId = button.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(button);
        if (form == null) {
            throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("MenuButton", button)
            .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, button, button.getAppendTo()))
            .attr("collision", button.getCollision())
            .attr("autoDisplay", button.isAutoDisplay())
            .attr("toggleEvent", button.getToggleEvent(), null)
            .attr("delay", button.getDelay())
            .attr("disableOnAjax", button.isDisableOnAjax(), true)
            .attr("disabledAttr", button.isDisabled(), false)
            .finish();
    }
}
