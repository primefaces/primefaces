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
package org.primefaces.component.chart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.chart.renderer.BasePlotRenderer;
import org.primefaces.component.chart.renderer.PieRenderer;
import org.primefaces.renderkit.CoreRenderer;

public class ChartRenderer extends CoreRenderer {
    
    private final static String TYPE_PIE = "pie";
    
    final static Map<String,org.primefaces.component.chart.renderer.BasePlotRenderer> CHART_RENDERERS;
    
    static {
        CHART_RENDERERS = new HashMap<String, org.primefaces.component.chart.renderer.BasePlotRenderer>();
        CHART_RENDERERS.put(TYPE_PIE, new PieRenderer());
    }
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
	}
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		Chart chart = (Chart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
	}
		
	protected void encodeMarkup(FacesContext context, Chart chart) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(context), null);
        if(style != null) writer.writeAttribute("style", style, "style");
		if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
		
		writer.endElement("div");
	}
    
    protected void encodeScript(FacesContext context, Chart chart) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        String type = chart.getType();
        BasePlotRenderer plotRenderer = CHART_RENDERERS.get(type);
		String clientId = chart.getClientId(context);
				
        startScript(writer, clientId);
		
		writer.write("$(function(){");
        writer.write("PrimeFaces.cw('Chart','" + chart.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",type:'" + type + "'");
        plotRenderer.render(context, chart);
        encodeClientBehaviors(context, chart);
		writer.write("},'charts');});");
        
		endScript(writer);
	}
}