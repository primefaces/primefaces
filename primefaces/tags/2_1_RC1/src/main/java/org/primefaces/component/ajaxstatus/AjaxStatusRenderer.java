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
package org.primefaces.component.ajaxstatus;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class AjaxStatusRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AjaxStatus status = (AjaxStatus) component;

		encodeMarkup(context, status);
		encodeScript(context, status);
	}

	protected void encodeScript(FacesContext facesContext, AjaxStatus status) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, status);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(var + " = new PrimeFaces.widget.AjaxStatus('" + clientId + "');\n");
		
		encodeCallback(facesContext, status, var, "ajaxSend", "onprestart", "prestart");
		encodeCallback(facesContext, status, var, "ajaxStart", "onstart", "start");
		encodeCallback(facesContext, status, var, "ajaxError", "onerror", "error");
		encodeCallback(facesContext, status, var, "ajaxSuccess", "onsuccess", "success");
		encodeCallback(facesContext, status, var, "ajaxComplete", "oncomplete", "complete");

		writer.endElement("script");
	}
	
	private void encodeCallback(FacesContext facesContext, AjaxStatus status, String var, String event, String callback, String facetName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		String fn = (String) status.getAttributes().get(callback);
		
		if(fn != null)
			writer.write(var + ".bindCallback('" + event + "',function(){" + fn + "});\n");
		else if(status.getFacet(facetName) != null)
			writer.write(var + ".bindFacet('" + event + "', '" + clientId + "_" + facetName + "');\n");
	}

	protected void encodeMarkup(FacesContext facesContext, AjaxStatus status) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		if(status.getStyle() != null)  writer.writeAttribute("style", status.getStyle(), "style");
		if(status.getStyleClass() != null)  writer.writeAttribute("class", status.getStyleClass(), "styleClass");
		
		for(String facetName : AjaxStatus.FACETS) {
			UIComponent facet = status.getFacet(facetName);
			if(facet != null) {
				writer.startElement("div", null);
				writer.writeAttribute("id", clientId + "_" + facetName, null);
				writer.writeAttribute("style", "display:none", null);
				
				renderChild(facesContext, facet);	
				
				writer.endElement("div");
			}
		}
		
		writer.endElement("div");
	}
}