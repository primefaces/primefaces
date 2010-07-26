/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.message;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class MessageRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		Message message = (Message) component;
		String forComponentClientId = ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), message.getFor()).getClientId(facesContext);
		Iterator<FacesMessage> msgs = facesContext.getMessages(forComponentClientId);
		FacesMessage msg = msgs.hasNext() ? msgs.next() : null;		//Only support one message to display 

		writer.startElement("span", message);
		writer.writeAttribute("id", message.getClientId(facesContext), null);
		
		if(msg != null) {
			Severity severity = msg.getSeverity();
			String severityKey = null;
			
			if(severity.equals(FacesMessage.SEVERITY_ERROR)) severityKey = "error";
			else if(severity.equals(FacesMessage.SEVERITY_INFO)) severityKey = "info";
			else if(severity.equals(FacesMessage.SEVERITY_WARN)) severityKey = "warn";
			else if(severity.equals(FacesMessage.SEVERITY_FATAL))  severityKey = "fatal";
				
			writer.writeAttribute("class", "pf-message-" + severityKey, null);
			
			if(message.isShowSummary())
				encodeMessageText(writer, msg.getSummary(), severityKey + "-summary");
			if(message.isShowDetail())
				encodeMessageText(writer, msg.getDetail(), severityKey + "-detail");
				
		}

		writer.endElement("span");
	}
	
	private void encodeMessageText(ResponseWriter writer, String text, String severity) throws IOException {
		writer.startElement("span", null);
		writer.writeAttribute("class", "pf-message-" + severity, null);
		writer.write(text);
		writer.endElement("span");
	}
}
