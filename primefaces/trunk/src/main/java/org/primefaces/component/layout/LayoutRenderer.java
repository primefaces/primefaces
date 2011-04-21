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
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        String clientId = layout.getClientId(context);

        if(!layout.isFullPage()) {
            writer.startElement("div", layout);
            writer.writeAttribute("id", clientId, "id");

            if(layout.getStyle() != null) writer.writeAttribute("style", layout.getStyle(), "style");
            if(layout.getStyleClass() != null) writer.writeAttribute("class", layout.getStyleClass(), "styleClass");
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        Layout layout = (Layout) component;

        if(!layout.isFullPage()) {
            writer.endElement("div");
        }

        encodeScript(facesContext, layout);
    }

    protected void encodeScript(FacesContext context, Layout layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = layout.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");
        writer.write(layout.resolveWidgetVar() + " = new PrimeFaces.widget.Layout('" + clientId + "', {");
        
        writer.write("full:" + layout.isFullPage());

        encodeUnits(context, layout);

        writer.write("});});");

        writer.endElement("script");
    }

    protected void encodeUnits(FacesContext facesContext, Layout layout) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        for(UIComponent child : layout.getChildren()) {
            if(child.isRendered() && child instanceof LayoutUnit) {
                LayoutUnit unit = (LayoutUnit) child;
                
                writer.write("," + unit.getLocation() + ":{");
                writer.write("size:'" + unit.getSize() + "'");

                if(unit.getMinSize() != 50) writer.write(",minSize:" + unit.getMinSize());
                if(unit.getMaxSize() != 0) writer.write(",maxSize:" + unit.getMaxSize());

                if(unit.isCollapsible()) {
                    writer.write(",spacing_open:" + unit.getGutter());
                    writer.write(",spacing_closed:" + unit.getCollapseSize());
                }

                writer.write(",togglerLength_open:0");

                writer.write("}");

            }
        }

    }
/*
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
                int width = Integer.valueOf(params.get(clientId + "_unitWidth"));
                int height = Integer.valueOf(params.get(clientId + "_unitHeight"));

                unit.setWidth(width);
                unit.setHeight(height);

                layout.queueEvent(new ResizeEvent(unit, width, height));
            }

        }
    }

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        Layout layout = (Layout) component;
        String clientId = layout.getClientId(facesContext);

        if (!layout.isFullPage()) {
            writer.startElement("div", layout);
            writer.writeAttribute("id", clientId, "id");
            if (layout.getStyle() != null) {
                writer.writeAttribute("style", layout.getStyle(), "style");
            }
            if (layout.getStyleClass() != null) {
                writer.writeAttribute("class", layout.getStyleClass(), "styleClass");
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        Layout layout = (Layout) component;

        if (!layout.isFullPage()) {
            writer.endElement("div");
        }

        encodeScript(facesContext, layout);
    }

    protected void encodeScript(FacesContext facesContext, Layout layout) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = layout.getClientId(facesContext);
        String widgetVar = layout.resolveWidgetVar();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        if(layout.isFullPage()) {
            writer.write(widgetVar + " = new PrimeFaces.widget.Layout({");
        } else {
            writer.write(widgetVar + " = new PrimeFaces.widget.Layout('" + clientId + "', {");
        }

        encodeUnits(facesContext, layout);
        encodeAjaxEventListeners(facesContext, layout);

        if(layout.isFullPage()) {
            writer.write(",clientId:'" + clientId + "'");
        }
        if(layout.getCloseTitle() != null) {
            writer.write(",closeTitle:'" + layout.getCloseTitle() + "'");
        }
        if(layout.getCollapseTitle() != null) {
            writer.write(",collapseTitle:'" + layout.getCollapseTitle() + "'");
        }
        if(layout.getExpandTitle() != null) {
            writer.write(",expandTitle:'" + layout.getExpandTitle() + "'");
        }

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeAjaxEventListeners(FacesContext facesContext, Layout layout) throws IOException {
        encodeAjaxEventListener(facesContext, layout, "onToggleUpdate", layout.getOnToggleUpdate(), "ajaxToggle", "onToggleComplete", layout.getOnToggleComplete());
        encodeAjaxEventListener(facesContext, layout, "onCloseUpdate", layout.getOnCloseUpdate(), "ajaxClose", "onCloseComplete", layout.getOnCloseComplete());
        encodeAjaxEventListener(facesContext, layout, "onResizeUpdate", layout.getOnResizeUpdate(), "ajaxResize", "onResizeComplete", layout.getOnResizeComplete());
    }

    protected void encodeAjaxEventListener(FacesContext facesContext, Layout layout, String updateParam, String update, String ajaxEventParam, String callbackName, String callback) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.write("," + ajaxEventParam + ":true");

        if (update != null) {
            writer.write("," + updateParam + ":'" + ComponentUtils.findClientIds(facesContext, layout, update) + "'");
        }

        if (callback != null) {
            writer.write("," + callbackName + ":function(xhr, status, args) {" + callback + ";}");
        }
    }

    protected void encodeUnits(FacesContext facesContext, Layout layout) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.write("units:[");

        boolean firstUnitWritten = false;
        for (UIComponent child : layout.getChildren()) {
            if (child.isRendered() && child instanceof LayoutUnit) {
                LayoutUnit unit = (LayoutUnit) child;

                if (firstUnitWritten) {
                    writer.write(",");
                } else {
                    firstUnitWritten = true;
                }

                writer.write("{position:'" + unit.getPosition() + "'");
                writer.write(",body:'" + unit.getClientId(facesContext) + "'");
                writer.write(",gutter:'" + unit.getGutter() + "'");
                writer.write(",close:" + unit.isClosable());
                writer.write(",resize:" + unit.isResizable());
                writer.write(",collapse:" + unit.isCollapsible());
                writer.write(",visible:" + unit.isVisible());
                writer.write(",collapsed:" + unit.isCollapsed());

                if(unit.getScrollable() != null) writer.write(",scroll:" + unit.getScrollable());
                if(unit.getWidth() != -1) writer.write(",width:" + unit.getWidth());
                if(unit.getHeight() != -1) writer.write(",height:" + unit.getHeight());
                if(unit.getMinWidth() != -1) writer.write(",minWidth:" + unit.getMinWidth());
                if(unit.getMinHeight() != -1) writer.write(",minHeight:" + unit.getMinHeight());
                if(unit.getMaxWidth() != -1) writer.write(",maxWidth:" + unit.getMaxWidth());
                if(unit.getMaxHeight() != -1) writer.write(",maxHeight:" + unit.getMaxHeight());
                if(unit.getHeader() != null) writer.write(",header:'" + unit.getHeader() + "'");
                if(unit.getFooter() != null) writer.write(",footer:'" + unit.getFooter() + "'");
                if(unit.getZindex() != -1) writer.write(",zIndex:" + unit.getZindex());
                if(!unit.isProxyResize()) writer.write(",proxy:false");
                if(unit.getCollapseSize() != -1) writer.write(",collapseSize:" + unit.getCollapseSize());

                writer.write("}");
            }
        }

        writer.write("]");
    }*/
}
