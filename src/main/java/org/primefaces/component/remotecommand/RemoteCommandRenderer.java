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
package org.primefaces.component.remotecommand;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import java.io.IOException;

import org.primefaces.renderkit.CoreRenderer;

public class RemoteCommandRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        RemoteCommand command = (RemoteCommand) component;

        if (context.getExternalContext().getRequestParameterMap().containsKey(command.getClientId(context))) {
            ActionEvent event = new ActionEvent(command);
            if (command.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            command.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RemoteCommand command = (RemoteCommand) component;
        String clientId = command.getClientId(context);
        String name = resolveName(command, context);

        String request = preConfiguredAjaxRequestBuilder(context, command)
                .passParams()
                .build();

        //script
        writer.startElement("script", command);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write(name + " = function() {");
        writer.write(request);
        writer.write("}");

        if (command.isAutoRun()) {
            writer.write(";$(function() {");
            writer.write(name + "();");
            writer.write("});");
        }

        writer.endElement("script");
    }

    protected String resolveName(RemoteCommand command, FacesContext context) {
        String userName = command.getName();

        if (userName != null) {
            return userName;
        }
        else {
            return "command_" + command.getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }
}
