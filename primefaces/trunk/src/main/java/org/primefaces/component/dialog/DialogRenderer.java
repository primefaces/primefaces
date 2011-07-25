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
        writer.write(",draggable:" + dialog.isDraggable());
        writer.write(",modal:" + dialog.isModal());
        writer.write(",resizable:" + dialog.isResizable());
        writer.write(",width:" + dialog.getWidth());
        writer.write(",height:" + dialog.getHeight());
        writer.write(",zIndex:" + dialog.getZindex());
        writer.write(",minWidth:" + dialog.getMinWidth());
        writer.write(",closeOnEscape:" + dialog.isCloseOnEscape());
        writer.write(",appendToBody:" + dialog.isAppendToBody());
        writer.write(",closable:" + dialog.isClosable());
        writer.write(",showHeader:" + dialog.isShowHeader());
        if(dialog.getShowEffect() != null) writer.write(",showEffect:'" + dialog.getShowEffect() + "'");
        if(dialog.getHideEffect() != null) writer.write(",hideEffect:'" + dialog.getHideEffect() + "'");
        if (dialog.getPosition() != null) writer.write(",position:'" + dialog.getPosition() + "'");

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

        encodeFooter(context, dialog);

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
        writer.writeAttribute("class", Dialog.TITLE_CLASS, null);
        if(headerFacet != null)
            headerFacet.encodeAll(context);
        else if(header != null)
            writer.write(header);
        writer.endElement("span");
        
        //close icon
        if(dialog.isClosable()){
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_CLOSE_CLASS, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.CLOSE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
        }
        
        //maximize icon
        if(dialog.isMaximize()){
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_MAXIMIZE_CLASS, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.MAXIMIZE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
        }
        
        //minimize icon
        if(dialog.isMinimize()){
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", Dialog.TITLE_BAR_MINIMIZE_CLASS, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", Dialog.MINIMIZE_ICON_CLASS, null);
            writer.endElement("span");

            writer.endElement("a");
            
        }
        
        
        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String footer = dialog.getFooter();
        UIComponent footerFacet = dialog.getFacet("footer");
        
        if( footer == null && footerFacet == null)
            return;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Dialog.FOOTER_CLASS, null);
        
        writer.startElement("span", null);
        if(footerFacet != null)
            footerFacet.encodeAll(context);
        else if(footer != null)
            writer.write(footer);
        writer.endElement("span");
        
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