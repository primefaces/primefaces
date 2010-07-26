/*
 * Copyright 2009 Prime Technology.
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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.panel.Panel;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DashboardRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		Dashboard dashboard = (Dashboard) component;
		String clientId = dashboard.getClientId(facesContext);
		String senderIndexParam = clientId + "_senderColumnIndex";
		Integer senderColumnIndex = null;
		
		if(params.containsKey(clientId + "_reordered")) {
			String widgetClientId = params.get(clientId + "_widgetId");
			Integer itemIndex = Integer.valueOf(params.get(clientId + "_itemIndex"));
			Integer receiverColumnIndex = Integer.valueOf(params.get(clientId + "_receiverColumnIndex"));
			
			if(params.containsKey(senderIndexParam)) {
				senderColumnIndex = Integer.valueOf(params.get(senderIndexParam));
			}
			
			String[] idTokens = widgetClientId.split(":");
			String widgetId = idTokens.length == 1 ? idTokens[0] : idTokens[idTokens.length - 1];
			
			DashboardReorderEvent event = new DashboardReorderEvent(component, widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);
			dashboard.queueEvent(event);
			
			updateDashboardModel(dashboard.getModel(), widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);
		}
	}

	protected void updateDashboardModel(DashboardModel model, String widgetId, Integer itemIndex, Integer receiverColumnIndex, Integer senderColumnIndex) {		
		if(senderColumnIndex == null) {
			//Reorder widget in same column
			DashboardColumn column = model.getColumn(receiverColumnIndex);
			column.reorderWidget(itemIndex, widgetId);
		} else {
			//Transfer widget
			DashboardColumn oldColumn = model.getColumn(senderColumnIndex);
			DashboardColumn newColumn = model.getColumn(receiverColumnIndex);
			
			model.transferWidget(oldColumn, newColumn, widgetId, itemIndex);
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Dashboard dashboard = (Dashboard) component;
		
		encodeMarkup(facesContext, dashboard);
		encodeScript(facesContext, dashboard);
	}

	protected void encodeMarkup(FacesContext facesContext, Dashboard dashboard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dashboard.getClientId(facesContext);
		
		writer.startElement("div", dashboard);
		writer.writeAttribute("id", clientId, "id");
		String styleClass = dashboard.getStyleClass() != null ? Dashboard.CONTAINER_CLASS + " " + dashboard.getStyleClass() : Dashboard.CONTAINER_CLASS;
		writer.writeAttribute("class", styleClass, "styleClass");
		if(dashboard.getStyle() != null)  writer.writeAttribute("style", dashboard.getStyle(), "style");
		
		DashboardModel model = dashboard.getModel();
		if(model != null) {
			for(DashboardColumn column : model.getColumns()) {
				writer.startElement("div", null);
				writer.writeAttribute("class", Dashboard.COLUMN_CLASS, null);
				
				for(String widgetId : column.getWidgets()) {
					Panel widget = findWidget(widgetId, dashboard);
					
					if(widget != null)
						renderChild(facesContext, widget);
				}
				
				writer.endElement("div");
			}
		}

		writer.endElement("div");
	}
	
	protected void encodeScript(FacesContext facesContext, Dashboard dashboard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dashboard.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, dashboard);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.Dashboard('" + clientId + "', {");
		writer.write("url:'" + getActionURL(facesContext) + "'");
		if(dashboard.isDisabled()) writer.write(",disabled:true");
		if(dashboard.getOnReorderUpdate() != null) writer.write(",onReorderUpdate:'" + ComponentUtils.findClientIds(facesContext, dashboard, dashboard.getOnReorderUpdate()) + "'");
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	protected Panel findWidget(String id, Dashboard dashboard) {
		for(UIComponent child : dashboard.getChildren()) {
			Panel panel = (Panel) child;
			
			if(panel.getId().equals(id))
				return panel;
		}
		
		return null;
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}