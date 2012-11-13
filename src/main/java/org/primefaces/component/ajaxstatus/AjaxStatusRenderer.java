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
package org.primefaces.component.ajaxstatus;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class AjaxStatusRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AjaxStatus status = (AjaxStatus) component;

		encodeMarkup(context, status);
		encodeScript(context, status);
	}

	protected void encodeScript(FacesContext context, AjaxStatus status) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = status.getClientId(context);
		String widgetVar = status.resolveWidgetVar();
		
        startScript(writer, clientId);
		
        writer.write("PrimeFaces.cw('AjaxStatus','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("});");
		
		encodeCallback(context, status, widgetVar, "ajaxSend", "onprestart", AjaxStatus.PRESTART_FACET);
		encodeCallback(context, status, widgetVar, "ajaxStart", "onstart", AjaxStatus.START_FACET);
		encodeCallback(context, status, widgetVar, "ajaxError", "onerror", AjaxStatus.ERROR_FACET);
		encodeCallback(context, status, widgetVar, "ajaxSuccess", "onsuccess", AjaxStatus.SUCCESS_FACET);
		encodeCallback(context, status, widgetVar, "ajaxComplete", "oncomplete", AjaxStatus.COMPLETE_FACET);

		endScript(writer);
	}
	
	protected void encodeCallback(FacesContext context, AjaxStatus status, String var, String event, String callback, String facetName) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String fn = (String) status.getAttributes().get(callback);
		
		if(fn != null)
			writer.write(var + ".bindCallback('" + event + "',function(){" + fn + "});");
		else if(status.getFacet(facetName) != null)
			writer.write(var + ".bindFacet('" + event + "', '" + facetName + "');");
	}

	protected void encodeMarkup(FacesContext context, AjaxStatus status) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = status.getClientId(context);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		if(status.getStyle() != null)  writer.writeAttribute("style", status.getStyle(), "style");
		if(status.getStyleClass() != null)  writer.writeAttribute("class", status.getStyleClass(), "styleClass");
		
		for(String facetName : AjaxStatus.FACETS) {
			UIComponent facet = status.getFacet(facetName);

            if(facet != null) {
                encodeFacet(context, clientId, facet, facetName, true);
            }
		}

        //Default facet
        UIComponent defaultFacet = status.getFacet(AjaxStatus.DEFAULT_FACET);
        if(defaultFacet != null) {
            encodeFacet(context, clientId, defaultFacet, AjaxStatus.DEFAULT_FACET, false);
        }
		
		writer.endElement("div");
	}

    protected void encodeFacet(FacesContext facesContext, String clientId, UIComponent facet, String facetName, boolean hidden) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_" + facetName, null);
        if(hidden) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChild(facesContext, facet);

        writer.endElement("div");
    }
}