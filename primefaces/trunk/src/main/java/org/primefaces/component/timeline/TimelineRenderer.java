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
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.timeline.Timeline;
import org.primefaces.model.timeline.TimelineEvent;
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
        
        writer.startElement("div", tl);
        writer.writeAttribute("id", clientId, "id"); 
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UITimeline tl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tl.getClientId(context);
        List<Timeline> model = tl.getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        
        writer.write("$(function() {");

		writer.write(tl.resolveWidgetVar() + " = new PrimeFaces.widget.Timeline('" + clientId +"', {");

        if(!model.isEmpty()) {
            writer.write("data_source: [");

            for(Iterator<Timeline> it = model.iterator(); it.hasNext();) {
                Timeline timeline = it.next();
                String id = timeline.getId();

                writer.write("{");
                writer.write("\"id\":\"" + timeline.getId() + "\"");
                writer.write(",\"title\":\"" + timeline.getTitle() + "\"");
                writer.write(",\"description\":\"" + timeline.getDescription() + "\"");
                writer.write(",\"initial_zoom\":\"" + timeline.getInitialZoom() + "\"");
   
                if(timeline.getFocusDate() != null) 
                    writer.write(",\"focus_date\":\"" + formatter.format(timeline.getFocusDate()) + "\"");

                //events
                writer.write(",\"events\":[");
                for(Iterator<TimelineEvent> eventIter = timeline.getEvents().iterator(); eventIter.hasNext();) {             
                    encodeEvent(context, eventIter.next(), formatter, id);

                    if(eventIter.hasNext()) {
                        writer.write(",");
                    }
                }
                writer.write("]}");

                if(it.hasNext()) {
                    writer.write(",");
                }
            }

            writer.write("]");
        }
        
        writer.write("});});");
        
        writer.endElement("script");
    }
    
    public void encodeEvent(FacesContext context, TimelineEvent event, SimpleDateFormat formatter, String timelineId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.write("{");
        writer.write("\"id\":\"" + timelineId + "-" + event.getId() + "\"");
        writer.write(",\"title\":\"" + event.getTitle() + "\"");
        writer.write(",\"description\":\"" + event.getDescription() + "\"");
        writer.write(",\"startdate\":\"" + formatter.format(event.getStartDate()) + "\"");
        
        if(event.getEndDate() != null) writer.write(",\"enddate\":\"" + formatter.format(event.getEndDate()) + "\"");
        if(event.getImportance() != 20) writer.write(",\"importance\":\"" + event.getImportance() + "\"");
        
        writer.write("}");
    }
}
