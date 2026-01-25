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
package org.primefaces.renderkit;

import org.primefaces.behavior.confirm.ConfirmBehavior;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.component.api.MenuItemAware;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.divider.Divider;
import org.primefaces.component.menuitem.UIMenuItem;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.BaseMenuModel;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuGroup;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.behavior.ClientBehavior;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.PhaseId;

public class MenuItemAwareRenderer<T extends UIComponent> extends OutcomeTargetRenderer<T> {

    private static final Logger LOGGER = Logger.getLogger(MenuItemAwareRenderer.class.getName());

    @Override
    public void decode(FacesContext context, T component) {
        decodeDynamicMenuItem(context, component);
    }

    protected boolean isMenuItemLink(FacesContext context, UIComponent source, MenuItem menuitem) {
        return LangUtils.isNotBlank(menuitem.getUrl()) || LangUtils.isNotBlank(menuitem.getOutcome());
    }

    protected boolean isMenuItemSubmitting(FacesContext context, UIComponent source, MenuItem menuitem) {
        boolean submitting;

        // #1 first check for assigned server side callbacks
        submitting = menuitem.getFunction() != null || LangUtils.isNotBlank(menuitem.getCommand());
        if (!submitting && menuitem instanceof UIMenuItem) {
            submitting = ((UIMenuItem) menuitem).getActionExpression() != null
                    || ((UIMenuItem) menuitem).getActionListeners().length > 0;
        }

        // 2# AJAX
        if (!submitting && menuitem.isAjax()) {
            submitting = menuitem.isResetValues()
                    || LangUtils.isNotBlank(menuitem.getUpdate())
                    || LangUtils.isNotBlank(menuitem.getProcess());
        }

        return submitting;
    }

    protected void encodeOnClick(FacesContext context, UIComponent source, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        setConfirmationScript(context, menuitem);

        String onclick = menuitem.getOnclick();
        boolean isLink = isMenuItemLink(context, source, menuitem);
        boolean isSubmitting = isMenuItemSubmitting(context, source, menuitem);

        //GET
        if (isLink) {
            String targetURL = getTargetURL(context, (UIOutcomeTarget) menuitem);
            writer.writeAttribute("href", targetURL, null);

            if (menuitem.getTarget() != null) {
                writer.writeAttribute("target", menuitem.getTarget(), null);
            }
        }
        //POST
        else {
            writer.writeAttribute("href", "#", null);
        }

        if (isSubmitting) {
            String menuClientId = source.getClientId(context);
            UIForm form = ComponentTraversalUtils.closestForm(source);
            if (form == null) {
                LOGGER.log(Level.FINE, () -> "Menu '" + menuClientId
                            + "' should be inside a form or should reference a form via its form attribute."
                            + " We will try to find a fallback form on the client side.");
            }

            String command;
            if (menuitem.isDynamic()) {
                Map<String, List<String>> params = menuitem.getParams();
                if (params == null) {
                    params = new LinkedHashMap<>();
                }

                params.put(menuClientId + "_menuid", Collections.singletonList(menuitem.getId()));

                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, source, (AjaxSource) menuitem, form, params)
                        : buildNonAjaxRequest(context, source, form, menuClientId, params, true);
            }
            else {
                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, (UIComponent & AjaxSource) menuitem, form)
                        : buildNonAjaxRequest(context, ((UIComponent) menuitem), form, ((UIComponent) menuitem).getClientId(context), true);
            }

            if (isLink) {
                // allow CTRL+CLICK link to open new tab
                command = "if(PF.metaKey(event)){return true};" + command;
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

        if (menuitem instanceof DialogReturnAware) {
            List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>(1);
            behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBTRUSIVE));
            String dialogReturnBehavior = getEventBehaviors(context, (ClientBehaviorHolder) menuitem, DialogReturnAware.EVENT_DIALOG_RETURN,
                    behaviorParams);
            if (dialogReturnBehavior != null) {
                writer.writeAttribute(DialogReturnAware.ATTRIBUTE_DIALOG_RETURN_SCRIPT, dialogReturnBehavior, null);
            }
        }
    }

    protected void encodeSeparator(FacesContext context, Separator separator) throws IOException {
        if (!separator.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String style = separator.getStyle();
        String styleClass;

        if (separator instanceof Divider) {
            String layout = ((Divider) separator).getLayout();
            String align = ((Divider) separator).getAlign();
            String type = ((Divider) separator).getType();
            boolean isHorizontal = "horizontal".equals(layout);
            boolean isVertical = "vertical".equals(layout);
            styleClass = getStyleClassBuilder(context)
                    .add(Divider.STYLE_CLASS)
                    .add(separator.getStyleClass())
                    .add(isHorizontal, Divider.HORIZONTAL_CLASS)
                    .add(isVertical, Divider.VERTICAL_CLASS)
                    .add("solid".equals(type), Divider.SOLID_CLASS)
                    .add("dashed".equals(type), Divider.DASHED_CLASS)
                    .add("dotted".equals(type), Divider.DOTTED_CLASS)
                    .add(isHorizontal && (align == null || "left".equals(align)), Divider.ALIGN_LEFT_CLASS)
                    .add(isHorizontal && "right".equals(align), Divider.ALIGN_RIGHT_CLASS)
                    .add((isHorizontal && "center".equals(align)) || (isVertical && (align == null || "center".equals(align))), Divider.ALIGN_CENTER_CLASS)
                    .add(isVertical && "top".equals(align), Divider.ALIGN_TOP_CLASS)
                    .add(isVertical && "bottom".equals(align), Divider.ALIGN_BOTTOM_CLASS)
                    .build();
        }

        else {
            styleClass = getStyleClassBuilder(context)
                    .add(Divider.STYLE_CLASS)
                    .add(Divider.HORIZONTAL_CLASS)
                    .add(Divider.SOLID_CLASS)
                    .add(separator.getStyleClass())
                    .build();
        }

        //title
        writer.startElement("li", null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_SEPARATOR, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.endElement("li");
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
                                context, (UIComponent) item, "click", item.getClientId(), Collections.emptyList());
                        clientBehavior.getScript(cbc);
                        break;
                    }
                }
            }
        }
    }

    protected MenuItem findMenuItemById(List<MenuElement> elements, String id) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        for (int i = 0; i < elements.size(); i++) {
            MenuElement element = elements.get(i);
            String menuId = element.getId();
            if (menuId != null) {
                menuId = menuId.split(Pattern.quote(BaseMenuModel.COORDINATES_SEPARATOR))[0];
            }
            if (Objects.equals(menuId, id)) {
                return (MenuItem) element;
            }
            if (element instanceof MenuGroup) {
                MenuItem result = findMenuItemById(((MenuGroup) element).getElements(), id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    protected MenuItem findMenuItemByCoordinates(List<MenuElement> elements, String coords) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }

        String[] paths = coords.split(BaseMenuModel.ID_SEPARATOR);
        if (paths.length == 0) {
            return null;
        }

        int childIndex = Integer.parseInt(paths[0]);
        if (childIndex >= elements.size()) {
            return null;
        }

        MenuElement childElement = elements.get(childIndex);

        if (paths.length == 1) {
            return (MenuItem) childElement;
        }
        else {
            String relativeIndex = coords.substring(coords.indexOf(BaseMenuModel.ID_SEPARATOR) + 1);
            return findMenuItemByCoordinates(((MenuGroup) childElement).getElements(), relativeIndex);
        }
    }

    /**
     * Decode menu item not present in Faces tree but added using model attribute.
     * ID is in format UUID|COORDS.
     *
     * @param context the FacesContext
     * @param component the menu component
     * @return true if a menu item has been decoded, otherwise false
     */
    protected boolean decodeDynamicMenuItem(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String menuid = params.get(clientId + "_menuid");
        if (menuid == null) {
            return false;
        }

        // split the UUID|COORDINATES by |
        String[] ids = menuid.split(Pattern.quote(BaseMenuModel.COORDINATES_SEPARATOR));
        if (ids.length == 0) {
            return false;
        }
        String uuid = ids[0];
        String coordinates = ids.length == 2 ? ids[1] : null;

        MenuItem menuitem = findMenuItemById(((MenuItemAware) component).getElements(), uuid);
        if (menuitem == null && coordinates != null) {
            // #8867 fallback to old PF logic for RequestScoped
            menuitem = findMenuItemByCoordinates(((MenuItemAware) component).getElements(), coordinates);
        }

        // skip removed/disabled menu items
        if (menuitem != null && !menuitem.isDisabled()) {
            MenuActionEvent event = new MenuActionEvent(component, menuitem);

            if (menuitem.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            component.queueEvent(event);
        }

        return true;
    }

    protected boolean shouldBeRendered(FacesContext context, MenuItemAware abstractMenu) {
        return abstractMenu.getElements().stream().anyMatch(me -> shouldBeRendered(context, me));
    }

    protected boolean shouldBeRendered(FacesContext context, MenuElement menuElement) {
        if (menuElement instanceof MenuGroup) {
            MenuGroup group = (MenuGroup) menuElement;
            return group.getElements().stream().anyMatch(me -> shouldBeRendered(context, me));
        }
        else if (menuElement instanceof Separator) {
            return false;
        }
        else {
            try {
                if (menuElement instanceof UIComponent) {
                    UIComponent component = (UIComponent) menuElement;
                    component.pushComponentToEL(context, component);
                }
                return menuElement.isRendered();
            }
            finally {
                if (menuElement instanceof UIComponent) {
                    UIComponent component = (UIComponent) menuElement;
                    component.popComponentFromEL(context);
                }
            }
        }
    }
}
