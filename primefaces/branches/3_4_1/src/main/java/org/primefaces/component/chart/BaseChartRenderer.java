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
package org.primefaces.component.chart;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class BaseChartRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
	}
		
	protected void encodeMarkup(FacesContext context, UIChart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(context), null);

        if(chart.getStyle() != null)
            writer.writeAttribute("style", chart.getStyle(), "style");

		if(chart.getStyleClass() != null)
			writer.writeAttribute("class", chart.getStyleClass(), "styleClass");
		
		writer.endElement("div");
	}
	
    protected void encodeOptions(FacesContext context, UIChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String legendPosition = chart.getLegendPosition();
        String title = chart.getTitle();
        String seriesColors = chart.getSeriesColors();
        String extender = chart.getExtender();

        if(title != null)
            writer.write(",title:'" + title + "'");
        
        if(!chart.isShadow())
            writer.write(",shadow:false");
        
        if(seriesColors != null)
            writer.write(",seriesColors:['#" +  seriesColors.replaceAll("[ ]*,[ ]*", "','#") + "']");
        
        if(legendPosition != null) {
            writer.write(",legendPosition:'" + legendPosition + "'");
            
            if(chart.getLegendCols() != 0)
                writer.write(",legendCols:" + chart.getLegendCols());
            
            if(chart.getLegendRows() != 0)
                writer.write(",legendRows:" + chart.getLegendRows());
        }
        
        if(extender != null)
            writer.write(",extender:" + extender);
        
    }
    
    protected void encodeAxis(FacesContext context, String name, String label, int angle, double min, double max) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String labelText = label == null ? "" : label;
        
        writer.write(name + ":{");
        writer.write("label:'" + labelText + "'");
        writer.write(",angle:" + angle);
        
        if(min != Double.MIN_VALUE) writer.write(",min:" + min);
        if(max != Double.MAX_VALUE) writer.write(",max:" + max);
       
        writer.write("}");
    }
}