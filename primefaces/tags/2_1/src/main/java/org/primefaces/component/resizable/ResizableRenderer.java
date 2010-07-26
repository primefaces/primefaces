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
package org.primefaces.component.resizable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ResizableRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Resizable resizable = (Resizable) component;
		String resizableVar = createUniqueWidgetVar(facesContext, component);
		String parentClientId = resizable.getParent().getClientId(facesContext);

		writer.startElement("script", resizable);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(document).ready(function(){");
		
		writer.write(resizableVar + " = new YAHOO.util.Resize('"+ parentClientId + "',{");
		writer.write("proxy:" + resizable.isProxy());
		
		if(resizable.isStatus()) writer.write(",status:" + resizable.isStatus());
		if(resizable.isKnobHandles()) writer.write(",knobHandles: true");
		if(resizable.isGhost()) writer.write(",ghost: true");
		
		if(resizable.isAnimate())
			encodeAnimation(facesContext, resizable);
		
		if(resizable.getHandles() != null)
			encodeHandles(facesContext, resizable);
		
		encodeBoundaries(facesContext, resizable);
		
		writer.write("});});\n");
		
		writer.endElement("script");
	}
	
	private void encodeBoundaries(FacesContext facesContext, Resizable resizable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String[] passThruBoundaries = new String[]{"maxHeight", "maxWidth", "minHeight", "minWidth"};
		
		for(String boundaryAttribute : passThruBoundaries) {
			Object value = resizable.getAttributes().get(boundaryAttribute);
			if(shouldRenderAttribute(value))
				writer.write("," + boundaryAttribute + ":" + value.toString());
		}
	}
	
	private void encodeAnimation(FacesContext facesContext, Resizable resizable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(",animate:true");
		writer.write(",animateDuration:" + resizable.getAnimateDuration());
		writer.write(",animateEasing:YAHOO.util.Easing." + resizable.getEffect());
	}
	
	private void encodeHandles(FacesContext facesContext, Resizable resizable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(resizable.getHandles().equals("all"))
			writer.write(",handles:\"all\"");
		else
			writer.write(",handles:" + convertHandlesToJSArray(resizable.getHandles()) + "");
	}

	private String convertHandlesToJSArray(String value) {
		String[] tokens = value.split(",");
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		
		for(String s : tokens) {
			buffer.append("\"");
			buffer.append(s);
			buffer.append("\"");
		}
		
		buffer.append("]");
		
		return buffer.toString();
	}
}