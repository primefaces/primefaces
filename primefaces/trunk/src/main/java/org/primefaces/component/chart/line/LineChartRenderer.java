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
package org.primefaces.component.chart.line;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

public class LineChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		LineChart chart = (LineChart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
	}

    protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		LineChart chart = (LineChart) uichart;
		String clientId = chart.getClientId(context);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("$(function(){");

        writer.write(chart.resolveWidgetVar() + " = new PrimeFaces.widget.LineChart('" + clientId + "', {");

        encodeData(context, chart);

        encodeOptions(context, chart);

		writer.write("});});");

		writer.endElement("script");
	}

    protected void encodeData(FacesContext context, LineChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getValue();

		writer.write("data:[" );

        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = it.next();
            
            writer.write("[");
            for(Iterator<Object> x = series.getData().keySet().iterator(); x.hasNext();) {
                Number xValue = (Number) x.next();
                Number yValue = series.getData().get(xValue);

                writer.write("[");
                writer.write(xValue + "," + yValue);
                writer.write("]");

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

    protected void encodeOptions(FacesContext context, LineChart chart) throws IOException {
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
            LineChartSeries series = (LineChartSeries) it.next();

            writer.write("{");
            writer.write("label:'" + series.getTitle() + "'");
            writer.write(",showLine:" + series.isShowLine());
            writer.write(",markerOptions:{style:'" + series.getMarkerStyle() + "'}");
            writer.write("}");

            if(it.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
        
    }
}