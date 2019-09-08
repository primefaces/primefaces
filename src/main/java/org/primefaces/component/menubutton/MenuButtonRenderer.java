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
package org.primefaces.component.menubutton;

import java.io.IOException;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class MenuButtonRenderer extends BaseMenuRenderer {

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
        encodeButton(context, button, clientId + "_button", disabled);
        if (!disabled) {
            encodeMenu(context, button, clientId + "_menu");
        }

        writer.endElement("span");
    }

    protected void encodeButton(FacesContext context, MenuButton button, String buttonId, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean isIconLeft = button.getIconPos().equals("left");
        String value = button.getValue();
        String buttonTextClass = isIconLeft ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
        if (isValueBlank(value)) {
            buttonTextClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
        }
        String buttonClass = disabled ? buttonTextClass + " ui-state-disabled" : buttonTextClass;

        writer.startElement("button", null);
        writer.writeAttribute("id", buttonId, null);
        writer.writeAttribute("name", buttonId, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass, null);
        writer.writeAttribute(HTML.ARIA_LABEL, button.getAriaLabel(), "ariaLabel");
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

        if (isValueBlank(value)) {
            writer.write("ui-button");
        }
        else {
            writer.writeText(value, "value");
        }

        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeMenu(FacesContext context, MenuButton button, String menuId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String menuStyleClass = button.getMenuStyleClass();
        menuStyleClass = (menuStyleClass == null) ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.DYNAMIC_CONTAINER_CLASS + " " + menuStyleClass;

        writer.startElement("div", null);
        if (!LangUtils.isValueEmpty(button.getMaxHeight())) {
            menuStyleClass = menuStyleClass + " " + Menu.CONTAINER_MAXHEIGHT_CLASS;
            // If maxHeight is a number, add the unit "px", otherwise use it as is
            char lastChar = button.getMaxHeight().charAt(button.getMaxHeight().length() - 1);
            String style = Character.isDigit(lastChar) ? button.getMaxHeight() + "px" : button.getMaxHeight();
            writer.writeAttribute("style", "max-height:" + style, null);
        }
        writer.writeAttribute("id", menuId, null);
        writer.writeAttribute("class", menuStyleClass, "styleClass");
        writer.writeAttribute("role", "menu", null);

        writer.startElement("ul", null);
        writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

        if (button.getElementsCount() > 0) {
            List<MenuElement> elements = button.getElements();

            for (MenuElement element : elements) {
                if (element.isRendered()) {
                    if (element instanceof MenuItem) {
                        writer.startElement("li", null);
                        writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                        writer.writeAttribute("role", "menuitem", null);
                        encodeMenuItem(context, button, (MenuItem) element, "-1");
                        writer.endElement("li");
                    }
                    else if (element instanceof Separator) {
                        encodeSeparator(context, (Separator) element);
                    }
                }
            }
        }

        writer.endElement("ul");
        writer.endElement("div");

    }

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        MenuButton button = (MenuButton) abstractMenu;
        String clientId = button.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(context, button);
        if (form == null) {
            throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("MenuButton", button.resolveWidgetVar(context), clientId);
        wb.attr("appendTo", SearchExpressionFacade.resolveClientId(context, button, button.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null);
        wb.attr("collision", button.getCollision());
        wb.finish();
    }
}
