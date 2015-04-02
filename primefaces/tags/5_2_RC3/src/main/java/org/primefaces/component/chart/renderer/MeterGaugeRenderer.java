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
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.MeterGaugeChartModel;

public class MeterGaugeRenderer extends BasePlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChartModel model = (MeterGaugeChartModel) chart.getModel();
        
        writer.write(",data:[[" + model.getValue() + "]]");
    }
    
    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);
        
        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChartModel model = (MeterGaugeChartModel) chart.getModel();
        String gaugeLabel = model.getGaugeLabel();
        double min = model.getMin();
        double max = model.getMax();
        Integer intervalInnerRadius = model.getIntervalInnerRadius();
        Integer intervalOuterRadius = model.getIntervalOuterRadius();
        
        encodeNumberList(context, "intervals", model.getIntervals());
        encodeNumberList(context, "ticks", model.getTicks());

        if(gaugeLabel != null) {
            writer.write(",gaugeLabel:\"" + gaugeLabel + "\"");
            writer.write(",gaugeLabelPosition:\"" + model.getGaugeLabelPosition() + "\"");
        }
              
        writer.write(",showTickLabels:" + model.isShowTickLabels());
        writer.write(",labelHeightAdjust:" + model.getLabelHeightAdjust());
        
        if(intervalInnerRadius != null) writer.write(",intervalInnerRadius:" + intervalInnerRadius);
        if(intervalOuterRadius != null) writer.write(",intervalOuterRadius:" + intervalOuterRadius);
        
        if(min != Double.MIN_VALUE) writer.write(",min:" + min);
        if(max != Double.MAX_VALUE) writer.write(",max:" + max);
    }
    
    private void encodeNumberList(FacesContext context, String name, List<Number> values) throws IOException {
        if(values != null) {
            ResponseWriter writer = context.getResponseWriter();
            
            writer.write("," + name + ":[");
            for(Iterator<Number> it = values.iterator(); it.hasNext();) {
                Number number = it.next();
                writer.write(number.toString());

                if(it.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");
        }
    }
    
}
