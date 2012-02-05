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
package org.primefaces.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class LayoutRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        String clientId = layout.getClientId(context);

        encodeScript(context, layout);

        if(layout.isElementLayout()) {
            writer.startElement("div", layout);
            writer.writeAttribute("id", clientId, "id");

            if(layout.getStyle() != null) writer.writeAttribute("style", layout.getStyle(), "style");
            if(layout.getStyleClass() != null) writer.writeAttribute("class", layout.getStyleClass(), "styleClass");
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;

        if(layout.isElementLayout()) {
            writer.endElement("div");
        }
    }

    protected void encodeScript(FacesContext context, Layout layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = layout.getClientId(context);
        
        startScript(writer, clientId);

        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('Layout','" + layout.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
                
        if(layout.isFullPage()) 
            writer.write(",full:true");

        if(layout.isNested())
            writer.write(",parent:'" + layout.getParent().getClientId(context) + "'");
        
        if(layout.isStateful())
            writer.write(",useStateCookie:true");

        encodeUnits(context, layout);

        encodeClientCallbacks(context, layout);

        encodeClientBehaviors(context, layout);

        writer.write("},'layout');});");

        endScript(writer);
    }

    protected void encodeUnits(FacesContext context, Layout layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for(UIComponent child : layout.getChildren()) {
            if(child.isRendered() && child instanceof LayoutUnit) {
                LayoutUnit unit = (LayoutUnit) child;
                
                writer.write("," + unit.getPosition() + ":{");
                writer.write("paneSelector:'" + ComponentUtils.escapeJQueryId(unit.getClientId(context)) + "'");
                writer.write(",size:'" + unit.getSize() + "'");
                writer.write(",resizable:" + unit.isResizable());
                writer.write(",closable:" + unit.isCollapsible());

                if(unit.getMinSize() != 50) writer.write(",minSize:" + unit.getMinSize());
                if(unit.getMaxSize() != 0) writer.write(",maxSize:" + unit.getMaxSize());

                if(unit.isCollapsible()) {
                    writer.write(",spacing_open:" + unit.getGutter());
                    writer.write(",spacing_closed:" + unit.getCollapseSize());
                }

                if(!unit.isVisible()) writer.write(",initHidden:true");
                if(unit.isCollapsed()) writer.write(",initClosed:true");

                if(unit.getEffect() != null) writer.write(",fxName:'" + unit.getEffect() + "'");
                if(unit.getEffectSpeed() != null) writer.write(",fxSpeed:'" + unit.getEffectSpeed() + "'");

                if(layout.getResizeTitle() != null) writer.write(",resizerTip:'" + layout.getResizeTitle() + "'");
                if(layout.getExpandTitle() != null) writer.write(",togglerTip_closed:'" + layout.getExpandTitle() + "'");

                writer.write("}");
            }
        }
    }

    protected void encodeClientCallbacks(FacesContext context, Layout layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if(layout.getOnToggle() != null)
            writer.write(",onToggle:function(e) {" + layout.getOnToggle() + "}");

        if(layout.getOnClose() != null)
            writer.write(",onClose:function(e) {" + layout.getOnClose() + "}");

        if(layout.getOnResize() != null)
            writer.write(",onResize:function(e) {" + layout.getOnResize() + "}");
    }
}
