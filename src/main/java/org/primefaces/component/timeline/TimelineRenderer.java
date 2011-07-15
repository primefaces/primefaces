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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class TimelineRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Timeline timeline = (Timeline) component;
        
        encodeMarkup(context, timeline);
        encodeScript(context, timeline);
    }

    public void encodeMarkup(FacesContext context, Timeline timeline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = timeline.getClientId(context);
        
        writer.startElement("div", timeline);
        writer.writeAttribute("id", clientId, "id");
        
        writer.startElement("div", timeline);
        writer.writeAttribute("id", clientId + "_container", null);
        writer.endElement("div");
        
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, Timeline timeline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = timeline.getClientId(context);
        
        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        
        writer.write("$(function() {");

		writer.write(timeline.resolveWidgetVar() + " = new PrimeFaces.widget.Schedule('" + clientId +"', {");
        
        writer.write("});});");
        
        writer.endElement("script");
    }
}
