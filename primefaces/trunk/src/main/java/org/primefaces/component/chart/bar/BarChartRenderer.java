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
package org.primefaces.component.chart.bar;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

public class BarChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		BarChart chart = (BarChart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
	}

    protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		BarChart chart = (BarChart) uichart;
		String clientId = chart.getClientId(context);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("$(function(){");

        writer.write(chart.resolveWidgetVar() + " = new PrimeFaces.widget.BarChart('" + clientId + "', {");

        encodeData(context, chart);

        encodeOptions(context, chart);

		writer.write("});});");

		writer.endElement("script");
	}

    protected void encodeData(FacesContext context, BarChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getValue();

		writer.write("data:[" );

        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = it.next();

            writer.write("[");
            for(Iterator<Number> x = series.getData().keySet().iterator(); x.hasNext();) {
                Number value = series.getData().get(x.next());

                writer.write(String.valueOf(value));

                if(x.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");

            if(it.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
	}

    protected void encodeOptions(FacesContext context, BarChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String legendPosition = chart.getLegendPosition();
        CartesianChartModel model = (CartesianChartModel) chart.getValue();

        if(chart.getTitle() != null)
            writer.write(",title:'" + chart.getTitle() + "'");

        if(legendPosition != null) {
            writer.write(",legend:{");
            writer.write("show:true");
            writer.write(",location:'" + legendPosition + "'}");
        }

        writer.write(",series:[");
        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = (ChartSeries) it.next();

            writer.write("{");
            writer.write("label:'" + series.getTitle() + "'");
            writer.write("}");

            if(it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");

        writer.write(",categories:[");
        for(Iterator<Number> it = model.getSeries().get(0).getData().keySet().iterator(); it.hasNext();) {
            writer.write("'" + it.next() + "'");

            if(it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");
    }
}