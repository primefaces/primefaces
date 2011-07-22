/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class DialogRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Dialog dialog = (Dialog) component;

        encodeMarkup(context, dialog);
        encodeScript(context, dialog);
    }

    protected void encodeScript(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");

        writer.write(dialog.resolveWidgetVar() + " = new PrimeFaces.widget.Dialog('" + clientId + "',");

        writer.write("{");
        writer.write("autoOpen:" + dialog.isVisible());
        writer.write(",minHeight:" + dialog.getMinHeight());

        if(dialog.getStyleClass() != null) writer.write(",dialogClass:'" + dialog.getStyleClass() + "'");
        if(dialog.getWidth() != 300) writer.write(",width:" + dialog.getWidth());
        if(dialog.getHeight() != Integer.MIN_VALUE) writer.write(",height:" + dialog.getHeight());
        if(!dialog.isDraggable()) writer.write(",draggable: false");
        if(dialog.isModal()) writer.write(",modal: true");
        if(dialog.getZindex() != 1000) writer.write(",zIndex:" + dialog.getZindex());
        if(!dialog.isResizable()) writer.write(",resizable:false");
        if(dialog.getMinWidth() != 150) writer.write(",minWidth:" + dialog.getMinWidth());
        if(dialog.getShowEffect() != null) writer.write(",showEffect:'" + dialog.getShowEffect() + "'");
        if(dialog.getHideEffect() != null) writer.write(",hideEffect:'" + dialog.getHideEffect() + "'");
        if(!dialog.isCloseOnEscape()) writer.write(",closeOnEscape:false");
        if(dialog.isAppendToBody()) writer.write(",appendToBody:true");
        if(!dialog.isClosable()) writer.write(",closable:false");
        if(!dialog.isShowHeader()) writer.write(",showHeader:false");

        //Position
        String position = dialog.getPosition();
        if (position != null) {
            if (position.contains(",")) {
                writer.write(",position:[" + position + "]");
            } else {
                writer.write(",position:'" + position + "'");
            }
        }

        //Client side callbacks
        if(dialog.getOnShow() != null) writer.write(",onShow:function(event, ui) {" + dialog.getOnShow() + "}");
        if(dialog.getOnHide() != null) writer.write(",onHide:function(event, ui) {" + dialog.getOnHide() + "}");

        //Behaviors
        encodeClientBehaviors(context, dialog);

        writer.write("});});");

        writer.endElement("script");
    }

    protected void encodeMarkup(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        styleClass = styleClass == null ? Dialog.CONTAINER_CLASS : Dialog.CONTAINER_CLASS + " " + styleClass;


        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        if(dialog.isShowHeader()) {
            encodeHeader(context, dialog);
        }
        
        encodeContent(context, dialog);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = dialog.getHeader();
        UIComponent headerFacet = dialog.getFacet("header");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.TITLE_BAR_CLASS, null);
        
        //title
        writer.startElement("span", null);
        if(headerFacet != null)
            headerFacet.encodeAll(context);
        else if(header != null)
            writer.write(header);
        writer.endElement("span");
        
        //close icon
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Dialog.TITLE_BAR_CLOSE_CLASS, null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", Dialog.CLOSE_ICON_CLASS, null);
        writer.endElement("span");
        
        writer.endElement("a");
        
        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.CONTENT_CLASS, null);
        
        renderChildren(context, dialog);
        
        writer.endElement("div");
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