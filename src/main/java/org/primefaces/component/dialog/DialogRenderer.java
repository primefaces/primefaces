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
package org.primefaces.component.dialog;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.CloseEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DialogRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Dialog dialog = (Dialog) component;
        String clientId = dialog.getClientId(facesContext);
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

        //Queue close event
        if (params.containsKey(clientId + "_ajaxClose")) {
            dialog.setVisible(false);
            dialog.queueEvent(new CloseEvent(dialog));
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Dialog dialog = (Dialog) component;

        encodeMarkup(facesContext, dialog);
        encodeScript(facesContext, dialog);
    }

    protected void encodeScript(FacesContext facesContext, Dialog dialog) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = dialog.getClientId(facesContext);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function() {");

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
        if(dialog.getShowEffect() != null) writer.write(",show:'" + dialog.getShowEffect() + "'");
        if(dialog.getHideEffect() != null) writer.write(",hide:'" + dialog.getHideEffect() + "'");
        if(!dialog.isCloseOnEscape()) writer.write(",closeOnEscape:false");
        if(!dialog.isClosable()) writer.write(",closable:false");

        //Position
        String position = dialog.getPosition();
        if (position != null) {
            if (position.contains(",")) {
                writer.write(",position:[" + position + "]");
            } else {
                writer.write(",position:'" + position + "'");
            }
        }

        //Ajax Close
        if (dialog.getCloseListener() != null || dialog.getOnCloseUpdate() != null) {
            writer.write(",ajaxClose:true");
            writer.write(",url:'" + getActionURL(facesContext) + "'");

            if (dialog.getOnCloseUpdate() != null) {
                writer.write(",onCloseUpdate:'" + ComponentUtils.findClientIds(facesContext, dialog, dialog.getOnCloseUpdate()) + "'");
            }
        }

        //Client side callbacks
        if(dialog.getOnShow() != null) writer.write(",onShow:function(event, ui) {" + dialog.getOnShow() + "}");
        if(dialog.getOnHide() != null) writer.write(",onHide:function(event, ui) {" + dialog.getOnHide() + "}");

        writer.write("});});");

        writer.endElement("script");
    }

    protected void encodeMarkup(FacesContext facesContext, Dialog dialog) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = dialog.getClientId(facesContext);
        String headerText = dialog.getHeader();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("style", "display:none", null);
        if (headerText != null) {
            writer.writeAttribute("title", headerText, null);
        }

        renderChildren(facesContext, dialog);

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
