/*
 * Copyright 2009-2013 PrimeTek.
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
import org.primefaces.component.panel.Panel;

public class PanelRenderer extends org.primefaces.component.panel.PanelRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, Panel panel) throws IOException {
        //No widget
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        boolean toggleable = panel.isToggleable();
        boolean collapsed = panel.isCollapsed();
        String containerClass = collapsed ? Panel.MOBILE_CLASS_COLLAPSED : Panel.MOBILE_CLASS;
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = (styleClass == null) ? containerClass : containerClass + " " + styleClass;
    
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("data-enhanced", "true", null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        if (toggleable) {
            writer.writeAttribute("data-role", "collapsible", null);
            writer.writeAttribute("data-collapsed", String.valueOf("collapsed"), null);
        }
        
        encodeHeader(context, panel, collapsed, toggleable);
        encodeContent(context, panel, collapsed);
        
        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Panel panel, boolean collapsed, boolean toggleable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
        String titleBarClass = collapsed ? Panel.MOBILE_TITLEBAR_CLASS_COLLAPSED : Panel.MOBILE_TITLEBAR_CLASS;
        String titleClass;
        if(toggleable) {
            titleClass = "ui-collapsible-heading-toggle ui-btn ui-btn-icon-left ui-icon-plus";
            titleClass = collapsed ? titleClass + " ui-icon-plus" : titleClass + " ui-icon-collapsed";
        }
        else {
            titleClass = "ui-btn";
        }
         
        writer.startElement("h4", null);
        writer.writeAttribute("class", titleBarClass, null);
        writer.startElement("a", null);
        writer.writeAttribute("class", titleClass, null);
        
        if (header != null) {
            renderChild(context, header);
        } else if (headerText != null) {
            writer.write(headerText);
        }
        
        writer.endElement("a");
        writer.endElement("h4");
    }
    
    protected void encodeContent(FacesContext context, Panel panel, boolean collapsed) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String contentClass = collapsed ? Panel.MOBILE_CONTENT_CLASS_COLLAPSED: Panel.MOBILE_CONTENT_CLASS;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", contentClass, null);
        writer.startElement("p", null);
        renderChildren(context, panel);
        writer.endElement("p");
        writer.endElement("div");
    }
}
