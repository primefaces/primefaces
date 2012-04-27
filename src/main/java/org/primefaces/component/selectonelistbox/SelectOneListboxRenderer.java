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
package org.primefaces.component.selectonelistbox;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.SelectOneRenderer;

public class SelectOneListboxRenderer extends SelectOneRenderer {

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Listbox").getConvertedValue(context, component, submittedValue);
	}
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneListbox listbox = (SelectOneListbox) component;

        encodeMarkup(context, listbox);
        encodeScript(context, listbox);
    }

    protected void encodeMarkup(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);
        
        String style = listbox.getStyle();
        String styleClass = listbox.getStyleClass();
        styleClass = styleClass == null ? SelectOneListbox.CONTAINER_CLASS : SelectOneListbox.CONTAINER_CLASS + " " + styleClass;
        styleClass = listbox.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !listbox.isValid() ? styleClass + " ui-state-error" : styleClass;
        
        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) writer.writeAttribute("style", style, "style");

        encodeInput(context, listbox, clientId);
        encodeList(context, listbox);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectListbox','" + listbox.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",selection:'single'");

        encodeClientBehaviors(context, listbox);

        writer.write("});");

        endScript(writer);
    }

    protected void encodeInput(FacesContext context, SelectOneListbox listbox, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", listbox);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        
        writer.startElement("select", listbox);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected
        
        if(listbox.getTabindex() != null) writer.writeAttribute("tabindex", listbox.getTabindex(), null);
        if(listbox.getOnchange() != null) writer.writeAttribute("onchange", listbox.getOnchange(), null);

        encodeSelectItems(context, listbox);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectOneListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);
        //dom created by widget
        writer.endElement("ul");
    }

    protected void encodeSelectItems(FacesContext context, SelectOneListbox listbox) throws IOException {
        List<SelectItem> selectItems = getSelectItems(context, listbox);
        Converter converter = listbox.getConverter();
        Object values = getValues(listbox);
        Object submittedValues = getSubmittedValues(listbox);
        
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, listbox, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectOneListbox listbox, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, listbox, converter, option.getValue());
        boolean disabled = option.isDisabled() || listbox.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, listbox, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }

        writer.startElement("option", null);
        writer.writeAttribute("value", itemValueAsString, null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(selected) writer.writeAttribute("selected", "selected", null);

        writer.write(option.getLabel());

        writer.endElement("option");
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }    
}