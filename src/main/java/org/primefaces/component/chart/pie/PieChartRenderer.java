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
package org.primefaces.component.chart.pie;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.resource.ResourceUtils;

public class PieChartRenderer extends BaseChartRenderer implements PartialRenderer {
	
	@Override
	protected void encodeScript(FacesContext facesContext, UIChart uichart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		PieChart chart = (PieChart) uichart;
		String clientId = chart.getClientId(facesContext);
		
		String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
		String dataFieldName = getFieldName(chart.getValueExpression("dataField"));
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(document).ready(function(){");
		
		if(!chart.isLive()) {
			encodeLocalData(facesContext, chart, categoryFieldName, dataFieldName);
		}
		
		encodeDataSource(facesContext, chart, categoryFieldName, dataFieldName);
		encodeChartWidget(facesContext, chart, clientId, categoryFieldName, dataFieldName);
		
		writer.write("});");

		writer.endElement("script");
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		((ServletResponse) facesContext.getExternalContext().getResponse()).setContentType("application/json");
		PieChart chart = (PieChart) component;
		
		String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
		String dataFieldName = getFieldName(chart.getValueExpression("dataField"));
		
		writer.write("{");
		writer.write("\"LiveChartDataResponse\" : {");
		writer.write("\"Data\" : [");
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String categoryFieldValue = chart.getValueExpression("categoryField").getValue(facesContext.getELContext()).toString();
			String dataFieldValue = chart.getValueExpression("dataField").getValue(facesContext.getELContext()).toString();
			
			writer.write("{\"" + categoryFieldName + "\":\"" + categoryFieldValue + "\",\"" + dataFieldName + "\":" + dataFieldValue + "}");
			
			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
		writer.write("}");
		writer.write("}");
	}
	
	private void encodeLocalData(FacesContext facesContext, UIChart chart, String categoryFieldName, String dataFieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + getLocalDataVar(chart) + " = [" );
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String categoryFieldValue = chart.getValueExpression("categoryField").getValue(facesContext.getELContext()).toString();
			String dataFieldValue = chart.getValueExpression("dataField").getValue(facesContext.getELContext()).toString();
			
			writer.write("{" + categoryFieldName + ":'" + categoryFieldValue + "'," + dataFieldName + ":" + dataFieldValue + "}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("];\n");
	}
	
	private void encodeDataSource(FacesContext facesContext, UIChart chart, String categoryFieldName, String dataFieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String dataSourceVar = getDataSourceVar(chart);
		
		if(chart.isLive()) {
			writer.write("var " + dataSourceVar + " = new YAHOO.util.DataSource(\"" + getActionURL(facesContext) + "\");\n");
			writer.write(dataSourceVar + ".connMethodPost = true;\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSON;\n");
			writer.write(dataSourceVar + ".responseSchema = {resultsList:\"LiveChartDataResponse.Data\", fields:[\"" + categoryFieldName + "\",\"" + dataFieldName + "\"]};\n");
		} else {
			writer.write("var " + dataSourceVar + " = new YAHOO.util.DataSource(" + getLocalDataVar(chart) + ");\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSARRAY;\n");
			writer.write(dataSourceVar + ".responseSchema = {fields:[\"" + categoryFieldName + "\",\"" + dataFieldName + "\"]};\n");
		}
	}
	
	private void encodeChartWidget(FacesContext facesContext, PieChart chart, String clientId, String categoryFieldName, String dataFieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(getChartVar(chart) + " = new YAHOO.widget.PieChart(\"" + clientId + "\", " + getDataSourceVar(chart) + ",{");
		writer.write("categoryField:\"" + categoryFieldName + "\"");
		writer.write(",dataField:\"" + dataFieldName + "\"");
		writer.write(",expressInstall:\"" + ResourceUtils.getResourceURL(facesContext,"/yui/assets/expressinstall.swf") + "\"");
		
		if(chart.getWmode() != null)
			writer.write(",wmode:\"" + chart.getWmode() + "\"");
		
		if(chart.isLive()) {
			writer.write(",polling:" + chart.getRefreshInterval());
			writer.write(",request:PrimeFaces.widget.ChartUtils.createPollingParams(\"" + clientId + "\")");
		}
		
		if(chart.getStyle() != null) {
			writer.write(",style:" + chart.getStyle() + "");
		}
		if(chart.getSeriesStyle() != null) {
			writer.write(",series: [{ style: " + chart.getSeriesStyle() + " }]");
		}
		if(chart.getDataTipFunction() != null) {
			writer.write(",dataTipFunction:" + chart.getDataTipFunction());
		}
		
		writer.write("});\n");
		
		if(chart.getItemSelectListener() != null)
			encodeItemSelectEvent(facesContext, chart);
	}
}