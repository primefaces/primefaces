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
package org.primefaces.component.slider;

import java.io.IOException;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class SliderRenderer extends CoreRenderer{

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Slider slider = (Slider) component;
		
		encodeMarkup(facesContext, slider);
		encodeScript(facesContext, slider);
	}
	
	protected void encodeMarkup(FacesContext context, Slider slider) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = slider.getClientId(context);
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", clientId , "id");
		if(slider.getStyle() != null)  writer.writeAttribute("style", slider.getStyle() , null);
		if(slider.getStyleClass() != null) writer.writeAttribute("class", slider.getStyleClass(), null);
		
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, Slider slider) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = slider.getClientId(context);
		UIComponent input = getTarget(context, slider, slider.getFor());
		UIComponent output = getTarget(context, slider, slider.getDisplay());

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Slider','" + slider.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",value:" + ComponentUtils.getValueToRender(context, input));
		writer.write(",input:'" + input.getClientId(context) + "'");
		writer.write(",min:" + slider.getMinValue());
		writer.write(",max:" + slider.getMaxValue());
		writer.write(",animate:" + slider.isAnimate());
		writer.write(",step:" + slider.getStep());
		writer.write(",orientation:'" + slider.getType() + "'");
		
		if(slider.isDisabled()) writer.write(",disabled:true");
		if(output != null) writer.write(",output:'" + output.getClientId(context) + "'");
        if(slider.getOnSlideStart() != null) writer.write(",onSlideStart:function(event, ui) {" + slider.getOnSlideStart() + "}");
        if(slider.getOnSlide() != null) writer.write(",onSlide:function(event, ui) {" + slider.getOnSlide() + "}");
        if(slider.getOnSlideEnd() != null) writer.write(",onSlideEnd:function(event, ui) {" + slider.getOnSlideEnd() + "}");

		encodeClientBehaviors(context, slider);
                
		writer.write("});");
	
		endScript(writer);
	}
	
	protected UIComponent getTarget(FacesContext context, Slider slider, String target) {
		if(target == null) {
			return null;
		} else {
			UIComponent targetComponent = slider.findComponent(target);
            if(targetComponent == null) {
                throw new FacesException("Cannot find slider target component '" + target + "' in view");
            }
			
			return targetComponent;
		}
	}
}