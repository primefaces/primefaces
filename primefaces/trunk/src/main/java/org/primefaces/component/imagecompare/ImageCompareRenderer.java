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
package org.primefaces.component.imagecompare;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ImageCompareRenderer extends CoreRenderer {
	
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ImageCompare compare = (ImageCompare) component;
		
		encodeMarkup(context, compare);
		encodeScript(context, compare);
	}
	
	protected void encodeScript(FacesContext context, ImageCompare compare) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = compare.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('ImageCompare','" + compare.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",handle:'" + getResourceRequestPath(context, "imagecompare/handle.gif") + "'");
        writer.write(",lt:'" + getResourceRequestPath(context, "imagecompare/lt-small.png") + "'");
        writer.write(",rt:'" + getResourceRequestPath(context, "imagecompare/rt-small.png") + "'");
		writer.write("},'imagecompare');");
        
		endScript(writer);
	}
	
	protected void encodeMarkup(FacesContext context, ImageCompare compare) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("div", compare);
		writer.writeAttribute("id", compare.getClientId(context), "id");
		renderImage(context, compare, "before", compare.getLeftImage());
		renderImage(context, compare, "fter", compare.getRightImage());
		writer.endElement("div");
	}

	
	private void renderImage(FacesContext context, ImageCompare compare, String type, String src) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("div", compare);
		
		writer.startElement("img", null);
		writer.writeAttribute("alt", type, null);
		writer.writeAttribute("src", getResourceURL(context, src), null);
		writer.writeAttribute("width", compare.getWidth(), null);
		writer.writeAttribute("height", compare.getHeight(), null);
		writer.endElement("img");
		
		writer.endElement("div");
	}
}