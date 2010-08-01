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
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;

public class CartesianChartRenderer extends BaseChartRenderer {

    @Override
	protected void encodeScript(FacesContext fc, UIChart uiChart) throws IOException {
		ResponseWriter writer = fc.getResponseWriter();
        CartesianChart chart = (CartesianChart) uiChart;
		String clientId = chart.getClientId(fc);
		String categoryFieldName = getFieldName(chart.getValueExpression(chart.getCategoryField()));
		List<ChartSeries> series = getSeries(chart);
        String widgetVar = createUniqueWidgetVar(fc, chart);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(function(){");

		writer.write(widgetVar + " = new " + chart.getChartWidget() + "('" + clientId + "', {");

		writer.write(chart.getCategoryAxis() + ":'" + categoryFieldName + "'");
		writer.write(",expressInstall:'" + ResourceUtils.getResourceURL(fc,"/yui/assets/expressinstall.swf") + "'");

        writer.write(",fields:['" + categoryFieldName + "'");
        for (ChartSeries axis : series) {
			writer.write(",'" + getFieldName(axis.getValueExpression("value")) + "'");
        }
        writer.write("]");

		if(chart.getWmode() != null) {
			writer.write(",wmode:'" + chart.getWmode() + "'");
        }
		if(chart.getStyle() != null) {
			writer.write(",style:" + chart.getStyle() + "");
		}
		if(chart.getDataTipFunction() != null) {
			writer.write(",dataTipFunction:" + chart.getDataTipFunction());
		}

        if(chart.getItemSelectListener() != null) {
            UIComponent form = ComponentUtils.findParentForm(fc, chart);
            if(form == null) {
                throw new FacesException("Chart: '" + clientId + "' must be inside a form element");
            }

            writer.write(",ajaxItemSelect: true");
            writer.write(",url:'" + getActionURL(fc) + "'");
            writer.write(",formId:'" + form.getClientId(fc) + "'");

            if(chart.getUpdate() != null) writer.write(",update:'" + ComponentUtils.findClientIds(fc, chart, chart.getUpdate()) + "'");
            if(chart.getOncomplete() != null) writer.write(",oncomplete: function() {" + chart.getOncomplete() + ";}");
        }

        encodeAxises(fc, chart, clientId, categoryFieldName);

        encodeLocalData(fc, chart, categoryFieldName, series);

		encodeSeries(fc, chart, series);

		writer.write("});});");

		writer.endElement("script");
	}

	protected void encodeLocalData(FacesContext facesContext, CartesianChart chart, String xfieldName, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
        String categoryFieldName = getFieldName(chart.getValueExpression(chart.getCategoryField()));

		writer.write(",data: [" );

		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());

			String categoryFieldValue = chart.getValueExpression(chart.getCategoryField()).getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any

			writer.write("{" + categoryFieldName + ":'" + categoryFieldValue + "'");

			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));

				writer.write("," + fieldName + ":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}

			writer.write("}");

			if(iterator.hasNext())
				writer.write(",");
		}

		writer.write("]");
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
