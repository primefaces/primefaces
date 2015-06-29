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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.dialog.Dialog;

public class ConfirmDialogRenderer extends org.primefaces.component.confirmdialog.ConfirmDialogRenderer {
    
    @Override
    protected void encodeMarkup(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = (styleClass == null) ? Dialog.MOBILE_CONTAINER_CLASS : Dialog.MOBILE_CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_mask", null);
        writer.writeAttribute("class", Dialog.MOBILE_MASK_CLASS, null);
        writer.endElement("div");
        
        writer.startElement("div", dialog);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.MOBILE_POPUP_CLASS, null);
        
        encodeHeader(context, dialog);
        encodeContent(context, dialog);
        
        renderDynamicPassThruAttributes(context, dialog);
        
        writer.endElement("div");

        writer.endElement("div");
    }
    
    @Override
    protected void encodeHeader(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = dialog.getHeader();
        UIComponent headerFacet = dialog.getFacet("header");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.MOBILE_TITLE_BAR_CLASS, null);
        
        //close
        if(dialog.isClosable()) {
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.MOBILE_CLOSE_ICON_CLASS, null);
            writer.endElement("a");
        }
        
        //title
        writer.startElement("h1", null);
        writer.writeAttribute("class", Dialog.MOBILE_TITLE_CLASS, null);
        writer.writeAttribute("role", "heading", null);
        
        if(headerFacet != null)
            headerFacet.encodeAll(context);
        else if(header != null)
            writer.write(header);
        
        writer.endElement("h1");
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodeContent(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = dialog.getMessage();
        UIComponent messageFacet = dialog.getFacet("message");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-content", null);
        writer.writeAttribute("role", "main", null);
        
        writer.startElement("span", null);
		writer.writeAttribute("class", "ui-title", null);
        
        if(messageFacet != null)
            messageFacet.encodeAll(context);
        else if(messageText != null)
			writer.writeText(messageText, null);
        
        writer.endElement("span");
        
        renderChildren(context, dialog);
        
        writer.endElement("div");
    }
}
