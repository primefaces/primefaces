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
import org.primefaces.util.WidgetBuilder;

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
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("Slider", slider.resolveWidgetVar(), clientId, false)
            .attr("value", ComponentUtils.getValueToRender(context, input))
            .attr("input", input.getClientId(context))
            .attr("min", slider.getMinValue())
            .attr("max", slider.getMaxValue())
            .attr("animate", slider.isAnimate())
            .attr("step", slider.getStep())
            .attr("orientation", slider.getType())
            .attr("disabled", slider.isDisabled(), false)
            .callback("onSlideStart", "function(event,ui)", slider.getOnSlideStart())
            .callback("onSlide", "function(event,ui)", slider.getOnSlide())
            .callback("onSlideEnd", "function(event,ui)", slider.getOnSlideEnd());
        
        if(output != null) {
            wb.attr("output", output.getClientId(context));
        }
        
        encodeClientBehaviors(context, slider, wb);

        startScript(writer, clientId);
        writer.write(wb.build());
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