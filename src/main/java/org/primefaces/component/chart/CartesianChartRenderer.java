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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.series.ChartSeries;

public class CartesianChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		CartesianChart chart = (CartesianChart) component;

        if(chart.isLiveDataRequest(context)) {
            String categoryFieldName = getFieldName(chart.getValueExpression(chart.getCategoryField()));
            List<ChartSeries> series = getSeries(chart);

            encodeData(context, chart, categoryFieldName, series, true);;
        } else {
            encodeResources(context);
            encodeMarkup(context, chart);
            encodeScript(context, chart);
        }
	}

	protected void encodeScript(FacesContext context, UIChart uiChart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        CartesianChart chart = (CartesianChart) uiChart;
		String clientId = chart.getClientId(context);
		String categoryFieldName = getFieldName(chart.getValueExpression(chart.getCategoryField()));
		List<ChartSeries> series = getSeries(chart);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(function(){");

		writer.write(chart.resolveWidgetVar() + " = new " + chart.getChartWidget() + "('" + clientId + "', {");

        encodeCommonConfig(context, chart);

        encodeAxises(context, chart, clientId, categoryFieldName);

        encodeData(context, chart, categoryFieldName, series, false);

		encodeSeries(context, chart, series);

        writer.write("," + chart.getCategoryAxis() + ":'" + categoryFieldName + "'");

        writer.write(",fields:['" + categoryFieldName + "'");
        for (ChartSeries axis : series) {
			writer.write(",'" + getFieldName(axis.getValueExpression("value")) + "'");
        }
        writer.write("]");

		writer.write("});});");

		writer.endElement("script");
	}

	protected void encodeData(FacesContext facesContext, CartesianChart chart, String xfieldName, List<ChartSeries> series, boolean remote) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
        String categoryFieldName = getFieldName(chart.getValueExpression(chart.getCategoryField()));

        if(remote) {
            writer.write("{");
        } else {
            writer.write(",");
        }

		writer.write("\"data\": [" );

		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());

			String categoryFieldValue = chart.getValueExpression(chart.getCategoryField()).getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any

			writer.write("{\"" + categoryFieldName + "\":\"" + categoryFieldValue + "\"");

			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));

				writer.write(",\"" + fieldName + "\":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}

			writer.write("}");

			if(iterator.hasNext())
				writer.write(",");
		}

		writer.write("]");

        if(remote) {
            writer.write("}");
        }
	}

	private void encodeSeries(FacesContext facesContext, CartesianChart chart, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		writer.write(",series : [");
		for (Iterator<ChartSeries> it = series.iterator(); it.hasNext();) {
            ChartSeries currentSeries = it.next();

			String fieldName = getFieldName(currentSeries.getValueExpression("value"));
			writer.write("{displayName:'" + currentSeries.getLabel() + "', " + chart.getNumericAxis() + ":'" + fieldName + "'");

			if(currentSeries.getStyle() != null) {
				writer.write(",style:" + currentSeries.getStyle());
			}

			writer.write("}");

			if(it.hasNext())
				writer.write(",");
		}
		writer.write("]");
	}

	protected void encodeAxises(FacesContext facesContext, CartesianChart chart, String clientId, String xfieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

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
}
