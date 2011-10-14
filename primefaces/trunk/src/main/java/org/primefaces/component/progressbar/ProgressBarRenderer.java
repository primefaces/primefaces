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

        FacesContext.getCurrentInstance().renderResponse();
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ProgressBar progressBar = (ProgressBar) component;

        encodeMarkup(context, progressBar);
        encodeScript(context, progressBar);
    }

    protected void encodeMarkup(FacesContext context, ProgressBar progressBar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", progressBar);
        writer.writeAttribute("id", progressBar.getClientId(context), "id");
        if(progressBar.getStyle() != null) writer.writeAttribute("style", progressBar.getStyle(), "style");
        if(progressBar.getStyleClass() != null) writer.writeAttribute("class", progressBar.getStyleClass(), "styleClass");
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ProgressBar progressBar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = progressBar.getClientId(context);
        boolean isAjax = progressBar.isAjax();

        startScript(writer, clientId);

        writer.write("$(function() {");

        writer.write(progressBar.resolveWidgetVar() + " = new PrimeFaces.widget.ProgressBar('" + clientId + "', {");
        writer.write("value:" + progressBar.getValue());
        writer.write(",ajax:" + isAjax);

        if(isAjax) {
            writer.write(",interval:" + progressBar.getInterval());

            if(progressBar.getOncomplete() != null) {
                writer.write(",oncomplete:function(xhr, status, args) {" + progressBar.getOncomplete() + "}");
            }

            encodeClientBehaviors(context, progressBar);
        }

        if(progressBar.isDisabled()) {
            writer.write(",disabled:true");
        }

        writer.write("});});");

        endScript(writer);
    }
}
