/*
 * Copyright 2010 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ApplicationRenderer extends CoreRenderer {
	
    @Override
	public void encodeBegin(FacesContext fc, UIComponent component) throws IOException {
		ResponseWriter writer = fc.getResponseWriter();
		Application application = (Application) component;
		String themePath = "touch/themes/" + application.getTheme() + "/theme.min.css";
		
		writer.startElement("html", null);
		
		writer.startElement("head", null);
		
		renderTheme(fc, themePath);
		
        //Resources
        ListIterator<UIComponent> iter = (fc.getViewRoot().getComponentResources(fc, "head")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent) iter.next();
            resource.encodeAll(fc);
        }
	
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("TouchFaces = new PrimeFaces.touch.Application({");
		//writer.write("themePath:'" + themeRealPath + "'");
		if(application.getIcon() != null)
			writer.write("icon:'" + getResourceURL(fc, application.getIcon()) + "'");
		
		writer.write("});");
		
		writer.endElement("script");
		
		UIComponent meta = application.getFacet("meta");
		if(meta != null) {
			renderChild(fc, meta);
		}
		
		writer.endElement("head");
		
		writer.startElement("body", null);
	}

    @Override
	public void encodeEnd(FacesContext fc, UIComponent component) throws IOException {
		ResponseWriter writer = fc.getResponseWriter();
				
		writer.endElement("body");
		writer.endElement("html");
	}

    protected void renderTheme(FacesContext fc, String cssPath) throws IOException{
        ResponseWriter writer = fc.getResponseWriter();

        writer.startElement("link", null);
        writer.writeAttribute("rel", "stylesheet", null);
        writer.writeAttribute("type", "text/css", null);
        writer.writeAttribute("href", getResourceRequestPath(fc, cssPath), null);
        writer.endElement("link");
    }
}