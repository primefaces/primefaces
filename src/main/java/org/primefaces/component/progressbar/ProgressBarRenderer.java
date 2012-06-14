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
package org.primefaces.component.progressbar;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;

public class ProgressBarRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ProgressBar progressBar = (ProgressBar) component;
        String clientId = progressBar.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if(params.containsKey(clientId)) {
            RequestContext.getCurrentInstance().addCallbackParam(progressBar.getClientId(context) + "_value", progressBar.getValue());
        }
        
        decodeBehaviors(context, progressBar);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ProgressBar progressBar = (ProgressBar) component;

        encodeMarkup(context, progressBar);
        
        if(!progressBar.isDisplayOnly()) {
            encodeScript(context, progressBar);
        }
    }

    protected void encodeMarkup(FacesContext context, ProgressBar progressBar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int value = progressBar.getValue();
        String labelTemplate = progressBar.getLabelTemplate();
        String style = progressBar.getStyle();
        String styleClass = progressBar.getStyleClass();
        styleClass = styleClass == null ? ProgressBar.CONTAINER_CLASS : ProgressBar.CONTAINER_CLASS + " " + styleClass;
        
        if(progressBar.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }
        
        writer.startElement("div", progressBar);
        writer.writeAttribute("id", progressBar.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        //value
        writer.startElement("div", progressBar);
        writer.writeAttribute("class", ProgressBar.VALUE_CLASS, null);
        if(value != 0) {
            writer.writeAttribute("style", "display:block;width:" + value + "%", style);
        }
        writer.endElement("div");
        
        //label
        writer.startElement("div", progressBar);
        writer.writeAttribute("class", ProgressBar.LABEL_CLASS, null);
        if(labelTemplate != null && value != 0) {
            writer.writeAttribute("style", "display:block", style);
            writer.write(labelTemplate.replaceAll("\\{value\\}", String.valueOf(value)));
        }
        writer.endElement("div");
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ProgressBar progressBar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = progressBar.getClientId(context);
        boolean isAjax = progressBar.isAjax();

        startScript(writer, clientId);

        writer.write("$(function() {");

        writer.write("PrimeFaces.cw('ProgressBar','" + progressBar.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",initialValue:" + progressBar.getValue());
        writer.write(",ajax:" + isAjax);
        
        if(progressBar.getLabelTemplate() != null)  
            writer.write(",labelTemplate:'" + progressBar.getLabelTemplate() + "'");

        if(isAjax) {
            writer.write(",interval:" + progressBar.getInterval());

            encodeClientBehaviors(context, progressBar);
        }

        writer.write("});});");

        endScript(writer);
    }
}
