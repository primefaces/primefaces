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

public class FocusRenderer extends CoreRenderer {

	private final static Map<String, Integer> severityOrdinals = new HashMap<String, Integer>();
	
	static {
		severityOrdinals.put("info", FacesMessage.SEVERITY_INFO.getOrdinal());
		severityOrdinals.put("warn", FacesMessage.SEVERITY_WARN.getOrdinal());
		severityOrdinals.put("error", FacesMessage.SEVERITY_ERROR.getOrdinal());
		severityOrdinals.put("fatal", FacesMessage.SEVERITY_FATAL.getOrdinal());
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Focus focus = (Focus) component;
		ResponseWriter writer = context.getResponseWriter();
		
		//Dummy markup for ajax update
		writer.startElement("span", focus);
		writer.writeAttribute("id", focus.getClientId(context), "id");
		writer.endElement("span");
		
		writer.startElement("script", focus);
		writer.writeAttribute("type", "text/javascript", null);
		
		if(focus.getFor() != null) {
			encodeExplicitFocus(context, focus);
		} else {
			encodeImplicitFocus(context, focus);
		}
		
		writer.endElement("script");
	}

	protected void encodeExplicitFocus(FacesContext context, Focus focus) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent forComponent = focus.findComponent(focus.getFor());
		
		if(forComponent == null) {
			throw new FacesException("Cannot find component '" + focus.getFor() + "' in view.");
		}
		String clientId = forComponent.getClientId(context);
		
		writer.write("$(function(){");
		writer.write("PrimeFaces.focus('" + clientId +"');");
		writer.write("});");
	}
	
	protected void encodeImplicitFocus(FacesContext context, Focus focus) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String invalidClientId = findFirstInvalidComponentClientId(context, focus);

        writer.write("$(function(){");
		
		if(invalidClientId != null){
            writer.write("PrimeFaces.focus('" + invalidClientId +"');");
		}
        else if(focus.getContext() != null) {		
            UIComponent focusContext = focus.findComponent(focus.getContext());

            if(context == null)
                throw new FacesException("Cannot find component " + focus.getContext() + " in view");
            else {
                writer.write("PrimeFaces.focus(null, '" + focusContext.getClientId(context) +"');");
            }
		} 
        else {
            writer.write("PrimeFaces.focus();");
        }

        writer.write("});");
	}
	
	protected String findFirstInvalidComponentClientId(FacesContext context, Focus focus) {
		int minSeverityOrdinal = severityOrdinals.get(focus.getMinSeverity());
		
		for(Iterator<String> iterator = context.getClientIdsWithMessages(); iterator.hasNext();) {
			String clientId = iterator.next();
			
			for(Iterator<FacesMessage> messageIter = context.getMessages(clientId); messageIter.hasNext();) {
				FacesMessage message = messageIter.next();
				
				if(message.getSeverity().getOrdinal() <= minSeverityOrdinal)
					return clientId;
			}
		}
		
		return null;
	}
}