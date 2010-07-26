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
package org.primefaces.component.tabslider;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;

public class TabSliderRenderer extends CoreRenderer {
	
	private static final Logger logger = Logger.getLogger(TabSliderRenderer.class.getName());
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		logger.info("TabSlider is deprecated, use Carousel instead");
		
		TabSlider slider = (TabSlider) component;
		
		encodeMarkup(facesContext, slider);
		encodeScript(facesContext, slider);
	}
	
	private void encodeScript(FacesContext facesContext, TabSlider slider) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, slider);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.TabSlider('" + clientId + "', {");
		writer.write("easing:'" + slider.getEffect() + "'");
		encodeHeaders(facesContext, slider);
		
		if(slider.getActiveIndex() != 1) writer.write(",activeIndex:" + slider.getActiveIndex());
		if(slider.getEffectDuration() != 600) writer.write(",animationTime:" + slider.getEffectDuration());
		if(!slider.isNavigator()) writer.write(",buildNavigation:false");
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private void encodeHeaders(FacesContext facesContext, TabSlider slider) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(",navHeaders:new Array(");
		
		for(Iterator<UIComponent> kids = slider.getChildren().iterator(); kids.hasNext();) {
			UIComponent kid = kids.next();
			if(kid.isRendered() && kid instanceof Tab) {
				Tab tab = (Tab) kid;
				
				writer.write("'" + tab.getTitle() + "'");
				
				if(kids.hasNext())
					writer.write(",");
			}
		}
		
		writer.write(")");
	}

	private void encodeMarkup(FacesContext facesContext, TabSlider slider) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		String styleClass = slider.getStyleClass() == null ? "pf-tabslider" : "pf-tabslider " + slider.getStyleClass();
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
		if(slider.getStyle() != null)
			writer.writeAttribute("style", slider.getStyle(), null);
		
		writer.startElement("div", slider);
		writer.writeAttribute("class", "wrapper", null);
		
		writer.startElement("ul", slider);
		
		for(UIComponent kid : slider.getChildren()) {
			if(kid.isRendered() && kid instanceof Tab) {
				Tab tab = (Tab) kid;
				
				writer.startElement("li", null);
				renderChild(facesContext, tab);
				writer.endElement("li");
			}
		}
		
		writer.endElement("ul");
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}