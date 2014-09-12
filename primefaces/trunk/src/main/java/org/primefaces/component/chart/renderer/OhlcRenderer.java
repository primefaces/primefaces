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
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;

public class OhlcRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        OhlcChartModel model = (OhlcChartModel) chart.getModel();
        List<OhlcChartSeries> data = model.getData();
        
        writer.write(",data:[[");
        for(Iterator<OhlcChartSeries> it = data.iterator(); it.hasNext();) {
            OhlcChartSeries s = it.next();
            writer.write("[");
            writer.write(String.valueOf(s.getValue()));
            writer.write(",");
            writer.write(String.valueOf(s.getOpen()));
            writer.write(",");
            writer.write(String.valueOf(s.getHigh()));
            writer.write(",");
            writer.write(String.valueOf(s.getLow()));
            writer.write(",");
            writer.write(String.valueOf(s.getClose()));
            writer.write("]");

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
        OhlcChartModel model = (OhlcChartModel) chart.getModel();
        
        if(model.isCandleStick())
            writer.write(",candleStick:true");
        
        if(model.isZoom())
            writer.write(",zoom:true");
        
        if(model.isAnimate())
            writer.write(",animate:true");
        
        if(model.isShowDatatip()) {
            writer.write(",datatip:true");
            if(model.getDatatipFormat() != null)
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
        }
    }    
}
