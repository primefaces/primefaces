/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.splitbutton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.behavior.confirm.ConfirmBehavior;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menubutton.MenuButton;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.*;

public class SplitButtonRenderer extends OutcomeTargetRenderer {

    private static final String SB_BUILD_ONCLICK = SplitButtonRenderer.class.getName() + "#buildOnclick";

    private static final String SB_BUILD_NON_AJAX_REQUEST = SplitButtonRenderer.class.getName() + "#buildNonAjaxRequest";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SplitButton button = (SplitButton) component;
        if (button.isDisabled()) {
            return;
        }

        String clientId = button.getClientId(context);

        String param = button.isAjax() ? clientId : clientId + "_button";
        if (context.getExternalContext().getRequestParameterMap().containsKey(param)) {
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
        wb.init("SplitButton", button.resolveWidgetVar(), clientId);
        wb.attr("appendTo", SearchExpressionFacade.resolveClientId(context, button, button.getAppendTo()), null);

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
            onclick.append(buildAjaxRequest(context, button, null));
        }
        else {
            UIComponent form = ComponentTraversalUtils.closestForm(context, button);
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

    protected void encodeElements(FacesContext context, SplitButton button, List<MenuElement> elements, boolean isSubmenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for (MenuElement element : elements) {
            if (element.isRendered()) {
                if (element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
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
                else if (element instanceof Submenu) {
                    encodeSubmenu(context, button, (Submenu) element);
                }
                else if (element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
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
                setConfirmationScript(context, menuitem);
                String onclick = menuitem.getOnclick();

                //GET
                if (menuitem.getUrl() != null || menuitem.getOutcome() != null) {
                    String targetURL = getTargetURL(context, (UIOutcomeTarget) menuitem);
                    writer.writeAttribute("href", targetURL, null);

                    if (menuitem.getTarget() != null) {
                        writer.writeAttribute("target", menuitem.getTarget(), null);
                    }
                }
                //POST
                else {
                    writer.writeAttribute("href", "#", null);

                    UIComponent form = ComponentTraversalUtils.closestForm(context, button);
                    if (form == null) {
                        throw new FacesException("MenuItem must be inside a form element");
                    }

                    String command;
                    if (menuitem.isDynamic()) {
                        String buttonClientId = button.getClientId(context);
                        Map<String, List<String>> params = menuitem.getParams();
                        if (params == null) {
                            params = new LinkedHashMap<>();
                        }
                        List<String> idParams = new ArrayList<>();
                        idParams.add(menuitem.getId());
                        params.put(buttonClientId + "_menuid", idParams);

                        command = menuitem.isAjax()
                                ? buildAjaxRequest(context, button, (AjaxSource) menuitem, form, params)
                                : buildNonAjaxRequest(context, button, form, buttonClientId, params, true);
                    }
                    else {
                        command = menuitem.isAjax()
                                ? buildAjaxRequest(context, (AjaxSource) menuitem, form)
                                : buildNonAjaxRequest(context, ((UIComponent) menuitem), form, ((UIComponent) menuitem).getClientId(context), true);
                    }

                    onclick = (onclick == null) ? command : onclick + ";" + command;
                }

                if (onclick != null) {
                    if (menuitem.requiresConfirmation()) {
                        writer.writeAttribute("data-pfconfirmcommand", onclick, null);
                        writer.writeAttribute("onclick", menuitem.getConfirmationScript(), "onclick");
                    }
                    else {
                        writer.writeAttribute("onclick", onclick, null);
                    }
                }
            }

            if (icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", AbstractMenu.MENUITEM_ICON_CLASS + " " + icon, null);
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

    protected void setConfirmationScript(FacesContext context, MenuItem item) {
        if (item instanceof ClientBehaviorHolder) {
            Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) item).getClientBehaviors();
            List<ClientBehavior> clickBehaviors = (behaviors == null) ? null : behaviors.get("click");

            if (clickBehaviors != null && !clickBehaviors.isEmpty()) {
                for (int i = 0; i < clickBehaviors.size(); i++) {
                    ClientBehavior clientBehavior = clickBehaviors.get(i);
                    if (clientBehavior instanceof ConfirmBehavior) {
                        ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                                context, (UIComponent) item, "click", item.getClientId(), Collections.EMPTY_LIST);
                        clientBehavior.getScript(cbc);
                        break;
                    }
                }
            }
        }
    }

    protected String buildAjaxRequest(FacesContext context, SplitButton button, AjaxSource source, UIComponent form,
            Map<String, List<String>> params) {

        String clientId = button.getClientId(context);

        AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance(context).getAjaxRequestBuilder();

        builder.init()
                .source(clientId)
                .process(button, source.getProcess())
                .update(button, source.getUpdate())
                .async(source.isAsync())
                .global(source.isGlobal())
                .delay(source.getDelay())
                .timeout(source.getTimeout())
                .partialSubmit(source.isPartialSubmit(), source.isPartialSubmitSet(), source.getPartialSubmitFilter())
                .resetValues(source.isResetValues(), source.isResetValuesSet())
                .ignoreAutoUpdate(source.isIgnoreAutoUpdate())
                .onstart(source.getOnstart())
                .onerror(source.getOnerror())
                .onsuccess(source.getOnsuccess())
                .oncomplete(source.getOncomplete())
                .params(params);

        if (form != null) {
            builder.form(form.getClientId(context));
        }

        builder.preventDefault();

        return builder.build();
    }

    protected String buildNonAjaxRequest(FacesContext context, UIComponent component, UIComponent form, String decodeParam,
            Map<String, List<String>> parameters, boolean submit) {

        StringBuilder request = SharedStringBuilder.get(context, SB_BUILD_NON_AJAX_REQUEST);
        String formId = form.getClientId(context);
        Map<String, Object> params = new HashMap<String, Object>();

        if (decodeParam != null) {
            params.put(decodeParam, decodeParam);
        }

        for (UIComponent child : component.getChildren()) {
            if (child instanceof UIParameter && child.isRendered()) {
                UIParameter param = (UIParameter) child;
                params.put(param.getName(), param.getValue());
            }
        }

        if (parameters != null && !parameters.isEmpty()) {
            for (Iterator<String> it = parameters.keySet().iterator(); it.hasNext();) {
                String paramName = it.next();
                params.put(paramName, parameters.get(paramName).get(0));
            }
        }

        //append params
        if (!params.isEmpty()) {
            request.append("PrimeFaces.addSubmitParam('").append(formId).append("',{");

            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                Object value = params.get(key);

                request.append("'").append(key).append("':'").append(value).append("'");

                if (it.hasNext()) {
                    request.append(",");
                }
            }

            request.append("})");
        }

        if (submit) {
            request.append(".submit('").append(formId).append("');return false;");
        }

        return request.toString();
    }
}
