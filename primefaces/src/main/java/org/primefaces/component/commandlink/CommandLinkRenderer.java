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
import jakarta.faces.component.UIForm;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = CommandLink.DEFAULT_RENDERER, componentFamily = CommandLink.COMPONENT_FAMILY)
public class CommandLinkRenderer extends CoreRenderer<CommandLink> {

    private static final String SB_BUILD_ONCLICK = CommandLinkRenderer.class.getName() + "#buildOnclick";

    @Override
    public void decode(FacesContext context, CommandLink component) {
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
    public void encodeEnd(FacesContext context, CommandLink component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, CommandLink component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        Object label = component.getValue();
        String form = component.getForm();

        String request;
        boolean ajax = component.isAjax();
        String styleClass = getStyleClassBuilder(context)
                .add(component.isDisabled(), CommandLink.DISABLED_STYLE_CLASS, CommandLink.STYLE_CLASS)
                .add(component.getStyleClass())
                .build();
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && component.isValidateClient();

        StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
        if (component.getOnclick() != null) {
            onclick.append(component.getOnclick()).append(";");
        }

        String onclickBehaviors = getEventBehaviors(context, component, "click", null);
        if (onclickBehaviors != null) {
            onclick.append(onclickBehaviors);
        }

        writer.startElement("a", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute(HTML.ARIA_LABEL, component.getAriaLabel(), null);

        if (!isValueBlank(form)) {
            writer.writeAttribute("data-pf-form", form, null);
        }

        if (ajax) {
            request = buildAjaxRequest(context, component);
        }
        else {
            UIForm uiForm = ComponentTraversalUtils.closestForm(component);
            if (uiForm == null) {
                throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
            }

            request = buildNonAjaxRequest(context, component, uiForm, clientId, true);
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

        onclick.append(request);

        if (onclick.length() > 0) {
            if (component.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick.toString(), null);
                writer.writeAttribute("onclick", component.getConfirmationScript(), "onclick");
            }
            else {
                writer.writeAttribute("onclick", onclick.toString(), "onclick");
            }
            renderPassThruAttributes(context, component, HTML.LINK_WITHOUT_CLICK_ATTRS);
        }
        else {
            renderPassThruAttributes(context, component, HTML.LINK_ATTRS);
        }

        if (component.isDisabled()) {
            writer.writeAttribute("tabindex", "-1", null);
        }

        List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>(1);
        behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBTRUSIVE));
        String dialogReturnBehavior = getEventBehaviors(context, component, DialogReturnAware.EVENT_DIALOG_RETURN, behaviorParams);
        if (dialogReturnBehavior != null) {
            writer.writeAttribute(DialogReturnAware.ATTRIBUTE_DIALOG_RETURN_SCRIPT, dialogReturnBehavior, null);
        }

        if (label != null) {
            writer.writeText(label, "value");
        }

        renderChildren(context, component);

        writer.endElement("a");
    }

    protected void encodeScript(FacesContext context, CommandLink component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("CommandLink", component)
                .attr("disableOnAjax", component.isDisableOnAjax(), true)
                .attr("disabledAttr", component.isDisabled(), false);

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, CommandLink component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
