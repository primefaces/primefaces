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
package org.primefaces.component.dialog;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.CloseEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DialogRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Dialog dialog = (Dialog) component;
		String clientId = dialog.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		//Queue close event
		if(params.containsKey(clientId + "_closed")) {
			dialog.setVisible(false);
			dialog.queueEvent(new CloseEvent(dialog));
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Dialog dialog = (Dialog) component;
		
		encodeMarkup(facesContext, dialog);
		encodeScript(facesContext, dialog);
	}

	protected void encodeScript(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, dialog);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(var + " = new PrimeFaces.widget.Dialog('" + clientId + "',");
		encodeConfig(facesContext, dialog);
		writer.write(");");
		writer.write(var + ".render();\n");
		
		writer.endElement("script");
	}

	protected void encodeConfig(FacesContext facesContext, Dialog dialog) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("{");
		writer.write("visible:" + dialog.isVisible());
		if(dialog.getWidth() != null) writer.write(",width: '" + dialog.getWidth() + "'");
		if(dialog.getHeight() != null) writer.write(",height: '" + dialog.getHeight() + "'");
		if(!dialog.isDraggable()) writer.write(",draggable: false");
		if(dialog.getUnderlay() != null && !dialog.getUnderlay().equalsIgnoreCase("shadow")) writer.write(",underlay: '" + dialog.getUnderlay() + "'");
		if(dialog.isFixedCenter()) writer.write(",fixedcenter: true");
		if(!dialog.isClose()) writer.write(",close: false");
		if(dialog.isConstrainToViewport()) writer.write(",constraintoviewport: true");
		if(dialog.getX() != -1) writer.write(",x:" + dialog.getX());
		if(dialog.getY() != -1) writer.write(",y:" + dialog.getY());
		if(dialog.getEffect() != null) writer.write(",effect:{effect:YAHOO.widget.ContainerEffect." + dialog.getEffect().toUpperCase() + ", duration: " + dialog.getEffectDuration() + "}");
		if(dialog.isModal()) writer.write(",modal: true");
		if(dialog.getZindex() != -1) writer.write(",zIndex:" + dialog.getZindex());
		if(dialog.isResizable()) writer.write(",resizable:" + dialog.isResizable());
		if(dialog.getMinWidth() != Integer.MIN_VALUE) writer.write(",minWidth:" + dialog.getMinWidth());
		if(dialog.getMinHeight() != Integer.MIN_VALUE) writer.write(",minHeight:" + dialog.getMinHeight());
		if(dialog.getContext() != null) writer.write(",context:[" + dialog.getContext() + "]");
		
		//Ajax Close
		if(dialog.getCloseListener() != null || dialog.getOnCloseUpdate() != null) {
			writer.write(",ajaxClose:true");
			writer.write(",url:'" + getActionURL(facesContext) + "'");
			
			if(dialog.getOnCloseUpdate() != null)
				writer.write(",onCloseUpdate:'" + ComponentUtils.findClientIds(facesContext, dialog, dialog.getOnCloseUpdate()) + "'");
		}
		
		writer.write("}");
	}

	protected void encodeMarkup(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
	
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		if(dialog.getStyle() != null) writer.writeAttribute("style", dialog.getStyle(), null);
		if(dialog.getStyleClass() != null) writer.writeAttribute("class", dialog.getStyleClass(), null);
		
		encodeHeader(facesContext, dialog);
		encodeBody(facesContext, dialog);
		encodeFooter(facesContext, dialog);
		
		writer.endElement("div");
	}
	
	protected void encodeHeader(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent header = dialog.getFacet("header");
		String headerText = dialog.getHeader();
		
		if(header == null && headerText == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "hd", null);
		
		if(header != null)
			renderChild(facesContext, header);
		else if(headerText != null)
			writer.write(headerText);
					
		writer.endElement("div");
	}
	
	protected void encodeBody(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		if(dialog.isResizable())
			writer.writeAttribute("style", "overflow:auto", null);
			
		renderChildren(facesContext, dialog);
		writer.endElement("div");
	}

	protected void encodeFooter(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent footer = dialog.getFacet("footer");
		String footerText = dialog.getFooter();
		
		if(footer == null && footerText == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "ft", null);
		
		if(footer != null)
			renderChild(facesContext, footer);
		if(footerText != null)
			writer.write(footerText);
		
		writer.endElement("div");
	}
	
	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}