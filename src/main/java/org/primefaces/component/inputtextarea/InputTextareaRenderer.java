/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

        if (!shouldDecode(inputTextarea)) {
            return;
        }

        decodeBehaviors(context, inputTextarea);

        String clientId = inputTextarea.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String submittedValue = params.get(clientId);

        if (submittedValue != null && submittedValue.length() > inputTextarea.getMaxlength()) {
            return;
        }

        inputTextarea.setSubmittedValue(submittedValue);

        //AutoComplete event
        String query = params.get(clientId + "_query");
        if (query != null) {
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

        if (query != null) {
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

        for (Object item : items) {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);
            writer.writeAttribute("data-item-value", item.toString(), null);
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
        wb.init("InputTextarea", inputTextarea.resolveWidgetVar(context), clientId)
                .attr("autoResize", autoResize)
                .attr("maxlength", inputTextarea.getMaxlength(), Integer.MAX_VALUE);

        if (counter != null) {
            UIComponent counterComponent = SearchExpressionFacade.resolveComponent(context, inputTextarea, counter);

            wb.attr("counter", counterComponent.getClientId(context))
                    .attr("counterTemplate", inputTextarea.getCounterTemplate(), null);
        }

        if (inputTextarea.getCompleteMethod() != null) {
            wb.attr("autoComplete", true)
                    .attr("minQueryLength", inputTextarea.getMinQueryLength())
                    .attr("queryDelay", inputTextarea.getQueryDelay())
                    .attr("scrollHeight", inputTextarea.getScrollHeight(), Integer.MAX_VALUE);
        }

        encodeClientBehaviors(context, inputTextarea);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputTextarea inputTextarea) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputTextarea.getClientId(context);

        writer.startElement("textarea", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);

        if (inputTextarea.getStyle() != null) {
            writer.writeAttribute("style", inputTextarea.getStyle(), null);
        }

        writer.writeAttribute("class", createStyleClass(inputTextarea), "styleClass");

        renderAccessibilityAttributes(context, inputTextarea);
        renderRTLDirection(context, inputTextarea);
        renderPassThruAttributes(context, inputTextarea, HTML.TEXTAREA_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputTextarea, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, inputTextarea);

        String valueToRender = ComponentUtils.getValueToRender(context, inputTextarea);
        if (valueToRender != null) {
            if (inputTextarea.isAddLine()) {
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

        if (inputTextarea.isAutoResize()) {
            styleClass = styleClass + " ui-inputtextarea-resizable";
        }

        return styleClass;
    }
}
