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
package org.primefaces.renderkit;

import org.primefaces.component.api.MenuItemAware;
import org.primefaces.behavior.confirm.ConfirmBehavior;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.component.divider.Divider;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.*;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import java.io.IOException;
import java.util.*;

public class MenuItemAwareRenderer extends OutcomeTargetRenderer {

    private static final String SEPARATOR = "_";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeDynamicMenuItem(context, component);
    }

    protected void encodeOnClick(FacesContext context, UIComponent source, MenuItem menuitem) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
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

            UIForm form = ComponentTraversalUtils.closestForm(context, source);
            if (form == null) {
                throw new FacesException("MenuItem must be inside a form element");
            }

            String command;
            if (menuitem.isDynamic()) {
                String menuClientId = source.getClientId(context);
                Map<String, List<String>> params = menuitem.getParams();
                if (params == null) {
                    params = new LinkedHashMap<>();
                }
                List<String> idParams = Collections.singletonList(menuitem.getId());
                params.put(menuClientId + "_menuid", idParams);

                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, source, (AjaxSource) menuitem, form, params)
                        : buildNonAjaxRequest(context, source, form, menuClientId, params, true);
            }
            else {
                command = menuitem.isAjax()
                        ? buildAjaxRequest(context, (UIComponent & AjaxSource) menuitem, form)
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

        if (menuitem instanceof DialogReturnAware) {
            List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>(1);
            behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));
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

    protected MenuItem findMenuitem(List<MenuElement> elements, String id) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        else {
            String[] paths = id.split(SEPARATOR);

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
                String relativeIndex = id.substring(id.indexOf(SEPARATOR) + 1);

                return findMenuitem(((MenuGroup) childElement).getElements(), relativeIndex);
            }
        }
    }

    /**
     * Decode menu item not present in JSF tree but added using model attribute
     * @param context
     * @param component
     * @return true if a menu item has been decoded, otherwise false
     */
    protected boolean decodeDynamicMenuItem(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String menuid = params.get(clientId + "_menuid");
        if (menuid != null) {
            MenuItem menuitem = findMenuitem(((MenuItemAware) component).getElements(), menuid);
            MenuActionEvent event = new MenuActionEvent(component, menuitem);

            if (menuitem.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            component.queueEvent(event);
        }

        return menuid != null;
    }
}
