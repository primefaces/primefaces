/*
 * Copyright 2009-2011 Prime Technology.
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class PushRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Push push = (Push) component;
		
		ResponseWriter writer = context.getResponseWriter();
        ExternalContext externalContext = context.getExternalContext();
        String widgetVar = push.resolveWidgetVar();
        
        StringBuilder urlBuilder = new StringBuilder("ws://");
        urlBuilder.append(externalContext.getRequestServerName())
                    .append(":")
                    .append(externalContext.getRequestServerPort())
                    .append(externalContext.getRequestContextPath())
                    .append("/prime-push/")
                    .append(push.getChannel());
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("$(function() {");
		writer.write(widgetVar + " = new PrimeFaces.widget.PrimeWebSocket({");
		writer.write("url:'" + urlBuilder.toString() + "'");
        writer.write(",channel:'" + push.getChannel() + "'");
		writer.write(",onmessage:" + push.getOnmessage());
        writer.write(",autoConnect:" + push.isAutoConnect());
        
        encodeClientBehaviors(context, push);
        
		writer.write("});});");
		
		writer.endElement("script");
	}
}
