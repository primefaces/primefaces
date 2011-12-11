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
package org.primefaces.component.chart.ohlc;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.OhlcChartModel;

public class OhlcChartRenderer extends BaseChartRenderer {

   @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OhlcChart chart = (OhlcChart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        OhlcChart chart = (OhlcChart) uichart;
        String clientId = chart.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("PrimeFaces.cw('OhlcChart','" + chart.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        encodeData(context, chart);
        
        encodeOptions(context, chart);

        encodeClientBehaviors(context, chart);

        writer.write("},'charts');});");

        endScript(writer);
    }
     
    protected void encodeOptions(FacesContext context, OhlcChart chart) throws IOException {
        
        encodeCommonConfig(context, chart);
        
        encodeSeriesDefaults(context, chart);
    }
    
    protected void encodeSeriesDefaults(FacesContext context, OhlcChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.write(", seriesDefaults : { renderer: $.jqplot.OHLCRenderer, rendererOptions : {");
        
        if(chart.isCandleStick())
            writer.write("candleStick:true");
 
        writer.write("}}");
    }

    private void encodeData(FacesContext context, OhlcChart chart) throws IOException {
        context.getResponseWriter().write(",data:[" + ((OhlcChartModel)chart.getValue()).toString() + "]");
    }
}