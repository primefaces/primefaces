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
package org.primefaces.component.notificationbar;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class NotificationBarRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		NotificationBar bar = (NotificationBar) component;
		
		encodeMarkup(facesContext, bar);
		encodeScript(facesContext, bar);
	}
	
	private void encodeMarkup(FacesContext facesContext, NotificationBar bar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String styleClass = bar.getStyleClass() == null ? "ui-notificationbar" : "ui-notificationbar " + bar.getStyleClass();
		String var = createUniqueWidgetVar(facesContext, bar);
		UIComponent close = bar.getFacet("close");
		
		writer.startElement("div", bar);
		writer.writeAttribute("id", bar.getClientId(facesContext), null);
		writer.writeAttribute("class", styleClass, null);
		if(bar.getStyle() != null) writer.writeAttribute("style", bar.getStyle(), null);
		
		if(close != null) {
			writer.startElement("span", null);
			writer.writeAttribute("class", "ui-notificationbar-close", null);
			writer.writeAttribute("onclick", var + ".hide()", null);
			renderChild(facesContext, close);
			writer.endElement("span");
		}

		renderChildren(facesContext, bar);
		
		writer.endElement("div");
	}

	private void encodeScript(FacesContext facesContext, NotificationBar bar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = bar.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, bar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(document).ready(function(){");

		writer.write(var + " = new PrimeFaces.widget.NotificationBar('" + clientId + "',{");
		writer.write("position:'" + bar.getPosition() + "'");
		writer.write(",effect:'" + bar.getEffect() + "'");
		writer.write(",effectSpeed:'" + bar.getEffectSpeed() + "'");
		
		if(bar.isAutoDisplay())
			writer.write(",autoDisplay:true");
		
		writer.write("});});");
		
		writer.endElement("script");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}