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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class TabViewRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String activeIndexValue = params.get(tabView.getClientId(context) + "_activeIndex");

        if(!isValueEmpty(activeIndexValue)) {
            tabView.setActiveIndex(Integer.parseInt(activeIndexValue));
        }
        
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String clientId = tabView.getClientId(context);
        String var = tabView.getVar();

        if(tabView.isContentLoadRequest(context)) {
            Tab tabToLoad = null;
            
            if(var == null) {
                String tabClientId = params.get(clientId + "_newTab");
                tabToLoad = (Tab) tabView.findTab(tabClientId);
                
                tabToLoad.encodeAll(context);
            } 
            else {
                int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                tabView.setRowIndex(tabindex);
                tabToLoad = (Tab) tabView.getChildren().get(0);
                tabToLoad.encodeAll(context);
                tabView.setRowIndex(-1);
            }
            
        } 
        else {
            encodeMarkup(context, tabView);
            encodeScript(context, tabView);
        }
    }

    protected void encodeScript(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tabView.getClientId(context);

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

        String disabledTabs = tabView.findDisabledTabs();
        
        if(disabledTabs.length() > 0)
            writer.write(",disabled:[" + disabledTabs + "]");
        
        encodeClientBehaviors(context, tabView);

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeMarkup(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tabView.getClientId(context);

        writer.startElement("div", tabView);
        writer.writeAttribute("id", clientId, null);
        if(tabView.getStyle() != null) writer.writeAttribute("style", tabView.getStyle(), "style");
        if(tabView.getStyleClass() != null) writer.writeAttribute("class", tabView.getStyleClass(), "styleClass");

        encodeHeaders(context, tabView);
        encodeContents(context, tabView);

        encodeActiveIndexHolder(context, tabView);

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

    protected void encodeHeaders(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = tabView.getVar();

        writer.startElement("ul", null);

        if(var == null) {
            for(UIComponent kid : tabView.getChildren()) {
                if(kid.isRendered() && kid instanceof Tab) {
                    encodeTabHeader(context, (Tab) kid);
                }
            }
        } 
        else {
            int dataCount = tabView.getRowCount();
            Tab tab = (Tab) tabView.getChildren().get(0);
            
            for(int i = 0; i < dataCount; i++) {
                tabView.setRowIndex(i);
                
                encodeTabHeader(context, tab);
            }
            
            tabView.setRowIndex(-1);
        }

        writer.endElement("ul");
    }
    
    protected void encodeTabHeader(FacesContext context, Tab tab) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //header container
        writer.startElement("li", null);
        if(tab.getTitleStyle() != null)  writer.writeAttribute("style", tab.getTitleStyle(), null);
        if(tab.getTitleStyleClass() != null)  writer.writeAttribute("class", tab.getTitleStyleClass(), null);

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "#" + tab.getClientId(context), null);
        writer.write(tab.getTitle());
        writer.endElement("a");

        writer.endElement("li");
    }

    protected void encodeContents(FacesContext context, TabView tabView) throws IOException {
        String var = tabView.getVar();
        int activeTabIndex = tabView.getActiveIndex();
        
        if(var == null) {
            for(int i = 0; i < tabView.getChildCount(); i++) {
                Tab tab = (Tab) tabView.getChildren().get(i);

                if(tab.isRendered()) {
                    encodeTabContent(context, tabView, tab, i, activeTabIndex);
                }
            }
        }
        else {
            int dataCount = tabView.getRowCount();
            Tab tab = (Tab) tabView.getChildren().get(0);
            
            for(int i = 0; i < dataCount; i++) {
                tabView.setRowIndex(i);
                
                encodeTabContent(context, tabView, tab, i, activeTabIndex);
            }
            
            tabView.setRowIndex(-1);
        }
        
    }
    
    protected void encodeTabContent(FacesContext context, TabView tabView, Tab tab, int index, int activeIndex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("id", tab.getClientId(context), null);

        if(tabView.isDynamic()) {
            if(index == activeIndex)
                tab.encodeAll(context);
        } 
        else {
            tab.encodeAll(context);
        }

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}