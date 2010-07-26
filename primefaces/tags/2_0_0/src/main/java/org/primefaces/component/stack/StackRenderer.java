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
package org.primefaces.component.stack;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class StackRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Stack stack = (Stack) component;
		
		encodeScript(facesContext, stack);
		encodeMarkup(facesContext, stack);
	}
	
	private void encodeScript(FacesContext facesContext, Stack stack) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = stack.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, stack);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(widgetVar + " = new PrimeFaces.widget.Stack('" + clientId + "', {");
		writer.write("openSpeed:" + stack.getOpenSpeed());
		writer.write(",closeSpeed:" + stack.getCloseSpeed());
		writer.write("});\n");
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Stack stack) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = stack.getClientId(facesContext);
		
		writer.startElement("div", stack);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", "pf-stack", null);
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(facesContext, stack.getIcon()), null);
		writer.endElement("img");
		
		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId + "_stack", "id");
		
		for(UIComponent child : stack.getChildren()) {
			if(child.isRendered()) {
				StackItem item = (StackItem) child;
				
				writer.startElement("li", null);

					writer.startElement("a", null);
					writer.writeAttribute("href", item.getUrl(), null);
					if(item.getOnclick() != null) {
						writer.writeAttribute("onclick", item.getOnclick(), null);
					}
						writer.startElement("span", null);
						writer.write(item.getLabel());
						writer.endElement("span");
						
						writer.startElement("img", null);
						writer.writeAttribute("src", getResourceURL(facesContext, item.getIcon()), null);
						writer.endElement("img");
				
					writer.endElement("a");
				
				writer.endElement("li");
				
				writer.write("\n");
			}
		}
		
		writer.endElement("ul");
		
		writer.endElement("div");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Encode children in encodeEnd
	}

	public boolean getRendersChildren() {
		return true;
	}
}
