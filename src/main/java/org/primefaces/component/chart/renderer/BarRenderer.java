/*
 * Copyright 2009-2013 PrimeTek.
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
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

public class BarRenderer extends BasePlotRenderer {
    
    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BarChartModel model = (BarChartModel) chart.getModel();
        
        //data
		writer.write(",data:[" );
        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = it.next();
            int i = 1;

            writer.write("[");
            for(Iterator<Object> its = series.getData().keySet().iterator(); its.hasNext();) {
                Object xValue = its.next();
                Number value = series.getData().get(xValue);
                String valueToRender = value != null ? value.toString() : "null";

                writer.write("[\"" + xValue + "\"," + valueToRender + "]");

                if(its.hasNext()) {
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
        BarChartModel model = (BarChartModel) chart.getModel();
        Map<AxisType,Axis> axes = model.getAxes();
        String orientation = model.getOrientation();
        int barPadding = 8;
        int barMargin = 10;
        
        //axes
        writer.write(",axes:{");
        for(Iterator<AxisType> it = axes.keySet().iterator(); it.hasNext();) {
            AxisType axisType = it.next();
            Axis axis = model.getAxes().get(axisType);
            
            encodeAxis(context, axisType, axis);
            
            if(it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("}");

        //series
        writer.write(",series:[");
        for(Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext();) {
            ChartSeries series = (ChartSeries) it.next();
            String seriesRenderer = series.getRenderer();
            boolean fill = series.isFill();

            writer.write("{");
            writer.write("label:'" + series.getLabel() + "'");
            if(seriesRenderer != null) {
                writer.write(",renderer: $.jqplot." + seriesRenderer);
            }
            if(fill) {
                writer.write(",fill:true");
            }
            writer.write("}");

            if(it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");

        if(orientation != null) writer.write(",orientation:'" + orientation + "'");
        if(barPadding != 8) writer.write(",barPadding:" + barPadding);
        if(barMargin != 10) writer.write(",barMargin:" + barMargin);        
        if(model.isStacked()) writer.write(",stackSeries:true");       
        if(model.isBreakOnNull()) writer.write(",breakOnNull:true");
        if(model.isZoom()) writer.write(",zoom:true");        
        if(model.isAnimate()) writer.write(",animate:true");        
        if(model.isShowDatatip()) {
            writer.write(",datatip:true");
            if(model.getDatatipFormat() != null)
                writer.write(",datatipFormat:'" + model.getDatatipFormat() + "'");
        }
    }
    
    private void encodeAxis(FacesContext context, AxisType axisType, Axis axis) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = axis.getLabel();
        double min = axis.getMin();
        double max = axis.getMax();
        String renderer = axis.getRenderer();
        int tickAngle = axis.getTickAngle();
        
        writer.write(axisType.toString() + ": {");
        
        writer.write("label:'" + label + "'");
        
        if(min != Double.MIN_VALUE) writer.write(",min:" + min);
        if(max != Double.MAX_VALUE) writer.write(",max:" + max);
        if(renderer != null) writer.write(",renderer:$.jqplot." + renderer);
        if(tickAngle != 0) writer.write(",tickOptions:{angle:" + tickAngle + "}");
        
        writer.write("}");
    }
}
