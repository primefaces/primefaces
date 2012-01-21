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
package org.primefaces.component.log;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class LogRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Log log = (Log) component;
        
        encodeMarkup(context, log);
        encodeScript(context, log);
    }

    protected void encodeMarkup(FacesContext context, Log log) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //container
        writer.startElement("div", log);
        writer.writeAttribute("id", log.getClientId(context), "id");
        writer.writeAttribute("class", Log.CONTAINER_CLASS, null);
        
        //header
        writer.startElement("div", null);
        writer.writeAttribute("class", Log.HEADER_CLASS, null);
        encodeIcon(context, Log.CLEAR_BUTTON_CLASS, "ui-icon ui-icon-trash", "Clear");
        encodeIcon(context, Log.ALL_BUTTON_CLASS, "ui-icon ui-icon-note", "All");
        encodeIcon(context, Log.INFO_BUTTON_CLASS, "ui-icon ui-icon-info", "Info");
        encodeIcon(context, Log.WARN_BUTTON_CLASS, "ui-icon ui-icon-notice", "Warn");
        encodeIcon(context, Log.DEBUG_BUTTON_CLASS, "ui-icon ui-icon-search", "Debug");
        encodeIcon(context, Log.ERROR_BUTTON_CLASS, "ui-icon ui-icon-alert", "Error");
        writer.endElement("div");
        
        //content
        writer.startElement("div", log);
        writer.writeAttribute("class", Log.CONTENT_CLASS, null);
        writer.startElement("ul", null);
        writer.writeAttribute("class", Log.ITEMS_CLASS, null);
        writer.endElement("ul");
        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    protected void encodeIcon(FacesContext context, String anchorClass, String iconClass, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", anchorClass, null);
        writer.writeAttribute("title", title, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }
    
    protected void encodeScript(FacesContext context, Log log) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = log.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("PrimeFaces.cw('Log','" + log.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("});});");

        endScript(writer);
    }
}
