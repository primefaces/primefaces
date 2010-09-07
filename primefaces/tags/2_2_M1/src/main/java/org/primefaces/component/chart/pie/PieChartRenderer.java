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

public class PieChartRenderer extends BaseChartRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		UIChart chart = (UIChart) component;

        if(chart.isLiveDataRequest(context)) {
            String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
            String dataFieldName = getFieldName(chart.getValueExpression("dataField"));

            encodeData(context, chart, categoryFieldName, dataFieldName, true);
        } else {
            encodeResources(context);
            encodeMarkup(context, chart);
            encodeScript(context, chart);
        }
	}

	protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		PieChart chart = (PieChart) uichart;
		String clientId = chart.getClientId(context);
		
		String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
		String dataFieldName = getFieldName(chart.getValueExpression("dataField"));
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function(){");

        writer.write(chart.resolveWidgetVar() + " = new PrimeFaces.widget.PieChart('" + clientId + "', {");

        encodeCommonConfig(context, chart);
        
        encodeData(context, chart, categoryFieldName, dataFieldName, false);

        writer.write(",categoryField:'" + categoryFieldName + "'");
		writer.write(",dataField:'" + dataFieldName + "'");

		if(chart.getSeriesStyle() != null) {
			writer.write(",series: [{ style: " + chart.getSeriesStyle() + " }]");
		}
        
		writer.write("})});");

		writer.endElement("script");
	}

	protected void encodeData(FacesContext facesContext, UIChart chart, String categoryFieldName, String dataFieldName, boolean remote) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

        if(remote) {
            writer.write("{");
        } else {
            writer.write(",");
        }

		writer.write("\"data\": [" );
		
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

        if(remote) {
            writer.write("}");
        }
	}
}