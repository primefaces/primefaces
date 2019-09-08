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
package org.primefaces.component.commandbutton;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class CommandButtonRenderer extends CoreRenderer {

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
        boolean pushButton = (type.equals("reset") || type.equals("button"));
        Object value = button.getValue();
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
            //For ScreenReader
            String text = (title != null) ? title : "ui-button";

            writer.writeText(text, "title");
        }
        else {
            if (button.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }

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
            UIForm form = ComponentTraversalUtils.closestForm(context, button);
            if (form == null) {
                throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
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
        String clientId = button.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("CommandButton", button.resolveWidgetVar(context), clientId);

        encodeClientBehaviors(context, button);

        wb.finish();
    }
}
