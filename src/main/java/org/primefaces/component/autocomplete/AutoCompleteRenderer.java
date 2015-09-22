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
package org.primefaces.component.autocomplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;
import org.primefaces.component.column.Column;
import org.primefaces.context.RequestContext;
import org.primefaces.event.AutoCompleteEvent;
import org.primefaces.expression.SearchExpressionFacade;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class AutoCompleteRenderer extends InputRenderer {

	private static final Logger LOG = Logger.getLogger(AutoCompleteRenderer.class.getName());
	
    @Override
    public void decode(FacesContext context, UIComponent component) {
        AutoComplete ac = (AutoComplete) component;
        String clientId = ac.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if(ac.isDisabled() || ac.isReadonly()) {
            return;
        }
        
        if(ac.isMultiple()) {
            decodeMultiple(context, ac);
        }
        else {
            decodeSingle(context, ac);
        }
        
        decodeBehaviors(context, ac);
        
        //AutoComplete event
        String query = params.get(clientId + "_query");
        if(query != null) {
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

        if(submittedValue != null) {
            ac.setSubmittedValue(submittedValue);
        }
    }
    
    protected void decodeMultiple(FacesContext context, AutoComplete ac) {
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();
        String clientId = ac.getClientId(context);
        String[] submittedValues = params.get(clientId + "_hinput");
        
        if(submittedValues != null) {
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

        if(query != null) {
            encodeResults(context, component, query);
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
        
        if(maxResults != Integer.MAX_VALUE && results != null && results.size() > maxResults) {
            results = results.subList(0, ac.getMaxResults());
        }

        encodeSuggestions(context, ac, results);
    }

    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        if(ac.isMultiple())
            encodeMultipleMarkup(context, ac);
        else
            encodeSingleMarkup(context, ac);
    }
    
    protected void encodeSingleMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.STYLE_CLASS : AutoComplete.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if(ac.getStyle() != null) {
            writer.writeAttribute("style", ac.getStyle(), null);
        }

        encodeInput(context, ac, clientId);
        
        if(ac.getVar() != null) {
            encodeHiddenInput(context, ac, clientId);
        }
        
        if(ac.isDropdown()) {
            encodeDropDown(context, ac);
        }
        
        encodePanel(context, ac);

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
        String labelledBy = ac.getLabelledBy();
            
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", ac.getType(), null);
        writer.writeAttribute("class", inputStyleClass, null);
        writer.writeAttribute("autocomplete", "off", null);
        
        if(labelledBy != null) {
            writer.writeAttribute("aria-labelledby", labelledBy, null);
        }
        
        if(inputStyle != null) {
            writer.writeAttribute("style", inputStyle, null);
        }
        
        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, ac, HTML.INPUT_TEXT_EVENTS);
        
        if(var == null) {
            itemLabel = ComponentUtils.getValueToRender(context, ac);
            
            if(itemLabel != null) {
                writer.writeAttribute("value", itemLabel , null);
            }
        }
        else {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            
            if(ac.isValid()) {
                requestMap.put(var, ac.getValue());
                itemLabel = ac.getItemLabel();
            }
            else {
                Object submittedValue = ac.getSubmittedValue();
                itemLabel = (submittedValue == null) ? null : String.valueOf(submittedValue);
                
                Object value = ac.getValue();
                
                if(itemLabel == null && value != null) {
                    requestMap.put(var, value);
                    itemLabel = ac.getItemLabel();
                }
            }

            if(itemLabel != null) {
                writer.writeAttribute("value", itemLabel, null);
            }
            
            requestMap.remove(var);
        }

        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(ac.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(ac.isRequired()) writer.writeAttribute("aria-required", "true", null);
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, ac);
        }

        writer.endElement("input");
    }
    
    protected void encodeHiddenInput(FacesContext context, AutoComplete ac, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String valueToRender = ComponentUtils.getValueToRender(context, ac);
        
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_hinput", null);
        writer.writeAttribute("name", clientId + "_hinput", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        if(valueToRender != null) {
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
        writer.writeAttribute("class", "ui-helper-hidden", null);
        
        if(ac.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        
        for(String value : values) {
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
        boolean disabled = ac.isDisabled()||ac.isReadonly();
        if(disabled) {
            dropdownClass += " ui-state-disabled";
        }
        
        writer.startElement("button", ac);
        writer.writeAttribute("class", dropdownClass, null);
        writer.writeAttribute("type", "button", null);
        if(disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if(ac.getTabindex() != null) {
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

        writer.startElement("div", null);
        writer.writeAttribute("id", ac.getClientId(context) + "_panel", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(ac.getPanelStyle() != null) {
            writer.writeAttribute("style", ac.getPanelStyle(), null);
        }
        
        writer.endElement("div");
    }
    
    protected void encodeMultipleMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        String inputId = clientId + "_input";
        List values = (List) ac.getValue();
        List<String> stringValues = new ArrayList<String>();
        boolean disabled = ac.isDisabled();
        
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.MULTIPLE_STYLE_CLASS : AutoComplete.MULTIPLE_STYLE_CLASS + " " + styleClass;
        String listClass = disabled ? AutoComplete.MULTIPLE_CONTAINER_CLASS + " ui-state-disabled" : AutoComplete.MULTIPLE_CONTAINER_CLASS;
        listClass = ac.isValid() ? listClass : listClass + " ui-state-error";
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(ac.getStyle() != null) {
            writer.writeAttribute("style", ac.getStyle(), null);
        }
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", listClass, null);
        
        if(values != null && !values.isEmpty()) {
        	Converter converter = ComponentUtils.getConverter(context, ac);
        	String var = ac.getVar();
        	boolean pojo = var != null;

            for(Iterator<Object> it = values.iterator(); it.hasNext();) {
                Object value = it.next();
                Object itemValue = null;
                String itemLabel = null;
                
                if(pojo) {
                    context.getExternalContext().getRequestMap().put(var, value);
                    itemValue = ac.getItemValue();
                    itemLabel = ac.getItemLabel();
                }
                else {
                    itemValue = value;
                    itemLabel = String.valueOf(value);
                }
                
                String tokenValue = converter != null ? converter.getAsString(context, ac, itemValue) : String.valueOf(itemValue);
                
                writer.startElement("li", null);
                writer.writeAttribute("data-token-value", tokenValue, null);
                writer.writeAttribute("class", AutoComplete.TOKEN_DISPLAY_CLASS, null);
                
                writer.startElement("span", null);
                writer.writeAttribute("class", AutoComplete.TOKEN_LABEL_CLASS, null);
                writer.writeText(itemLabel, null);
                writer.endElement("span");
                
                writer.startElement("span", null);
                writer.writeAttribute("class", AutoComplete.TOKEN_ICON_CLASS, null);
                writer.endElement("span");
                
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
        writer.writeAttribute("autocomplete", "off", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", "disabled");

        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, ac, HTML.INPUT_TEXT_EVENTS);
        
        writer.endElement("input");
        writer.endElement("li");
        
        writer.endElement("ul");
        
        if(ac.isDropdown()) {
            encodeDropDown(context, ac);
        }
                        
        encodePanel(context, ac);
        
        encodeHiddenSelect(context, ac, clientId, stringValues);

        writer.endElement("div");
    }
    
    protected void encodeSuggestions(FacesContext context, AutoComplete ac, List items) throws IOException {
        boolean customContent = ac.getColums().size() > 0;
        Converter converter = ComponentUtils.getConverter(context, ac);
        
        if(customContent) {
            encodeSuggestionsAsTable(context, ac, items, converter);
        } 
        else {
            encodeSuggestionsAsList(context, ac, items, converter);
        }
    }
    
    protected void encodeSuggestionsAsTable(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        UIComponent itemtip = ac.getFacet("itemtip");
        boolean hasHeader = false;
        boolean hasGroupByTooltip = (ac.getValueExpression("groupByTooltip") != null);
        
        for(Column column : ac.getColums()) {
            if(column.isRendered() && (column.getHeaderText() != null || column.getFacet("header") != null)) {
                hasHeader = true;
                break;
            }
        }
        
        writer.startElement("table", ac);
        writer.writeAttribute("class", AutoComplete.TABLE_CLASS, null);
        
        if(hasHeader) { 
            writer.startElement("thead", ac);
            for(Column column : ac.getColums()) {
                if(!column.isRendered()) {
                    continue;
                }

                String headerText = column.getHeaderText();
                UIComponent headerFacet = column.getFacet("header");
                String styleClass = column.getStyleClass() == null ? "ui-state-default" : "ui-state-default " + column.getStyleClass();
                
                writer.startElement("th", null);
                writer.writeAttribute("class", styleClass, null);
                
                if(column.getStyle() != null) 
                    writer.writeAttribute("style", column.getStyle(), null);
                
                if(headerFacet != null)
                    headerFacet.encodeAll(context);
                else if(headerText != null)
                    writer.write(headerText);

                writer.endElement("th");
            }
            writer.endElement("thead");
        }
        
        writer.startElement("tbody", ac);
        
        if(items != null) {
            for(Object item : items) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", AutoComplete.ROW_CLASS, null);

                if(pojo) {
                    requestMap.put(var, item);
                    String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                    writer.writeAttribute("data-item-value", value, null);
                    writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                    writer.writeAttribute("data-item-group", ac.getGroupBy(), null);

                    if(hasGroupByTooltip) {
                        writer.writeAttribute("data-item-group-tooltip", ac.getGroupByTooltip(), null);
                    }
                }

                for(Column column : ac.getColums()) {
                    if(column.isRendered()) {
                        writer.startElement("td", null);
                        if(column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
                        if(column.getStyleClass() != null) writer.writeAttribute("class", column.getStyleClass(), null);

                        column.encodeAll(context);

                        writer.endElement("td");
                    }
                }

                if(itemtip != null && itemtip.isRendered()) {
                    writer.startElement("td", null);
                    writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
                    itemtip.encodeAll(context);
                    writer.endElement("td");
                }

                writer.endElement("tr");     
            }
        }

        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodeSuggestionsAsList(FacesContext context, AutoComplete ac, List items, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = ac.getVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        boolean pojo = var != null;
        UIComponent itemtip = ac.getFacet("itemtip");
        boolean hasGroupByTooltip = (ac.getValueExpression("groupByTooltip") != null);
        
        writer.startElement("ul", ac);
        writer.writeAttribute("class", AutoComplete.LIST_CLASS, null);

        if(items != null) {
            for(Object item : items) {
                writer.startElement("li", null);
                writer.writeAttribute("class", AutoComplete.ITEM_CLASS, null);

                if(pojo) {
                    requestMap.put(var, item);
                    String value = converter == null ? (String) ac.getItemValue() : converter.getAsString(context, ac, ac.getItemValue());
                    writer.writeAttribute("data-item-value", value, null);
                    writer.writeAttribute("data-item-label", ac.getItemLabel(), null);
                    writer.writeAttribute("data-item-group", ac.getGroupBy(), null);

                    if(hasGroupByTooltip) {
                        writer.writeAttribute("data-item-group-tooltip", ac.getGroupByTooltip(), null);
                    }

                    writer.writeText(ac.getItemLabel(), null);
                }
                else {
                    writer.writeAttribute("data-item-label", item, null);
                    writer.writeAttribute("data-item-value", item, null);

                    writer.writeText(item, null);
                }

                writer.endElement("li");

                if(itemtip != null && itemtip.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", AutoComplete.ITEMTIP_CONTENT_CLASS, null);
                    itemtip.encodeAll(context);
                    writer.endElement("li");
                }
            }
        }
        
        writer.endElement("ul");
        
        if(pojo) {
            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, AutoComplete ac) throws IOException {
        String clientId = ac.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("AutoComplete", ac.resolveWidgetVar(), clientId);
        
        wb.attr("minLength", ac.getMinQueryLength(), 1)
            .attr("delay", ac.getQueryDelay())
            .attr("forceSelection", ac.isForceSelection(), false)
            .attr("scrollHeight", ac.getScrollHeight(), Integer.MAX_VALUE)
            .attr("multiple", ac.isMultiple(), false)
            .attr("appendTo", SearchExpressionFacade.resolveClientId(context, ac, ac.getAppendTo()), null)
            .attr("grouping", ac.getValueExpression("groupBy") != null, false)
            .attr("queryEvent", ac.getQueryEvent(), null)
            .attr("dropdownMode", ac.getDropdownMode(), null)
            .attr("autoHighlight", ac.isAutoHighlight(), true)
            .attr("myPos", ac.getMy(), null)
            .attr("atPos", ac.getAt(), null)
            .attr("active", ac.isActive(), true);
        
        if(ac.isCache()) {
            wb.attr("cache", true).attr("cacheTimeout", ac.getCacheTimeout());            
        }

        String effect = ac.getEffect();
        if(effect != null) {
            wb.attr("effect", effect, null)
                .attr("effectDuration", ac.getEffectDuration(), Integer.MAX_VALUE);
        }
        
        wb.attr("emptyMessage", ac.getEmptyMessage(), null)
            .attr("resultsMesage", ac.getResultsMessage(), null);
        
        if(ac.getFacet("itemtip") != null) {
            wb.attr("itemtip", true, false)
                .attr("itemtipMyPosition", ac.getItemtipMyPosition(), null)
                .attr("itemtipAtPosition", ac.getItemtipAtPosition(), null);
        }
        
        if(ac.isMultiple()) {
            wb.attr("selectLimit", ac.getSelectLimit(), Integer.MAX_VALUE);
        }
        
        encodeClientBehaviors(context, ac);

        wb.finish();
    }
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {        
        if(submittedValue == null || submittedValue.equals("")) {
            return null;
        }
        
        AutoComplete ac = (AutoComplete) component;
		Converter converter = ComponentUtils.getConverter(context, component);

        if(ac.isMultiple()) {
            String[] values = (String[]) submittedValue;
            List list = new ArrayList();

            for(String value : values) {
                if(isValueBlank(value)) {
                    continue;
                }
            
                Object convertedValue = converter != null ? converter.getAsObject(context, ac, value) : value;

                if(convertedValue != null) {
                    list.add(convertedValue);
                }
            }
            
            return list;
        }
        else {
            if(converter != null)
                return converter.getAsObject(context, component, (String) submittedValue);
            else
                return submittedValue;
        }
	}
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
