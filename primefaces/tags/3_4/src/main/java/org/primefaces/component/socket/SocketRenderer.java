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
package org.primefaces.component.socket;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

public class SocketRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		Socket socket = (Socket) component;
        String channel = socket.getChannel();
        String channelUrl = Constants.PUSH_PATH + channel;
        String url = getResourceURL(context, channelUrl);
        String pushServer = context.getExternalContext().getInitParameter(Constants.PUSH_SERVER_URL);
        
        if(pushServer != null) {
            url = pushServer + url;
        }

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("$(function() {");
		writer.write(socket.resolveWidgetVar() + " = new PrimeFaces.widget.Socket({");
		writer.write("url:'" + url + "'");
		
        writer.write(",autoConnect:" + socket.isAutoConnect());
        writer.write(",transport:'" + socket.getTransport() + "'");
        writer.write(",fallbackTransport:'" + socket.getFallbackTransport() + "'");
        
        //callbacks
        if(socket.getOnMessage() != null) writer.write(",onMessage:" + socket.getOnMessage());
        if(socket.getOnError() != null) writer.write(",onError:" + socket.getOnError());
        
        encodeClientBehaviors(context, socket);
        
		writer.write("});});");
		
		writer.endElement("script");
	}
}
