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
package org.primefaces.component.spinner;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.RequestContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

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
            if (prefix != null && submittedValue.startsWith(prefix)) {
                submittedValue = submittedValue.substring(prefix.length());
            }
            if (suffix != null && submittedValue.endsWith(suffix)) {
                submittedValue = submittedValue.substring(0, (submittedValue.length() - suffix.length()));
            }
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
        String clientId = spinner.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Spinner", spinner.resolveWidgetVar(), clientId)
            .attr("step", spinner.getStepFactor(), 1.0)
            .attr("min", spinner.getMin(), Double.MIN_VALUE)
            .attr("max", spinner.getMax(), Double.MAX_VALUE)
            .attr("prefix", spinner.getPrefix(), null)
            .attr("suffix", spinner.getSuffix(), null);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Spinner spinner) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = spinner.getClientId(context);
        String styleClass = spinner.getStyleClass();
        boolean valid = spinner.isValid();
        styleClass = styleClass == null ? Spinner.CONTAINER_CLASS : Spinner.CONTAINER_CLASS + " " + styleClass;
        styleClass = spinner.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !spinner.isValid() ? styleClass + " ui-state-error" : styleClass;
        String upButtonClass = (valid) ? Spinner.UP_BUTTON_CLASS : Spinner.UP_BUTTON_CLASS + " ui-state-error";
        String downButtonClass = (valid) ? Spinner.DOWN_BUTTON_CLASS : Spinner.DOWN_BUTTON_CLASS + " ui-state-error";

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(spinner.getStyle() != null) {
            writer.writeAttribute("style", spinner.getStyle(), null);
        }

        encodeInput(context, spinner);

        encodeButton(context, upButtonClass, Spinner.UP_ICON_CLASS);
        encodeButton(context, downButtonClass, Spinner.DOWN_ICON_CLASS);

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, Spinner spinner) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = spinner.getClientId(context) + "_input";
        String inputClass = spinner.isValid() ? Spinner.INPUT_CLASS : Spinner.INPUT_CLASS + " ui-state-error";
        String labelledBy = spinner.getLabelledBy();
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", inputClass, null);
        writer.writeAttribute("autocomplete", "off", null);

        String valueToRender = ComponentUtils.getValueToRender(context, spinner);
        if(valueToRender != null) {
            valueToRender = spinner.getPrefix() != null ? spinner.getPrefix() + valueToRender : valueToRender;
            valueToRender = spinner.getSuffix() != null ? valueToRender + spinner.getSuffix(): valueToRender;
            writer.writeAttribute("value", valueToRender, null);
        }

        renderPassThruAttributes(context, spinner, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, spinner, HTML.INPUT_TEXT_EVENTS);

        if(spinner.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(spinner.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(spinner.isRequired()) writer.writeAttribute("aria-required", "true", null);
        if(labelledBy != null) writer.writeAttribute("aria-labelledby", labelledBy, null);
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, spinner);
        }

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
    public String getHighlighter() {
        return "spinner";
    }
}