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
package org.primefaces.component.spinner;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class SpinnerRenderer extends InputRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		Spinner spinner = (Spinner) component;

        if(spinner.isDisabled() || spinner.isReadonly()) {
            return;
        }

        decodeBehaviors(context, spinner);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(spinner.getClientId(context) + "_input");
        String prefix = spinner.getPrefix();
        String suffix = spinner.getSuffix();
        
        try {
            
            if(prefix != null)
                submittedValue = submittedValue.substring(prefix.length());
            else if(suffix != null)
                submittedValue = submittedValue.substring(0, (submittedValue.length() - suffix.length()));
            
            }
        catch(Exception e) {
            
        }
        finally {
            spinner.setSubmittedValue(submittedValue);
        }

	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Spinner spinner = (Spinner) component;

		encodeMarkup(context, spinner);
		encodeScript(context, spinner);
	}
	
	protected void encodeScript(FacesContext context, Spinner spinner) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = spinner.getClientId(context);

        startScript(writer, clientId);

		writer.write("$(function(){");
        
        writer.write("PrimeFaces.cw('Spinner','" + spinner.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",step:" + spinner.getStepFactor());
		
		if(spinner.getMin() != Double.MIN_VALUE) writer.write(",min:" + spinner.getMin());
		if(spinner.getMax() != Double.MAX_VALUE) writer.write(",max:" + spinner.getMax());
		if(spinner.getPrefix() != null) writer.write(",prefix:'" + spinner.getPrefix() + "'");
		if(spinner.getSuffix() != null) writer.write(",suffix:'" + spinner.getSuffix() + "'");

        encodeClientBehaviors(context, spinner);
 		
		writer.write("});});");
		
		endScript(writer);
	}
	
	protected void encodeMarkup(FacesContext context, Spinner spinner) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = spinner.getClientId(context);
        String styleClass = spinner.getStyleClass();
        styleClass = styleClass == null ? Spinner.CONTAINER_CLASS : Spinner.CONTAINER_CLASS + " " + styleClass;
        styleClass = spinner.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !spinner.isValid() ? styleClass + " ui-state-error" : styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(spinner.getStyle() != null) {
            writer.writeAttribute("style", spinner.getStyle(), null);
        }

		encodeInput(context, spinner);

        encodeButton(context, Spinner.UP_BUTTON_CLASS, Spinner.UP_ICON_CLASS);
        encodeButton(context, Spinner.DOWN_BUTTON_CLASS, Spinner.DOWN_ICON_CLASS);

        writer.endElement("span");
	}

    protected void encodeInput(FacesContext context, Spinner spinner) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = spinner.getClientId(context) + "_input";
        
        writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", Spinner.INPUT_CLASS, null);
        writer.writeAttribute("autocomplete", "off", null);

		String valueToRender = ComponentUtils.getValueToRender(context, spinner);
        valueToRender = spinner.getPrefix() != null ? spinner.getPrefix() + valueToRender : valueToRender;
        valueToRender = spinner.getSuffix() != null ? valueToRender + spinner.getSuffix(): valueToRender;
        
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender, null);
		}
        
		renderPassThruAttributes(context, spinner, HTML.INPUT_TEXT_ATTRS);

        if(spinner.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(spinner.isReadonly()) writer.writeAttribute("readonly", "readonly", null);

		writer.endElement("input");
    }

    protected void encodeButton(FacesContext context, String styleClass, String iconClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("span");

        writer.endElement("a");
    }
}