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
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {		
		String param = component.getClientId(facesContext) + "_button";

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
			
			writer.write("PrimeFaces.onContentReady(\"" + clientId + "\", function() {\n");
			String confirmDialogCFGVar = confirmVar + "_cfg";
			writeConfirmDialogCFGVariable(facesContext, confirmDialog, confirmDialogCFGVar);
			
			writer.write(confirmVar + " = new YAHOO.widget.SimpleDialog(\"" + clientId + "_confirmation\", " + confirmDialogCFGVar + ");\n");
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
		String buttonId = confirmDialog == null ? clientId + "_button" : clientId + "_proxy";

		UIComponent form = ComponentUtils.findParentForm(facesContext, button);
		if(form == null) {
			throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);

		writer.startElement("input", button);
		writer.writeAttribute("id", buttonId, "id");
		writer.writeAttribute("name", buttonId, "name");
		if(button.getValue() != null)
			writer.writeAttribute("value", button.getValue(), "name");
		
		String onclick = null;
		
		if(confirmDialog != null) {
			writer.writeAttribute("onclick", confirmVar + ".show();return false;", null);
		}
		
		
		if(button.getType().equals("submit") && button.isAsync()) {
			String formClientId = form.getClientId(facesContext);
			String ajaxRequest = getAjaxRequest(facesContext, button, formClientId);
			
			onclick = button.getOnclick() != null ? button.getOnclick() + ";" + ajaxRequest : ajaxRequest;
			
			if(confirmDialog == null)
				writer.writeAttribute("onclick", onclick + ";return false", "onclick");
			else
				onclick = onclick + ";" + confirmVar + ".hide();return false;";
		}
		
		if(button.getStyleClass() != null) {
			writer.writeAttribute("class", button.getStyleClass(), null);
		}
		
		renderPassThruAttributes(facesContext, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
			
		writer.endElement("input");
		
		if(confirmDialog != null) {
			encodeConfirmDialogMarkup(facesContext, button, confirmDialog, onclick, confirmVar);
		}
		
		writer.endElement("span");
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

		encodeConfirmButtonMarkup(facesContext, null, clientId + "_button", "submit", confirmDialog.getYesLabel(), onclick);
		encodeConfirmButtonMarkup(facesContext, null, clientId + "_noButton", "button", confirmDialog.getNoLabel(), confirmVar + ".hide();");
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeConfirmButtonMarkup(FacesContext facesContext, CommandButton button, String id, String type, String label, String onclick) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("button", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", type, null);
		
		if(onclick != null)
			writer.writeAttribute("onclick", onclick, null);
		
		if(label != null)
			writer.write(label);
		
		writer.endElement("button");
	}
	

	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, CommandButton button, String formClientId) {
		String clientId = button.getClientId(facesContext);
		
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formClientId:'");
		req.append(formClientId);
		req.append("'");
		
		if(button.getOnstart() != null) {
			req.append(",onstart:function(){" + button.getOnstart() + ";}");
		}
		if(button.getOncomplete() != null) {
			req.append(",oncomplete:function(){" + button.getOncomplete() + ";}");
		}
		
		req.append("},");
		
		req.append("'update=");
		if(button.getUpdate() != null) {
			req.append(button.getUpdate());
		}
		else {
 			req.append(formClientId);
		}
		
		req.append("&");
		req.append(clientId + "_button");
		req.append("=");
		req.append(clientId + "_button'");
		req.append(");");
		
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