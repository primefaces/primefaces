/*
 * Copyright 2009-2012 Prime Teknoloji.
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
        
        String defaultStyleClass = nativeMode ? ScrollPanel.SCROLL_PANEL_NATIVE_CLASS : ScrollPanel.SCROLL_PANEL_CLASS;
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        writer.startElement("div", panel); 
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        renderChildren(context, panel);
        
        //scrollpanel
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);

        startScript(writer, clientId);

        writer.write("PrimeFaces.cw('ScrollPanel','" + panel.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",mode:'" + panel.getMode() + "'");
        writer.write("});");

        endScript(writer);
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
