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
import org.primefaces.model.chart.BubbleChartModel;
import org.primefaces.model.chart.BubbleChartSeries;
import org.primefaces.util.ComponentUtils;

public class BubbleRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BubbleChartModel model = (BubbleChartModel) chart.getModel();
        List<BubbleChartSeries> data = model.getData();

        writer.write(",data:[[");
        for(Iterator<BubbleChartSeries> it = data.iterator(); it.hasNext();) {
            BubbleChartSeries s = it.next();
            writer.write("[");
            writer.write(String.valueOf(s.getX()));
            writer.write(",");
            writer.write(String.valueOf(s.getY()));
            writer.write(",");
            writer.write(String.valueOf(s.getRadius()));
            writer.write(",\"");
            writer.write(ComponentUtils.escapeText(String.valueOf(s.getLabel())));
            writer.write("\"]");

            if(it.hasNext()) {
                writer.write(",");
            }
        }
        
        writer.write("]]");
    }
    
    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);
        
        ResponseWriter writer = context.getResponseWriter();
        BubbleChartModel model = (BubbleChartModel) chart.getModel();
        
        writer.write(",showLabels:" + model.isShowLabels());
        writer.write(",bubbleGradients:" + model.isBubbleGradients());
        writer.write(",bubbleAlpha:" + model.getBubbleAlpha());
        
        if(model.isZoom())
            writer.write(",zoom:true");
        
        if(model.isShowDatatip()) {
            writer.write(",datatip:true");
            if(model.getDatatipFormat() != null)
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
        }
    }
    
}
