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
package org.primefaces.component.tabview;

import java.io.IOException;
import java.util.Map;
import javax.el.MethodExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import org.primefaces.event.TabChangeEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class TabViewRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String activeIndexValue = params.get(tabView.getClientId(context) + "_activeIndex");

        if(!isValueEmpty(activeIndexValue)) {
            tabView.setActiveIndex(Integer.parseInt(activeIndexValue));
        }

        if(tabView.isTabChangeRequest(context)) {
            TabChangeEvent changeEvent = new TabChangeEvent(tabView, tabView.findTabToLoad(context));
            changeEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);

            tabView.queueEvent(changeEvent);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;

        if(tabView.isContentLoadRequest(context)) {
            Tab tabToLoad = (Tab) tabView.findTabToLoad(context);
            
            tabToLoad.encodeAll(context);
        } else {
            encodeMarkup(context, tabView);
            encodeScript(context, tabView);
        }
    }

    protected void encodeScript(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tabView.getClientId(context);
        MethodExpression tabChangeListener = tabView.getTabChangeListener();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write(tabView.resolveWidgetVar() + " = new PrimeFaces.widget.TabView('" + clientId + "', {");

        writer.write("selected:" + tabView.getActiveIndex());
        writer.write(",dynamic:" + tabView.isDynamic());
        writer.write(",cache:" + tabView.isCache());

        if(tabView.isCollapsible()) writer.write(",collapsible:true");
        if(tabView.getEvent() != null) writer.write(",event:'" + tabView.getEvent() + "'");
        if(tabView.getOnTabChange() != null) writer.write(",onTabChange: function(event, ui) {" + tabView.getOnTabChange() + "}");
        if(tabView.getOnTabShow() != null) writer.write(",onTabShow:function(event, ui) {" + tabView.getOnTabShow() + "}");

        if(tabView.getEffect() != null) {
            writer.write(",fx: {");
            writer.write(tabView.getEffect() + ":'toggle'");
            writer.write(",duration:'" + tabView.getEffectDuration() + "'");
            writer.write("}");
        }

        if(tabChangeListener != null) {
            writer.write(",ajaxTabChange:true");

            if(tabView.getOnTabChangeUpdate() != null) {
                writer.write(",onTabChangeUpdate:'" + ComponentUtils.findClientIds(context, tabView, tabView.getOnTabChangeUpdate()) + "'");
            }
        }

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeMarkup(FacesContext facesContext, TabView tabView) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = tabView.getClientId(facesContext);
        int activeIndex = tabView.getActiveIndex();

        writer.startElement("div", tabView);
        writer.writeAttribute("id", clientId, null);
        if(tabView.getStyle() != null) writer.writeAttribute("style", tabView.getStyle(), "style");
        if(tabView.getStyleClass() != null) writer.writeAttribute("class", tabView.getStyleClass(), "styleClass");

        encodeHeaders(facesContext, tabView, activeIndex);
        encodeContents(facesContext, tabView, activeIndex);

        encodeActiveIndexHolder(facesContext, tabView);

        writer.endElement("div");
    }

    protected void encodeActiveIndexHolder(FacesContext facesContext, TabView tabView) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String paramName = tabView.getClientId(facesContext) + "_activeIndex";

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", paramName, null);
        writer.writeAttribute("name", paramName, null);
        writer.writeAttribute("value", tabView.getActiveIndex(), null);
        writer.endElement("input");
    }

    protected void encodeHeaders(FacesContext facesContext, TabView tabView, int activeTabIndex) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("ul", null);

        for (UIComponent kid : tabView.getChildren()) {
            if (kid.isRendered() && kid instanceof Tab) {
                Tab tab = (Tab) kid;

                writer.startElement("li", null);

                writer.startElement("a", null);
                writer.writeAttribute("href", "#" + tab.getClientId(facesContext), null);
                writer.startElement("em", null);
                writer.write(tab.getTitle());
                writer.endElement("em");
                writer.endElement("a");

                writer.endElement("li");
            }
        }

        writer.endElement("ul");
    }

    protected void encodeContents(FacesContext facesContext, TabView tabView, int activeTabIndex) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        for (int i = 0; i < tabView.getChildren().size(); i++) {
            UIComponent kid = tabView.getChildren().get(i);

            if (kid.isRendered() && kid instanceof Tab) {
                Tab tab = (Tab) kid;
                writer.startElement("div", null);
                writer.writeAttribute("id", tab.getClientId(facesContext), null);

                if (tabView.isDynamic()) {
                    if (i == activeTabIndex) {
                        tab.encodeAll(facesContext);
                    }
                } else {
                    tab.encodeAll(facesContext);
                }

                writer.endElement("div");
            }
        }
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