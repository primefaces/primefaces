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
import jakarta.faces.component.UIForm;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = CommandButton.DEFAULT_RENDERER, componentFamily = CommandButton.COMPONENT_FAMILY)
public class CommandButtonRenderer extends CoreRenderer<CommandButton> {

    private static final Logger LOGGER = Logger.getLogger(CommandButtonRenderer.class.getName());

    @Override
    public void decode(FacesContext context, CommandButton component) {
        if (component.isDisabled()) {
            return;
        }

        String param = component.getClientId(context);
        if (context.getExternalContext().getRequestParameterMap().containsKey(param)) {
            component.queueEvent(new ActionEvent(component));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, CommandButton component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, CommandButton component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String type = component.getType();
        boolean pushButton = ("reset".equals(type) || "button".equals(type));
        Object value = component.getValue();
        String form = component.getForm();
        String icon = component.getIcon();
        String title = component.getTitle();
        String onclick = null;

        if (!component.isDisabled() || component.isRenderDisabledClick()) {
            String request = pushButton ? null : buildRequest(context, component, clientId);
            onclick = buildDomEvent(context, component, "onclick", "click", "action", request);
        }

        writer.startElement("button", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("class", component.resolveStyleClass(), "styleClass");
        writer.writeAttribute(HTML.ARIA_LABEL, component.getAriaLabel(), null);

        if (onclick != null) {
            if (component.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick, null);
                writer.writeAttribute("onclick", component.getConfirmationScript(), "onclick");
            }
            else {
                writer.writeAttribute("onclick", onclick, "onclick");
            }
            renderPassThruAttributes(context, component, HTML.BUTTON_WITHOUT_CLICK_ATTRS);
        }
        else {
            renderPassThruAttributes(context, component, HTML.BUTTON_WITH_CLICK_ATTRS);

        }

        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        if (component.isEnabledByValidateClient()) {
            writer.writeAttribute("data-pf-validateclient-dynamic", component.isEnabledByValidateClient(), "data-pf-validateclient-dynamic");
            writer.writeAttribute("data-pf-validateclient-source", "button", "data-pf-validateclient-source");
            writer.writeAttribute("data-pf-validateclient-ajax", Boolean.toString(component.isAjax()), "data-pf-validateclient-ajax");
            writer.writeAttribute("data-pf-validateclient-process",
                    SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getProcess()),
                    "data-pf-validateclient-process");
            writer.writeAttribute("data-pf-validateclient-update",
                    SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getUpdate()),
                    "data-pf-validateclient-update");
        }

        if (!isValueBlank(form)) {
            writer.writeAttribute("data-pf-form", form, null);
        }

        //icon
        if (!isValueBlank(icon)) {
            String defaultIconClass = component.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS;
            String iconClass = defaultIconClass + " " + icon;

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        renderButtonValue(writer, component.isEscape(), value, title, component.getAriaLabel());

        writer.endElement("span");

        writer.endElement("button");
    }

    protected String buildRequest(FacesContext context, CommandButton component, String clientId) throws FacesException {
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && component.isValidateClient();
        String request = null;
        boolean ajax = component.isAjax();

        if (ajax) {
            request = buildAjaxRequest(context, component);
        }
        else {
            UIForm form = ComponentTraversalUtils.closestForm(component);
            if (form == null) {
                LOGGER.log(Level.FINE, "CommandButton '{0}' should be inside a form or should reference a form via its form attribute."
                            + " We will try to find a fallback form on the client side.", clientId);
            }

            request = buildNonAjaxRequest(context, component, form, null, false);
        }

        if (csvEnabled) {
            CSVBuilder csvb = requestContext.getCSVBuilder();
            request = csvb.init()
                    .source("this")
                    .ajax(ajax)
                    .process(component, component.getProcess())
                    .update(component, component.getUpdate())
                    .command(request).build();
        }

        return request;
    }

    protected void encodeScript(FacesContext context, CommandButton component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("CommandButton", component)
            .attr("disableOnAjax", component.isDisableOnAjax(), true)
            .attr("disabledAttr", component.isDisabled(), false)
            .attr("validateClientDynamic", component.isEnabledByValidateClient(), false);

        encodeClientBehaviors(context, component);

        wb.finish();
    }
}
