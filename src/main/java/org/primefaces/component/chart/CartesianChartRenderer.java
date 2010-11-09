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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.model.chart.CartesianChartModel;

public class CartesianChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		CartesianChart chart = (CartesianChart) component;

        if(chart.isLiveDataRequest(context)) {
            encodeData(context, chart, getSeries(chart), true);
        }
        else {
            encodeResources(context);
            encodeMarkup(context, chart);
            encodeScript(context, chart);
        }
	}

	protected void encodeScript(FacesContext context, UIChart uiChart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        CartesianChart chart = (CartesianChart) uiChart;
		String clientId = chart.getClientId(context);
		List<ChartSeries> series = getSeries(chart);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(function(){");

		writer.write(chart.resolveWidgetVar() + " = new " + chart.getChartWidget() + "('" + clientId + "', {");

        encodeCommonConfig(context, chart);
        encodeAxises(context, chart, clientId);
        encodeData(context, chart, series, false);
		encodeSeries(context, chart, series);
        encodeFields(context, chart, series);

        writer.write("," + chart.getCategoryAxis() + ":'category'");

		writer.write("});});");

		writer.endElement("script");
	}

	protected void encodeData(FacesContext context, CartesianChart chart, List<ChartSeries> series, boolean remote) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        if(remote) {
            writer.write("{");
        } else {
            writer.write(",");
        }

		writer.write("\"data\": [" );

        if(chart.hasModel()) {
            CartesianChartModel model = (CartesianChartModel) chart.getModel();

            for(Iterator<String> iter = model.getCategoryFields().iterator(); iter.hasNext();) {
                String categoryField = iter.next();

                writer.write("{\"category\":\"" + categoryField + "\"");

                for(ChartSeries chartSeries : series) {
                    writer.write(",\"" + chartSeries.getKey() + "\":\"" + chartSeries.getData().get(categoryField) + "\"");
                }

                writer.write("}");

                if(iter.hasNext()) {
                    writer.write(",");
                }
            }

        }
        else {
            Collection<?> value = (Collection<?>) chart.getValue();
            for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
                context.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());

                Object categoryFieldValue = chart.getValueExpression(chart.getCategoryField()).getValue(context.getELContext());

                writer.write("{\"category\":\"" + categoryFieldValue + "\"");

                for(ChartSeries chartSeries : series) {
                    Object seriesValue = chartSeries.getValueExpression("value").getValue(context.getELContext());

                    writer.write(",\"" + chartSeries.getId() + "\":" + seriesValue);
                }

                writer.write("}");

                if(iterator.hasNext())
                    writer.write(",");
            }
        }

		writer.write("]");

        if(remote) {
            writer.write("}");
        }
	}

	protected void encodeSeries(FacesContext context, CartesianChart chart, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.write(",series:[");

        for(Iterator<ChartSeries> iterator = series.iterator(); iterator.hasNext();) {
            ChartSeries chartSeries = iterator.next();

            writer.write("{displayName:'" + chartSeries.getLabel() + "'," + chart.getNumericAxis() + ":'" + chartSeries.getKey() + "'");
          

            if(chartSeries.getStyle() != null) {
                writer.write(",style:" + chartSeries.getStyle());
            }

            writer.write("}");

            if(iterator.hasNext())
                writer.write(",");
        }

		writer.write("]");
	}

	protected void encodeAxises(FacesContext context, CartesianChart chart, String clientId) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        if(shouldRenderAttribute(chart.getMinY()))
			writer.write(",minY: " + chart.getMinY());
		if(shouldRenderAttribute(chart.getMaxY()))
			writer.write(",maxY: " + chart.getMaxY());

		if(chart.getTitleX() != null)
			writer.write(",titleX: '" + chart.getTitleX() + "'");
		if(chart.getTitleY() != null)
			writer.write(",titleY: '" + chart.getTitleY() + "'");

		if(chart.getLabelFunctionX() != null)
			writer.write(",labelFunctionX:" + chart.getLabelFunctionX());
		if(chart.getLabelFunctionY() != null)
			writer.write(",labelFunctionY:" + chart.getLabelFunctionY());
	}

    protected void encodeFields(FacesContext context, CartesianChart chart, List<ChartSeries> series) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.write(",fields:['category'");

        for(ChartSeries chartSeries : series) {
            writer.write(",'" + chartSeries.getKey() + "'");
        }
        
        writer.write("]");
    }

    protected List<ChartSeries> getSeries(UIChart chart) {
        List<ChartSeries> series = null;

        if(chart.hasModel()) {
            series = ((CartesianChartModel) chart.getModel()).getAllSeries();
        }
        else {
            List<UIComponent> children = chart.getChildren();
            series = new ArrayList<ChartSeries>();

            for (UIComponent component : children) {
                if(component instanceof ChartSeries && component.isRendered())
                    series.add((ChartSeries) component);
            }
        }

        return series;
	}
}
