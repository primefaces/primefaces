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
import javax.faces.FacesException;

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
        
        boolean validationFailed = context.isValidationFailed();
        String displayStyle = validationFailed ? "none" : "inline";
        String contentStyle = validationFailed ? "inline" : "none";

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
        writer.writeAttribute("style", "display:" + displayStyle, null);
		writer.write(getLabelToRender(context, inplace));
		writer.endElement("span");

        //content
		if(!inplace.isDisabled()) {	
			writer.startElement("span", null);
			writer.writeAttribute("id", clientId + "_content", "id");
			writer.writeAttribute("class", Inplace.CONTENT_CLASS, null);
            writer.writeAttribute("style", "display:" + contentStyle, null);
            
			renderChildren(context, inplace);

            if(inplace.isEditor()) {
                encodeEditor(context, inplace);
            }

			writer.endElement("span");
		}
		
		writer.endElement("span");
	}
	
	protected String getLabelToRender(FacesContext context, Inplace inplace) {
        String label = inplace.getLabel();
        String emptyLabel = inplace.getEmptyLabel();

		if(label != null) {
			return label;
        }
		else {
            String value = ComponentUtils.getStringValueToRender(context, inplace.getChildren().get(0));

            if(value == null || isValueBlank(value)) {
                if(emptyLabel != null)
                    return emptyLabel;
                else
                    return "";
            }
            else {
                return value;
            }
        }
	}

	protected void encodeScript(FacesContext context, Inplace inplace) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inplace.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(inplace.resolveWidgetVar() + " = new PrimeFaces.widget.Inplace('" + clientId + "', {");
		writer.write("effect:'" + inplace.getEffect() + "'");
		writer.write(",effectSpeed:'" + inplace.getEffectSpeed() + "'");
        writer.write(",event:'" + inplace.getEvent() + "'");
        
		if(inplace.isDisabled()) writer.write(",disabled:true");
        if(inplace.isEditor()) {
            UIComponent form = ComponentUtils.findParentForm(context, inplace);
            if (form == null) {
                throw new FacesException("Inplace : \"" + inplace.getClientId(context) + "\" must be inside a form element");
            }
            
            writer.write(",editor:true");
            writer.write(",url:'" + getActionURL(context) + "'");
            writer.write(",formId:'" + form.getClientId(context) + "'");

            String onEditUpdate = inplace.getOnEditUpdate();
            if(onEditUpdate != null) {
                writer.write(",onEditUpdate:'" + ComponentUtils.findClientIds(context, inplace, onEditUpdate) + "'");
            }
        }

		writer.write("});");
		writer.endElement("script");
	}

    protected void encodeEditor(FacesContext context, Inplace inplace) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("id", inplace.getClientId(context) + "_editor", null);
        writer.writeAttribute("class", Inplace.EDITOR_CLASS, null);

        encodeButton(context, inplace, inplace.getSaveLabel(), Inplace.SAVE_BUTTON_CLASS);
        encodeButton(context, inplace, inplace.getCancelLabel(), Inplace.CANCEL_BUTTON_CLASS);

        writer.endElement("span");
    }
    
    protected void encodeButton(FacesContext context, Inplace inplace, String label, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", styleClass, null);
        writer.write(label);
        writer.endElement("button");
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