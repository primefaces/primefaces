/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.column.Column;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = AutoComplete.DEFAULT_RENDERER, componentFamily = AutoComplete.COMPONENT_FAMILY)
public class AutoCompleteRenderer extends InputRenderer<AutoComplete> {

    @Override
    public void decode(FacesContext context, AutoComplete component) {
        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (!shouldDecode(component)) {
            return;
        }

        if (component.isMultiple()) {
            decodeMultiple(context, component);
        }
        else {
            decodeSingle(context, component);
        }

        decodeBehaviors(context, component);

        // AutoComplete event
        String query = params.get(clientId + "_query");
        if (query != null || component.isClientCacheRequest(context)) {
            AutoCompleteEvent autoCompleteEvent = new AutoCompleteEvent(component, query);
            autoCompleteEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            component.queueEvent(autoCompleteEvent);
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
    public void encodeEnd(FacesContext context, AutoComplete component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(component.getClientId(context) + "_query");

        if ((component.isClientQueryMode() || component.isHybridQueryMode()) && !component.isCache()) {
            component.setCache(true);
        }

        if (query != null) {
            if (component.isDynamicLoadRequest(context)) {
                encodePanel(context, component);
            }
            else {
                encodeResults(context, component);
            }
        }
        else if (component.isClientCacheRequest(context)) {
            encodeResults(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    public void encodeResults(FacesContext context, UIComponent component) throws IOException {
        AutoComplete ac = (AutoComplete) component;
        Object results = ac.getSuggestions();
        int maxResults = ac.getMaxResults();

        if (ac.isServerQueryMode() && maxResults != Integer.MAX_VALUE && results != null && ((List) results).size() > maxResults) {
            results = ((List<?>) results).subList(0, ac.getMaxResults());
        }

        encodeSuggestions(context, ac, results);
    }

    protected void encodeMarkup(FacesContext context, AutoComplete component) throws IOException {
        if (component.isMultiple()) {
            encodeMultipleMarkup(context, component);
        }
        else {
            encodeSingleMarkup(context, component);
        }
    }

    protected void encodeSingleMarkup(FacesContext context, AutoComplete component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean isDropdown = component.isDropdown();
        boolean disabled = component.isDisabled();

        String styleClass = getStyleClassBuilder(context)
                .add(AutoComplete.STYLE_CLASS)
                .add(component.getStyleClass())
                .add(isDropdown, AutoComplete.DROPDOWN_SYLE_CLASS)
                .add(disabled, "ui-state-disabled")
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        encodeInput(context, component, clientId);

        if (component.getVar() != null) {
            encodeHiddenInput(context, component, clientId);
        }

        if (isDropdown) {
            encodeDropDown(context, component, clientId);
        }

        if (!component.isDynamic()) {
            encodePanel(context, component);
        }

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, AutoComplete component, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        String itemLabel;
        String inputStyle = component.getInputStyle();
        String defaultStyleClass = component.isDropdown() ? AutoComplete.INPUT_WITH_DROPDOWN_CLASS : AutoComplete.INPUT_CLASS;
        String inputStyleClass = createStyleClass(component, AutoComplete.PropertyKeys.inputStyleClass.name(), defaultStyleClass);
        String autocompleteProp = (component.getAutocomplete() != null) ? component.getAutocomplete() : "off";

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", component.getType(), null);
        writer.writeAttribute("class", inputStyleClass, null);
        writer.writeAttribute("autocomplete", autocompleteProp, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_COMBOBOX, null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_panel", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, HTML.ARIA_ROLE_LISTBOX, null);

        if (inputStyle != null) {
            writer.writeAttribute("style", inputStyle, null);
        }

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);

        if (var == null) {
            itemLabel = ComponentUtils.getValueToRender(context, component);

            if (itemLabel != null) {
                writer.writeAttribute("value", itemLabel, null);
            }
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (component.isValid()) {
                requestMap.put(var, component.getValue());
                itemLabel = component.getItemLabel();
            }
            else {
                Object submittedValue = component.getSubmittedValue();

                Object value = component.getValue();

                if (submittedValue == null && value != null) {
                    requestMap.put(var, value);
                    itemLabel = component.getItemLabel();
                }
                else if (submittedValue != null) {
                    // retrieve the actual item (pojo) from the converter
                    try {
                        Object item = getConvertedValue(context, component, String.valueOf(submittedValue));
                        requestMap.put(var, item);
                        itemLabel = component.getItemLabel();
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

        renderValidationMetadata(context, component);

        writer.endElement("input");
    }

    protected void encodeHiddenInput(FacesContext context, AutoComplete component, String clientId) throws IOException {
        String valueToRender = ComponentUtils.getValueToRender(context, component);
        renderHiddenInput(context, clientId + "_hinput", valueToRender, component.isDisabled());
    }

    protected void encodeHiddenSelect(FacesContext context, AutoComplete component, String clientId, List<String> values) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = clientId + "_hinput";

        writer.startElement("select", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);

        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        renderValidationMetadata(context, component);

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            writer.startElement("option", null);
            writer.writeAttribute("value", value, null);
            writer.writeAttribute("selected", "selected", null);
            writer.endElement("option");
        }

        writer.endElement("select");
    }

    protected void encodeDropDown(FacesContext context, AutoComplete component, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String dropdownClass = AutoComplete.DROPDOWN_CLASS;
        boolean disabled = component.isDisabled() || component.isReadonly();
        if (disabled) {
            dropdownClass += " ui-state-disabled";
        }

        writer.startElement("button", component);
        writer.writeAttribute("id", clientId + "_button", null);
        writer.writeAttribute("class", dropdownClass, null);
        writer.writeAttribute("type", "button", null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (component.getDropdownTabindex() != null) {
            writer.writeAttribute("tabindex", component.getDropdownTabindex(), null);
        }
        else if (component.getTabindex() != null) {
            writer.writeAttribute("tabindex", component.getTabindex(), null);
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

    protected void encodePanel(FacesContext context, AutoComplete component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = component.getPanelStyleClass();
        styleClass = styleClass == null ? AutoComplete.PANEL_CLASS : AutoComplete.PANEL_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", component.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("tabindex", "-1", null);

        if (component.getPanelStyle() != null) {
            writer.writeAttribute("style", component.getPanelStyle(), null);
        }

        if (component.isDynamic() && component.isDynamicLoadRequest(context)) {
            encodeResults(context, component);
        }

        writer.endElement("span");
    }

    protected void encodeMultipleMarkup(FacesContext context, AutoComplete component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String inputId = clientId + "_input";

        List<?> values;
        if (component.isValid()) {
            values = (List<?>) component.getValue();
        }
        else {
            Object submittedValue = component.getSubmittedValue();
            try {
                values = (List<?>) getConvertedValue(context, component, submittedValue);
            }
            catch (ConverterException ce) {
                values = Arrays.asList((String[]) submittedValue);
            }
        }

        List<String> stringValues = new ArrayList<>();
        boolean disabled = component.isDisabled();
        boolean isDropdown = component.isDropdown();
        String title = component.getTitle();

        String style = component.getStyle();

        String styleClass = getStyleClassBuilder(context)
                .add(AutoComplete.MULTIPLE_STYLE_CLASS)
                .add(component.getStyleClass())
                .add(isDropdown, AutoComplete.DROPDOWN_SYLE_CLASS)
                .add(disabled, "ui-state-disabled")
                .build();

        String listClass = isDropdown ? AutoComplete.MULTIPLE_CONTAINER_WITH_DROPDOWN_CLASS : AutoComplete.MULTIPLE_CONTAINER_CLASS;
        listClass = createStyleClass(component, null, listClass);
        String autocompleteProp = (component.getAutocomplete() != null) ? component.getAutocomplete() : "off";

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
        writer.writeAttribute("tabindex", -1, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
        writer.writeAttribute(HTML.ARIA_ORIENTATION, HTML.ARIA_ORIENTATION_HORIZONTAL, null);

        if (values != null && !values.isEmpty()) {
            Converter converter = ComponentUtils.getConverter(context, component);
            String var = component.getVar();
            boolean pojo = var != null;

            Collection<?> items = component.isUnique() ? new LinkedHashSet<>(values) : values;
            for (Object value : items) {
                Object itemValue = null;
                String itemLabel = null;

                if (pojo) {
                    context.getExternalContext().getRequestMap().put(var, value);
                    itemValue = component.getItemValue();
                    itemLabel = component.getItemLabel();
                }
                else {
                    itemValue = value;
                    itemLabel = String.valueOf(value);
                }

                String tokenValue = converter != null ? converter.getAsString(context, component, itemValue) : String.valueOf(itemValue);
                String itemStyleClass = AutoComplete.TOKEN_DISPLAY_CLASS;
                if (component.getItemStyleClass() != null) {
                    itemStyleClass += " " + component.getItemStyleClass();
                }

                writer.startElement("li", null);
                writer.writeAttribute("data-token-value", tokenValue, null);
                writer.writeAttribute("class", itemStyleClass, null);
                writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_OPTION, null);
                writer.writeAttribute(HTML.ARIA_LABEL, itemLabel, null);
                writer.writeAttribute(HTML.ARIA_SELECTED, "true", null);

                String labelClass = disabled ? AutoComplete.TOKEN_LABEL_DISABLED_CLASS : AutoComplete.TOKEN_LABEL_CLASS;
                writer.startElement("span", null);
                writer.writeAttribute("class", labelClass, null);
                writer.writeText(itemLabel, null);
                writer.endElement("span");

                if (!disabled) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", AutoComplete.TOKEN_ICON_CLASS, null);
                    writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
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

        renderAccessibilityAttributes(context, component);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_COMBOBOX, null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_panel", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, HTML.ARIA_ROLE_LISTBOX, null);

        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("input");
        writer.endElement("li");

        writer.endElement("ul");

        if (component.isDropdown()) {
            encodeDropDown(context, component, clientId);
        }

        if (!component.isDynamic()) {
            encodePanel(context, component);
        }

        encodeHiddenSelect(context, component, clientId, stringValues);

        writer.endElement("div");
    }

    protected void encodeSuggestions(FacesContext context, AutoComplete component, Object items) throws IOException {
        boolean customContent = !component.getColums().isEmpty();
        Converter<?> converter = ComponentUtils.getConverter(context, component);

        if (customContent) {
            ComponentUtils.runWithoutFacesContextVar(context, Constants.HELPER_RENDERER, () -> {
                encodeSuggestionsAsTable(context, component, items, converter);
            });
        }
        else {
            encodeSuggestionsAsList(context, component, items, converter);
        }

        encodeFooter(context, component);
    }

    protected void encodeFooter(FacesContext context, AutoComplete component) throws IOException {
        UIComponent footer = component.getFooterFacet();
        if (FacetUtils.shouldRenderFacet(footer)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-autocomplete-footer", null);
            footer.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected void encodeSuggestionsAsTable(FacesContext context, AutoComplete component, Object items, Converter<?> converter)
            throws IOException {

        // do not render table if empty message and there are no records
        if (items == null
                || (items instanceof Collection<?> && ((Collection<?>) items).isEmpty())
                || (items instanceof Map<?, ?> && ((Map<?, ?>) items).isEmpty())) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        boolean pojo = var != null;
        boolean hasHeader = false;

        for (int i = 0; i < component.getColums().size(); i++) {
            Column column = component.getColums().get(i);
            if (column.isRendered() && (column.getHeaderText() != null || FacetUtils.shouldRenderFacet(column.getFacet("header")))) {
                hasHeader = true;
                break;
            }
        }

        writer.startElement("table", component);
        writer.writeAttribute("class", AutoComplete.TABLE_CLASS, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);

        if (hasHeader) {
            writer.startElement("thead", component);
            for (int i = 0; i < component.getColums().size(); i++) {
                Column column = component.getColums().get(i);
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

                if (FacetUtils.shouldRenderFacet(headerFacet)) {
                    headerFacet.encodeAll(context);
                }
                else if (headerText != null) {
                    writer.writeText(headerText, null);
                }

                writer.endElement("th");
            }
            writer.endElement("thead");
        }

        writer.startElement("tbody", component);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);

        if (items != null) {
            int index = 0;
            if (component.isClientQueryMode() || items instanceof Map) {
                for (Map.Entry<String, List<String>> entry : ((Map<String, List<String>>) items).entrySet()) {
                    String key = entry.getKey();
                    List<String> list = entry.getValue();

                    for (Object item : list) {
                        encodeSuggestionItemsAsTable(context, component, item, converter, pojo, var, key, index++);
                    }
                }
            }
            else {
                for (Object item : (List<?>) items) {
                    encodeSuggestionItemsAsTable(context, component, item, converter, pojo, var, null, index++);
                }

                if (component.hasMoreSuggestions()) {
                    encodeMoreText(context, component);
                }
            }
        }

        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodeSuggestionsAsList(FacesContext context, AutoComplete component, Object items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;

        writer.startElement("ul", component);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);

        if (items != null) {
            int index = 0;
            if (component.isClientQueryMode() || items instanceof Map) {
                for (Map.Entry<String, List<String>> entry : ((Map<String, List<String>>) items).entrySet()) {
                    String key = entry.getKey();
                    List<String> list = entry.getValue();

                    for (Object item : list) {
                        encodeSuggestionItemsAsList(context, component, item, converter, pojo, var, key, index++);
                    }
                }
            }
            else {
                for (Object item : (List) items) {
                    encodeSuggestionItemsAsList(context, component, item, converter, pojo, var, null, index++);
                }

                if (component.hasMoreSuggestions()) {
                    encodeMoreText(context, component);
                }
            }
        }

        writer.endElement("ul");

        if (pojo) {
            requestMap.remove(var);
        }
    }

    protected void encodeSuggestionItemsAsList(FacesContext context, AutoComplete component, Object item, Converter converter,
            boolean pojo, String var, String key, int rowNumber) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        UIComponent itemtip = component.getItemtipFacet();
        boolean hasGroupByTooltip = (component.getValueExpression(AutoComplete.PropertyKeys.groupByTooltip.toString()) != null);

        writer.startElement("li", null);
        writer.writeAttribute("id", component.getClientId(context) + "_item_" + rowNumber, null);
        writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);

        if (pojo) {
            requestMap.put(var, item);
            String value = converter == null ? String.valueOf(component.getItemValue()) : converter.getAsString(context, component, component.getItemValue());
            String itemLabel = component.getItemLabel();
            writer.writeAttribute("data-item-value", value, null);
            writer.writeAttribute("data-item-label", itemLabel, null);
            writer.writeAttribute("data-item-class", component.getItemStyleClass(), null);
            writer.writeAttribute("data-item-group", component.getGroupBy(), null);

            if (key != null) {
                writer.writeAttribute("data-item-key", key, null);
            }

            if (hasGroupByTooltip) {
                writer.writeAttribute("data-item-group-tooltip", component.getGroupByTooltip(), null);
            }

            if (component.isEscape()) {
                writer.writeText(itemLabel, null);
            }
            else {
                writer.write(itemLabel);
            }
        }
        else {
            String itemAsString = item.toString();
            writer.writeAttribute("data-item-label", itemAsString, null);
            writer.writeAttribute("data-item-value", itemAsString, null);
            writer.writeAttribute("data-item-class", component.getItemStyleClass(), null);

            if (key != null) {
                writer.writeAttribute("data-item-key", key, null);
            }

            if (component.isEscape()) {
                writer.writeText(item, null);
            }
            else {
                writer.write(itemAsString);
            }
        }

        writer.endElement("li");

        if (FacetUtils.shouldRenderFacet(itemtip)) {
            writer.startElement("li", null);
            writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
            itemtip.encodeAll(context);
            writer.endElement("li");
        }
    }

    protected void encodeSuggestionItemsAsTable(FacesContext context, AutoComplete component, Object item,
                                                Converter converter, boolean pojo, String var, String key, int index)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        UIComponent itemtip = component.getItemtipFacet();
        boolean hasGroupByTooltip = (component.getValueExpression(AutoComplete.PropertyKeys.groupByTooltip.toString()) != null);

        writer.startElement("tr", null);
        writer.writeAttribute("id", component.getClientId(context) + "_item_" + index, null);
        writer.writeAttribute("class", AutoComplete.ROW_CLASS, null);

        if (pojo) {
            requestMap.put(var, item);
            String value = converter == null ? String.valueOf(component.getItemValue()) : converter.getAsString(context, component, component.getItemValue());
            writer.writeAttribute("data-item-value", value, null);
            writer.writeAttribute("data-item-label", component.getItemLabel(), null);
            writer.writeAttribute("data-item-class", component.getItemStyleClass(), null);
            writer.writeAttribute("data-item-group", component.getGroupBy(), null);

            if (key != null) {
                writer.writeAttribute("data-item-key", key, null);
            }

            if (hasGroupByTooltip) {
                writer.writeAttribute("data-item-group-tooltip", component.getGroupByTooltip(), null);
            }
        }

        for (int i = 0; i < component.getColums().size(); i++) {
            Column column = component.getColums().get(i);
            if (column.isRendered()) {
                writer.startElement("td", null);
                if (key != null) {
                    writer.writeAttribute("data-item-key", key, null);
                }
                if (column.getStyle() != null) {
                    writer.writeAttribute("style", column.getStyle(), null);
                }
                if (column.getStyleClass() != null) {
                    writer.writeAttribute("class", column.getStyleClass(), null);
                }

                encodeIndexedId(context, column, index);

                writer.endElement("td");
            }
        }

        if (FacetUtils.shouldRenderFacet(itemtip)) {
            writer.startElement("td", null);
            writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
            encodeIndexedId(context, itemtip, index);
            writer.endElement("td");
        }

        writer.endElement("tr");
    }

    protected void encodeScript(FacesContext context, AutoComplete component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AutoComplete", component);

        wb.attr("minLength", component.getMinQueryLength(), 1)
                .attr("delay", component.getQueryDelay())
                .attr("forceSelection", component.isForceSelection(), false)
                .attr("scrollHeight", component.getScrollHeight(), Integer.MAX_VALUE)
                .attr("multiple", component.isMultiple(), false)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("grouping", component.getValueExpression(AutoComplete.PropertyKeys.groupBy.toString()) != null, false)
                .attr("queryEvent", component.getQueryEvent(), null)
                .attr("dropdownMode", component.getDropdownMode(), null)
                .attr("highlightSelector", component.getHighlightSelector(), null)
                .attr("autoHighlight", component.isAutoHighlight(), true)
                .attr("showEmptyMessage", component.isShowEmptyMessage(), true)
                .attr("emptyMessage", component.getEmptyMessage(), null)
                .attr("myPos", component.getMy(), null)
                .attr("atPos", component.getAt(), null)
                .attr("active", component.isActive(), true)
                .attr("unique", component.isUnique(), false)
                .attr("dynamic", component.isDynamic(), false)
                .attr("autoSelection", component.isAutoSelection(), true)
                .attr("escape", component.isEscape(), true)
                .attr("queryMode", component.getQueryMode())
                .attr("completeEndpoint", component.getCompleteEndpoint())
                .attr("moreText", component.getMoreText())
                .attr("hasFooter", FacetUtils.shouldRenderFacet(component.getFacet("footer")));

        if (component.isCache()) {
            wb.attr("cache", true).attr("cacheTimeout", component.getCacheTimeout());
        }

        if (FacetUtils.shouldRenderFacet(component.getFacet("itemtip"))) {
            wb.attr("itemtip", true, false)
                    .attr("itemtipMyPosition", component.getItemtipMyPosition(), null)
                    .attr("itemtipAtPosition", component.getItemtipAtPosition(), null);
        }

        if (component.isMultiple()) {
            wb.attr("selectLimit", component.getSelectLimit(), Integer.MAX_VALUE);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        AutoComplete ac = (AutoComplete) component;
        boolean isMultiple = ac.isMultiple();

        if (submittedValue == null || submittedValue.equals("") || ac.isMoreTextRequest(context)) {
            return isMultiple ? new ArrayList<>() : null;
        }

        Converter<?> converter = ComponentUtils.getConverter(context, component);

        if (isMultiple) {
            String[] values = (String[]) submittedValue;
            List<Object> list = new ArrayList<>();

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
    public void encodeChildren(FacesContext context, AutoComplete component) throws IOException {
        // Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeMoreText(FacesContext context, AutoComplete component) throws IOException {
        int colSize = component.getColums().size();
        String moreText = component.getMoreText();
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
            writer.writeAttribute("id", component.getClientId(context) + "_moretext", null);
            writer.writeAttribute("class", AutoComplete.MORE_TEXT_LIST_CLASS, null);
            writer.writeText(moreText, "moreText");
            writer.endElement("li");
        }
    }
}
