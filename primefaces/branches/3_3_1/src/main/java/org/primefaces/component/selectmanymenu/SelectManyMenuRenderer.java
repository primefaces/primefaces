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
package org.primefaces.component.selectmanymenu;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import org.primefaces.renderkit.SelectManyRenderer;

public class SelectManyMenuRenderer extends SelectManyRenderer {
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Menu").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyMenu menu = (SelectManyMenu) component;

        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }

    protected void encodeMarkup(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? SelectManyMenu.CONTAINER_CLASS : SelectManyMenu.CONTAINER_CLASS + " " + styleClass;
        styleClass = menu.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !menu.isValid() ? styleClass + " ui-state-error" : styleClass;

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) writer.writeAttribute("style", style, "style");

        encodeInput(context, menu, clientId);
        encodeList(context, menu);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('SelectListbox','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");        
        writer.write(",selection:'multiple'");

        if(menu.isDisabled()) 
            writer.write(",disabled:true");

        encodeClientBehaviors(context, menu);

        writer.write("});");

        endScript(writer);
    }

    protected void encodeInput(FacesContext context, SelectManyMenu menu, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";
        
        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("select", menu);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("size", "2", null);               //prevent browser to send value when no item is selected
 
        if(menu.getTabindex() != null) writer.writeAttribute("tabindex", menu.getTabindex(), null);
        if(menu.getOnchange() != null) writer.writeAttribute("onchange", menu.getOnchange(), null);

        encodeSelectItems(context, menu);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);
        //dom created by widget
        writer.endElement("ul");
    }

    protected void encodeSelectItems(FacesContext context, SelectManyMenu menu) throws IOException {
        List<SelectItem> selectItems = getSelectItems(context, menu);
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, menu, values, submittedValues, converter, selectItem);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectManyMenu menu, Object values, Object submittedValues, Converter converter, SelectItem option) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
        boolean disabled = option.isDisabled() || menu.isDisabled();

        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, menu, itemValue, valuesArray, converter);
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
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        return selectMany.getClientId(context) + "_input";
    }
    
    
}