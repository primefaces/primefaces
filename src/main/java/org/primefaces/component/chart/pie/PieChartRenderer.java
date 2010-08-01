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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.resource.ResourceUtils;

public class PieChartRenderer extends BaseChartRenderer {

	@Override
	protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		PieChart chart = (PieChart) uichart;
		String clientId = chart.getClientId(context);
        String widgetVar = createUniqueWidgetVar(context, chart);
		
		String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
		String dataFieldName = getFieldName(chart.getValueExpression("dataField"));
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function(){");

        writer.write(widgetVar + " = new PrimeFaces.widget.PieChart('" + clientId + "', {");

        writer.write("categoryField:'" + categoryFieldName + "'");
		writer.write(",dataField:'" + dataFieldName + "'");
		writer.write(",expressInstall:'" + ResourceUtils.getResourceURL(context,"/yui/assets/expressinstall.swf") + "'");

        if(chart.getWmode() != null) {
			writer.write(",wmode:'" + chart.getWmode() + "'");
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

        encodeLocalData(context, chart, categoryFieldName, dataFieldName);

        encodeItemSelectEvent(context, chart);
		
		writer.write("})});");

		writer.endElement("script");
	}
	
	protected void encodeLocalData(FacesContext facesContext, UIChart chart, String categoryFieldName, String dataFieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(",data: [" );
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String categoryFieldValue = chart.getValueExpression("categoryField").getValue(facesContext.getELContext()).toString();
			String dataFieldValue = chart.getValueExpression("dataField").getValue(facesContext.getELContext()).toString();
			
			writer.write("{" + categoryFieldName + ":'" + categoryFieldValue + "'," + dataFieldName + ":" + dataFieldValue + "}");
			
			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
}