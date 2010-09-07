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
package org.primefaces.component.slider;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class SliderRenderer extends CoreRenderer{
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Slider slider = (Slider) component;
		
		encodeMarkup(facesContext, slider);
		encodeScript(facesContext, slider);
	}
	
	protected void encodeMarkup(FacesContext facesContext, Slider slider) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", clientId , "id");
		if(slider.getStyle() != null) 
			writer.writeAttribute("style", slider.getStyle() , null);
		if(slider.getStyleClass() != null) 
			writer.writeAttribute("class", slider.getStyleClass(), null);
		
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext facesContext, Slider slider) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		String input = getTarget(facesContext, slider, slider.getFor());
		String output = getTarget(facesContext, slider, slider.getDisplay());
		
		int value = 0;
		String stringValue = ComponentUtils.getStringValueToRender(facesContext, slider);
		if(!isValueBlank(stringValue)) {
			value = Integer.parseInt(stringValue);
		}
		
		writer.startElement("script", slider);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(slider.resolveWidgetVar() + " = new PrimeFaces.widget.Slider('" + clientId + "', {");
		writer.write("value:" + value);
		writer.write(",input:'" + input + "'");
		writer.write(",min:" + slider.getMinValue());
		writer.write(",max:" + slider.getMaxValue());
		writer.write(",animate:" + slider.isAnimate());
		writer.write(",step:" + slider.getStep());
		writer.write(",orientation:'" + slider.getType() + "'");
		
		if(slider.isDisabled()) writer.write(",disabled:true");
		if(output != null) writer.write(",output:'" + output + "'");
		
		writer.write("});");
	
		writer.endElement("script");
	}
	
	protected String getTarget(FacesContext facesContext, Slider slider, String target) {
		if(target == null) {
			return null;
		} else {
			UIComponent targetComponent = slider.findComponent(target);
			
			return targetComponent.getClientId(facesContext);
		}
	}
}