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
package org.primefaces.component.remotecommand;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.CSVBuilder;

import java.io.IOException;

import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = RemoteCommand.DEFAULT_RENDERER, componentFamily = RemoteCommand.COMPONENT_FAMILY)
public class RemoteCommandRenderer extends CoreRenderer<RemoteCommand> {

    @Override
    public void decode(FacesContext context, RemoteCommand component) {
        if (context.getExternalContext().getRequestParameterMap().containsKey(component.getClientId(context))) {
            ActionEvent event = new ActionEvent(component);
            if (component.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            component.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, RemoteCommand component) throws IOException {
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && component.isValidateClient();
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String name = resolveName(component, context);

        String request = preConfiguredAjaxRequestBuilder(context, component)
                .passParams()
                .build();

        if (csvEnabled) {
            CSVBuilder csvb = requestContext.getCSVBuilder();
            request = csvb.init()
                        .source("this")
                        .ajax(true)
                        .process(component, component.getProcess())
                        .update(component, component.getUpdate())
                        .command("return " + request)
                        .build();
        }
        else {
            request = "return " + request;
        }

        //script
        writer.startElement("script", component);
        writer.writeAttribute("id", clientId, null);
        RendererUtils.encodeScriptTypeIfNecessary(context);

        writer.write(name + " = function() {");
        writer.write(request);
        writer.write("}");

        if (component.isAutoRun()) {
            writer.write(";$(function() {");
            writer.write(name + "();");
            writer.write("});");
        }

        writer.endElement("script");
    }

    protected String resolveName(RemoteCommand component, FacesContext context) {
        String userName = component.getName();

        if (userName != null) {
            return userName;
        }
        else {
            return "command_" + component.getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }
}
