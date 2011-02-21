/*
 * Copyright 2009-2010 Prime Technology.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.InputRenderer;

public class SelectManyMenuRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        SelectManyMenu menu = (SelectManyMenu) component;

        if(menu.isDisabled()) {
            return;
        }

        decodeBehaviors(context, menu);

        String clientId = menu.getClientId(context);
        String[] values = context.getExternalContext().getRequestParameterValuesMap().get(clientId + "_input");

        if(values != null) {
            menu.setSubmittedValue(values);
        } else {
            menu.setSubmittedValue(new String[0]);
        }
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

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(menu.resolveWidgetVar() + " = new PrimeFaces.widget.SelectListbox('" + clientId + "',{");
        
        writer.write("selection:'multiple'");

        if(menu.isDisabled()) writer.write(",disabled:true");

        encodeClientBehaviors(context, menu);

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeInput(FacesContext context, SelectManyMenu menu, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", menu);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        writer.startElement("select", menu);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected
        if(menu.getOnchange() != null) writer.writeAttribute("onchange", menu.getOnchange(), null);

        encodeSelectItems(context, menu);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", null);

        writer.endElement("ul");
    }

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		SelectManyMenu menu = (SelectManyMenu) component;
		String[] values = (String[]) submittedValue;
		Converter converter = getConverter(context, menu);
        List list = null;

        if(converter != null) {
            list = new ArrayList();

            for(String value : values) {
                list.add(converter.getAsObject(context, menu, value));
            }
        }
        else {
            list = Arrays.asList(values);
        }

        return list;
	}

    @Override
    protected void encodeOption(FacesContext context, UIInput component, Object componentValue, Converter converter, String label, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String formattedValue = formatOptionValue(context, component, converter, value);

        writer.startElement("option", null);
        writer.writeAttribute("value", formattedValue, null);

        if(componentValue != null && ((List) componentValue).contains(value)) {
            writer.writeAttribute("selected", "selected", null);
        }

        writer.write(label);
        writer.endElement("option");
    }
}
