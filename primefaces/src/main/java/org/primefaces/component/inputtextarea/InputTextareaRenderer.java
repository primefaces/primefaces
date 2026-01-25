/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = InputTextarea.DEFAULT_RENDERER, componentFamily = InputTextarea.COMPONENT_FAMILY)
public class InputTextareaRenderer extends InputRenderer<InputTextarea> {

    private static final Pattern NEWLINE_NORMALIZE_PATTERN = Pattern.compile("\\r\\n?");

    @Override
    public void decode(FacesContext context, InputTextarea component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String submittedValue = params.get(clientId);

        if (submittedValue != null) {
            // #5381: normalize new lines to match JavaScript
            submittedValue = NEWLINE_NORMALIZE_PATTERN.matcher(submittedValue).replaceAll("\n");
            int maxlength = component.getMaxlength();
            if (maxlength > 0 && submittedValue.length() > maxlength) {
                submittedValue = LangUtils.substring(submittedValue, 0, maxlength);
            }
        }

        component.setSubmittedValue(submittedValue);

        //AutoComplete event
        String query = params.get(clientId + "_query");
        if (query != null) {
            AutoCompleteEvent autoCompleteEvent = new AutoCompleteEvent(component, query);
            autoCompleteEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            component.queueEvent(autoCompleteEvent);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, InputTextarea component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(component.getClientId(context) + "_query");

        if (query != null) {
            encodeSuggestions(context, component, query);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    @SuppressWarnings("unchecked")
    public void encodeSuggestions(FacesContext context, InputTextarea component, String query) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<Object> items = component.getSuggestions();

        writer.startElement("ul", component);
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

    protected void encodeScript(FacesContext context, InputTextarea component) throws IOException {
        boolean autoResize = component.isAutoResize();
        String counter = component.getCounter();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputTextarea", component)
                .attr("autoResize", autoResize)
                .attr("maxlength", component.getMaxlength(), Integer.MIN_VALUE);

        if (counter != null) {
            UIComponent counterComponent = SearchExpressionUtils.contextlessResolveComponent(context, component, counter);

            wb.attr("counter", counterComponent.getClientId(context))
                    .attr("counterTemplate", component.getCounterTemplate(), null)
                    .attr("countBytesAsChars", component.getCountBytesAsChars());
        }

        if (component.getCompleteMethod() != null) {
            wb.attr("autoComplete", true)
                    .attr("minQueryLength", component.getMinQueryLength())
                    .attr("queryDelay", component.getQueryDelay())
                    .attr("scrollHeight", component.getScrollHeight(), Integer.MAX_VALUE);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputTextarea component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("textarea", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        writer.writeAttribute("class", createStyleClass(component), "styleClass");

        renderAccessibilityAttributes(context, component);
        renderRTLDirection(context, component);
        renderPassThruAttributes(context, component, HTML.TEXTAREA_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        if (valueToRender != null) {
            if (component.isAddLine()) {
                writer.writeText("\n", null);
            }

            writer.writeText(valueToRender, "value");
        }

        writer.endElement("textarea");
    }

    protected String createStyleClass(InputTextarea component) {
        String styleClass = createStyleClass(component, InputTextarea.STYLE_CLASS) ;

        if (component.isAutoResize()) {
            styleClass = styleClass + " ui-inputtextarea-resizable";
        }

        return styleClass;
    }
}
