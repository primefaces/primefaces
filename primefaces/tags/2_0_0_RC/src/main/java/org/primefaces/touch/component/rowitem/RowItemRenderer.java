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
package org.primefaces.touch.component.rowitem;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RowItemRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		RowItem rowItem = (RowItem) component;
		
		if(params.get(rowItem.getClientId(facesContext)) != null) {
			rowItem.queueEvent(new ActionEvent(rowItem));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		RowItem rowItem = (RowItem) component;
		boolean isNavigation = rowItem.getView() != null;
		boolean isDynamic = rowItem.getUpdate() != null;
		
		writer.startElement("li", null);
		
		if(isNavigation) {
			writer.writeAttribute("class", "arrow", null);
		}
		
		if(rowItem.getValue() == null) {
			renderChildren(facesContext, rowItem);
		} else {
			String href;
			
			if(isNavigation) {
				href = "javascript:none";
			} else if(rowItem.getUrl() != null){
				href = rowItem.getUrl();
			} else {
				href = "#";
			}
			
			writer.startElement("a", null);
			writer.writeAttribute("href", href, null);
			if(rowItem.getUrl() != null) {
				writer.writeAttribute("target", "_blank", null);
			}
			
			if(isNavigation) {
				if(isDynamic) {
					UIComponent form = ComponentUtils.findParentForm(facesContext, rowItem);
					String ajaxRequest = getAjaxRequest(facesContext, rowItem, form.getClientId(facesContext));
					
					writer.writeAttribute("onclick", ajaxRequest, null);
				} else {
					writer.writeAttribute("onclick", "TouchFaces.goTo('" + rowItem.getView() + "','slide')", null);
				}
			}
			
			if(rowItem.getValue() != null) {
				writer.write(rowItem.getValue().toString());
			}
		
			writer.endElement("a");
		}
		
		writer.endElement("li");
	}
	
	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, RowItem item, String formClientId) {
		String clientId = item.getClientId(facesContext);
		
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formClientId:'");
		req.append(formClientId);
		req.append("',");
		req.append("oncomplete:function(){");
		req.append("TouchFaces.goTo('");
		req.append(item.getView());
		req.append("','slide');}");
		req.append("},");
		
		req.append("'update=");
		if(item.getUpdate() != null) {
			req.append(item.getUpdate());
		}
		else {
 			req.append(formClientId);
		}
		for(UIComponent component : item.getChildren()) {
			if(component instanceof UIParameter) {
				UIParameter parameter = (UIParameter) component;
				
				req.append("&");
				req.append(parameter.getName());
				req.append("=");
				req.append(parameter.getValue());
			}
		}
		
		req.append("&");
		req.append(clientId);
		req.append("=");
		req.append(clientId);
		req.append("');");
		
		return req.toString();
	}
	
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Encode at encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
