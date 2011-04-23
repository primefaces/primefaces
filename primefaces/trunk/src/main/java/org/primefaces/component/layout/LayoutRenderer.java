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
package org.primefaces.component.layout;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class LayoutRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Layout layout = (Layout) component;
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = layout.getClientId(facesContext);

        if(params.containsKey(clientId)) {

            //Queue toggle event
            if (params.containsKey(clientId + "_toggled")) {
                boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
                LayoutUnit unit = layout.getLayoutUnitByPosition(params.get(clientId + "_unit"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;
                unit.setCollapsed(collapsed);

                layout.queueEvent(new ToggleEvent(unit, visibility));
            }

            //Queue close event
            if (params.containsKey(clientId + "_closed")) {
                LayoutUnit unit = layout.getLayoutUnitByPosition(params.get(clientId + "_unit"));
                unit.setVisible(false);

                layout.queueEvent(new CloseEvent(unit));
            }

            //Queue resize event
            if (params.containsKey(clientId + "_resized")) {
                LayoutUnit unit = layout.getLayoutUnitByPosition(params.get(clientId + "_unit"));
                String position = unit.getPosition();
                int width = Integer.valueOf(params.get(clientId + "_width"));
                int height = Integer.valueOf(params.get(clientId + "_height"));

                if(position.equals("west") || position.equals("east")) {
                    unit.setSize(String.valueOf(width));
                } else if(position.equals("north") || position.equals("south")) {
                    unit.setSize(String.valueOf(height));
                }
                
                layout.queueEvent(new ResizeEvent(unit, width, height));
            }
        }
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

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");
        writer.write(layout.resolveWidgetVar() + " = new PrimeFaces.widget.Layout('" + clientId + "', {");
        
        writer.write("full:" + layout.isFullPage());

        if(layout.isNested()) {
            writer.write(",parent:'" + layout.getParent().getClientId(context) + "'");
        }

        encodeUnits(context, layout);

        encodeAjaxEventListeners(context, layout);

        writer.write("});});");

        writer.endElement("script");
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

    protected void encodeAjaxEventListeners(FacesContext context, Layout layout) throws IOException {
        if(layout.getToggleListener() != null)
            encodeAjaxEventListener(context, layout, "onToggleUpdate", layout.getOnToggleUpdate(), "ajaxToggle");

        if(layout.getCloseListener() != null)
            encodeAjaxEventListener(context, layout, "onCloseUpdate", layout.getOnCloseUpdate(), "ajaxClose");

        if(layout.getResizeListener() != null)
            encodeAjaxEventListener(context, layout, "onResizeUpdate", layout.getOnResizeUpdate(), "ajaxResize");
    }

    protected void encodeAjaxEventListener(FacesContext facesContext, Layout layout, String updateParam, String update, String ajaxEventParam) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.write("," + ajaxEventParam + ":true");

        if (update != null) {
            writer.write("," + updateParam + ":'" + ComponentUtils.findClientIds(facesContext, layout, update) + "'");
        }
    }
}
