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
	
    protected void encodeCommonConfig(FacesContext context, UIChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String legendPosition = chart.getLegendPosition();

        if(chart.getTitle() != null) {
            writer.write(",title:'" + chart.getTitle() + "'");
        }
        
        if(!chart.isShadow())
            writer.write(",shadow:false");
        
        if(chart.getSeriesColors() != null)
            writer.write(",seriesColors:['#" +  chart.getSeriesColors().replaceAll("[ ]*,[ ]*", "','#") + "']");
        
        
        if(legendPosition != null) {
            writer.write(",legend:{");
            writer.write("show:true");
            writer.write(",renderer: $.jqplot.EnhancedLegendRenderer, rendererOptions: {");
            
            writer.write("seriesToggle:");
            
            if(chart.isLegendToggle()) {
                writer.write("'normal'");
            }
            else {
                writer.write("false");
            }
            
            if(chart.getLegendCols() != 1) {
                writer.write(",numberColumns:" + chart.getLegendCols());
            }
            
            if(chart.getLegendRows() > 0) {
                writer.write(",numberRows:" + chart.getLegendRows());
            }
            
            writer.write("}, location:'" + legendPosition + "'}");
        }
        
        writer.write(",axes:{");
        writer.write("xaxis:{");
        
        boolean xLabeled = chart.getXaxisLabel() != null;
        if(xLabeled){
            writer.write("labelRenderer: $.jqplot.CanvasAxisLabelRenderer,");
            writer.write("label:'" + chart.getXaxisLabel() + "'");
        }
        if(chart.getXaxisAngle() != 0){
            if(xLabeled){
                writer.write(",");
            }
            writer.write("tickRenderer:$.jqplot.CanvasAxisTickRenderer,");
            writer.write("tickOptions:{ angle:" + chart.getXaxisAngle() + "}");
        }
        writer.write("}");
        
        boolean yLabeled = chart.getYaxisLabel() != null;
        writer.write(",yaxis:{");
        if(yLabeled){
            writer.write("label:'" + chart.getYaxisLabel() + "',");
            writer.write("labelRenderer: $.jqplot.CanvasAxisLabelRenderer");
        }
        if(chart.getYaxisAngle() != 0){
            if(yLabeled){
                writer.write(",");
            }
            writer.write("tickRenderer:$.jqplot.CanvasAxisTickRenderer,");
            writer.write("tickOptions:{ angle:" + chart.getYaxisAngle() + "}");
        }
        writer.write("}}");
    }
}