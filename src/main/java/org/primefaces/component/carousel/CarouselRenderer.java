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

public class CarouselRenderer extends CoreRenderer{
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Carousel carousel = (Carousel) component;
		
		if(isPostback(facesContext)) {
			restoreState(facesContext, carousel);
		}
		
		encodeCarouselScript(facesContext, carousel);
		encodeCarouselMarkup(facesContext, carousel);
	}

	private void encodeCarouselMarkup(FacesContext facesContext, Carousel carousel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", getContainerElementId(clientId), null);
		
		writer.startElement("ol", null);
		
		carousel.setRowIndex(-1);
		
		for (int i=0; i < carousel.getRowCount(); i++) {
			carousel.setRowIndex(i);		
			writer.startElement("li", null);
			writer.writeAttribute("id", carousel.getClientId(facesContext), null);
			renderChildren(facesContext, carousel);
			writer.endElement("li");
		}
		
		carousel.setRowIndex(-1); 	//reset and clear
		
		writer.endElement("ol");
		writer.endElement("div");
		
		encodeHiddenStateField(facesContext, getPagerStateHolderId(clientId));
		encodeHiddenStateField(facesContext, getSelectedItemStateHolderId(clientId));
		
		writer.endElement("div");
	}

	private void encodeCarouselScript(FacesContext facesContext, Carousel carousel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		String carouselVar = createUniqueWidgetVar(facesContext, carousel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.addListener(window, \"load\", function() {\n");
		writer.write(carouselVar + " = new PrimeFaces.widget.Carousel(\"" + getContainerElementId(clientId) + "\", {pagerStateHolder:\"" + getPagerStateHolderId(clientId) + "\"" +
				", selectedItemStateHolder:\"" + getSelectedItemStateHolderId(clientId) + "\"});\n");
		
		if(carousel.isCircular()) writer.write(carouselVar + ".set(\"isCircular\", true);\n");
		if(carousel.isVertical()) writer.write(carouselVar + ".set(\"isVertical\", true);\n");
		if(carousel.getRows() != 0) writer.write(carouselVar + ".set(\"numVisible\"," + carousel.getRows() + ");\n");
		if(carousel.getFirst() != 0) writer.write(carouselVar + ".set(\"firstVisible\"," + carousel.getFirst() + ");\n");
		if(carousel.getAutoPlayInterval() != 0) writer.write(carouselVar + ".set(\"autoPlayInterval\"," + carousel.getAutoPlayInterval() + ");\n");
		if(carousel.getScrollIncrement() != 1) writer.write(carouselVar + ".set(\"scrollIncrement\"," + carousel.getScrollIncrement()+ ");\n");
		if(carousel.getRevealAmount() != 0) writer.write(carouselVar + ".set(\"revealAmount\"," + carousel.getRevealAmount() + ");\n");
		if(carousel.isAnimate()) {
			writer.write(carouselVar + ".set(\"animation\", {speed:" + carousel.getSpeed());
			if(carousel.getEffect() != null)
				writer.write(",effect:YAHOO.util.Easing." + carousel.getEffect() + "});\n");
			else
				writer.write("});\n");
		}
		
		writer.write(carouselVar + ".render();\n");
		
		if(carousel.getSelectedItem() != 0) {
			writer.write(carouselVar + ".set(\"selectedItem\"," + carousel.getSelectedItem() + ");\n");
		}
		
		writer.write(carouselVar + ".show();\n");
		
		if(carousel.isCircular() && carousel.getAutoPlayInterval() != 0) {
			writer.write(carouselVar + ".startAutoPlay();\n");
		}
			
		writer.write("});\n");
		
		writer.endElement("script");
	}
	
	public boolean getRendersChildren() {
		return true;
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Do Nothing
	}
	
	private void restoreState(FacesContext facesContext, Carousel carousel) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = carousel.getClientId(facesContext);
		
		String firstValue = params.get(getPagerStateHolderId(clientId));
		String selectedItemIndex = params.get(getSelectedItemStateHolderId(clientId));
		
		if(firstValue != null && !firstValue.equals(""))
			carousel.setFirst(Integer.parseInt(firstValue));
		
		if(selectedItemIndex != null && !selectedItemIndex.equals(""))
			carousel.setSelectedItem(Integer.parseInt(selectedItemIndex));
	}
	
	private String getContainerElementId(String clientId) {
		return clientId + ":container";
	}
	
	private String getPagerStateHolderId(String clientId) {
		return clientId + ":pagerstate";
	}
	
	private String getSelectedItemStateHolderId(String clientId) {
		return clientId + ":selecteditem";
	}
	
	private void encodeHiddenStateField(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
		writer.endElement("input");
	}
}