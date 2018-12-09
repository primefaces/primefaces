/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.primefaces.util.ComponentTraversalUtils;

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

        String request = buildAjaxRequest(context, command, ComponentTraversalUtils.closestForm(context, command));

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
