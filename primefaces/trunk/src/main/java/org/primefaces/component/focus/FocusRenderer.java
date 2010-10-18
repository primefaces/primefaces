/*
 * Copyright 2009,2010 Prime Technology.
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
package org.primefaces.component.focus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class FocusRenderer extends CoreRenderer {

	private final static Map<String, Integer> severityOrdinals = new HashMap<String, Integer>();
	
	static {
		severityOrdinals.put("info", FacesMessage.SEVERITY_INFO.getOrdinal());
		severityOrdinals.put("warn", FacesMessage.SEVERITY_WARN.getOrdinal());
		severityOrdinals.put("error", FacesMessage.SEVERITY_ERROR.getOrdinal());
		severityOrdinals.put("fatal", FacesMessage.SEVERITY_FATAL.getOrdinal());
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Focus focus = (Focus) component;
		ResponseWriter writer = facesContext.getResponseWriter();
		
		//Dummy markup for ajax update
		writer.startElement("span", focus);
		writer.writeAttribute("id", focus.getClientId(facesContext), "id");
		writer.endElement("span");
		
		writer.startElement("script", focus);
		writer.writeAttribute("type", "text/javascript", null);
		
		if(focus.getFor() != null) {
			encodeExplicitFocus(facesContext, focus);
		} else {
			encodeImplicitFocus(facesContext, focus);				
		}
		
		writer.endElement("script");
	}

	protected void encodeExplicitFocus(FacesContext facesContext, Focus focus) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent forComponent = focus.findComponent(focus.getFor());
		
		if(forComponent == null) {
			throw new FacesException("Cannot find component '" + focus.getFor() + "' in view.");
		}
		String clientId = forComponent.getClientId(facesContext);
		
		writer.write("jQuery(function(){");
		writer.write("jQuery(PrimeFaces.escapeClientId('" + clientId +"')).focus();");
		writer.write("});");
	}
	
	protected void encodeImplicitFocus(FacesContext facesContext, Focus focus) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(isPostBack()){
			String clientId = findFirstInvalidClientId(facesContext, focus);
			
			if(clientId != null) {
				writer.write("setTimeout(function() {");
                writer.write("var focusTarget = PrimeFaces.escapeClientId('" + clientId + "');");
				writer.write("jQuery(focusTarget + ',' + focusTarget + ' input').focus();");
				writer.write("}, 500);");
			}
			
		} else {
			writer.write("jQuery(function(){");
			String selector = Focus.INPUT_SELECTOR;
			
			if(focus.getContext() != null) {
				UIComponent context = focus.findComponent(focus.getContext());
				
				if(context == null)
					throw new FacesException("Cannot find component " + focus.getContext() + " in view");
				else {
					selector = ComponentUtils.escapeJQueryId(context.getClientId(facesContext)) + " " +  selector;
				}
			}
			
			writer.write("jQuery('" + selector + "').focus();");
			writer.write("});");
		}
	}
	
	protected String findFirstInvalidClientId(FacesContext facesContext, Focus focus) {
		int minSeverityOrdinal = severityOrdinals.get(focus.getMinSeverity());
		
		for(Iterator<String> iterator = facesContext.getClientIdsWithMessages(); iterator.hasNext();) {
			String clientId = iterator.next();
			
			for(Iterator<FacesMessage> messageIter = facesContext.getMessages(clientId); messageIter.hasNext();) {
				FacesMessage message = messageIter.next();
				
				if(message.getSeverity().getOrdinal() <= minSeverityOrdinal)
					return clientId;
			}
		}
		
		return null;
	}
}