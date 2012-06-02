/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.chart.metergauge;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.MeterGaugeChartModel;

public class MeterGaugeChartRenderer extends BaseChartRenderer {

   @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        MeterGaugeChart chart = (MeterGaugeChart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChart chart = (MeterGaugeChart) uichart;
        String clientId = chart.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("PrimeFaces.cw('MeterGaugeChart','" + chart.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        encodeOptions(context, chart);

        encodeClientBehaviors(context, chart);

        writer.write("},'charts');});");

        endScript(writer);
    }

    protected void encodeOptions(FacesContext context, MeterGaugeChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChartModel model = (MeterGaugeChartModel)chart.getValue();
        
        writer.write(",data:[[" + model.getValue() + "]]");
        
        super.encodeOptions(context, chart);
        
        writer.write(",seriesDefaults : {renderer: $.jqplot.MeterGaugeRenderer, rendererOptions : {");
        
        writer.write("intervals:" + model.getIntervals().toString());
        
        writer.write(",label:'" + model.getLabel() + "'");
        
        if(!chart.isShowTickLabels())
            writer.write(",showTickLabels:false");
        
        writer.write(",labelHeightAdjust:" + chart.getLabelHeightAdjust());
        writer.write(",intervalOuterRadius:" + chart.getIntervalOuterRadius());
        
        writer.write("}}");
    }
}