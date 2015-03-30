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
package org.primefaces.component.socket;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.WidgetBuilder;

public class SocketRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Socket socket = (Socket) component;
        String channel = socket.getChannel();
        String channelUrl = Constants.PUSH_PATH + channel;
        String url = getResourceURL(context, channelUrl);
        String pushServer = RequestContext.getCurrentInstance().getApplicationContext().getConfig().getPushServerURL();
        String clientId = socket.getClientId(context);
        
        if(pushServer != null) {
            url = pushServer + url;
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Socket", socket.resolveWidgetVar(), clientId);
        
        wb.attr("url", url)
        	.attr("autoConnect", socket.isAutoConnect())
        	.attr("transport", socket.getTransport())
        	.attr("fallbackTransport", socket.getFallbackTransport())
        	.callback("onMessage", socket.getOnMessage())
        	.callback("onError", "function(response)", socket.getOnError())
        	.callback("onClose", "function(response)", socket.getOnClose())
        	.callback("onOpen", "function(response)", socket.getOnOpen())
        	.callback("onReconnect", "function(response)", socket.getOnReconnect())
        	.callback("onMessagePublished", "function(response)", socket.getOnMessagePublished())
        	.callback("onTransportFailure", "function(response, request)", socket.getOnTransportFailure())
        	.callback("onLocalMessage", "function(response)", socket.getOnLocalMessage());

        encodeClientBehaviors(context, socket);

        wb.finish();
    }
}
