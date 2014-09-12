/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.chart.renderer;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.util.ComponentUtils;

public class LineRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getModel();
        
		writer.write(",data:[" );
        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = it.next();

            writer.write("[");
            for(Iterator<Object> x = series.getData().keySet().iterator(); x.hasNext();) {
                Object xValue = x.next();
                Number yValue = series.getData().get(xValue);
                String yValueAsString = (yValue != null) ? yValue.toString() : "null";

                writer.write("[");
                
                if(xValue instanceof String)
                    writer.write("\"" + ComponentUtils.escapeText(xValue.toString()) + "\"," + yValueAsString);
                else
                    writer.write(xValue + "," + yValueAsString);
                
                writer.write("]");

                if(x.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");

            if(it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");
    }
    
    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);
        
        ResponseWriter writer = context.getResponseWriter();
        LineChartModel model = (LineChartModel) chart.getModel();
        
        writer.write(",series:[");
        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = (ChartSeries) it.next();
            series.encode(writer);

            if(it.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
        
        if(model.isStacked()) writer.write(",stackSeries:true");
        if(model.isBreakOnNull()) writer.write(",breakOnNull:true");
        if(model.isZoom()) writer.write(",zoom:true");
        if(model.isAnimate()) writer.write(",animate:true");
        if(model.isShowPointLabels()) writer.write(",showPointLabels:true");
        
        if(model.isShowDatatip()) {
            writer.write(",datatip:true");
            if(model.getDatatipFormat() != null)
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
        }
    }
}
