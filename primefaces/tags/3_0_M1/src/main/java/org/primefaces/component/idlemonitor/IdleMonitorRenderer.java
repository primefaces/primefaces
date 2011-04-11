/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.idlemonitor;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.IdleEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class IdleMonitorRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        IdleMonitor monitor = (IdleMonitor) component;

        if(params.containsKey(monitor.getClientId(facesContext))) {
            monitor.queueEvent(new IdleEvent(monitor));
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        IdleMonitor monitor = (IdleMonitor) component;
        String clientId = monitor.getClientId();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");

        writer.write(monitor.resolveWidgetVar() + " = new PrimeFaces.widget.IdleMonitor('" + clientId + "', {");
        writer.write("timeout:" + monitor.getTimeout());

        if(monitor.getIdleListener() != null) {         
            writer.write(",hasIdleListener:true");

            String update = monitor.getUpdate();
            if(update != null)
                writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, monitor, update) + "'");
        }

        if(monitor.getOnidle() != null) writer.write(",onidle: function() {" + monitor.getOnidle() + ";}");
        if(monitor.getOnactive() != null) writer.write(",onactive: function() {" + monitor.getOnactive() + ";}");

        writer.write("});});");

        writer.endElement("script");
    }
}