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

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class MessageRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		Message uiMessage = (Message) component;
		UIComponent target = uiMessage.findComponent(uiMessage.getFor());
		if(target == null) {
			throw new FacesException("Cannot find component \"" + uiMessage.getFor() + "\" in view.");
		}
			
		Iterator<FacesMessage> msgs = facesContext.getMessages(target.getClientId(facesContext));

		writer.startElement("span", uiMessage);
		writer.writeAttribute("id", uiMessage.getClientId(facesContext), null);
		
		if(msgs.hasNext()) {
			FacesMessage msg = msgs.next();
			
			if(msg.isRendered() && !uiMessage.isRedisplay()) {
				writer.endElement("span");
				return;
				
			} else {
				Severity severity = msg.getSeverity();
				String severityKey = null;
				
				if(severity.equals(FacesMessage.SEVERITY_ERROR)) severityKey = "error";
				else if(severity.equals(FacesMessage.SEVERITY_INFO)) severityKey = "info";
				else if(severity.equals(FacesMessage.SEVERITY_WARN)) severityKey = "warn";
				else if(severity.equals(FacesMessage.SEVERITY_FATAL))  severityKey = "fatal";
					
				writer.writeAttribute("class", "ui-message-" + severityKey + " ui-widget ui-corner-all", null);
				
				if(uiMessage.isShowSummary())
					encodeMessageText(writer, msg.getSummary(), severityKey + "-summary");
				if(uiMessage.isShowDetail())
					encodeMessageText(writer, msg.getDetail(), severityKey + "-detail");
					
				msg.rendered();
			}
		}
		
		writer.endElement("span");
	}
	
	private void encodeMessageText(ResponseWriter writer, String text, String severity) throws IOException {
		writer.startElement("span", null);
		writer.writeAttribute("class", "ui-message-" + severity, null);
		writer.write(text);
		writer.endElement("span");
	}
}