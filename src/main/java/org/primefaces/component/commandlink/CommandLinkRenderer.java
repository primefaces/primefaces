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
package org.primefaces.component.commandlink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.*;

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
        ResponseWriter writer = context.getResponseWriter();
        CommandLink link = (CommandLink) component;
        String clientId = link.getClientId(context);
        Object label = link.getValue();

        if (!link.isDisabled()) {
            String request;
            boolean ajax = link.isAjax();
            String styleClass = link.getStyleClass();
            styleClass = styleClass == null ? CommandLink.STYLE_CLASS : CommandLink.STYLE_CLASS + " " + styleClass;
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
            if (link.getAriaLabel() != null) {
                writer.writeAttribute(HTML.ARIA_LABEL, link.getAriaLabel(), null);
            }
            else if (link.getTitle() != null) {
                writer.writeAttribute(HTML.ARIA_LABEL, link.getTitle(), null);
            }

            if (ajax) {
                request = buildAjaxRequest(context, link);
            }
            else {
                UIForm form = ComponentTraversalUtils.closestForm(context, link);
                if (form == null) {
                    throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
                }

                request = buildNonAjaxRequest(context, link, form, clientId, true);
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
            }

            List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>();
            behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));
            String dialogReturnBehavior = getEventBehaviors(context, link, DialogReturnAware.EVENT_DIALOG_RETURN, behaviorParams);
            if (dialogReturnBehavior != null) {
                writer.writeAttribute(DialogReturnAware.ATTRIBUTE_DIALOG_RETURN_SCRIPT, dialogReturnBehavior, null);
            }

            renderPassThruAttributes(context, link, HTML.LINK_ATTRS, HTML.CLICK_EVENT);

            if (label != null) {
                writer.writeText(label, "value");
            }

            renderChildren(context, link);

            writer.endElement("a");
        }
        else {
            String styleClass = link.getStyleClass();
            styleClass = styleClass == null ? CommandLink.DISABLED_STYLE_CLASS : CommandLink.DISABLED_STYLE_CLASS + " " + styleClass;

            writer.startElement("span", link);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleclass");

            if (link.getStyle() != null) {
                writer.writeAttribute("style", link.getStyle(), "style");
            }

            if (label != null) {
                writer.writeText(label, "value");
            }

            renderChildren(context, link);

            writer.endElement("span");
        }
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
