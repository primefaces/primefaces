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
package org.primefaces.component.imagecompare;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;

public class ImageCompareRenderer extends CoreRenderer {
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ImageCompare compare = (ImageCompare) component;
		
		encodeMarkup(facesContext, compare);
		encodeScript(facesContext, compare);
	}
	
	private void encodeScript(FacesContext facesContext, ImageCompare compare) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = compare.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(PrimeFaces.escapeClientId('" + clientId + "')).beforeAfter({");
		writer.write("imagePath:'" + ResourceUtils.getResourceURL(facesContext, "/jquery/plugins/imagecompare/")  + "'");
		writer.write(",showFullLinks : false");
		writer.write("});");
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, ImageCompare compare) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", compare);
		writer.writeAttribute("id", compare.getClientId(facesContext), "id");
		renderImage(facesContext, compare, "before", compare.getLeftImage());
		renderImage(facesContext, compare, "fter", compare.getRightImage());
		writer.endElement("div");
	}

	
	private void renderImage(FacesContext facesContext, ImageCompare compare, String type, String src) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", compare);
		
		writer.startElement("img", null);
		writer.writeAttribute("alt", type, null);
		writer.writeAttribute("src", getResourceURL(facesContext, src), null);
		writer.writeAttribute("width", compare.getWidth(), null);
		writer.writeAttribute("height", compare.getHeight(), null);
		writer.endElement("img");
		
		writer.endElement("div");
	}
}