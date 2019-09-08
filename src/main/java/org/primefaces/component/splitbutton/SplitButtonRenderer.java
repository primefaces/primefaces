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
package org.primefaces.component.splitbutton;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menubutton.MenuButton;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import java.io.IOException;
import java.util.*;
import org.primefaces.expression.SearchExpressionHint;

public class SplitButtonRenderer extends OutcomeTargetRenderer {

    private static final String SB_BUILD_ONCLICK = SplitButtonRenderer.class.getName() + "#buildOnclick";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SplitButton button = (SplitButton) component;
        if (button.isDisabled()) {
            return;
        }

        String clientId = button.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String param = button.isAjax() ? clientId : clientId + "_button";
        String itemParam = clientId + "_menuid";

        if (params.containsKey(itemParam)) {
            String menuid = params.get(clientId + "_menuid");
            MenuItem menuitem = button.findMenuitem(button.getElements(), menuid);
            MenuActionEvent event = new MenuActionEvent(button, menuitem);

            if (menuitem.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            component.queueEvent(event);
        }
        else if (params.containsKey(param)) {
            component.queueEvent(new ActionEvent(component));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SplitButton button = (SplitButton) component;
        MenuModel model = button.getModel();
        if (model != null && button.getElementsCount() > 0) {
            model.generateUniqueIds();
        }

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, SplitButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String menuId = clientId + "_menu";
        String menuButtonId = clientId + "_menuButton";
        String buttonId = clientId + "_button";
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? SplitButton.STYLE_CLASS : SplitButton.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if (button.getStyle() != null) {
            writer.writeAttribute("style", button.getStyle(), "id");
        }

        encodeDefaultButton(context, button, buttonId);
        if (button.getElementsCount() > 0) {
            encodeMenuIcon(context, button, menuButtonId);
            encodeMenu(context, button, menuId);
        }

        writer.endElement("div");
    }

    protected void encodeDefaultButton(FacesContext context, SplitButton button, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String value = (String) button.getValue();
        String icon = button.getIcon();
        String onclick = buildOnclick(context, button);

        writer.startElement("button", button);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("name", id, "name");
        writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");

        if (onclick.length() > 0) {
            writer.writeAttribute("onclick", onclick, "onclick");
        }

        renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if (button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        //icon
        if (!isValueBlank(icon)) {
            String defaultIconClass = button.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        if (value == null) {
            writer.write("ui-button");
        }
        else {
            writer.writeText(value, "value");
        }

        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeMenuIcon(FacesContext context, SplitButton button, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String buttonClass = SplitButton.MENU_ICON_BUTTON_CLASS;
        if (button.isDisabled()) {
            buttonClass += " ui-state-disabled";
        }

        writer.startElement("button", button);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass, null);

        if (button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-icon-left ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeScript(FacesContext context, SplitButton button) throws IOException {
        String clientId = button.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SplitButton", button.resolveWidgetVar(context), clientId);
        wb.attr("appendTo", SearchExpressionFacade.resolveClientId(context, button, button.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null);

        if (button.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", button.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", button.getFilterFunction(), null);
        }

        wb.finish();
    }

    protected String buildOnclick(FacesContext context, SplitButton button) throws IOException {
        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
        if (button.getOnclick() != null) {
            onclick.append(button.getOnclick()).append(";");
        }

        if (button.isAjax()) {
            onclick.append(buildAjaxRequest(context, button));
        }
        else {
            UIForm form = ComponentTraversalUtils.closestForm(context, button);
            if (form == null) {
                throw new FacesException("SplitButton : \"" + button.getClientId(context) + "\" must be inside a form element");
            }

            onclick.append(buildNonAjaxRequest(context, button, form, null, false));
        }

        String onclickBehaviors = getEventBehaviors(context, button, "click", null);
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors).append(";");
        }

        return onclick.toString();
    }

    protected void encodeMenu(FacesContext context, SplitButton button, String menuId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String menuStyleClass = button.getMenuStyleClass();
        menuStyleClass = (menuStyleClass == null) ? SplitButton.SPLITBUTTON_CONTAINER_CLASS : SplitButton.SPLITBUTTON_CONTAINER_CLASS + " " + menuStyleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", menuId, null);
        writer.writeAttribute("class", menuStyleClass, "styleClass");
        writer.writeAttribute("role", "menu", null);

        if (button.isFilter()) {
            encodeFilter(context, button);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", SplitButton.LIST_WRAPPER_CLASS, "styleClass");

        writer.startElement("ul", null);
        writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

        encodeElements(context, button, button.getElements(), false);

        writer.endElement("ul");
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeElements(FacesContext context, SplitButton button, List<Object> elements, boolean isSubmenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for (Object element : elements) {
            if (element instanceof MenuItem) {
                MenuItem menuItem = (MenuItem) element;
                if (menuItem.isRendered()) {
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = menuItem.getContainerStyleClass();
                    containerStyleClass = (containerStyleClass == null) ? Menu.MENUITEM_CLASS : Menu.MENUITEM_CLASS + " " + containerStyleClass;

                    if (isSubmenu) {
                        containerStyleClass = containerStyleClass + " " + Menu.SUBMENU_CHILD_CLASS;
                    }

                    writer.startElement("li", null);
                    writer.writeAttribute("class", containerStyleClass, null);
                    writer.writeAttribute("role", "menuitem", null);
                    if (containerStyle != null) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    encodeMenuItem(context, button, menuItem);
                    writer.endElement("li");
                }
            }
            else if (element instanceof Submenu) {
                encodeSubmenu(context, button, (Submenu) element);
            }
            else if (element instanceof Separator) {
                encodeSeparator(context, (Separator) element);
            }
        }
    }

    protected void encodeMenuItem(FacesContext context, SplitButton button, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = menuitem.getIcon();
        String title = menuitem.getTitle();

        if (menuitem.shouldRenderChildren()) {
            renderChildren(context, (UIComponent) menuitem);
        }
        else {
            boolean disabled = menuitem.isDisabled();

            writer.startElement("a", null);
            writer.writeAttribute("id", menuitem.getClientId(), null);
            if (title != null) {
                writer.writeAttribute("title", title, null);
            }

            String styleClass = menuitem.getStyleClass();
            styleClass = styleClass == null ? AbstractMenu.MENUITEM_LINK_CLASS : AbstractMenu.MENUITEM_LINK_CLASS + " " + styleClass;
            styleClass = disabled ? styleClass + " ui-state-disabled" : styleClass;

            writer.writeAttribute("class", styleClass, null);

            if (menuitem.getStyle() != null) {
                writer.writeAttribute("style", menuitem.getStyle(), null);
            }

            if (disabled) {
                writer.writeAttribute("href", "#", null);
                writer.writeAttribute("onclick", "return false;", null);
            }
            else {
                encodeOnClick(context, button, menuitem);
            }

            if (icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon, null);
                writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
                writer.endElement("span");
            }

            if (menuitem.getValue() != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_TEXT_CLASS, null);
                writer.writeText(menuitem.getValue(), "value");
                writer.endElement("span");
            }

            writer.endElement("a");
        }
    }

    protected void encodeSubmenu(FacesContext context, SplitButton button, Submenu submenu) throws IOException {
        if (!submenu.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;

        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("h3", null);

        if (label != null) {
            writer.writeText(label, "value");
        }

        writer.endElement("h3");

        writer.endElement("li");

        encodeElements(context, button, submenu.getElements(), true);
    }

    protected void encodeSeparator(FacesContext context, Separator separator) throws IOException {
        if (!separator.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String style = separator.getStyle();
        String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? Menu.SEPARATOR_CLASS : Menu.SEPARATOR_CLASS + " " + styleClass;

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.endElement("li");
    }

    protected void encodeFilter(FacesContext context, SplitButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = button.getClientId(context) + "_filter";

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-splitbuttonmenu-filter-container", null);

        writer.startElement("input", null);
        writer.writeAttribute("class", "ui-splitbuttonmenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);

        if (button.getFilterPlaceholder() != null) {
            writer.writeAttribute("placeholder", button.getFilterPlaceholder(), null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", id);
        writer.endElement("span");

        writer.endElement("input");

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
