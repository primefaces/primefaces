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
import org.primefaces.model.chart.CartesianChartModel;

public abstract class CartesianPlotRenderer extends BasePlotRenderer {
    
    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);
        
        ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getModel();
        Map<AxisType,Axis> axes = model.getAxes();
        
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
    }
    
    protected void encodeAxis(FacesContext context, AxisType axisType, Axis axis) throws IOException {
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
