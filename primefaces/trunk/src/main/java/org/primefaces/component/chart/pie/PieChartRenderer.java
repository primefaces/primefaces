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
package org.primefaces.component.chart.pie;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.PieChartModel;

public class PieChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		PieChart chart = (PieChart) component;

        if(chart.isLiveDataRequest(context)) {
            encodeData(context, chart, true);
        }
        else {
            encodeResources(context);
            encodeMarkup(context, chart);
            encodeScript(context, chart);
        }
	}

	protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		PieChart chart = (PieChart) uichart;
		String clientId = chart.getClientId(context);
				
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("$(function(){");

        writer.write(chart.resolveWidgetVar() + " = new PrimeFaces.widget.PieChart('" + clientId + "', {");

        encodeCommonConfig(context, chart);

        writer.write(",categoryField:'category'");
        writer.write(",dataField:'data'");
        
		if(chart.getSeriesStyle() != null) {
			writer.write(",series: [{style:" + chart.getSeriesStyle() + "}]");
		}

        encodeData(context, chart, false);
        
		writer.write("});});");

		writer.endElement("script");
	}

	protected void encodeData(FacesContext context, PieChart chart, boolean remote) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        if(remote) {
            writer.write("{");
        } else {
            writer.write(",");
        }

		writer.write("\"data\":[" );

        if(chart.hasModel()) {
            PieChartModel model = (PieChartModel) chart.getModel();

            for(Iterator<String> it = model.getData().keySet().iterator(); it.hasNext();) {
                String category = it.next();

                writer.write("{\"category\":\"" + category + "\",\"data\":" + model.getData().get(category) + "}");

                if(it.hasNext())
                    writer.write(",");
                
            }
        }
        else {
            Collection<?> value = (Collection<?>) chart.getValue();
            
            for(Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
                context.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
                
                writer.write("{\"category\":\"" + chart.getCategoryField() + "\",\"data\":" + chart.getDataField() + "}");

                if(iterator.hasNext())
                    writer.write(",");
            }

            context.getExternalContext().getRequestMap().remove(chart.getVar());
        }

        writer.write("]");

        if(remote) {
            writer.write("}");
        }
	}
}