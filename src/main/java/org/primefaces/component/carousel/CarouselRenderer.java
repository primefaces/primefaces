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
		String firstParam = clientId + "_first";
		
		if(params.containsKey(firstParam)) {
			carousel.setFirst(Integer.parseInt(params.get(firstParam)));
		}
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
		String widgetVar = carousel.resolveWidgetVar();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.Carousel('" + clientId + "', {");
		writer.write("isCircular:" + carousel.isCircular());
		
		if(carousel.getFirst() != 0) writer.write(",firstVisible:" + carousel.getFirst());
		if(carousel.isVertical()) writer.write(",isVertical:" + carousel.isVertical());
		if(carousel.getRows() != 0) writer.write(",numVisible:" + carousel.getRows());
		if(carousel.getAutoPlayInterval() != 0) writer.write(",autoPlayInterval:" + carousel.getAutoPlayInterval());
		if(carousel.getRevealAmount() != 0) writer.write(",revealAmount:" + carousel.getRevealAmount());
		writer.write(",pagerFormat:'" + carousel.getPagerFormat() + "'"); 
		writer.write(",maxButton:" + carousel.getMaxButton());
		writer.write(",speed:" + carousel.getSpeed());
		writer.write(",animate:" + carousel.isAnimate());
		writer.write(",effect:'" + carousel.getEffect() + "'");
		writer.write(",easing:'" + carousel.getEasing() + "'");

		writer.write("});");
		
		if(carousel.isCircular() && carousel.getAutoPlayInterval() != 0) {
			writer.write(widgetVar + ".startAutoPlay();");
		}		
		
		writer.endElement("script");
	}

    
    protected void encodeCarouselFooter(FacesContext facesContext, Carousel carousel) throws IOException {
        
        if(carousel.getFacet("footer") == null && carousel.getFooterText() == null)
          return;
      
        ResponseWriter writer = facesContext.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-carousel-footer ui-widget-header ui-corner-all", null);

        //Footer content
        UIComponent facet = carousel.getFacet("footer");
        String text = carousel.getFooterText();
        if(facet != null) {
            facet.encodeAll(facesContext);
        } else if(text != null) {
            writer.write(text);
        }

        writer.endElement("div");
    }
    
    
    protected void encodeCarouselHeader(FacesContext facesContext, Carousel carousel) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        //header container
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-widget-header ui-corner-all ui-carousel-header", null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-carousel-header-content", null);
        
        //Footer content
        UIComponent facet = carousel.getFacet("header");
        String text = carousel.getHeaderText();
        if(facet != null) {
            facet.encodeAll(facesContext);
        } else if(text != null) {
            writer.write(text);
        }
        
        writer.endElement("div");
        
        //next button
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-carousel-button ui-carousel-next-button ui-icon" + (carousel.isVertical() ? " ui-icon-circle-triangle-s" : " ui-icon-circle-triangle-e"), null);
        writer.endElement("span");

        //prev button
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-carousel-button ui-carousel-prev-button ui-icon" + (carousel.isVertical() ? " ui-icon-circle-triangle-n" : " ui-icon-circle-triangle-w"), null);
        writer.endElement("span");
        writer.endElement("div");
    }
    
    
    
    
	protected void encodeMarkup(FacesContext facesContext, Carousel carousel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		String itemStyleClass = "ui-widget-content ui-corner-all";
		if(carousel.getItemStyleClass() != null) itemStyleClass += " " + carousel.getItemStyleClass(); 
		
        //wrapper
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "ui-widget ui-widget-content ui-corner-all ui-carousel", null);
        
        if(carousel.getStyle() != null) 
			writer.writeAttribute("style", carousel.getStyle(), "style");

        //header
        encodeCarouselHeader(facesContext, carousel);
        
        
        //data container
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
        writer.writeAttribute("class", (carousel.isVertical() ? "ui-carousel-vertical-container " : "") + "ui-widget ui-carousel-container", null);
		
        //data list
		writer.startElement("ol", null);
		
		if(carousel.getVar() != null) {
			carousel.setRowIndex(-1);
			
			for (int i=0; i < carousel.getRowCount(); i++) {
				carousel.setRowIndex(i);
				
				writer.startElement("li", null);
				writer.writeAttribute("class", itemStyleClass, "itemStyleClass");
				if(carousel.getItemStyle() != null) 
					writer.writeAttribute("style", carousel.getItemStyle(), "itemStyle");
				
				renderChildren(facesContext, carousel);
				
				writer.endElement("li");
			}
			
			carousel.setRowIndex(-1); 	//reset and clear
			
		} else {
			for(UIComponent kid : carousel.getChildren()) {
				writer.startElement("li", null);
				writer.writeAttribute("class", itemStyleClass, "itemStyleClass");
				if(carousel.getItemStyle() != null) 
					writer.writeAttribute("style", carousel.getItemStyle(), "itemStyle");
				
				renderChild(facesContext, kid);
				
				writer.endElement("li");
			}
		}
		
		writer.endElement("ol");
		
		writer.endElement("div");
		
        //footer
        encodeCarouselFooter(facesContext, carousel);
        
		encodeHiddenStateField(facesContext, clientId + "_first", String.valueOf(carousel.getFirst()));
		
		writer.endElement("div");
	}
	
	private void encodeHiddenStateField(FacesContext facesContext, String id, String value) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", value, null);
		writer.endElement("input");
	}

	public boolean getRendersChildren() {
		return true;
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}
}