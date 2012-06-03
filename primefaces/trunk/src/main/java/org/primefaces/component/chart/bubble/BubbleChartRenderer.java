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
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.BaseChartRenderer;
import org.primefaces.component.chart.UIChart;
import org.primefaces.model.chart.BubbleChartModel;
import org.primefaces.model.chart.BubbleChartSeries;

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
    
    protected void encodeData(FacesContext context, BubbleChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BubbleChartModel model = (BubbleChartModel) chart.getValue();
        List<BubbleChartSeries> data = model.getData();
        StringBuilder builder = new StringBuilder();
        
        writer.write(",data:[[");
        for(Iterator<BubbleChartSeries> it = data.iterator(); it.hasNext();) {
            BubbleChartSeries s = it.next();
            builder.append("[").append(s.getX()).append(",").append(s.getY()).append(",")
                    .append(s.getRadius()).append(",'").append(s.getLabel()).append("']");
            
            writer.write(builder.toString());
            builder.setLength(0);
            
            if(it.hasNext()) {
                writer.write(",");
            }
        }
        
        writer.write("]]");
    }

    protected void encodeOptions(FacesContext context, BubbleChart chart) throws IOException {
        super.encodeOptions(context, chart);
        
        ResponseWriter writer = context.getResponseWriter();
        
        //axes
        writer.write(",axes:{");
        encodeAxis(context, "xaxis", chart.getXaxisLabel(), chart.getXaxisAngle(), Double.MIN_VALUE, Double.MAX_VALUE);
        encodeAxis(context, ",yaxis", chart.getYaxisLabel(), chart.getYaxisAngle(), Double.MIN_VALUE, Double.MAX_VALUE);
        writer.write("}");
        
        writer.write(",showLabels:" + chart.isShowLabels());
        writer.write(",bubbleGradients:" + chart.isBubbleGradients());
        writer.write(",bubbleAlpha:" + chart.getBubbleAlpha());
        
        if(chart.isZoom())
            writer.write(",zoom:true");
        
        if(chart.isShowDatatip()) {
            writer.write(",datatip:true");
            if(chart.getDatatipFormat() != null)
                writer.write(",datatipFormat:'" + chart.getDatatipFormat() + "'");
        }
    }
}