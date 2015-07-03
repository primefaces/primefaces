/*
 * Copyright 2009-2014 PrimeTek.
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
import org.primefaces.util.WidgetBuilder;

public class CarouselRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		Carousel carousel = (Carousel) component;
		String clientId = carousel.getClientId(context);
		String param = clientId + "_page";
		
		if(params.containsKey(param)) {
			carousel.setFirstVisible(Integer.parseInt(params.get(param)) * carousel.getNumVisible());
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Carousel carousel = (Carousel) component;
        		
		encodeMarkup(context, carousel);
		encodeScript(context, carousel);
	}
	
	private void encodeScript(FacesContext context, Carousel carousel) throws IOException{
		String clientId = carousel.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Carousel", carousel.resolveWidgetVar(), clientId);
        
        wb.attr("firstVisible", carousel.getFirstVisible(), 0)
            .attr("circular", carousel.isCircular(), false)
            .attr("vertical", carousel.isVertical(), false)
            .attr("numVisible", carousel.getNumVisible(), 3)
            .attr("autoplayInterval", carousel.getAutoPlayInterval(), 0)
            .attr("pageLinks", carousel.getPageLinks(), 3)
            .attr("effect", carousel.getEffect(), null)
            .attr("effectDuration", carousel.getEffectDuration(), Integer.MIN_VALUE)
            .attr("easing", carousel.getEasing(), null)
            .attr("responsive", carousel.isResponsive(), false)
            .attr("breakpoint", carousel.getBreakpoint(), 560);
		
        wb.finish();
	}
    
    protected void encodeMarkup(FacesContext context, Carousel carousel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = carousel.getClientId(context);
        String style = carousel.getStyle();
        String styleClass = carousel.getStyleClass();
        styleClass = (styleClass == null) ? Carousel.CONTAINER_CLASS : Carousel.CONTAINER_CLASS + " " + styleClass;

        //container
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) 
            writer.writeAttribute("style", style, "style");

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
         writer.writeAttribute("class", Carousel.ITEMS_CLASS, null);
		
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
        String clientId = carousel.getClientId(context);
        int numVisible = carousel.getNumVisible();
        String var = carousel.getVar();
        int rowCount = carousel.getRowCount();
        int renderedChildCount = carousel.getRenderedChildCount();
        int pageCount = var != null ? (int) (Math.ceil(rowCount / (1d * numVisible))) : renderedChildCount;
        int itemCount = var != null ? rowCount : renderedChildCount;

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
        if(pageCount <= carousel.getPageLinks())
            encodePageLinks(context, carousel, pageCount);
        else
            encodeDropDown(context, carousel, clientId + "_dropdown", Carousel.DROPDOWN_CLASS, pageCount);
        
        if(carousel.isResponsive()) {
            encodeDropDown(context, carousel, clientId + "_mobiledropdown", Carousel.MOBILE_DROPDOWN_CLASS, itemCount);
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
    
    protected void encodeDropDown(FacesContext context, Carousel carousel, String name, String styleClass, int pageCount) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String template = carousel.getDropdownTemplate();
        
        writer.startElement("select", null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("class", styleClass, null);
        
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
        String id = carousel.getClientId(context) + "_page";
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
		writer.writeAttribute("value", carousel.getPage(), null);
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