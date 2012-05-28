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
package org.primefaces.component.commandlink;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandLinkRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
        CommandLink link = (CommandLink) component;
        if(link.isDisabled()) {
            return;
        }
        
		String param = component.getClientId();

		if(context.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		CommandLink link = (CommandLink) component;
		String clientId = link.getClientId(context);
        Object label = link.getValue();

		if(!link.isDisabled()) {
            String request;
            String styleClass = link.getStyleClass();
            styleClass = styleClass == null ? CommandLink.STYLE_CLASS : CommandLink.STYLE_CLASS + " " + styleClass;

			writer.startElement("a", link);
			writer.writeAttribute("id", clientId, "id");
			writer.writeAttribute("href", "#", null);
			writer.writeAttribute("class", styleClass, null);
            
            if(link.isAjax()) {
                request = buildAjaxRequest(context, link, null);
            }
            else {
                UIComponent form = ComponentUtils.findParentForm(context, link);
                if(form == null) {
                    throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
                }
                
                request = buildNonAjaxRequest(context, link, form, clientId, true);
            }

			String onclick = link.getOnclick() != null ? link.getOnclick() + ";" + request : request;
			writer.writeAttribute("onclick", onclick, "onclick");
			
			renderPassThruAttributes(context, link, HTML.LINK_ATTRS, HTML.CLICK_EVENT);

			if(label != null)
				writer.writeText(label, "value");
			else
				renderChildren(context, link);
			
			writer.endElement("a");
		}
        else {
            String styleClass = link.getStyleClass();
            styleClass = styleClass == null ? CommandLink.DISABLED_STYLE_CLASS : CommandLink.DISABLED_STYLE_CLASS + " " + styleClass;

			writer.startElement("span", link);
			writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleclass");

            if(link.getStyle() != null)
                writer.writeAttribute("style", link.getStyle(), "style");
			
			if(label != null)
				writer.writeText(label, "value");
			else
				renderChildren(context, link);
			
			writer.endElement("span");
		}
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}