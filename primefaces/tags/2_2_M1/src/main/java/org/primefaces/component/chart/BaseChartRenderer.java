/*
 * Copyright 2010 Prime Technology.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class BaseChartRenderer extends CoreRenderer {
	
    @Override
	public void decode(FacesContext fc, UIComponent component) {
		String clientId = component.getClientId(fc);
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();

		if(params.containsKey(clientId + "_ajaxItemSelect")) {
			int seriesIndex = Integer.parseInt(params.get(clientId + "_seriesIndex"));
			int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

			component.queueEvent(new ItemSelectEvent(component, itemIndex, seriesIndex));
        }
	}
    	
	protected List<ChartSeries> getSeries(UIChart chart) {
		List<UIComponent> children = chart.getChildren();
		List<ChartSeries> series = new ArrayList<ChartSeries>();
		
		for (UIComponent component : children) {
			if(component instanceof ChartSeries && component.isRendered())
				series.add((ChartSeries) component);	
		}
		
		return series;
	}
	
	protected String getFieldName(ValueExpression fieldExpression) {
		String expressionString = fieldExpression.getExpressionString();
		String expressionContent = expressionString.substring(2, expressionString.length() - 1);
		int firstIndex = expressionContent.indexOf("[");
		
		if(firstIndex != -1) {
			int lastIndex = expressionContent.indexOf("]");
			
			return expressionContent.substring(firstIndex + 1, lastIndex);
		} else {
			String[] tokens = expressionContent.split("\\.");
			
			return tokens[tokens.length-1];
		}	
	}
	
	protected void encodeResources(FacesContext facesContext) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        writer.write("YAHOO.widget.Chart.SWFURL = '" + getResourceRequestPath(facesContext, "yui/charts/assets/charts.swf") + "'");
		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext facesContext, UIChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(facesContext), null);
		writer.writeAttribute("style", "width:" + chart.getWidth() + ";height:" + chart.getHeight(), null);
		
		if(chart.getStyleClass() != null)
			writer.writeAttribute("class", chart.getStyleClass(), "styleClass");
		
		writer.endElement("div");
	}
	
    protected void encodeCommonConfig(FacesContext context, UIChart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write("expressInstall:'" + getResourceRequestPath(context,"yui/assets/expressinstall.swf") + "'");

        if(chart.getWmode() != null) {
			writer.write(",wmode:'" + chart.getWmode() + "'");
        }
		if(chart.getStyle() != null) {
			writer.write(",style:" + chart.getStyle() + "");
		}
		if(chart.getDataTipFunction() != null) {
			writer.write(",dataTipFunction:" + chart.getDataTipFunction());
		}

        if(chart.isLive() || chart.getItemSelectListener() != null) {
            UIComponent form = ComponentUtils.findParentForm(context, chart);
            if(form == null) {
                throw new FacesException("Chart: '" + chart.getClientId(context) + "' must be inside a form element");
            }

            writer.write(",url:'" + getActionURL(context) + "'");
            writer.write(",formId:'" + form.getClientId(context) + "'");
        }

        if(chart.isLive()) {
            writer.write(",live:true");
            writer.write(",refreshInterval:" + chart.getRefreshInterval());
        }

        if(chart.getItemSelectListener() != null) {
            writer.write(",ajaxItemSelect: true");
            
            if(chart.getUpdate() != null) writer.write(",update:'" + ComponentUtils.findClientIds(context, chart, chart.getUpdate()) + "'");
            if(chart.getOncomplete() != null) writer.write(",oncomplete: function() {" + chart.getOncomplete() + ";}");
        }
    }
    
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}