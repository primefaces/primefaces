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
package org.primefaces.component.timeline;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.timeline.TimelineColumn;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.renderkit.CoreRenderer;

public class TimelineRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        UITimeline tl = (UITimeline) component;
        
        encodeMarkup(context, tl);
        encodeScript(context, tl);
    }

    public void encodeMarkup(FacesContext context, UITimeline tl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tl.getClientId(context);
        TimelineModel model = (TimelineModel) tl.getValue();
        
        writer.startElement("div", tl);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "ui-timeline ui-widget ui-widget-content ui-corner-all", "id");
        
        writer.startElement("div", tl);
        writer.writeAttribute("class", "ui-timeline-container", null);
        
        for(TimelineColumn column : model.getColumns()) {
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-timeline-column", null);
            
            writer.startElement("span", null);
            writer.writeAttribute("class", "ui-timeline-column-title", null);
            writer.write(column.getTitle());
            writer.endElement("span");
            
            writer.startElement("ul", null);
            writer.writeAttribute("class", "ui-timeline-events", null);
            for(TimelineEvent event : column.getEvents()) {
                writer.startElement("li", null);
                writer.writeAttribute("class", "ui-timeline-event ui-state-default", null);
                writer.write(event.getTitle());
                writer.endElement("li");
            }
            writer.endElement("ul");
            
            writer.endElement("div");
        }
        
        writer.endElement("div");
        
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UITimeline tl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tl.getClientId(context);
        
        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        
        writer.write("$(function() {");

		writer.write(tl.resolveWidgetVar() + " = new PrimeFaces.widget.Timeline('" + clientId +"', {");
        
        writer.write("});});");
        
        writer.endElement("script");
    }
    
    protected void encodeEvents(FacesContext context, TimelineEvent event) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        
    }
}
