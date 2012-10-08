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
package org.primefaces.component.panel;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.Menu;
import org.primefaces.renderkit.CoreRenderer;

public class PanelRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Panel panel = (Panel) component;
        String clientId = panel.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //Restore toggle state
        String collapsedParam = params.get(clientId + "_collapsed");
        if(collapsedParam != null) {
            panel.setCollapsed(Boolean.valueOf(collapsedParam));
        }

        //Restore visibility state
        String visibleParam = params.get(clientId + "_visible");
        if(visibleParam != null) {
            panel.setVisible(Boolean.valueOf(visibleParam));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Panel panel = (Panel) component;

        encodeMarkup(facesContext, panel);
        encodeScript(facesContext, panel);
    }

    protected void encodeScript(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Panel','" + panel.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        //Toggle configuration
        if(panel.isToggleable()) {
            writer.write(",toggleable:true");
            writer.write(",toggleSpeed:" + panel.getToggleSpeed());
            writer.write(",collapsed:" + panel.isCollapsed());
            writer.write(",toggleOrientation:'" + panel.getToggleOrientation() + "'");
        }

        //Toggle configuration
        if(panel.isClosable()) {
            writer.write(",closable:true");
            writer.write(",closeSpeed:" + panel.getCloseSpeed());
        }

        //Options menu configuration
        if(panel.getOptionsMenu() != null) {
            writer.write(",hasMenu:true");
        }

        encodeClientBehaviors(context, panel);

        writer.write("});");
        
        endScript(writer);
    }

    protected void encodeMarkup(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        Menu optionsMenu = panel.getOptionsMenu();
        boolean collapsed = panel.isCollapsed();
        boolean visible = panel.isVisible();
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        String styleClass = panel.getStyleClass() == null ? Panel.PANEL_CLASS : Panel.PANEL_CLASS + " " + panel.getStyleClass(); 
        
        if(collapsed) {
            styleClass += " ui-hidden-container";
            
            if(panel.getToggleOrientation().equals("horizontal")) {
                styleClass += " ui-panel-collapsed-h";
            }
        }
        
        if(!visible) {
            styleClass += " ui-helper-hidden";
        }
            
        writer.writeAttribute("class", styleClass, "styleClass");
        
        if(panel.getStyle() != null) {
            writer.writeAttribute("style", panel.getStyle(), "style");
        }

        encodeHeader(context, panel);
        encodeContent(context, panel);
        encodeFooter(context, panel);

        if(panel.isToggleable()) {
            encodeStateHolder(context, panel, clientId + "_collapsed", String.valueOf(collapsed));
        }

        if(panel.isClosable()) {
            encodeStateHolder(context, panel, clientId + "_visible", String.valueOf(visible));
        }

        if (optionsMenu != null) {
            optionsMenu.setOverlay(true);
            optionsMenu.setTrigger(clientId + "_menu");
            optionsMenu.setMy("left top");
            optionsMenu.setAt("left bottom");

            optionsMenu.encodeAll(context);
        }

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
        String clientId = panel.getClientId(context);

        if(headerText == null && header == null) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId(context) + "_header", null);
        writer.writeAttribute("class", Panel.PANEL_TITLEBAR_CLASS, null);

        //Title
        writer.startElement("span", null);
        writer.writeAttribute("class", Panel.PANEL_TITLE_CLASS, null);

        if(header != null) {
            renderChild(context, header);
        } 
        else if(headerText != null) {
            writer.write(headerText);
        }

        writer.endElement("span");

        //Options
        if(panel.isClosable()) {
            encodeIcon(context, panel, "ui-icon-closethick", clientId + "_closer", panel.getCloseTitle());
        }

        if(panel.isToggleable()) {
            String icon = panel.isCollapsed() ? "ui-icon-plusthick" : "ui-icon-minusthick";
            encodeIcon(context, panel, icon, clientId + "_toggler", panel.getToggleTitle());
        }

        if(panel.getOptionsMenu() != null) {
            encodeIcon(context, panel, "ui-icon-gear", clientId + "_menu", panel.getMenuTitle());
        }
        
        //Actions
        UIComponent actionsFacet = panel.getFacet("actions");
        if(actionsFacet != null) {
            actionsFacet.encodeAll(context);
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext facesContext, Panel panel) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId() + "_content", null);
        writer.writeAttribute("class", Panel.PANEL_CONTENT_CLASS, null);
        if (panel.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChildren(facesContext, panel);

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext facesContext, Panel panel) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        UIComponent footer = panel.getFacet("footer");
        String footerText = panel.getFooter();

        if (footer != null || footerText != null) {
            writer.startElement("div", null);
            writer.writeAttribute("id", panel.getClientId(facesContext) + "_footer", null);
            writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);

            if (footer != null) {
                renderChild(facesContext, footer);
            } else if (footerText != null) {
                writer.write(footerText);
            }

            writer.endElement("div");
        }
    }

    protected void encodeIcon(FacesContext context, Panel panel, String iconClass, String id, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        if(id != null) {
            writer.writeAttribute("id", id, null);
        }
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Panel.PANEL_TITLE_ICON_CLASS, null);
        if(title != null) {
            writer.writeAttribute("title", title, null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon " + iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    protected void encodeStateHolder(FacesContext context, Panel panel, String name, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("value", value, null);
        writer.endElement("input");
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
