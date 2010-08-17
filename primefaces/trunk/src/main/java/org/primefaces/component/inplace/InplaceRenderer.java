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
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Inplace inplace = (Inplace) component;
		
		encodeMarkup(facesContext, inplace);
		encodeScript(facesContext, inplace);
	}

	protected void encodeMarkup(FacesContext facesContext, Inplace inplace) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = inplace.getClientId(facesContext);
		String displayClass = inplace.isDisabled() ? "pf-inplace-display-disabled" : "pf-inplace-display";
		
		writer.startElement("span", inplace);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", "pf-inplace", "id");
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "_display", "id");
		writer.writeAttribute("class", displayClass, null);
		writer.write(getLabelToRender(facesContext, inplace));
		writer.endElement("span");
		
		if(!inplace.isDisabled()) {	
			writer.startElement("span", null);
			writer.writeAttribute("id", clientId + "_content", "id");
			writer.writeAttribute("class", "pf-inplace-content", null);
			renderChildren(facesContext, inplace);
			writer.endElement("span");
		}
		
		writer.endElement("span");
	}
	
	protected String getLabelToRender(FacesContext facesContext, Inplace inplace) {
		if(inplace.getLabel() != null) {
			return inplace.getLabel();
		} else {
			return ComponentUtils.getStringValueToRender(facesContext, inplace.getChildren().get(0));
		}
	}

	protected void encodeScript(FacesContext facesContext, Inplace inplace) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = inplace.getClientId(facesContext);
		
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
