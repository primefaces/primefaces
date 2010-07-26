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
package org.primefaces.component.growl;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;

public class GrowlRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Growl growl = (Growl) component;
		String clientId = growl.getClientId(facesContext);
		
		writer.startElement("span", growl);
		writer.writeAttribute("id", clientId, "id");
		writer.endElement("span");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function(){");

		Iterator<FacesMessage> messages = growl.isGlobalOnly() ? facesContext.getMessages(null) : facesContext.getMessages();
		
		while(messages.hasNext()) {
			FacesMessage message = messages.next();
			String severityImage = getImage(facesContext, growl, message);
			String summary = message.getSummary().replaceAll("'", "\\\\'");
			String detail = message.getDetail().replaceAll("'", "\\\\'");
			
			writer.write("jQuery.gritter.add({");
			
			if(growl.isShowSummary() && growl.isShowDetail()) 
				writer.write("title:'" + summary + "',text:'" + detail + "'");
			else if(growl.isShowSummary() && !growl.isShowDetail())
				writer.write("title:'" + summary + "',text:''");
			else if(!growl.isShowSummary() && growl.isShowDetail())
				writer.write("title:'',text:'" + detail + "'");
			
			if(!isValueBlank(severityImage))
				writer.write(",image:'" + severityImage + "'");
			
			if(growl.isSticky())
				writer.write(",sticky:true");
			else
				writer.write(",sticky:false");
			
			if(growl.getLife() != 6000) writer.write(",time:" + growl.getLife());
			
			writer.write("});");	
			
			message.rendered();
		}
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private String getImage(FacesContext facesContext, Growl growl, FacesMessage message) {
		if(message.getSeverity() == null)
			return "";
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_INFO))
			return growl.getInfoIcon() != null ? getResourceURL(facesContext, growl.getInfoIcon()) : ResourceUtils.getResourceURL(facesContext, Growl.INFO_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_ERROR))
			return growl.getErrorIcon() != null ? getResourceURL(facesContext, growl.getErrorIcon()) : ResourceUtils.getResourceURL(facesContext, Growl.ERROR_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_WARN))
			return growl.getWarnIcon() != null ? getResourceURL(facesContext, growl.getWarnIcon()) : ResourceUtils.getResourceURL(facesContext, Growl.WARN_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_FATAL))
			return growl.getFatalIcon() != null ? getResourceURL(facesContext, growl.getFatalIcon()) : ResourceUtils.getResourceURL(facesContext, Growl.FATAL_ICON);
		else
			return "";
	}
}