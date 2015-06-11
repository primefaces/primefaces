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
package org.primefaces.component.message;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.InputHolder;
import org.primefaces.context.RequestContext;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.WidgetBuilder;

public class MessageRenderer extends UINotificationRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Message uiMessage = (Message) component;

        UIComponent target = SearchExpressionFacade.resolveComponent(context, uiMessage, uiMessage.getFor());
        String targetClientId = target.getClientId(context);

        encodeMarkup(context, uiMessage, targetClientId);
        encodeScript(context, uiMessage, target);
	}
    
    protected void encodeMarkup(FacesContext context, Message uiMessage, String targetClientId) throws IOException {        
        ResponseWriter writer = context.getResponseWriter();
        String display = uiMessage.getDisplay();
        boolean iconOnly = display.equals("icon");
        boolean escape = uiMessage.isEscape();
        String styleClass = display.equals("tooltip") ? "ui-message ui-helper-hidden" : "ui-message";
        
		Iterator<FacesMessage> msgs = context.getMessages(targetClientId);

		writer.startElement("div", uiMessage);
		writer.writeAttribute("id", uiMessage.getClientId(context), null);
        writer.writeAttribute("aria-live", "polite", null);
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("data-display", display, null);
            writer.writeAttribute("data-target", targetClientId, null);
            writer.writeAttribute("data-redisplay", String.valueOf(uiMessage.isRedisplay()), null);
        }
		
		if(msgs.hasNext()) {
			FacesMessage msg = msgs.next();
            String severityName = getSeverityName(msg);
			
			if(!shouldRender(uiMessage, msg, severityName)) {
                writer.writeAttribute("class", styleClass, null);
				writer.endElement("div");
                
				return;
			} 
            else {
				Severity severity = msg.getSeverity();
				String severityKey = null;
				
				if(severity.equals(FacesMessage.SEVERITY_ERROR)) severityKey = "error";
				else if(severity.equals(FacesMessage.SEVERITY_INFO)) severityKey = "info";
				else if(severity.equals(FacesMessage.SEVERITY_WARN)) severityKey = "warn";
				else if(severity.equals(FacesMessage.SEVERITY_FATAL))  severityKey = "fatal";

                styleClass += " ui-message-" + severityKey + " ui-widget ui-corner-all";
                
                if(iconOnly) {
                    styleClass +=  " ui-message-icon-only ui-helper-clearfix";
                }
					
				writer.writeAttribute("class", styleClass , null);

                if(!display.equals("text")) {
                    encodeIcon(writer, severityKey, msg.getDetail(), iconOnly);
                }

                if(!iconOnly) {
                    if(uiMessage.isShowSummary())
                        encodeText(writer, msg.getSummary(), severityKey + "-summary", escape);
                    if(uiMessage.isShowDetail())
                        encodeText(writer, msg.getDetail(), severityKey + "-detail", escape);
                }
					
				msg.rendered();
			}
		}
        else {
            writer.writeAttribute("class", styleClass, null);
        }
		
		writer.endElement("div");
    }
    
    protected void encodeText(ResponseWriter writer, String text, String severity, boolean escape) throws IOException {
		writer.startElement("span", null);
		writer.writeAttribute("class", "ui-message-" + severity, null);
        
        if(escape)
            writer.writeText(text, null);
        else
            writer.write(text);
        
		writer.endElement("span");
	}

    protected void encodeIcon(ResponseWriter writer, String severity, String title, boolean iconOnly) throws IOException {
		writer.startElement("span", null);
		writer.writeAttribute("class", "ui-message-" + severity + "-icon", null);
        if(iconOnly) {
            writer.writeAttribute("title", title, null);
        }
		writer.endElement("span");
	}

    protected void encodeScript(FacesContext context, Message uiMessage, UIComponent target) throws IOException {
        if(uiMessage.getDisplay().equals("tooltip")) {
            String clientId = uiMessage.getClientId(context);
            String targetClientId = (target instanceof InputHolder) ? ((InputHolder) target).getInputClientId() : target.getClientId(context);
            WidgetBuilder wb = getWidgetBuilder(context);

            wb.initWithDomReady("Message", uiMessage.resolveWidgetVar(), clientId)
                .attr("target", targetClientId)
                .finish();
        }
    }    
}