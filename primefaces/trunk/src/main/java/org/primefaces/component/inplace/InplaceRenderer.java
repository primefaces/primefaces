/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.inplace;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class InplaceRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Inplace inplace = (Inplace) component;
		
		encodeMarkup(context, inplace);
		encodeScript(context, inplace);
	}

	protected void encodeMarkup(FacesContext context, Inplace inplace) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inplace.getClientId(context);
		String userStyleClass = inplace.getStyleClass();
        String userStyle = inplace.getStyle();
        String styleClass = userStyleClass == null ? Inplace.CONTAINER_CLASS : Inplace.CONTAINER_CLASS + " " + userStyleClass;
        boolean disabled = inplace.isDisabled();
        String displayClass = disabled ? Inplace.DISABLED_DISPLAY_CLASS : Inplace.DISPLAY_CLASS;

        //container
		writer.startElement("span", inplace);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", styleClass, "id");
        if(userStyle != null) {
            writer.writeAttribute("style", userStyle, "id");
        }

        //display
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "_display", "id");
		writer.writeAttribute("class", displayClass, null);
		writer.write(getLabelToRender(context, inplace));
		writer.endElement("span");

        //content
		if(!inplace.isDisabled()) {	
			writer.startElement("span", null);
			writer.writeAttribute("id", clientId + "_content", "id");
			writer.writeAttribute("class", Inplace.CONTENT_CLASS, null);
			renderChildren(context, inplace);
			writer.endElement("span");
		}
		
		writer.endElement("span");
	}
	
	protected String getLabelToRender(FacesContext context, Inplace inplace) {
		if(inplace.getLabel() != null)
			return inplace.getLabel();
		else
			return ComponentUtils.getStringValueToRender(context, inplace.getChildren().get(0));
	}

	protected void encodeScript(FacesContext context, Inplace inplace) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inplace.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(inplace.resolveWidgetVar() + " = new PrimeFaces.widget.Inplace('" + clientId + "', {");
		writer.write("effect:'" + inplace.getEffect() + "'");
		writer.write(",effectSpeed:'" + inplace.getEffectSpeed() + "'");
        
		if(inplace.isDisabled()) writer.write(",disabled:true");

		writer.write("});");
		writer.endElement("script");
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