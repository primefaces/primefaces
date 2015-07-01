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
import org.primefaces.component.panel.Panel;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class PanelRenderer extends org.primefaces.component.panel.PanelRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, Panel panel) throws IOException {
        String clientId = panel.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Panel", panel.resolveWidgetVar(), clientId);
        
        wb.attr("toggleable", panel.isToggleable(), false);
        
        encodeClientBehaviors(context, panel);

        wb.finish();
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        boolean toggleable = panel.isToggleable();
        boolean collapsed = panel.isCollapsed();
        String widgetVar = panel.resolveWidgetVar();
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = (styleClass == null) ? Panel.MOBILE_CLASS : Panel.MOBILE_CLASS + " " + styleClass;
        if (collapsed) {
            styleClass += " ui-hidden-container";
        }
    
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);
                
        encodeHeader(context, panel, collapsed, toggleable);
        encodeContent(context, panel, collapsed);
        
        renderDynamicPassThruAttributes(context, panel);
        
        if (toggleable) {
            encodeStateHolder(context, panel, clientId + "_collapsed", String.valueOf(collapsed));
        }
        
        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Panel panel, boolean collapsed, boolean toggleable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Panel.MOBILE_TITLE_CLASS, null);
                 
        writer.startElement("h3", null);
        if (header != null) {
            renderChild(context, header);
        } else if (headerText != null) {
            writer.write(headerText);
        }
        writer.endElement("h3");
        
        if (toggleable) {
            String toggleIconClass = collapsed ? Panel.MOBILE_TOGGLEICON_COLLAPSED_CLASS : Panel.MOBILE_TOGGLEICON_EXPANDED_CLASS;
            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", toggleIconClass, null);
            writer.endElement("a");
        }
        
        writer.endElement("div");
    }
    
    protected void encodeContent(FacesContext context, Panel panel, boolean collapsed) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
 
        writer.startElement("div", null);
        writer.writeAttribute("class", Panel.MOBILE_CONTENT_CLASS, null);
        if (collapsed) {
            writer.writeAttribute("style", "display:none", null);
        }
        writer.startElement("p", null);
        renderChildren(context, panel);
        writer.endElement("p");
        writer.endElement("div");
    }
}
