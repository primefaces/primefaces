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
package org.primefaces.component.commandlink;

import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;

public class CommandLinkRenderer extends CoreRenderer {

    private static final String SB_BUILD_ONCLICK = CommandLinkRenderer.class.getName() + "#buildOnclick";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        CommandLink link = (CommandLink) component;
        if (link.isDisabled()) {
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
        CommandLink link = (CommandLink) component;

        encodeMarkup(context, link);
        encodeScript(context, link);
    }

    protected void encodeMarkup(FacesContext context, CommandLink link) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = link.getClientId(context);
        Object label = link.getValue();
        String form = link.getForm();

        String request;
        boolean ajax = link.isAjax();
        String styleClass = getStyleClassBuilder(context)
                .add(link.isDisabled(), CommandLink.DISABLED_STYLE_CLASS, CommandLink.STYLE_CLASS)
                .add(link.getStyleClass())
                .build();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && link.isValidateClient();

        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
        if (link.getOnclick() != null) {
            onclick.append(link.getOnclick()).append(";");
        }

        String onclickBehaviors = getEventBehaviors(context, link, "click", null);
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors);
        }

        writer.startElement("a", link);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute(HTML.ARIA_LABEL, link.getAriaLabel(), null);

        if (!isValueBlank(form)) {
            writer.writeAttribute("data-pf-form", form, null);
        }

        if (ajax) {
            request = buildAjaxRequest(context, link);
        }
        else {
            UIForm uiForm = ComponentTraversalUtils.closestForm(link);
            if (uiForm == null) {
                throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
            }

            request = buildNonAjaxRequest(context, link, uiForm, clientId, true);
        }

        if (csvEnabled) {
            CSVBuilder csvb = requestContext.getCSVBuilder();
            request = csvb.init().source("this").ajax(ajax).process(link, link.getProcess()).update(link, link.getUpdate()).command(request).build();
        }

        onclick.append(request);

        if (onclick.length() > 0) {
            if (link.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick.toString(), null);
                writer.writeAttribute("onclick", link.getConfirmationScript(), "onclick");
            }
            else {
                writer.writeAttribute("onclick", onclick.toString(), "onclick");
            }
            renderPassThruAttributes(context, link, HTML.LINK_WITHOUT_CLICK_ATTRS);
        }
        else {
            renderPassThruAttributes(context, link, HTML.LINK_ATTRS);
        }

        if (link.isDisabled()) {
            writer.writeAttribute("tabindex", "-1", null);
        }

        List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>(1);
        behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));
        String dialogReturnBehavior = getEventBehaviors(context, link, DialogReturnAware.EVENT_DIALOG_RETURN, behaviorParams);
        if (dialogReturnBehavior != null) {
            writer.writeAttribute(DialogReturnAware.ATTRIBUTE_DIALOG_RETURN_SCRIPT, dialogReturnBehavior, null);
        }

        if (label != null) {
            writer.writeText(label, "value");
        }

        renderChildren(context, link);

        writer.endElement("a");
    }

    protected void encodeScript(FacesContext context, CommandLink link) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("CommandLink", link)
                .attr("disableOnAjax", link.isDisableOnAjax(), true)
                .attr("disabledAttr", link.isDisabled(), false);

        encodeClientBehaviors(context, link);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
