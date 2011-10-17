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
package org.primefaces.component.scrollpanel;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ScrollPanelRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ScrollPanel panel = (ScrollPanel) component;

        encodeMarkup(context, panel);
        encodeScript(context, panel);
    }

    protected void encodeMarkup(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        boolean nativeMode = panel.getMode().equals("native");
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        String defaultStyleClass = nativeMode ? ScrollPanel.SCROLL_PANEL_NATIVE_CLASS : ScrollPanel.SCROLL_PANEL_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", panel); 
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", defaultStyleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        if(nativeMode) {
            renderChildren(context, panel);
        } 
        else {
            writer.startElement("div", panel);
            writer.writeAttribute("class", ScrollPanel.SCROLL_PANEL_CONTAINER_CLASS, "container");

            writer.startElement("div", panel);
            writer.writeAttribute("class", ScrollPanel.SCROLL_PANEL_WRAPPER_CLASS, "wrapper");

            writer.startElement("div", panel);
            writer.writeAttribute("class", ScrollPanel.SCROLL_PANEL_CONTENT_CLASS, "content");
            renderChildren(context, panel);
            writer.endElement("div");
            
            writer.endElement("div");

            encodeScrollBar(context, panel, false);
            encodeScrollBar(context, panel, true);

            writer.endElement("div");
        }

        //scrollpanel
        writer.endElement("div");
    }
    
    protected void encodeScrollBar(FacesContext context, ScrollPanel panel, boolean vertical) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        
        String barClass, gripClass, buttonUpClass, buttonDownClass, iconUpClass, iconDownClass;
        
        if(vertical){
            barClass = ScrollPanel.SCROLL_PANEL_VBAR_CLASS;
            gripClass = ScrollPanel.SCROLL_PANEL_HGRIP_CLASS;
            buttonUpClass = ScrollPanel.SCROLL_PANEL_BTOP_CLASS;
            buttonDownClass = ScrollPanel.SCROLL_PANEL_BBOTTOM_CLASS;
            iconUpClass = ScrollPanel.SCROLL_PANEL_INORTH_CLASS;
            iconDownClass = ScrollPanel.SCROLL_PANEL_ISOUTH_CLASS;
        }
        else{
            barClass = ScrollPanel.SCROLL_PANEL_HBAR_CLASS;
            gripClass = ScrollPanel.SCROLL_PANEL_VGRIP_CLASS;
            buttonUpClass = ScrollPanel.SCROLL_PANEL_BLEFT_CLASS;
            buttonDownClass = ScrollPanel.SCROLL_PANEL_BRIGHT_CLASS;
            iconUpClass = ScrollPanel.SCROLL_PANEL_IWEST_CLASS;
            iconDownClass = ScrollPanel.SCROLL_PANEL_IEAST_CLASS;
        }
        
        writer.startElement("div", panel);
        writer.writeAttribute("class", barClass, "scrollbars");
        
        writer.startElement("div", panel);
        writer.writeAttribute("class", ScrollPanel.SCROLL_PANEL_HANDLE_CLASS, "barhandler");
        writer.startElement("span", panel);
        writer.writeAttribute("class", gripClass, "grips");
        writer.endElement("span");
        writer.endElement("div");
        
        writer.startElement("div", panel);
        writer.writeAttribute("class", buttonUpClass, "buttonup");
        writer.startElement("span", panel);
        writer.writeAttribute("class", iconUpClass, "iconup");
        writer.endElement("span");
        writer.endElement("div");
        
        
        writer.startElement("div", panel);
        writer.writeAttribute("class", buttonDownClass, "buttondown");
        writer.startElement("span", panel);
        writer.writeAttribute("class", iconDownClass, "icondown");
        writer.endElement("span");        
        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean nativeMode = panel.getMode().equals("native");
        
        if(!nativeMode) {
            String clientId = panel.getClientId(context);

            startScript(writer, clientId);

            writer.write("$(function(){");
            writer.write(panel.resolveWidgetVar() + " = new PrimeFaces.widget.ScrollPanel('" + clientId + "',{");
            writer.write("mode:'" + panel.getMode() + "'");
            writer.write("});});");

            endScript(writer);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
