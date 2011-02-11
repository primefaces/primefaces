/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.lightbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class LightBoxRenderer extends CoreRenderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
		
		writer.startElement("div", lb);
        writer.writeAttribute("id", clientId, "id");

        if(lb.getStyle() != null) writer.writeAttribute("style", lb.getStyle(), null);
        if(lb.getStyleClass() != null) writer.writeAttribute("class", lb.getStyleClass(), null);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LightBox lb = (LightBox) component;
        UIComponent inline = lb.getFacet("inline");

		if(inline != null) {
			writer.startElement("div", null);
			writer.writeAttribute("style", "display:none", null);
			
			writer.startElement("div", null);
			writer.writeAttribute("id", lb.getClientId(context) + "_inline", null);
			
			inline.encodeAll(context);
			
			writer.endElement("div");
			writer.endElement("div");
		}
		
		writer.endElement("div");

        encodeScript(context, component);
	}

	public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
        
        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(lb.resolveWidgetVar() + " = new PrimeFaces.widget.LightBox('" + clientId + "', {");

		writer.write("transition:'" + lb.getTransition() + "'");

        if(lb.getSpeed() != 350) writer.write(",speed:" + lb.getSpeed());
		if(lb.getWidth() != null) writer.write(",width:'" + lb.getWidth() + "'");
		if(lb.getHeight() != null) writer.write(",height:'" + lb.getHeight() + "'");
		if(lb.isIframe()) writer.write(",iframe:true");
		if(lb.getFacet("inline") != null) {
			writer.write(",inline:true");
		}
		if(lb.getOpacity() != 0.85) writer.write(",opacity:" + lb.getOpacity());
		if(lb.isVisible()) writer.write(",open:true");
		if(lb.isSlideshow()) {
			writer.write(",slideshow:true");
			writer.write(",slideshowSpeed:" + lb.getSlideshowSpeed());

			if(lb.getSlideshowStartText() != null) writer.write(",slideshowStart:'" + lb.getSlideshowStartText() + "'");
			if(lb.getSlideshowStopText() != null) writer.write(",slideshowStop:'" + lb.getSlideshowStopText() + "'");
			if(!lb.isSlideshowAuto()) writer.write(",slideshowAuto:false");
		}
		if(!lb.isOverlayClose()) writer.write(",overlayClose:false");
		if(lb.getCurrentTemplate() != null) writer.write(",current:'" + lb.getCurrentTemplate() + "'");
		if(lb.isGroup()) writer.write(",rel:'" + clientId + "'");

		writer.write("});");

		writer.endElement("script");
    }
}