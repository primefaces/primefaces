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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class BaseChartRenderer extends CoreRenderer {

    private final static Logger logger = Logger.getLogger(BaseChartRenderer.class.getName());
	
    @Override
	public void decode(FacesContext fc, UIComponent component) {
		String clientId = component.getClientId(fc);
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();

		if(params.containsKey(clientId + "_ajaxItemSelect")) {
			int seriesIndex = Integer.parseInt(params.get(clientId + "_seriesIndex"));
			int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

			component.queueEvent(new ItemSelectEvent(component, itemIndex, seriesIndex));
        }
	}
	
	protected void encodeResources(FacesContext facesContext) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        writer.write("YAHOO.widget.Chart.SWFURL = '" + getResourceRequestPath(facesContext, "yui/charts/assets/charts.swf") + "'");
		writer.endElement("script");
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

        if(chart.isLive()) {
            writer.write(",live:true");
            writer.write(",refreshInterval:" + chart.getRefreshInterval());
        }
    }
    
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}