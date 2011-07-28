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
package org.primefaces.component.confirmdialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.dialog.Dialog;

import org.primefaces.renderkit.CoreRenderer;

public class ConfirmDialogRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ConfirmDialog dialog = (ConfirmDialog) component;
		
		encodeMarkup(context, dialog);
		encodeScript(context, dialog);
	}

	protected void encodeMarkup(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = styleClass == null ? ConfirmDialog.CONTAINER_CLASS : ConfirmDialog.CONTAINER_CLASS + " " + styleClass;


        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeHeader(context, dialog);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
          
		//severity
		writer.startElement("span", null);
		writer.writeAttribute("class", "ui-icon ui-icon-" + dialog.getSeverity(), null);
		writer.endElement("span");

        
        String messageText = dialog.getMessage();
        UIComponent messageFacet = dialog.getFacet("message");
        if(messageFacet != null) {
            messageFacet.encodeAll(context);
        }
        else if(messageText != null) {
			writer.write(dialog.getMessage());
		}
        
        writer.endElement("div");
        
		//buttons
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_buttons", null);
        writer.writeAttribute("class", ConfirmDialog.BUTTONPANE_CLASS, style);
		renderChildren(context, dialog);
		writer.endElement("div");
        
        writer.endElement("div");
	}
    
    

	protected void encodeScript(FacesContext context, ConfirmDialog dialog) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = dialog.getClientId();
		
		writer.startElement("script", dialog);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(dialog.resolveWidgetVar() + " = new PrimeFaces.widget.ConfirmDialog('" + clientId + "', {");
		
		writer.write("minHeight:0");
		
		if(dialog.getStyleClass() != null) writer.write(",dialogClass:'" + dialog.getStyleClass() + "'");
		if(dialog.getWidth() != 300) writer.write(",width:" + dialog.getWidth());
		if(dialog.getHeight() != Integer.MIN_VALUE) writer.write(",height:" + dialog.getHeight());
		if(dialog.getZindex() != 1000) writer.write(",zIndex:" + dialog.getZindex());
		if(!dialog.isCloseOnEscape()) writer.write(",closeOnEscape:false");
		if(!dialog.isClosable()) writer.write(",closable:false");
        if(dialog.isAppendToBody()) writer.write(",appendToBody:true");

        writer.write("});");

		writer.endElement("script");
	}

    protected void encodeHeader(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = dialog.getHeader();
        UIComponent headerFacet = dialog.getFacet("header");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.TITLE_BAR_CLASS, null);
        
        //title
        writer.startElement("span", null);
        writer.writeAttribute("class", Dialog.TITLE_CLASS, null);
        
        if(headerFacet != null)
            headerFacet.encodeAll(context);
        else if(header != null)
            writer.write(header);
        
        writer.endElement("span");
        
        if(dialog.isClosable()){
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_CLOSE_CLASS, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.CLOSE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
        }
        
        writer.endElement("div");
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