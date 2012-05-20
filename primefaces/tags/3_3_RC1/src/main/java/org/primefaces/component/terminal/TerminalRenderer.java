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
package org.primefaces.component.terminal;

import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class TerminalRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Terminal terminal = (Terminal) component;

        if(context.getExternalContext().getRequestParameterMap().containsKey(terminal.getClientId(context))) {
            handleCommand(context, component);
        } else {
            encodeMarkup(context, terminal);
            encodeScript(context, terminal);
        }
	}

	protected void encodeMarkup(FacesContext context, Terminal terminal) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = terminal.getClientId(context);
		
		writer.startElement("div", terminal);
		writer.writeAttribute("id", clientId, "id");
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, Terminal terminal) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = terminal.getClientId(context);
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Terminal','" + terminal.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",PS1:'" + terminal.getPrompt() + "'");

  		if(terminal.getWelcomeMessage() != null) writer.write(",WELCOME_MESSAGE:'" + terminal.getWelcomeMessage() + "'");
		if(terminal.getWidth() != null) writer.write(",WIDTH:'" + terminal.getWidth() + "'");
		if(terminal.getHeight() != null) writer.write(",HEIGHT:'" + terminal.getHeight() + "'");

		writer.write("},'terminal');});");
		
		endScript(writer);
	}

	protected void handleCommand(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Terminal terminal = (Terminal) component;
        String clientId = terminal.getClientId(context);
		String argsParam = context.getExternalContext().getRequestParameterMap().get(clientId + "_args");
		String tokens[] = argsParam.split(",");
		String command = tokens[0];
		String[] args;
		if(tokens.length > 1) {
			args = new String[tokens.length - 1];
			for (int t = 1; t < tokens.length; t++) {
				args[t - 1] = tokens[t];
			}
		}
		else {
			args = new String[0];
        }
		
		MethodExpression commandHandler = terminal.getCommandHandler();
		String result = (String) commandHandler.invoke (context.getELContext(), new Object[]{command, args});
		
		writer.write(result);
	}
}
