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
package org.primefaces.component.dashboard;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.panel.Panel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DashboardRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Dashboard dashboard = (Dashboard) component;
		
		encodeMarkup(context, dashboard);
		encodeScript(context, dashboard);
	}

	protected void encodeMarkup(FacesContext contextr, Dashboard dashboard) throws IOException {
		ResponseWriter writer = contextr.getResponseWriter();
		String clientId = dashboard.getClientId(contextr);
		
		writer.startElement("div", dashboard);
		writer.writeAttribute("id", clientId, "id");
		String styleClass = dashboard.getStyleClass() != null ? Dashboard.CONTAINER_CLASS + " " + dashboard.getStyleClass() : Dashboard.CONTAINER_CLASS;
		writer.writeAttribute("class", styleClass, "styleClass");
		if(dashboard.getStyle() != null) 
            writer.writeAttribute("style", dashboard.getStyle(), "style");
		
		DashboardModel model = dashboard.getModel();
		if(model != null) {
			for(DashboardColumn column : model.getColumns()) {
                String columnStyle = column.getStyle();
                String columnStyleClass = column.getStyleClass();
                columnStyleClass = (columnStyleClass == null) ? Dashboard.COLUMN_CLASS : Dashboard.COLUMN_CLASS + " " + columnStyleClass;
                
				writer.startElement("div", null);
				writer.writeAttribute("class", columnStyleClass, null);
                if(columnStyle != null) {
                    writer.writeAttribute("style", columnStyle, null);
                }
                
				for(String widgetId : column.getWidgets()) {
					Panel widget = findWidget(widgetId, dashboard);
					
					if(widget != null)
						renderChild(contextr, widget);
				}
				
				writer.endElement("div");
			}
		}

		writer.endElement("div");
	}
	
	protected void encodeScript(FacesContext context, Dashboard dashboard) throws IOException {
		String clientId = dashboard.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Dashboard", dashboard.resolveWidgetVar(), clientId)
            .attr("disabled", dashboard.isDisabled(), false);
        
        encodeClientBehaviors(context, dashboard);
        
        wb.finish();
	}
	
	protected Panel findWidget(String id, Dashboard dashboard) {
		for(UIComponent child : dashboard.getChildren()) {
			Panel panel = (Panel) child;
			
			if(panel.getId().equals(id))
				return panel;
		}
		
		return null;
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}