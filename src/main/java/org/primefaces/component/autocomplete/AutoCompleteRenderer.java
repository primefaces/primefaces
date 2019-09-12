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
package org.primefaces.component.autocomplete;

import java.io.IOException;
import java.util.*;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;

import org.primefaces.component.column.Column;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class AutoCompleteRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        AutoComplete ac = (AutoComplete) component;
        String clientId = ac.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (!shouldDecode(ac)) {
            return;
        }

        if (ac.isMultiple()) {
            decodeMultiple(context, ac);
        }
        else {
            decodeSingle(context, ac);
        }

        decodeBehaviors(context, ac);

        // AutoComplete event
        String query = params.get(clientId + "_query");
        if (query != null) {
            AutoCompleteEvent autoCompleteEvent = new AutoCompleteEvent(ac, query);
            autoCompleteEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            ac.queueEvent(autoCompleteEvent);
        }
    }

    protected void decodeSingle(FacesContext context, AutoComplete ac) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = ac.getClientId(context);
        String valueParam = (ac.getVar() != null) ? clientId + "_hinput" : clientId + "_input";
        String submittedValue = params.get(valueParam);

        if (submittedValue != null) {
            ac.setSubmittedValue(submittedValue);
        }
    }

    protected void decodeMultiple(FacesContext context, AutoComplete ac) {
        Map<String, String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = ac.getClientId(context);
        String[] hinputValues = paramValues.get(clientId + "_hinput");
        String[] submittedValues = (hinputValues != null) ? hinputValues : new String[]{};
        String inputValue = params.get(clientId + "_input");

        if (!isValueBlank(inputValue)) {
            submittedValues = LangUtils.concat(submittedValues, new String[]{inputValue});
        }

        if (submittedValues.length > 0) {
            ac.setSubmittedValue(submittedValues);
        }
        else {
            ac.setSubmittedValue("");
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AutoComplete autoComplete = (AutoComplete) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(autoComplete.getClientId(context) + "_query");

        if (query != null) {
            if (autoComplete.isDynamicLoadRequest(context)) {
                encodePanel(context, autoComplete);
            }
            else {
                encodeResults(context, component, query);
            }
        }
        else {
            encodeMarkup(context, autoComplete);
            encodeScript(context, autoComplete);
        }
    }

    @SuppressWarnings("unchecked")
    public void encodeResults(FacesContext context, UIComponent component, String query) throws IOException {
        AutoComplete ac = (AutoComplete) component;
        List results = ac.getSuggestions();
        int maxResults = ac.getMaxResults();

        if (maxResults != Integer.MAX_VALUE && results != null && results.size() > maxResults) {
            results = results.subList(0, ac.getMaxResults());
        }

        encodeSuggestions(context, ac, results);
    }

    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        if (ac.isMultiple()) {
            encodeMultipleMarkup(context, ac);
        }
        else {
            encodeSingleMarkup(context, ac);
        }
    }

    protected void encodeSingleMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.STYLE_CLASS : AutoComplete.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (ac.getStyle() != null) {
            writer.writeAttribute("style", ac.getStyle(), null);
        }

        encodeInput(context, ac, clientId);

        if (ac.getVar() != null) {
            encodeHiddenInput(context, ac, clientId);
        }

        if (ac.isDropdown()) {
            encodeDropDown(context, ac);
        }

        if (!ac.isDynamic()) {
            encodePanel(context, ac);
        }

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, AutoComplete ac, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean disabled = ac.isDisabled();
        String var = ac.getVar();
        String itemLabel;
        String defaultStyleClass = ac.isDropdown() ? AutoComplete.INPUT_WITH_DROPDOWN_CLASS : AutoComplete.INPUT_CLASS;
        String styleClass = disabled ? defaultStyleClass + " ui-state-disabled" : defaultStyleClass;
        styleClass = ac.isValid() ? styleClass : styleClass + " ui-state-error";
        String inputStyle = ac.getInputStyle();
        String inputStyleClass = ac.getInputStyleClass();
        inputStyleClass = (inputStyleClass == null) ? styleClass : styleClass + " " + inputStyleClass;
        String autocompleteProp = (ac.getAutocomplete() != null) ? ac.getAutocomplete() : "off";

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", ac.getType(), null);
        writer.writeAttribute("class", inputStyleClass, null);
        writer.writeAttribute("autocomplete", autocompleteProp, null);

        if (inputStyle != null) {
            writer.writeAttribute("style", inputStyle, null);
        }

        renderAccessibilityAttributes(context, ac);
        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, ac, HTML.INPUT_TEXT_EVENTS);

        if (var == null) {
            itemLabel = ComponentUtils.getValueToRender(context, ac);

            if (itemLabel != null) {
                writer.writeAttribute("value", itemLabel, null);
            }
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (ac.isValid()) {
                requestMap.put(var, ac.getValue());
                itemLabel = ac.getItemLabel();
            }
            else {
                Object submittedValue = ac.getSubmittedValue();

                Object value = ac.getValue();

                if (submittedValue == null && value != null) {
                    requestMap.put(var, value);
                    itemLabel = ac.getItemLabel();
                }
                else if (submittedValue != null) {
                    // retrieve the actual item (pojo) from the converter
                    try {
                        Object item = getConvertedValue(context, ac, String.valueOf(submittedValue));
                        requestMap.put(var, item);
                        itemLabel = ac.getItemLabel();
                    }
                    catch (ConverterException ce) {
                        itemLabel = String.valueOf(submittedValue);
                    }

                }
                else {
                    itemLabel = null;
                }

            }

            if (itemLabel != null) {
                writer.writeAttribute("value", itemLabel, null);
            }

            requestMap.remove(var);
        }

        renderValidationMetadata(context, ac);

        writer.endElement("input");
    }

    protected void encodeHiddenInput(FacesContext context, AutoComplete ac, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String valueToRender = ComponentUtils.getValueToRender(context, ac);
        String autocompleteProp = (ac.getAutocomplete() != null) ? ac.getAutocomplete() : "off";

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_hinput", null);
        writer.writeAttribute("name", clientId + "_hinput", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", autocompleteProp, null);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }
        writer.endElement("input");
    }

    protected void encodeHiddenSelect(FacesContext context, AutoComplete ac, String clientId, List<String> values) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = clientId + "_hinput";

        writer.startElement("select", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute("tabindex", "-1", null);

        if (ac.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        renderValidationMetadata(context, ac);

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            writer.startElement("option", null);
            writer.writeAttribute("value", value, null);
            writer.writeAttribute("selected", "selected", null);
            writer.endElement("option");
        }

        writer.endElement("select");
    }

    protected void encodeDropDown(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String dropdownClass = AutoComplete.DROPDOWN_CLASS;
        boolean disabled = ac.isDisabled() || ac.isReadonly();
        if (disabled) {
            dropdownClass += " ui-state-disabled";
        }

        writer.startElement("button", ac);
        writer.writeAttribute("class", dropdownClass, null);
        writer.writeAttribute("type", "button", null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (ac.getTabindex() != null) {
            writer.writeAttribute("tabindex", ac.getTabindex(), null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-icon-primary ui-icon ui-icon-triangle-1-s", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);
        writer.write("&nbsp;");
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodePanel(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = ac.getPanelStyleClass();
        styleClass = styleClass == null ? AutoComplete.PANEL_CLASS : AutoComplete.PANEL_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", ac.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "listbox", null);

        if (ac.getPanelStyle() != null) {
            writer.writeAttribute("style", ac.getPanelStyle(), null);
        }

        if (ac.isDynamic() && ac.isDynamicLoadRequest(context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String query = params.get(ac.getClientId(context) + "_query");
            encodeResults(context, ac, query);
        }

        writer.endElement("span");
    }

    protected void encodeMultipleMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String inputId = clientId + "_input";

        List values;
        if (ac.isValid()) {
            values = (List) ac.getValue();
        }
        else {
            Object submittedValue = ac.getSubmittedValue();
            try {
                values = (List) getConvertedValue(context, ac, submittedValue);
            }
            catch (ConverterException ce) {
                values = Arrays.asList((String[]) submittedValue);
            }
        }

        List<String> stringValues = new ArrayList<>();
        boolean disabled = ac.isDisabled();
        String title = ac.getTitle();

        String style = ac.getStyle();
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.MULTIPLE_STYLE_CLASS : AutoComplete.MULTIPLE_STYLE_CLASS + " " + styleClass;
        String listClass = ac.isDropdown() ? AutoComplete.MULTIPLE_CONTAINER_WITH_DROPDOWN_CLASS : AutoComplete.MULTIPLE_CONTAINER_CLASS;
        listClass = disabled ? listClass + " ui-state-disabled" : listClass;
        listClass = ac.isValid() ? listClass : listClass + " ui-state-error";
        String autocompleteProp = (ac.getAutocomplete() != null) ? ac.getAutocomplete() : "off";

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", listClass, null);

        if (values != null && !values.isEmpty()) {
            Converter converter = ComponentUtils.getConverter(context, ac);
            String var = ac.getVar();
            boolean pojo = var != null;

            Collection<Object> items = ac.isUnique() ? new HashSet<>(values) : values;
            for (Object value : items) {
                Object itemValue = null;
                String itemLabel = null;

                if (pojo) {
                    context.getExternalContext().getRequestMap().put(var, value);
                    itemValue = ac.getItemValue();
                    itemLabel = ac.getItemLabel();
                }
                else {
                    itemValue = value;
                    itemLabel = String.valueOf(value);
                }

                String tokenValue = converter != null ? converter.getAsString(context, ac, itemValue) : String.valueOf(itemValue);
                String itemStyleClass = AutoComplete.TOKEN_DISPLAY_CLASS;
                if (ac.getItemStyleClass() != null) {
                    itemStyleClass += " " + ac.getItemStyleClass();
                }

                writer.startElement("li", null);
                writer.writeAttribute("data-token-value", tokenValue, null);
                writer.writeAttribute("class", itemStyleClass, null);

                String labelClass = disabled ? AutoComplete.TOKEN_LABEL_DISABLED_CLASS : AutoComplete.TOKEN_LABEL_CLASS;
                writer.startElement("span", null);
                writer.writeAttribute("class", labelClass, null);
                writer.writeText(itemLabel, null);
                writer.endElement("span");

                if (!disabled) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", AutoComplete.TOKEN_ICON_CLASS, null);
                    writer.endElement("span");
                }

                writer.endElement("li");

                stringValues.add(tokenValue);
            }
        }

        writer.startElement("li", null);
        writer.writeAttribute("class", AutoComplete.TOKEN_INPUT_CLASS, null);
        writer.startElement("input", null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("autocomplete", autocompleteProp, null);

        renderAccessibilityAttributes(context, ac);
        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, ac, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("input");
        writer.endElement("li");

        writer.endElement("ul");

        if (ac.isDropdown()) {
            encodeDropDown(context, ac);
        }

        if (!ac.isDynamic()) {
            encodePanel(context, ac);
        }

        encodeHiddenSelect(context, ac, clientId, stringValues);

        writer.endElement("div");
    }

    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        boolean customContent = !ac.getColums().isEmpty();
        Converter converter = ComponentUtils.getConverter(context, ac);

        if (customContent) {
            encodeSuggestionsAsTable(context, ac, items, converter);
        }
        else {
            encodeSuggestionsAsList(context, ac, items, converter);
        }
    }

    protected void encodeSuggestionsAsTable(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        UIComponent itemtip = ac.getFacet("itemtip");
        boolean hasHeader = false;
        boolean hasGroupByTooltip = (ac.getValueExpression(AutoComplete.PropertyKeys.groupByTooltip.toString()) != null);

        for (int i = 0; i < ac.getColums().size(); i++) {
            Column column = ac.getColums().get(i);
            if (column.isRendered() && (column.getHeaderText() != null || column.getFacet("header") != null)) {
                hasHeader = true;
                break;
            }
        }

        writer.startElement("table", ac);
        writer.writeAttribute("class", AutoComplete.TABLE_CLASS, null);

        if (hasHeader) {
            writer.startElement("thead", ac);
            for (int i = 0; i < ac.getColums().size(); i++) {
                Column column = ac.getColums().get(i);
                if (!column.isRendered()) {
                    continue;
                }

                String headerText = column.getHeaderText();
                UIComponent headerFacet = column.getFacet("header");
                String styleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default " + column.getStyleClass();

                writer.startElement("th", null);
                writer.writeAttribute("class", styleClass, null);

                if (column.getStyle() != null) {
                    writer.writeAttribute("style", column.getStyle(), null);
                }

                if (headerFacet != null) {
                    headerFacet.encodeAll(context);
                }
                else if (headerText != null) {
                    writer.writeText(headerText, null);
                }

                writer.endElement("th");
            }
            writer.endElement("thead");
        }

        writer.startElement("tbody", ac);

        if (items != null) {
            for (Object item : items) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", AutoComplete.ROW_CLASS, null);

                if (pojo) {
                    requestMap.put(var, item);
                    String value = converter == null ? String.valueOf(ac.getItemValue()) : converter.getAsString(context, ac, ac.getItemValue());
                    writer.writeAttribute("data-item-value", value, null);
                    writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                    writer.writeAttribute("data-item-class", ac.getItemStyleClass(), null);
                    writer.writeAttribute("data-item-group", ac.getGroupBy(), null);

                    if (hasGroupByTooltip) {
                        writer.writeAttribute("data-item-group-tooltip", ac.getGroupByTooltip(), null);
                    }
                }

                for (int i = 0; i < ac.getColums().size(); i++) {
                    Column column = ac.getColums().get(i);
                    if (column.isRendered()) {
                        writer.startElement("td", null);
                        if (column.getStyle() != null) {
                            writer.writeAttribute("style", column.getStyle(), null);
                        }
                        if (column.getStyleClass() != null) {
                            writer.writeAttribute("class", column.getStyleClass(), null);
                        }

                        column.encodeAll(context);

                        writer.endElement("td");
                    }
                }

                if (itemtip != null && itemtip.isRendered()) {
                    writer.startElement("td", null);
                    writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
                    itemtip.encodeAll(context);
                    writer.endElement("td");
                }

                writer.endElement("tr");
            }
        }

        if (ac.getSuggestions().size() > ac.getMaxResults()) {
            encodeMoreText(context, ac);
        }

        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodeSuggestionsAsList(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        UIComponent itemtip = ac.getFacet("itemtip");
        boolean hasGroupByTooltip = (ac.getValueExpression(AutoComplete.PropertyKeys.groupByTooltip.toString()) != null);

        writer.startElement("ul", ac);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);

        if (items != null) {
            for (Object item : items) {
                writer.startElement("li", null);
                writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);

                if (pojo) {
                    requestMap.put(var, item);
                    String value = converter == null ? String.valueOf(ac.getItemValue()) : converter.getAsString(context, ac, ac.getItemValue());
                    writer.writeAttribute("data-item-value", value, null);
                    writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                    writer.writeAttribute("data-item-class", ac.getItemStyleClass(), null);
                    writer.writeAttribute("data-item-group", ac.getGroupBy(), null);

                    if (hasGroupByTooltip) {
                        writer.writeAttribute("data-item-group-tooltip", ac.getGroupByTooltip(), null);
                    }

                    writer.writeText(ac.getItemLabel(), null);
                }
                else {
                    writer.writeAttribute("data-item-label", item.toString(), null);
                    writer.writeAttribute("data-item-value", item.toString(), null);
                    writer.writeAttribute("data-item-class", ac.getItemStyleClass(), null);

                    writer.writeText(item, null);
                }

                writer.endElement("li");

                if (itemtip != null && itemtip.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
                    itemtip.encodeAll(context);
                    writer.endElement("li");
                }
            }
        }

        if (ac.getSuggestions().size() > ac.getMaxResults()) {
            encodeMoreText(context, ac);
        }

        writer.endElement("ul");

        if (pojo) {
            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, AutoComplete ac) throws IOException {
        String clientId = ac.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AutoComplete", ac.resolveWidgetVar(context), clientId);

        wb.attr("minLength", ac.getMinQueryLength(), 1)
                .attr("delay", ac.getQueryDelay())
                .attr("forceSelection", ac.isForceSelection(), false)
                .attr("scrollHeight", ac.getScrollHeight(), Integer.MAX_VALUE)
                .attr("multiple", ac.isMultiple(), false)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, ac, ac.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                .attr("grouping", ac.getValueExpression(AutoComplete.PropertyKeys.groupBy.toString()) != null, false)
                .attr("queryEvent", ac.getQueryEvent(), null)
                .attr("dropdownMode", ac.getDropdownMode(), null)
                .attr("autoHighlight", ac.isAutoHighlight(), true)
                .attr("myPos", ac.getMy(), null)
                .attr("atPos", ac.getAt(), null)
                .attr("active", ac.isActive(), true)
                .attr("unique", ac.isUnique(), false)
                .attr("dynamic", ac.isDynamic(), false)
                .attr("autoSelection", ac.isAutoSelection(), true)
                .attr("escape", ac.isEscape(), true);

        if (ac.isCache()) {
            wb.attr("cache", true).attr("cacheTimeout", ac.getCacheTimeout());
        }

        String effect = ac.getEffect();
        if (effect != null) {
            wb.attr("effect", effect, null)
                    .attr("effectDuration", ac.getEffectDuration(), Integer.MAX_VALUE);
        }

        wb.attr("emptyMessage", ac.getEmptyMessage(), null)
                .attr("resultsMessage", ac.getResultsMessage(), null);

        if (ac.getFacet("itemtip") != null) {
            wb.attr("itemtip", true, false)
                    .attr("itemtipMyPosition", ac.getItemtipMyPosition(), null)
                    .attr("itemtipAtPosition", ac.getItemtipAtPosition(), null);
        }

        if (ac.isMultiple()) {
            wb.attr("selectLimit", ac.getSelectLimit(), Integer.MAX_VALUE);
        }

        encodeClientBehaviors(context, ac);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        AutoComplete ac = (AutoComplete) component;
        boolean isMultiple = ac.isMultiple();

        if (submittedValue == null || submittedValue.equals("") || ac.isMoreTextRequest(context)) {
            return isMultiple ? new ArrayList() : null;
        }

        Converter converter = ComponentUtils.getConverter(context, component);

        if (isMultiple) {
            String[] values = (String[]) submittedValue;
            List list = new ArrayList();

            for (String value : values) {
                if (isValueBlank(value)) {
                    continue;
                }

                Object convertedValue = converter != null ? converter.getAsObject(context, ac, value) : value;

                if (convertedValue != null) {
                    list.add(convertedValue);
                }
            }

            return list;
        }
        else {
            if (converter != null) {
                return converter.getAsObject(context, component, (String) submittedValue);
            }
            else {
                return submittedValue;
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeMoreText(FacesContext context, AutoComplete ac) throws IOException {
        int colSize = ac.getColums().size();
        String moreText = ac.getMoreText();
        ResponseWriter writer = context.getResponseWriter();

        if (colSize > 0) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", AutoComplete.MORE_TEXT_TABLE_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", colSize, null);
            writer.writeText(moreText, "moreText");
            writer.endElement("td");

            writer.endElement("tr");
        }
        else {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.MORE_TEXT_LIST_CLASS, null);
            writer.writeText(moreText, "moreText");
            writer.endElement("li");
        }
    }
}
