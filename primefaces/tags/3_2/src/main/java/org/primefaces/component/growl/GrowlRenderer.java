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
package org.primefaces.component.growl;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class GrowlRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Growl growl = (Growl) component;
		String clientId = growl.getClientId(context);
        String widgetVar = growl.resolveWidgetVar();
        
        writer.startElement("span", growl);
		writer.writeAttribute("id", clientId, "id");
		writer.endElement("span");
				
        startScript(writer, clientId);

        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('Growl','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",sticky:" + growl.isSticky());
        writer.write(",life:" + growl.getLife());

        writer.write(",msgs:");
        encodeMessages(context, growl);

        writer.write("});});");
	
		endScript(writer);
	}

    protected void encodeMessages(FacesContext context, Growl growl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Iterator<FacesMessage> messages = growl.isGlobalOnly() ? context.getMessages(null) : context.getMessages();

        writer.write("[");

		while(messages.hasNext()) {
			FacesMessage message = messages.next();
            
            if(message.isRendered() && !growl.isRedisplay()) {
                continue;
            }
            
			String summary = escapeText(message.getSummary());
			String detail = escapeText(message.getDetail());
            int ordinal = message.getSeverity().getOrdinal();
            String severity = null;

            writer.write("{");

			if(growl.isShowSummary() && growl.isShowDetail())
				writer.writeText("summary:\"" + summary + "\",detail:\"" + detail + "\"", null);
			else if(growl.isShowSummary() && !growl.isShowDetail())
				writer.writeText("summary:\"" + summary + "\",detail:\"\"", null);
			else if(!growl.isShowSummary() && growl.isShowDetail())
				writer.writeText("summary:\"\",detail:\"" + detail + "\"", null);
            
            if(ordinal == FacesMessage.SEVERITY_INFO.getOrdinal())
                severity = "info";
            else if(ordinal == FacesMessage.SEVERITY_ERROR.getOrdinal())
                severity = "error";
            else if(ordinal == FacesMessage.SEVERITY_WARN.getOrdinal())
                severity = "warn";
            else if(ordinal == FacesMessage.SEVERITY_FATAL.getOrdinal())
                severity = "fatal";
            
            writer.write(",severity:'" + severity + "'");

            writer.write("}");

            if(messages.hasNext())
                writer.write(",");
            
			message.rendered();
		}

        writer.write("]");
    }
}