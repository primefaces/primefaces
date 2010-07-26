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
package org.primefaces.touch.component.application;

import java.io.IOException;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;

public class ApplicationRenderer extends CoreRenderer {
	
	private static final Logger logger = Logger.getLogger(ApplicationRenderer.class.getName());

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Application application = (Application) component;
		
		String themeRelativePath = "/jquery/plugins/jqtouch/themes/" + application.getTheme();
		String themeRealPath = ResourceUtils.getResourceURL(facesContext, themeRelativePath);
		
		writer.startElement("html", null);
		
		writer.startElement("head", null);
		
		renderCSSDependency(facesContext, "/jquery/plugins/jqtouch/jqtouch.min.css");
		renderCSSDependency(facesContext, themeRelativePath + "/theme.min.css");
		
		ListIterator<UIComponent> iter = (facesContext.getViewRoot().getComponentResources(facesContext, "head")).listIterator();
		while(iter.hasNext()) {
			UIComponent resource = (UIComponent)iter.next();
			resource.encodeAll(facesContext);
		}
	
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("TouchFaces = new PrimeFaces.touch.Application({");
		writer.write("themePath:'" + themeRealPath + "'");
		if(application.getIcon() != null)
			writer.write(",icon:'" + getResourceURL(facesContext, application.getIcon()) + "'");
		
		writer.write("});\n");
		
		writer.endElement("script");
		
		UIComponent meta = application.getFacet("meta");
		if(meta != null) {
			renderChild(facesContext, meta);
		}
		
		writer.endElement("head");
		
		writer.startElement("body", null);
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
				
		writer.endElement("body");
		writer.write("\n");
		writer.endElement("html");
	}
}