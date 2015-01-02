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
package org.primefaces.component.inputtextarea;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class InputTextareaRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputTextarea inputTextarea = (InputTextarea) component;

        if(inputTextarea.isDisabled() || inputTextarea.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputTextarea);

		String clientId = inputTextarea.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String submittedValue = params.get(clientId);
        
		inputTextarea.setSubmittedValue(submittedValue);
        
        //AutoComplete event
        String query = params.get(clientId + "_query");
        if(query != null) {
            AutoCompleteEvent autoCompleteEvent = new AutoCompleteEvent(inputTextarea, query);
            autoCompleteEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            inputTextarea.queueEvent(autoCompleteEvent);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		InputTextarea inputTextarea = (InputTextarea) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(inputTextarea.getClientId(context) + "_query");

        if(query != null) {
            encodeSuggestions(context, inputTextarea, query);
        }
        else {
            encodeMarkup(context, inputTextarea);
            encodeScript(context, inputTextarea);
        }
	}
    
    @SuppressWarnings("unchecked")
    public void encodeSuggestions(FacesContext context, InputTextarea inputTextarea, String query) throws IOException {        
        ResponseWriter writer = context.getResponseWriter();
        List<Object> items = inputTextarea.getSuggestions();

        writer.startElement("ul", inputTextarea);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);

        for(Object item : items) {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);
            writer.writeAttribute("data-item-value", item, null);
            writer.writeText(item, null); 
            
            writer.endElement("li");
        }
        
        writer.endElement("ul");
    }

	protected void encodeScript(FacesContext context, InputTextarea inputTextarea) throws IOException {
		String clientId = inputTextarea.getClientId(context);
        boolean autoResize = inputTextarea.isAutoResize();
        String counter = inputTextarea.getCounter();
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("InputTextarea", inputTextarea.resolveWidgetVar(), clientId)
            .attr("autoResize", autoResize)
            .attr("maxlength", inputTextarea.getMaxlength(), Integer.MAX_VALUE);
        
        if(counter != null) {
            UIComponent counterComponent = SearchExpressionFacade.resolveComponent(context, inputTextarea, counter);

        	wb.attr("counter", counterComponent.getClientId(context))
            	.attr("counterTemplate", inputTextarea.getCounterTemplate(), null);
        }
        
        if(inputTextarea.getCompleteMethod() != null) {
            wb.attr("autoComplete", true)
                .attr("minQueryLength", inputTextarea.getMinQueryLength())
                .attr("queryDelay", inputTextarea.getQueryDelay())
                .attr("scrollHeight", inputTextarea.getScrollHeight(), Integer.MAX_VALUE);
        }

        wb.finish();
	}

	protected void encodeMarkup(FacesContext context, InputTextarea inputTextarea) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputTextarea.getClientId(context);
                
		writer.startElement("textarea", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);

		renderPassThruAttributes(context, inputTextarea, HTML.TEXTAREA_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputTextarea, HTML.INPUT_TEXT_EVENTS);
        
        if(inputTextarea.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(inputTextarea.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(inputTextarea.getStyle() != null) writer.writeAttribute("style", inputTextarea.getStyle(), null);
        if(inputTextarea.isRequired()) writer.writeAttribute("aria-required", "true", null);
        
        writer.writeAttribute("class", createStyleClass(inputTextarea), "styleClass");
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, inputTextarea);
        }

        String valueToRender = ComponentUtils.getValueToRender(context, inputTextarea);
		if(valueToRender != null) {
            if(inputTextarea.isAddLine()) {
                writer.writeText("\n", null);
            }
            
			writer.writeText(valueToRender, "value");
		}

        writer.endElement("textarea");
	}
    
    protected String createStyleClass(InputTextarea inputTextarea) {
        String defaultClass = InputTextarea.STYLE_CLASS;
        defaultClass = inputTextarea.isValid() ? defaultClass : defaultClass + " ui-state-error";
        defaultClass = !inputTextarea.isDisabled() ? defaultClass : defaultClass + " ui-state-disabled";
        
        String styleClass = inputTextarea.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        
        if(inputTextarea.isAutoResize()) {
            styleClass = styleClass + " ui-inputtextarea-resizable";
        }
        
        return styleClass;
    }
}
