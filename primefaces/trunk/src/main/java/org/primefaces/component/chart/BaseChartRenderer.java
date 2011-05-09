/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.chart;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class BaseChartRenderer extends CoreRenderer {

    private final static Logger logger = Logger.getLogger(BaseChartRenderer.class.getName());
	
    @Override
	public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
	}
		
	protected void encodeMarkup(FacesContext context, UIChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(context), null);

        if(chart.getStyle() != null)
            writer.writeAttribute("style", chart.getStyle(), "style");

		if(chart.getStyleClass() != null)
			writer.writeAttribute("class", chart.getStyleClass(), "styleClass");
		
		writer.endElement("div");
	}
	
    protected void encodeCommonConfig(FacesContext context, UIChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String legendPosition = chart.getLegendPosition();

        if(chart.getTitle() != null) {
            writer.write(",title:'" + chart.getTitle() + "'");
        }

        if(legendPosition != null) {
            writer.write(",legend:{");
            writer.write("show:true");
            writer.write(",location:'" + legendPosition + "'}");
        }
    }

    protected void encodeBehaviors(FacesContext context, UIChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Map<String,List<ClientBehavior>> behaviorEvents = chart.getClientBehaviors();

        if(!behaviorEvents.isEmpty()) {
            List<ClientBehaviorContext.Parameter> params = Collections.emptyList();

            writer.write(",behaviors:{");

            for(Iterator<String> eventIterator = behaviorEvents.keySet().iterator(); eventIterator.hasNext();) {
                String event = eventIterator.next();
                ClientBehavior clientBehavior = behaviorEvents.get(event).get(0);
                ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, chart, event, chart.getClientId(context), params);

                writer.write(event + ":");
                writer.write("function(data) {" + clientBehavior.getScript(cbc) +  "}");

                if(eventIterator.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("}");
        }
    }
}