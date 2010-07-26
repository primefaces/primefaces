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
package org.primefaces.component.commandbutton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {		
		String param = component.getClientId(facesContext) + "_submit";

		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(param))
			component.queueEvent(new ActionEvent(component));
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		CommandButton button = (CommandButton) component;
		
		//myfaces bug fix
		if(button.getType() == null)
			button.setType("submit");
		
		encodeScript(facesContext, button);
		encodeMarkup(facesContext, button);
	}

	private void encodeScript(FacesContext facesContext, CommandButton button) throws IOException {
		ConfirmDialog confirmDialog = getConfirmation(button);
		
		if(confirmDialog != null) {
			ResponseWriter writer = facesContext.getResponseWriter();
			String clientId = button.getClientId(facesContext);
			String confirmVar = createUniqueWidgetVar(facesContext, button);
	
			writer.startElement("script", button);
			writer.writeAttribute("type", "text/javascript", null);
			
			writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
			String confirmDialogCFGVar = confirmVar + "_cfg";
			writeConfirmDialogCFGVariable(facesContext, confirmDialog, confirmDialogCFGVar);
			
			writer.write(confirmVar + " = new YAHOO.widget.SimpleDialog('" + clientId + "_confirmation', " + confirmDialogCFGVar + ");\n");
			writer.write(confirmVar + ".render();");
			writer.write("});");
	
			writer.endElement("script");
		}
	}
	
	private void encodeMarkup(FacesContext facesContext, CommandButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		ConfirmDialog confirmDialog = getConfirmation(button);
		String confirmVar = createUniqueWidgetVar(facesContext, button);
		String buttonId = confirmDialog == null ? clientId + "_submit" : clientId + "_proxy";

		UIComponent form = ComponentUtils.findParentForm(facesContext, button);
		if(form == null) {
			throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);

		writer.startElement("input", button);
		writer.writeAttribute("id", buttonId, "id");
		writer.writeAttribute("name", buttonId, "name");
		
		if(button.getValue() != null) writer.writeAttribute("value", button.getValue(), "name");
		if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), null);
		if(confirmDialog != null) writer.writeAttribute("onclick", confirmVar + ".show();return false;", null);
		
		String onclick = null;
		
		if(button.getType().equals("submit")) {
			String formClientId = form.getClientId(facesContext);		
			String request = button.isAjax() ? getAjaxRequest(facesContext, button, formClientId) : getNonAjaxRequest(facesContext, button, formClientId);
			
			onclick = button.getOnclick() != null ? button.getOnclick() + ";" + request : request;
				
			if(confirmDialog == null)
				writer.writeAttribute("onclick", onclick, "onclick");
			else
				onclick = confirmVar + ".hide();" + onclick;
		}
		
		renderPassThruAttributes(facesContext, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
			
		writer.endElement("input");
		
		if(confirmDialog != null)
			encodeConfirmDialogMarkup(facesContext, button, confirmDialog, onclick, confirmVar);
		
		writer.endElement("span");
	}
	
	private String getNonAjaxRequest(FacesContext facesContext, CommandButton button, String formClientId) {
		String clientId = button.getClientId(facesContext);
		Map<String,Object> params = new HashMap<String, Object>();
		boolean isPartialProcess = button.getProcess() != null;
		
		for(UIComponent component : button.getChildren()) {
			if(component instanceof UIParameter) {
				UIParameter parameter = (UIParameter) component;
				params.put(parameter.getName(), parameter.getValue());
			}
		}
		
		if(!params.isEmpty() || isPartialProcess) {
			StringBuffer request = new StringBuffer();
			request.append("PrimeFaces.addSubmitParam('" + clientId + "', {");
			
			if(isPartialProcess) {
				request.append(Constants.PARTIAL_PROCESS_PARAM + ":'" + ComponentUtils.findClientIds(facesContext, button, button.getProcess()) + "'");
			}
			
			if(!params.isEmpty()) {
				if(isPartialProcess)
					request.append(",");
				
				for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String paramName = iterator.next();
					Object paramValue = (Object) params.get(paramName);
					String toSend = paramValue != null ? paramValue.toString() : "";
					
					request.append(paramName + ":'" + toSend + "'");
					
					if(iterator.hasNext())
						request.append(",");
				}
			}
			
			request.append("});");

			return request.toString();
		} else {
			return null;
		}
	}
	
	protected void encodeConfirmDialogMarkup(FacesContext facesContext, CommandButton button, ConfirmDialog confirmDialog, String onclick, String confirmVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_confirmation", null);
		
		//header
		writer.startElement("div", null);
		writer.writeAttribute("class", "hd", null);
		if(confirmDialog.getHeader() != null)
			writer.write(confirmDialog.getHeader());
		
		writer.endElement("div");
		
		//body
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		if(confirmDialog.getMessage() != null)
			writer.write(confirmDialog.getMessage());

		writer.endElement("div");
		
		//footer
		writer.startElement("div", null);
		writer.writeAttribute("class", "ft", null);

		encodeConfirmButtonMarkup(facesContext, null, clientId + "_submit", confirmDialog.getYesLabel(), onclick);
		encodeConfirmButtonMarkup(facesContext, null, clientId + "_noButton", confirmDialog.getNoLabel(), confirmVar + ".hide();return false;");
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeConfirmButtonMarkup(FacesContext facesContext, CommandButton button, String id, String label, String onclick) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "submit", null);
		
		if(label != null) writer.writeAttribute("value", label, null);
		if(onclick != null) writer.writeAttribute("onclick", onclick, null);
		
		writer.endElement("input");
	}

	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, CommandButton button, String formClientId) {
		String clientId = button.getClientId(facesContext);
		
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formId:'");
		req.append(formClientId);
		req.append("'");
		
		if(button.isAsync()) req.append(",async:true");

		//Callbacks
		if(button.getOnstart() != null) req.append(",onstart:function(xhr){" + button.getOnstart() + ";}");
		if(button.getOnerror() != null) req.append(",onerror:function(xhr, status, error){" + button.getOnerror() + ";}");
		if(button.getOnsuccess() != null) req.append(",onsuccess:function(data, status, xhr, args){" + button.getOnsuccess() + ";}"); 
		if(button.getOncomplete() != null) req.append(",oncomplete:function(xhr, status, args){" + button.getOncomplete() + ";}");

		req.append(",global:" + button.isGlobal());
		
		req.append("},{");
		
		req.append("'" + clientId + "_submit'");
		req.append(":");
		req.append("'" + clientId + "_submit'");
		
		if(button.getUpdate() != null) {
			req.append(",'" + Constants.PARTIAL_UPDATE_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, button, button.getUpdate()) + "'");
		}
		
		if(button.getProcess() != null) {
			req.append(",'" + Constants.PARTIAL_PROCESS_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, button, button.getProcess()) + "'");
		}
		
		for(UIComponent component : button.getChildren()) {
			if(component instanceof UIParameter) {
				UIParameter parameter = (UIParameter) component;
				
				req.append(",");
				req.append("'" + parameter.getName() + "'");
				req.append(":");
				req.append("'" + parameter.getValue() + "'");
			}
		}
		
		req.append("});");
		
		req.append("return false;");
		
		return req.toString();
	}
	
	private void writeConfirmDialogCFGVariable(FacesContext facesContext, ConfirmDialog dialog, String cfgVariable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + cfgVariable + " = {\n");
		writer.write("visible: false");
		writer.write(",icon: YAHOO.widget.SimpleDialog.ICON_" + dialog.getSeverity().toUpperCase());
		if(dialog.getWidth() != null) writer.write(",width:'" + dialog.getWidth() + "'");
		if(dialog.getHeight() != null) writer.write(",height:'" + dialog.getHeight() + "'");
		if(!dialog.isDraggable()) writer.write(",draggable: false");
		if(dialog.getUnderlay() != null && !dialog.getUnderlay().equalsIgnoreCase("shadow")) writer.write(",underlay: '" + dialog.getUnderlay() + "'");
		if(dialog.isFixedCenter()) writer.write(",fixedcenter: true");
		if(!dialog.isClose()) writer.write(",close: false");
		if(dialog.isConstrainToViewport()) writer.write(",constraintoviewport: true");
		if(dialog.getX() != -1) writer.write(",x:" + dialog.getX());
		if(dialog.getY() != -1) writer.write(",y:" + dialog.getY());
		if(dialog.isModal()) writer.write(",modal: true");
		if(dialog.getEffect() != null) writer.write(",effect:{effect:YAHOO.widget.ContainerEffect." + dialog.getEffect().toUpperCase() + ", duration: " + dialog.getEffectDuration() + "}");
		
		writer.write("};\n");
	}
	
	private ConfirmDialog getConfirmation(CommandButton button) {
		List<UIComponent> kids = button.getChildren();
		
		for(UIComponent kid : kids) {
			if(kid instanceof ConfirmDialog)
				return (ConfirmDialog) kid;
		}
		
		return null;
	}
}