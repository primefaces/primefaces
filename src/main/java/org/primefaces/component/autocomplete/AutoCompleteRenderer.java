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
package org.primefaces.component.autocomplete;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;

import org.primefaces.event.SelectEvent;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class AutoCompleteRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        AutoComplete autoComplete = (AutoComplete) component;

        if(autoComplete.isDisabled() || autoComplete.isReadonly()) {
            return;
        }

        decodeBehaviors(facesContext, autoComplete);

        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = autoComplete.getClientId(facesContext);
        String valueParam = autoComplete.getVar() == null ? clientId + "_input" : clientId + "_hinput";
        String submittedValue = params.get(valueParam);

        if(submittedValue != null) {
            autoComplete.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        AutoComplete autoComplete = (AutoComplete) component;
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

        if(params.containsKey(autoComplete.getClientId(facesContext) + "_query")) {
            encodeResults(facesContext, component);
        } else {
            encodeMarkup(facesContext, autoComplete);
            encodeScript(facesContext, autoComplete);
        }
    }

    @SuppressWarnings("unchecked")
    public void encodeResults(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        AutoComplete autoComplete = (AutoComplete) component;
        String clientId = autoComplete.getClientId(facesContext);
        String var = autoComplete.getVar();

        MethodExpression me = autoComplete.getCompleteMethod();
        String query = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_query");

        List results = (List) me.invoke(facesContext.getELContext(), new Object[]{query});
        writer.write("{");
        writer.write("\"results\" : [");

        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
            Object result = iterator.next();

            if (result != null) {

                if (var == null) {
                    writer.write("{\"label\":\"" + escape((String) result) + "\"}");
                } else {
                    facesContext.getExternalContext().getRequestMap().put(var, result);
                    String itemLabel = escape(autoComplete.getItemLabel());
                    String itemValue = escape(ComponentUtils.getStringValueToRender(facesContext, autoComplete, autoComplete.getItemValue()));

                    writer.write("{");
                    writer.write("\"label\":\"" + itemLabel + "\"");
                    writer.write(",\"data\":\"" + itemValue + "\"");
                    writer.write("}");
                }

                if (iterator.hasNext()) {
                    writer.write(",");
                }
            }
        }

        if (var != null) {
            facesContext.getExternalContext().getRequestMap().remove(var);	//clean
        }

        writer.write("]}");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        } else {
            return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
        }
    }

    protected void encodeMarkup(FacesContext context, AutoComplete ac) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ac.getClientId(context);
        Object value = ac.getValue();
        String styleClass = ac.getStyleClass();
        styleClass = styleClass == null ? AutoComplete.STYLE_CLASS : AutoComplete.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("type", "text", null);
        if (value != null) {
            if (ac.getVar() == null) {
                writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, ac), null);
            } else {
                context.getExternalContext().getRequestMap().put(ac.getVar(), value);
                writer.writeAttribute("value", ac.getItemLabel(), null);
            }
        }

        renderPassThruAttributes(context, ac, HTML.INPUT_TEXT_ATTRS);

        writer.writeAttribute("class", styleClass, "styleClass");

        writer.endElement("input");

        //hidden input for pojo support
        if (ac.getVar() != null) {
            writer.startElement("input", null);
            writer.writeAttribute("id", clientId + "_hinput", null);
            writer.writeAttribute("name", clientId + "_hinput", null);
            writer.writeAttribute("type", "hidden", null);
            if (value != null) {
                writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, ac, ac.getItemValue()), null);
            }
            writer.endElement("input");

            context.getExternalContext().getRequestMap().remove(ac.getVar());	//clean
        }

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext facesContext, AutoComplete ac) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = ac.getClientId(facesContext);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function(){");

        writer.write(ac.resolveWidgetVar() + " = new PrimeFaces.widget.AutoComplete('" + clientId + "', {");
        writer.write("pojo:" + (ac.getVar() != null));
        writer.write(",maxResults:" + ac.getMaxResults());

        //Configuration
        if(ac.getMinQueryLength() != 1) writer.write(",minLength:" + ac.getMinQueryLength());
        if(ac.getQueryDelay() != 300) writer.write(",delay:" + ac.getQueryDelay());
        if(ac.isDisabled()) writer.write(",disabled:true");
        if(ac.isForceSelection()) writer.write(",forceSelection:true");

        //Instant ajax selection
        if (ac.getSelectListener() != null) {
            writer.write(",ajaxSelect:true");

            if (ac.getOnSelectUpdate() != null) {
                writer.write(",onSelectUpdate:'" + ComponentUtils.findClientIds(facesContext, ac, ac.getOnSelectUpdate()) + "'");
            }
        }

        //Client side callbacks
        if(ac.getOnstart() != null) writer.write(",onstart:function(request) {" + ac.getOnstart() + ";}");
        if(ac.getOncomplete() != null) writer.write(",oncomplete:function(response) {" + ac.getOncomplete() + ";}");

        encodeClientBehaviors(facesContext, ac);

        writer.write("});});");

        writer.endElement("script");
    }

    @Override
    public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
        AutoComplete autoComplete = (AutoComplete) component;
        Object value = submittedValue;
        Converter converter = autoComplete.getConverter();

        //first ask the converter
        if (converter != null) {
            value = converter.getAsObject(facesContext, autoComplete, (String) submittedValue);
        } //Try to guess
        else {
            ValueExpression expr = autoComplete.getValueExpression("value");
            if (expr != null) {
                Class<?> valueType = expr.getType(facesContext.getELContext());
                Converter converterForType = facesContext.getApplication().createConverter(valueType);

                if (converterForType != null) {
                    value = converterForType.getAsObject(facesContext, autoComplete, (String) submittedValue);
                }
            }
        }

        //Queue ajax select event
        if (facesContext.getExternalContext().getRequestParameterMap().containsKey(autoComplete.getClientId(facesContext) + "_ajaxSelect")) {
            SelectEvent selectEvent = new SelectEvent(autoComplete, value);
            selectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
            autoComplete.queueEvent(selectEvent);
        }

        return value;
    }
}
