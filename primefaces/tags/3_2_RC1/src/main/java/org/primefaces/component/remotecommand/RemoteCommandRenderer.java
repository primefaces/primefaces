/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import java.io.IOException;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import org.primefaces.component.api.AjaxSource;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RemoteCommandRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        RemoteCommand command = (RemoteCommand) component;

        if(context.getExternalContext().getRequestParameterMap().containsKey(command.getClientId(context))) {
            ActionEvent event = new ActionEvent(command);
            if(command.isImmediate())
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            else
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);

            command.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RemoteCommand command = (RemoteCommand) component;
        String name = command.getName();

        //script
        writer.startElement("script", command);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write(name + " = function() {");

        writer.write(buildAjaxRequest(context, command));

        writer.write("}\n");
        
        if(command.isAutoRun()) {
            writer.write("$(function() {");
            writer.write(name + "();");
            writer.write("});");
        }

        writer.endElement("script");
    }
    
    @Override
    protected String buildAjaxRequest(FacesContext context, AjaxSource source) {
        UIComponent component = (UIComponent) source;
        String clientId = component.getClientId(context);
        UIComponent form = ComponentUtils.findParentForm(context, component);
        
        if(form == null) {
            throw new FacesException("Component " + component.getClientId(context) + " must be enclosed in a form.");
        }

        StringBuilder req = new StringBuilder();
        req.append("PrimeFaces.ab(");

        //form
        req.append("{formId:").append("'").append(form.getClientId(context)).append("'");

        //source
        req.append(",source:").append("'").append(clientId).append("'");

        //process
        String process = source.getProcess();
        if(process == null) {
            process = "@all";
        } else {
            process = ComponentUtils.findClientIds(context, component, process);
            
            //add @this   
            if(process.indexOf(clientId) == -1)
                process = process + " " + clientId;
        }
        req.append(",process:'").append(process).append("'");


        //update
        if(source.getUpdate() != null) {
            req.append(",update:'").append(ComponentUtils.findClientIds(context, component, source.getUpdate())).append("'");
        }

        //async
        if(source.isAsync())
            req.append(",async:true");

        //global
        if(!source.isGlobal())
            req.append(",global:false");

        //callbacks
        if(source.getOnstart() != null)
            req.append(",onstart:function(){").append(source.getOnstart()).append(";}");
        if(source.getOnerror() != null)
            req.append(",onerror:function(xhr, status, error){").append(source.getOnerror()).append(";}");
        if(source.getOnsuccess() != null)
            req.append(",onsuccess:function(data, status, xhr){").append(source.getOnsuccess()).append(";}");
        if(source.getOncomplete() != null)
            req.append(",oncomplete:function(xhr, status, args){").append(source.getOncomplete()).append(";}");

        //params
        req.append(",params:arguments[0]");
       
        req.append("});");

        return req.toString();
    }
}
