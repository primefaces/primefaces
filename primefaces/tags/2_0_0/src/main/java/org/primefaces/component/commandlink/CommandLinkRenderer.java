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
package org.primefaces.component.commandlink;

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

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;

public class CommandLinkRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {	
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String submittedValue = params.get(component.getClientId(facesContext) + "_submit");
		boolean isSubmitted = submittedValue != null && submittedValue.equals("true");

		if(isSubmitted)
			component.queueEvent(new ActionEvent(component));
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		CommandLink commandLink = (CommandLink) component;
		
		encodeScript(facesContext, commandLink);
		encodeMarkup(facesContext, commandLink);
	}

	private void encodeScript(FacesContext facesContext, CommandLink commandLink) throws IOException {
		ConfirmDialog confirmDialog = getConfirmation(commandLink);
		
		if(confirmDialog != null) {
			ResponseWriter writer = facesContext.getResponseWriter();
			String clientId = commandLink.getClientId(facesContext);
			String confirmVar = createUniqueWidgetVar(facesContext, commandLink);
	
			writer.startElement("script", commandLink);
			writer.writeAttribute("type", "text/javascript", null);
			
			writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
			String confirmDialogCFGVar = confirmVar + "_cfg";
			writeConfirmDialogCFGVariable(facesContext, confirmDialog, confirmDialogCFGVar);
			
			writer.write(confirmVar + " = new YAHOO.widget.SimpleDialog(\"" + clientId + "_confirmation\", " + confirmDialogCFGVar + ");\n");
			writer.write(confirmVar + ".render();");
			writer.write("});");
	
			writer.endElement("script");
		}
	}
	
	private void encodeMarkup(FacesContext facesContext, CommandLink link) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = link.getClientId(facesContext);
		ConfirmDialog confirmDialog = getConfirmation(link);
		String confirmVar = createUniqueWidgetVar(facesContext, link);

		UIComponent form = ComponentUtils.findParentForm(facesContext, link);
		if(form == null) {
			throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("a", link);
		writer.writeAttribute("href", "javascript:void(0);", null);
		if(link.getStyleClass() != null) writer.writeAttribute("class", link.getStyleClass(), null);
		if(confirmDialog != null) writer.writeAttribute("onclick", confirmVar + ".show();", null);
		
		String onclick = null;
		String formClientId = form.getClientId(facesContext);
		
		String request = link.isAjax() ? getAjaxRequest(facesContext, link, formClientId) : getNonAjaxRequest(facesContext, link, formClientId);
		
		onclick = link.getOnclick() != null ? link.getOnclick() + ";" + request : request;
			
		if(confirmDialog == null)
			writer.writeAttribute("onclick", onclick, "onclick");
		else
			onclick = confirmVar + ".hide();" + onclick;
		
		renderPassThruAttributes(facesContext, link, HTML.LINK_ATTRS, HTML.CLICK_EVENT);

		if(link.getValue() != null) {
			writer.write(link.getValue().toString());
		} else {
			renderChildren(facesContext, link);
		}
		
		writer.endElement("a");
		
		if(confirmDialog != null)
			encodeConfirmDialogMarkup(facesContext, link, confirmDialog, onclick, confirmVar);
		
		writer.endElement("span");
	}

	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, CommandLink link, String formClientId) {
		String clientId = link.getClientId(facesContext);
		
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formId:'");
		req.append(formClientId);
		req.append("'");
		
		if(link.isAsync()) req.append(",async:true");
		
		//Callbacks
		if(link.getOnstart() != null) req.append(",onstart:function(xhr){" + link.getOnstart() + ";}");
		if(link.getOnerror() != null) req.append(",onerror:function(xhr, status, error){" + link.getOnerror() + ";}");
		if(link.getOnsuccess() != null) req.append(",onsuccess:function(data, status, xhr, args){" + link.getOnsuccess() + ";}"); 
		if(link.getOncomplete() != null) req.append(",oncomplete:function(xhr, status, args){" + link.getOncomplete() + ";}");

		req.append(",global:" + link.isGlobal());
		
		req.append("},{");
		
		req.append("'" + clientId + "_submit" + "'");
		req.append(":");
		req.append("'true'");
		
		if(link.getUpdate() != null) {
			req.append(",'" + Constants.PARTIAL_UPDATE_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, link, link.getUpdate()) + "'");
		}
		
		if(link.getProcess() != null) {
			req.append(",'" + Constants.PARTIAL_PROCESS_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, link, link.getProcess()) + "'");
		}
		
		for(UIComponent component : link.getChildren()) {
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
	
	private String getNonAjaxRequest(FacesContext facesContext, CommandLink link, String formClientId) {
		String clientId = link.getClientId(facesContext);
		Map<String,Object> params = new HashMap<String, Object>();
		boolean isPartialProcess = link.getProcess() != null;
		
		for(UIComponent component : link.getChildren()) {
			if(component instanceof UIParameter) {
				UIParameter parameter = (UIParameter) component;
				params.put(parameter.getName(), parameter.getValue());
			}
		}
		
		StringBuffer request = new StringBuffer();
		request.append("PrimeFaces.addSubmitParam('" + clientId + "', {'" + clientId + "_submit':'true'");
		
		if(!params.isEmpty() || isPartialProcess) {
			if(isPartialProcess) {
				request.append("," + Constants.PARTIAL_PROCESS_PARAM + ":'" + ComponentUtils.findClientIds(facesContext, link, link.getProcess()) + "'");
			}
			
			if(!params.isEmpty()) {
				for(Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String paramName = iterator.next();
					Object paramValue = (Object) params.get(paramName);
					String toSend = paramValue != null ? paramValue.toString() : "";
					
					request.append(",'" + paramName + "':'" + toSend + "'");
				}
			}
		}
		
		request.append("});");
		request.append("jQuery(PrimeFaces.escapeClientId('" + formClientId + "')).submit();");
		
		return request.toString();
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Render children at encodeEnd
	}
	
	protected void encodeConfirmDialogMarkup(FacesContext facesContext, CommandLink link, ConfirmDialog confirmDialog, String onclick, String confirmVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = link.getClientId(facesContext);
		
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

		encodeConfirmButtonMarkup(facesContext, null, clientId + "_link", confirmDialog.getYesLabel(), onclick);
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
	
	private ConfirmDialog getConfirmation(CommandLink link) {
		List<UIComponent> kids = link.getChildren();
		
		for(UIComponent kid : kids) {
			if(kid instanceof ConfirmDialog)
				return (ConfirmDialog) kid;
		}
		
		return null;
	}
}