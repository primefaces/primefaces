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
        if(tl.getStyle() != null) writer.writeAttribute("style", tl.getStyle(), "style");
        if(tl.getStyleClass() != null) writer.writeAttribute("class", tl.getStyleClass(), "styleClass"); 
        
        //rest of the dom is created on the client side
        
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UITimeline tl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tl.getClientId(context);
        List<Timeline> model = tl.getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        startScript(writer, clientId);
        
        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Timeline','" + tl.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",min_zoom:" + tl.getMinZoom());
        writer.write(",max_zoom:" + tl.getMaxZoom());
        
        if(!tl.isZoomable())
            writer.write(",display_zoom_level:false);");

        if(!model.isEmpty()) {
            writer.write(",data_source: [");

            for(Iterator<Timeline> it = model.iterator(); it.hasNext();) {
                Timeline timeline = it.next();
                String id = timeline.getId();

                writer.write("{");
                writer.write("\"id\":\"" + timeline.getId() + "\"");
                writer.write(",\"title\":\"" + timeline.getTitle() + "\"");
                writer.write(",\"initial_zoom\":\"" + timeline.getInitialZoom() + "\"");
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
        
        writer.write("},'timeline');});");
        
        endScript(writer);
    }
    
    public void encodeEvent(FacesContext context, TimelineEvent event, SimpleDateFormat formatter, String timelineId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.write("{");
        writer.write("\"id\":\"" + timelineId + "-" + event.getId() + "\"");
        writer.write(",\"title\":\"" + event.getTitle() + "\"");
        writer.write(",\"description\":\"" + event.getDescription() + "\"");
        writer.write(",\"startdate\":\"" + formatter.format(event.getStartDate()) + "\"");
        writer.write(",\"importance\":\"" + event.getImportance() + "\"");
        
        if(event.getEndDate() != null) 
            writer.write(",\"enddate\":\"" + formatter.format(event.getEndDate()) + "\"");
        if(event.getIcon() != null) 
            writer.write(",\"icon\":\"" + getResourceURL(context , event.getIcon()) + "\"");

        writer.write("}");
    }
}
