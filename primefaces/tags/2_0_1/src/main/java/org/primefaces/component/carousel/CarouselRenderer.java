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
package org.primefaces.component.carousel;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class CarouselRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		Carousel carousel = (Carousel) component;
		String clientId = carousel.getClientId(facesContext);
		
		String firstValue = params.get(clientId + "_first");
		String selectedItemIndex = params.get(clientId + "_selected");
		
		if(!isValueBlank(firstValue))
			carousel.setFirst(Integer.parseInt(firstValue));
		
		if(!isValueBlank(selectedItemIndex))
			carousel.setSelectedItem(Integer.parseInt(selectedItemIndex));
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Carousel carousel = (Carousel) component;
		
		encodeMarkup(facesContext, carousel);
		encodeScript(facesContext, carousel);
	}
	
	private void encodeScript(FacesContext facesContext, Carousel carousel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		String carouselVar = createUniqueWidgetVar(facesContext, carousel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(carouselVar + " = new PrimeFaces.widget.Carousel('" + clientId + "', {");
		writer.write("isCircular:" + carousel.isCircular());
		
		if(carousel.isVertical()) writer.write(",isVertical:" + carousel.isVertical());
		if(carousel.getRows() != 0) writer.write(",numVisible:" + carousel.getRows());
		if(carousel.getAutoPlayInterval() != 0) writer.write(",autoPlayInterval:" + carousel.getAutoPlayInterval());
		if(carousel.getScrollIncrement() != 1) writer.write(",scrollIncrement:" + carousel.getScrollIncrement());
		if(carousel.getRevealAmount() != 0) writer.write(",revealAmount:" + carousel.getRevealAmount());
		
		if(carousel.isAnimate()) {
			writer.write(",animation:{speed:" + carousel.getSpeed());
			if(carousel.getEffect() != null) {
				writer.write(",effect:YAHOO.util.Easing." + carousel.getEffect());
			}
			
			writer.write("}");
		}
		
		writer.write("});");
		
		writer.write(carouselVar + ".render();\n");

		if(carousel.isCircular() && carousel.getAutoPlayInterval() != 0) {
			writer.write(carouselVar + ".startAutoPlay();\n");
		}
		
		if(carousel.getFirst() != 0) writer.write(carouselVar + ".set('firstVisible'," + carousel.getFirst() + ");\n");
		if(carousel.getSelectedItem() != 0) writer.write(carouselVar + ".set('selectedItem'," + carousel.getSelectedItem() + ");\n");
		if(carousel.getPagerPrefix() != null) writer.write(carouselVar + ".STRINGS.PAGER_PREFIX_TEXT = '" + carousel.getPagerPrefix() + "';\n"); 
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext facesContext, Carousel carousel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		
		writer.startElement("ol", null);
		
		carousel.setRowIndex(-1);
		
		for (int i=0; i < carousel.getRowCount(); i++) {
			carousel.setRowIndex(i);
			
			writer.startElement("li", null);
			renderChildren(facesContext, carousel);
			writer.endElement("li");
		}
		
		carousel.setRowIndex(-1); 	//reset and clear
		
		writer.endElement("ol");
		
		writer.endElement("div");
		
		encodeHiddenStateField(facesContext, clientId + "_first");
		encodeHiddenStateField(facesContext, clientId + "_selected");
		
		writer.endElement("div");
	}
	
	private void encodeHiddenStateField(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
		writer.endElement("input");
	}

	public boolean getRendersChildren() {
		return true;
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}
}