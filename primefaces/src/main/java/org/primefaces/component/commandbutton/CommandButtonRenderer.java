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
package org.primefaces.component.commandbutton;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;

public class CommandButtonRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(CommandButtonRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        CommandButton button = (CommandButton) component;
        if (button.isDisabled()) {
            return;
        }

        String param = component.getClientId(context);
        if (context.getExternalContext().getRequestParameterMap().containsKey(param)) {
            component.queueEvent(new ActionEvent(component));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        CommandButton button = (CommandButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, CommandButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        String type = button.getType();
        boolean pushButton = ("reset".equals(type) || "button".equals(type));
        Object value = button.getValue();
        String form = button.getForm();
        String icon = button.getIcon();
        String title = button.getTitle();
        String onclick = null;

        if (!button.isDisabled() || button.isRenderDisabledClick()) {
            String request = pushButton ? null : buildRequest(context, button, clientId);
            onclick = buildDomEvent(context, button, "onclick", "click", "action", request);
        }

        writer.startElement("button", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");
        writer.writeAttribute(HTML.ARIA_LABEL, button.getAriaLabel(), null);

        if (onclick != null) {
            if (button.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick, null);
                writer.writeAttribute("onclick", button.getConfirmationScript(), "onclick");
            }
            else {
                writer.writeAttribute("onclick", onclick, "onclick");
            }
            renderPassThruAttributes(context, button, HTML.BUTTON_WITHOUT_CLICK_ATTRS);
        }
        else {
            renderPassThruAttributes(context, button, HTML.BUTTON_WITH_CLICK_ATTRS);

        }

        if (button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        if (button.isEnabledByValidateClient()) {
            writer.writeAttribute("data-pf-validateclient-dynamic", button.isEnabledByValidateClient(), "data-pf-validateclient-dynamic");
            writer.writeAttribute("data-pf-validateclient-source", "button", "data-pf-validateclient-source");
            writer.writeAttribute("data-pf-validateclient-ajax", Boolean.toString(button.isAjax()), "data-pf-validateclient-ajax");
            writer.writeAttribute("data-pf-validateclient-process", SearchExpressionUtils.resolveClientIdsForClientSide(context, button, button.getProcess()),
                    "data-pf-validateclient-process");
            writer.writeAttribute("data-pf-validateclient-update", SearchExpressionUtils.resolveClientIdsForClientSide(context, button, button.getUpdate()),
                    "data-pf-validateclient-update");
        }

        if (!isValueBlank(form)) {
            writer.writeAttribute("data-pf-form", form, null);
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

        renderButtonValue(writer, button.isEscape(), value, title, button.getAriaLabel());

        writer.endElement("span");

        writer.endElement("button");
    }

    protected String buildRequest(FacesContext context, CommandButton button, String clientId) throws FacesException {
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && button.isValidateClient();
        String request = null;
        boolean ajax = button.isAjax();

        if (ajax) {
            request = buildAjaxRequest(context, button);
        }
        else {
            UIForm form = ComponentTraversalUtils.closestForm(button);
            if (form == null) {
                LOGGER.log(Level.FINE, "CommandButton '{0}' should be inside a form or should reference a form via its form attribute."
                            + " We will try to find a fallback form on the client side.", clientId);
            }

            request = buildNonAjaxRequest(context, button, form, null, false);
        }

        if (csvEnabled) {
            CSVBuilder csvb = requestContext.getCSVBuilder();
            request = csvb.init().source("this").ajax(ajax).process(button, button.getProcess()).update(button, button.getUpdate()).command(request).build();
        }

        return request;
    }

    protected void encodeScript(FacesContext context, CommandButton button) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("CommandButton", button)
            .attr("disableOnAjax", button.isDisableOnAjax(), true)
            .attr("disabledAttr", button.isDisabled(), false)
            .attr("validateClientDynamic", button.isEnabledByValidateClient(), false);

        encodeClientBehaviors(context, button);

        wb.finish();
    }
}
