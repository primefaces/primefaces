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
package org.primefaces.component.carousel;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class CarouselRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		Carousel carousel = (Carousel) component;
		String clientId = carousel.getClientId(context);
		String firstParam = clientId + "_first";
		
		if(params.containsKey(firstParam)) {
			carousel.setFirstVisible(Integer.parseInt(params.get(firstParam)));
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Carousel carousel = (Carousel) component;
        
        //workaround for proper event processing
        carousel.setFirstVisible(carousel.getFirst());
        carousel.setFirst(0);
		
		encodeMarkup(context, carousel);
		encodeScript(context, carousel);
	}
	
	private void encodeScript(FacesContext context, Carousel carousel) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = carousel.getClientId(context);
		
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Carousel','" + carousel.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
	
		if(carousel.getFirstVisible() != 0) writer.write(",firstVisible:" + carousel.getFirstVisible());
		if(carousel.isCircular()) writer.write(",isCircular:" + carousel.isCircular());
		if(carousel.isVertical()) writer.write(",vertical:true");
		if(carousel.getRows() != 0) writer.write(",numVisible:" + carousel.getRows());
		if(carousel.getAutoPlayInterval() != 0) writer.write(",autoPlayInterval:" + carousel.getAutoPlayInterval());
        if(carousel.getDropdownTemplate() != null) writer.write(",dropDownTemplate:'" + carousel.getDropdownTemplate() + "'");
        if(carousel.getEffectDuration() != Integer.MIN_VALUE) writer.write(",effectDuration:" + carousel.getEffectDuration());
        if(carousel.getPageLinks() != 3) writer.write(",pageLinks:" + carousel.getPageLinks());
        if(carousel.getEffect() != null) writer.write(",effect:'" + carousel.getEffect() + "'");
        if(carousel.getEasing() != null) writer.write(",easing:'" + carousel.getEasing() + "'");

        writer.write("});");
			
		endScript(writer);
	}
    
    protected void encodeMarkup(FacesContext context, Carousel carousel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = carousel.getClientId(context);

        //container
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", Carousel.CONTAINER_CLASS, null);
        if(carousel.getStyle() != null) 
            writer.writeAttribute("style", carousel.getStyle(), "style");

        encodeHeader(context, carousel);
        encodeContent(context, carousel);
        encodeFooter(context, carousel);

		encodeStateField(context, carousel);
		
		writer.endElement("div");
	}
    
    protected void encodeContent(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        String itemStyleClass = carousel.getItemStyleClass();
        itemStyleClass = itemStyleClass == null ? Carousel.ITEM_CLASS : Carousel.ITEM_CLASS + " " + itemStyleClass;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", carousel.isVertical() ? Carousel.VERTICAL_VIEWPORT_CLASS : Carousel.VIEWPORT_CLASS, null);

		writer.startElement("ul", null);
		
		if(carousel.getVar() != null) {		
			for(int i=0; i < carousel.getRowCount(); i++) {
				carousel.setRowIndex(i);
				
				writer.startElement("li", null);
				writer.writeAttribute("class", itemStyleClass, "itemStyleClass");
				if(carousel.getItemStyle() != null) 
					writer.writeAttribute("style", carousel.getItemStyle(), "itemStyle");
				
				renderChildren(context, carousel);
				
				writer.endElement("li");
			}
			
			carousel.setRowIndex(-1); 	//clear
			
		} 
        else {
			for(UIComponent kid : carousel.getChildren()) {
                if(kid.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", itemStyleClass, "itemStyleClass");
                    if(carousel.getItemStyle() != null) 
                        writer.writeAttribute("style", carousel.getItemStyle(), "itemStyle");

                    renderChild(context, kid);

                    writer.endElement("li");
                }
			}
		}
		
		writer.endElement("ul");
		
		writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Carousel carousel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean vertical = carousel.isVertical();
        int rows = carousel.getRows();
        int numVisible = rows == 0 ? 3 : rows;
        int pageCount = carousel.getVar() != null ? (int) (Math.ceil(carousel.getRowCount() / (1d * numVisible))) : carousel.getRenderedChildCount();

        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.HEADER_CLASS, null);

        //title
        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.HEADER_TITLE_CLASS, null);
        
        UIComponent facet = carousel.getFacet("header");
        String text = carousel.getHeaderText();
        if(facet != null)
            facet.encodeAll(context);
        else if(text != null)
            writer.write(text);
        
        writer.endElement("div");
        
        //next button
        writer.startElement("span", null);
        writer.writeAttribute("class", vertical ? Carousel.VERTICAL_NEXT_BUTTON : Carousel.HORIZONTAL_NEXT_BUTTON, null);
        writer.endElement("span");

        //prev button
        writer.startElement("span", null);
        writer.writeAttribute("class", vertical ? Carousel.VERTICAL_PREV_BUTTON : Carousel.HORIZONTAL_PREV_BUTTON, null);
        writer.endElement("span");
        
        //pageLinks
        if(pageCount <= carousel.getPageLinks()) {
            encodePageLinks(context, carousel, pageCount);
        } 
        else {
            encodeDropDown(context, carousel, pageCount);
        }

        writer.endElement("div");
    }
    
    protected void encodePageLinks(FacesContext context, Carousel carousel, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.PAGE_LINKS_CONTAINER_CLASS, null);
        
        for(int i = 0; i < pageCount; i++) {
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Carousel.PAGE_LINK_CLASS, null);
            writer.endElement("a");
        }
        
        writer.endElement("div");
    }
    
     protected void encodeDropDown(FacesContext context, Carousel carousel, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String template = carousel.getDropdownTemplate();
        
        writer.startElement("select", null);
        writer.writeAttribute("name", carousel.getClientId(context) + "_dropdown", null);
        writer.writeAttribute("class", Carousel.DROPDOWN_CLASS, null);
        
        for(int i = 0; i < pageCount; i++) {
            writer.startElement("option", null);
            writer.writeAttribute("value", i + 1, null);
            writer.write(template.replaceAll("\\{page\\}", String.valueOf(i + 1)));
            writer.endElement("option");
        }
        
        writer.endElement("select");
    }
    
    protected void encodeFooter(FacesContext context, Carousel carousel) throws IOException {
        UIComponent facet = carousel.getFacet("footer");
        String text = carousel.getFooterText();
        
        if(facet == null && text == null)
          return;
      
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Carousel.FOOTER_CLASS, null);

        if(facet != null)
            facet.encodeAll(context);
        else if(text != null)
            writer.write(text);

        writer.endElement("div");
    }

	protected void encodeStateField(FacesContext context, Carousel carousel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String id = carousel.getClientId(context) + "_first";
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", carousel.getFirst(), null);
		writer.endElement("input");
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
	
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}   
}