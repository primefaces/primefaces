/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.chart.stackedcolumn;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.resource.ResourceUtils;

public class StackedColumnChartRenderer extends BaseChartRenderer implements PartialRenderer {

	@Override
	protected void encodeScript(FacesContext facesContext, UIChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = chart.getClientId(facesContext);
		String xfieldName = getFieldName(chart.getValueExpression("xfield"));
		List<ChartSeries> series = getSeries(chart);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(document).ready(function(){");
		
		if(!chart.isLive()) {
			encodeLocalData(facesContext, chart, xfieldName, series);
		}
		
		encodeDataSource(facesContext, chart, xfieldName, series);
		encodeSeriesDef(facesContext, chart, series);
		encodeChartWidget(facesContext, chart, clientId, xfieldName);
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private void encodeLocalData(FacesContext facesContext, UIChart chart, String xfieldName, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(getLocalDataVar(chart) + " = [" );
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String xfieldValue = chart.getValueExpression("xfield").getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any
			
			writer.write("{" + xfieldName + ":\"" + xfieldValue + "\"");
			
			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));
				
				writer.write("," + fieldName + ":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}			
			
			writer.write("}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("];\n");
	}
	
	private void encodeDataSource(FacesContext facesContext, UIChart chart, String xfieldName, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String dataSourceVar = getDataSourceVar(chart);
		
		if(chart.isLive()) {
			writer.write("var " + dataSourceVar +  " = new YAHOO.util.DataSource(\"" + getActionURL(facesContext) + "\");\n");
			writer.write(dataSourceVar + ".connMethodPost = true;\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSON;\n");
			writer.write(dataSourceVar + ".responseSchema = {resultsList:\"LiveChartDataResponse.Data\", fields:[\"" + xfieldName + "\"");
		} else {
			writer.write("var " + dataSourceVar +  " = new YAHOO.util.DataSource(" + getLocalDataVar(chart) + ");\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSARRAY;\n");
			writer.write(dataSourceVar + ".responseSchema = {fields:[\"" + xfieldName + "\"");
		}
		
		for (ChartSeries axis : series)
			writer.write(",\"" + getFieldName(axis.getValueExpression("value")) + "\"");
		
		writer.write("]};\n");
	}
	
	private void encodeSeriesDef(FacesContext facesContext, UIChart chart, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + getSeriesDefVar(chart) + " = [");
		for (int i=0; i<series.size();i++) {
			ChartSeries currentSeries = series.get(i);
			String fieldName = getFieldName(currentSeries.getValueExpression("value"));
			writer.write("{displayName:\"" + currentSeries.getLabel() + "\", yField:\"" + fieldName + "\"");
			
			if(currentSeries.getStyle() != null) {
				writer.write(",style:" + currentSeries.getStyle());
			}
			
			writer.write("}");
			
			if(i != series.size() -1)
				writer.write(",");
		}
		writer.write("];\n");
	}
	
	private void encodeChartWidget(FacesContext facesContext, UIChart chart, String clientId, String xfieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		StackedColumnChart stackedColumnChart = (StackedColumnChart) chart;
		String chartVar = getChartVar(stackedColumnChart);
		
		writer.write(chartVar + " = new YAHOO.widget.StackedColumnChart(\"" + clientId + "\"," + getDataSourceVar(stackedColumnChart) + ",{");
		writer.write("xField:\"" + xfieldName + "\"");
		writer.write(",series:" + getSeriesDefVar(stackedColumnChart) );
		writer.write(",expressInstall:\"" + ResourceUtils.getResourceURL(facesContext,"/yui/assets/expressinstall.swf") + "\"");
		
		if(stackedColumnChart.getWmode() != null)
			writer.write(",wmode:\"" + stackedColumnChart.getWmode() + "\"");
		
		if(stackedColumnChart.isLive()) {
			writer.write(",polling:" + stackedColumnChart.getRefreshInterval());
			writer.write(",request:PrimeFaces.widget.ChartUtils.createPollingParams(\"" + clientId + "\")");
		}
		
		if(stackedColumnChart.getStyle() != null) {
			writer.write(",style:" + stackedColumnChart.getStyle() + "");
		}
		
		if(chart.getDataTipFunction() != null) {
			writer.write(",dataTipFunction:" + chart.getDataTipFunction());
		}
		
		writer.write("});\n");
		
		if(shouldRenderAttribute(stackedColumnChart.getMinY()) || shouldRenderAttribute(stackedColumnChart.getMaxY())) {
			writer.write("var axis = new YAHOO.widget.NumericAxis();\n");
			if(shouldRenderAttribute(stackedColumnChart.getMinY()))
				writer.write("axis.minimum = " + stackedColumnChart.getMinY() + ";\n");
			if(shouldRenderAttribute(stackedColumnChart.getMaxY()))
				writer.write("axis.maximum = " + stackedColumnChart.getMaxY() + ";\n");
			
			writer.write(chartVar + ".set('yAxis', axis);\n");
		}
		
		if(chart.getItemSelectListener() != null)
			encodeItemSelectEvent(facesContext, chart);
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		((ServletResponse) facesContext.getExternalContext().getResponse()).setContentType("application/json");
		ResponseWriter writer = facesContext.getResponseWriter();
		StackedColumnChart chart = (StackedColumnChart) component;
		String xfieldName = getFieldName(chart.getValueExpression("xfield"));
		List<ChartSeries> series = getSeries(chart);
		
		writer.write("{");
		writer.write("\"LiveChartDataResponse\" : {\n");
		writer.write("\t\"Data\" : [\n");
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String xfieldValue = chart.getValueExpression("xfield").getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any
			
			writer.write("{\"" + xfieldName + "\":\"" + xfieldValue + "\"");
			
			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));
				
				writer.write(",\"" + fieldName + "\":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}			
			
			writer.write("}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("\t]\n");
		writer.write("}\n");
		writer.write("}");
	}
}