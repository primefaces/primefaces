/*
 * Copyright 2009-2011 Prime Technology.
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
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;

public class SpinnerRenderer extends InputRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		Spinner spinner = (Spinner) component;

        if(spinner.isDisabled() || spinner.isReadonly()) {
            return;
        }

        decodeBehaviors(context, spinner);

		String clientId = spinner.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if(submittedValue != null) {
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
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("$(function(){");

		writer.write(spinner.resolveWidgetVar() + " = new PrimeFaces.widget.Spinner('" + clientId + "',{");

        writer.write("step:" + spinner.getStepFactor());
		
		if(spinner.getMin() != Double.MIN_VALUE) writer.write(",min:" + spinner.getMin());
		if(spinner.getMax() != Double.MAX_VALUE) writer.write(",max:" + spinner.getMax());
		if(spinner.getPrefix() != null) writer.write(",prefix:'" + spinner.getPrefix() + "'");
		if(spinner.getSuffix() != null) writer.write(",suffix:'" + spinner.getSuffix() + "'");

        encodeClientBehaviors(context, spinner);
 		
		writer.write("});});");
		
		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext context, Spinner spinner) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = spinner.getClientId(context);
        String inputId = spinner.getClientId(context) + "_input";
        String styleClass = spinner.getStyleClass();
        styleClass = styleClass == null ? Spinner.CONTAINER_CLASS : Spinner.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(spinner.getStyle() != null)
            writer.writeAttribute("style", spinner.getStyle(), null);

		encodeInput(context, spinner);

        encodeButton(context, Spinner.UP_BUTTON_CLASS, Spinner.UP_ICON_CLASS);
        encodeButton(context, Spinner.DOWN_BUTTON_CLASS, Spinner.DOWN_ICON_CLASS);

        writer.endElement("span");
/*
        <span class="ui-spinner ui-state-default ui-widget ui-widget-content ui-corner-all ui-state-active">
        <input name="value" id="spinner" class="ui-spinner-input" autocomplete="off" role="spinbutton">
        <a class="ui-spinner-button ui-spinner-up ui-corner-tr ui-button ui-widget ui-state-default ui-button-text-only ui-state-focus" tabindex="-1" role="button">
            <span class="ui-button-text">
                <span class="ui-icon ui-icon-triangle-1-n">▲</span>
            </span>
        </a>
        <a class="ui-spinner-button ui-spinner-down ui-corner-br ui-button ui-widget ui-state-default ui-button-text-only" tabindex="-1" role="button">
            <span class="ui-button-text">
                <span class="ui-icon ui-icon-triangle-1-s">▼</span>
            </span>
        </a>
    </span>*/
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

		String valueToRender = ComponentUtils.getStringValueToRender(context, spinner);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		//renderPassThruAttributes(context, spinner, HTML.INPUT_TEXT_ATTRS);
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

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		Spinner spinner = (Spinner) component;
		String value = (String) submittedValue;
		Converter converter = spinner.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, spinner, value);
		}
		//Try to guess
		else {
			ValueExpression ve = spinner.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, spinner, value);
                }
            }
		}
		
		return value;
	}
}