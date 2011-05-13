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
package org.primefaces.component.chart.pie;

import java.io.IOException;
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

        encodeMarkup(context, chart);
        encodeScript(context, chart);
	}

	protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		PieChart chart = (PieChart) uichart;
		String clientId = chart.getClientId(context);
				
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("$(function(){");

        writer.write(chart.resolveWidgetVar() + " = new PrimeFaces.widget.PieChart('" + clientId + "', {");

        encodeData(context, chart);

        encodeOptions(context, chart);
        
        encodeClientBehaviors(context, chart);

		writer.write("});});");

		writer.endElement("script");
	}

	protected void encodeData(FacesContext context, PieChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.write("data:[[" );
        PieChartModel model = (PieChartModel) chart.getValue();

        for(Iterator<String> it = model.getData().keySet().iterator(); it.hasNext();) {
            String key = it.next();
            Number value = model.getData().get(key);

            writer.write("['" + key + "'," + value + "]");

            if(it.hasNext())
                writer.write(",");
        }

        writer.write("]]");
	}

    protected void encodeOptions(FacesContext context, PieChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        encodeCommonConfig(context, chart);

        //chart specific config
        if(chart.getDiameter() != Integer.MIN_VALUE) 
            writer.write(",diameter:" + chart.getDiameter());
        if(chart.getSliceMargin() != 0)
            writer.write(",sliceMargin:" + chart.getSliceMargin());
        if(chart.isFill() == false)
            writer.write(",fill:false");
    }
}