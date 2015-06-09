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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.util.ComponentUtils;

public abstract class BasePlotRenderer {
    
    public void render(FacesContext context, Chart chart) throws IOException {
        encodeData(context, chart);
        encodeOptions(context, chart);
    }
    
    protected abstract void encodeData(FacesContext context, Chart chart) throws IOException;
    
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ChartModel model = chart.getModel();
        String legendPosition = model.getLegendPosition();
        String title = model.getTitle();
        String seriesColors = model.getSeriesColors();
        String negativeSeriesColors = model.getNegativeSeriesColors();
        String extender = model.getExtender();

        if(title != null)
            writer.write(",title:\"" + ComponentUtils.escapeText(title) + "\"");
        
        if(!model.isShadow())
            writer.write(",shadow:false");
        
        if(seriesColors != null)
            writer.write(",seriesColors:[\"#" +  seriesColors.replaceAll("[ ]*,[ ]*", "\",\"#") + "\"]");
        
        if(negativeSeriesColors != null)
            writer.write(",negativeSeriesColors:[\"#" +  negativeSeriesColors.replaceAll("[ ]*,[ ]*", "\",\"#") + "\"]");
        
        if(legendPosition != null) {
            LegendPlacement legendPlacement = model.getLegendPlacement();
            writer.write(",legendPosition:\"" + legendPosition + "\"");
            
            if(model.getLegendCols() != 0)
                writer.write(",legendCols:" + model.getLegendCols());
            
            if(model.getLegendRows() != 0)
                writer.write(",legendRows:" + model.getLegendRows());
            
            if(legendPlacement != null)
                writer.write(",legendPlacement:\"" + legendPlacement + "\"");
        }
        
        if(!model.isMouseoverHighlight())
            writer.write(",highlightMouseOver:" + false);
        
        if(extender != null)
            writer.write(",extender:" + extender);
        
        if(!model.isResetAxesOnResize())
            writer.write(",resetAxesOnResize:" + false);        
    }
}
