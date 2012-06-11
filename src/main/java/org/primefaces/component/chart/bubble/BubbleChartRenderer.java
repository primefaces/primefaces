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
package org.primefaces.component.chart.bubble;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.BubbleChartModel;

public class BubbleChartRenderer extends BaseChartRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        BubbleChart chart = (BubbleChart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, UIChart uichart) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        BubbleChart chart = (BubbleChart) uichart;
        String clientId = chart.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("PrimeFaces.cw('BubbleChart','" + chart.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        encodeData(context, chart);
        
        encodeOptions(context, chart);

        encodeClientBehaviors(context, chart);

        writer.write("},'charts');});");

        endScript(writer);
    }

    protected void encodeOptions(FacesContext context, BubbleChart chart) throws IOException {

        encodeCommonConfig(context, chart);
        
        encodeSeriesDefaults(context, chart);
    }
    
    protected void encodeSeriesDefaults(FacesContext context, BubbleChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.write(", seriesDefaults : { renderer: $.jqplot.BubbleRenderer, rendererOptions : {");
        
        writer.write("showLabels : " + chart.isShowLabels());
        
        if(chart.isBubbleGradients())
            writer.write(",bubbleGradients:true");
        
        if(chart.getBubbleAlpha()  != 70)
            writer.write(",bubbleAlpha:" + ((chart.getBubbleAlpha()%100)/100));
        
        writer.write("}}");
    }

    private void encodeData(FacesContext context, BubbleChart chart) throws IOException {
        context.getResponseWriter().write(",data:[" + ((BubbleChartModel) chart.getValue()).toString() + "]");
    }
}