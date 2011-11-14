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
package org.primefaces.component.selectmanycheckbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.InputRenderer;

public class SelectManyCheckboxRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        if(checkbox.isDisabled()) {
            return;
        }

        decodeBehaviors(context, checkbox);

        String clientId = checkbox.getClientId(context);
        String[] values = context.getExternalContext().getRequestParameterValuesMap().get(clientId);

        if(values != null) {
            checkbox.setSubmittedValue(values);
        } else {
            checkbox.setSubmittedValue(new String[0]);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;

        encodeMarkup(context, checkbox);
        encodeScript(context, checkbox);
    }

    protected void encodeMarkup(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = styleClass == null ? SelectManyCheckbox.STYLE_CLASS : SelectManyCheckbox.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("table", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null)
            writer.writeAttribute("style", style, "style");

        encodeSelectItems(context, checkbox);

        writer.endElement("table");
    }

    protected void encodeScript(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectManyCheckbox','" + checkbox.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        if(checkbox.isDisabled()) 
            writer.write(",disabled: true");

        encodeClientBehaviors(context, checkbox);

        writer.write("});");

        endScript(writer);
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
		String[] values = (String[]) submittedValue;
		Converter converter = getConverter(context, checkbox);
        List list = null;

        if(converter != null) {
            list = new ArrayList();

            for(String value : values) {
                list.add(converter.getAsObject(context, checkbox, value));
            }
        }
        else {
            list = Arrays.asList(values);
        }

        return list;
	}

    protected void encodeOptionInput(FacesContext context, SelectManyCheckbox checkbox, String clientId, String containerClientId, boolean checked, boolean disabled, String label, String formattedValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-checkbox-inputwrapper", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", containerClientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", formattedValue, null);

        if(checked) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(checkbox.getOnchange() != null) writer.writeAttribute("onchange", checkbox.getOnchange(), null);

        writer.endElement("input");

        writer.endElement("div");
    }

    protected void encodeOptionLabel(FacesContext context, SelectManyCheckbox checkbox, String containerClientId, SelectItem option) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("label", null);
        writer.writeAttribute("for", containerClientId, null);
        if(option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        writer.endElement("label");
    }

    protected void encodeOptionOutput(FacesContext context, SelectManyCheckbox checkbox, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = "ui-checkbox-box ui-widget ui-corner-all ui-checkbox-relative ui-state-default";
        styleClass = checked ? styleClass + " ui-state-active" : styleClass;

        String iconClass = "ui-checkbox-icon";
        iconClass = checked ? iconClass + " ui-icon ui-icon-check" : iconClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeSelectItems(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = getConverter(context, checkbox);
        Object value = checkbox.getValue();
        String layout = checkbox.getLayout();
        boolean pageDirection = layout != null && layout.equals("pageDirection");

        for(SelectItem selectItem : selectItems) {
            if(pageDirection) {
                writer.startElement("tr", null);
            }

            encodeOption(context, checkbox, value, converter, selectItem);

            if(pageDirection) {
                writer.endElement("tr");
            }
        }
    }

    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, SelectItem option) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
        String formattedValue = getOptionAsString(context, component, converter, option.getValue());
        String clientId = component.getClientId(context);
        String containerClientId = component.getContainerClientId(context);
        boolean checked = componentValue != null && ((Collection) componentValue).contains(option.getValue());
        boolean disabled = checkbox.isDisabled() || option.isDisabled();

        writer.startElement("td", null);

        String styleClass = "ui-checkbox ui-widget";
        if(disabled) {
            styleClass += " ui-state-disabled";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptionInput(context, checkbox, clientId, containerClientId, checked, disabled, option.getLabel(), formattedValue);
        encodeOptionOutput(context, checkbox, checked);

        writer.endElement("div");
        writer.endElement("td");

        writer.startElement("td", null);
        encodeOptionLabel(context, checkbox, containerClientId, option);
        writer.endElement("td");
    }
}
