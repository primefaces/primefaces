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
package org.primefaces.component.terminal;

import java.io.IOException;
import java.util.Arrays;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class TerminalRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Terminal terminal = (Terminal) component;
           
        if(terminal.isCommandRequest()) {
            handleCommand(context, terminal);
        }
        else {
            encodeMarkup(context, terminal);
            encodeScript(context, terminal);
        }
    }

    protected void encodeMarkup(FacesContext context, Terminal terminal) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = terminal.getClientId(context);
        String style = terminal.getStyle();
        String styleClass = terminal.getStyleClass();
        styleClass = (styleClass == null) ? Terminal.CONTAINER_CLASS : Terminal.CONTAINER_CLASS + " " + styleClass;
        String welcomeMessage = terminal.getWelcomeMessage();
        String inputId = clientId + "_input";
        
        writer.startElement("div", terminal);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        if(welcomeMessage != null) {
            writer.startElement("div", null);
            writer.writeText(welcomeMessage, null);
            writer.endElement("div");
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Terminal.CONTENT_CLASS, null);
        writer.endElement("div");
        
        writer.startElement("div", null);
        writer.startElement("span", null);
        writer.writeAttribute("class", Terminal.PROMPT_CLASS, null);
        writer.writeText(terminal.getPrompt(), null);
        writer.endElement("span");
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("class", Terminal.INPUT_CLASS, null);
        writer.endElement("input");
        
        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Terminal terminal) throws IOException {
        String clientId = terminal.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Terminal", terminal.resolveWidgetVar(), clientId);
        wb.finish();
    }
    
    protected void handleCommand(FacesContext context, Terminal terminal) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String clientId = terminal.getClientId(context);
		String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
		String tokens[] = value.split(" ");
		String command = tokens[0];
		String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
		
		MethodExpression commandHandler = terminal.getCommandHandler();
		String result = (String) commandHandler.invoke(context.getELContext(), new Object[]{command, args});
		
		writer.write(result);
	}
    
    
}
