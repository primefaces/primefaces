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
package org.primefaces.component.push;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.comet.CometContext;
import org.primefaces.renderkit.CoreRenderer;

public class PushRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Push push = (Push) component;
		
		encodeMarkup(facesContext, push);
		encodeScript(facesContext, push);
	}
	
	private void encodeScript(FacesContext facesContext, Push push) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = push.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, push);
		String channel = CometContext.CHANNEL_PATH + push.getChannel();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function() {\n");
		writer.write(var + " = new PrimeFaces.widget.Subscriber('" + clientId + "', {");
		writer.write("channel:'" + getResourceURL(facesContext, channel) + "?widget=" + var + "'");
		writer.write(",onpublish:" + push.getOnpublish());
		writer.write("});});");
		
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Push push) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("iframe", push);
		writer.writeAttribute("id", push.getClientId(facesContext), "id");
		writer.writeAttribute("style", "display:none", null);
		writer.endElement("iframe");
	}
}
