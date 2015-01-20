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
package org.primefaces.component.confirmdialog;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.expression.SearchExpressionFacade;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class ConfirmDialogRenderer extends CoreRenderer {

	private static final Logger LOG = Logger.getLogger(ConfirmDialogRenderer.class.getName());
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ConfirmDialog dialog = (ConfirmDialog) component;
		
		encodeScript(context, dialog);
        encodeMarkup(context, dialog);
	}

	protected void encodeMarkup(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = styleClass == null ? ConfirmDialog.CONTAINER_CLASS : ConfirmDialog.CONTAINER_CLASS + " " + styleClass;

        if(ComponentUtils.isRTL(context, dialog)) {
            styleClass += " ui-dialog-rtl";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeHeader(context, dialog);
        encodeContent(context, dialog);
        encodeButtonPane(context, dialog);
        
        writer.endElement("div");
	}
    
	protected void encodeScript(FacesContext context, ConfirmDialog dialog) throws IOException {
		String clientId = dialog.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.initWithDomReady("ConfirmDialog", dialog.resolveWidgetVar(), clientId)
            .attr("visible", dialog.isVisible(), false)
            .attr("width", dialog.getWidth(), null)
            .attr("height", dialog.getHeight(), null)
            .attr("appendTo", SearchExpressionFacade.resolveClientId(context, dialog, dialog.getAppendTo()), null)
            .attr("showEffect", dialog.getShowEffect(), null)
            .attr("hideEffect", dialog.getHideEffect(), null)
            .attr("closeOnEscape", dialog.isCloseOnEscape(), false)
            .attr("global", dialog.isGlobal(), false);
  
        wb.finish();
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
            writer.writeText(header, null);
        
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
    
    protected void encodeContent(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = dialog.getMessage();
        UIComponent messageFacet = dialog.getFacet("message");
        String defaultIcon = dialog.isGlobal() ? "ui-icon" : "ui-icon ui-icon-" + dialog.getSeverity();
        String severityIcon = defaultIcon + " " + ConfirmDialog.SEVERITY_ICON_CLASS;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        
		//severity
		writer.startElement("span", null);
		writer.writeAttribute("class", severityIcon, null);
		writer.endElement("span");

        writer.startElement("span", null);
		writer.writeAttribute("class", ConfirmDialog.MESSAGE_CLASS, null);
        
        if(messageFacet != null)
            messageFacet.encodeAll(context);
        else if(messageText != null)
			writer.writeText(messageText, null);
        
        writer.endElement("span");
        
        writer.endElement("div");
    }
    
    protected void encodeButtonPane(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", ConfirmDialog.BUTTONPANE_CLASS, null);
        
		renderChildren(context, dialog);
		
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