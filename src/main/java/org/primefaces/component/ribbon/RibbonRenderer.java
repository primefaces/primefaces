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
package org.primefaces.component.ribbon;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class RibbonRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Ribbon ribbon = (Ribbon) component;

        encodeMarkup(context, ribbon);
        encodeScript(context, ribbon);
    }

    private void encodeScript(FacesContext context, Ribbon ribbon) throws IOException {
        String clientId = ribbon.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Ribbon", ribbon.resolveWidgetVar(), clientId, "ribbon");
        
        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ribbon.getClientId(context);
        String style = ribbon.getStyle();
        String styleClass = ribbon.getStyleClass();
        styleClass = (styleClass == null) ? Ribbon.CONTAINER_CLASS: Ribbon.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", ribbon);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("class", style, "style");
        }
        
        encodeTabHeaders(context, ribbon);
        encodeTabContents(context, ribbon);
        
        writer.endElement("div");
    }

    protected void encodeTabHeaders(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int activeIndex = ribbon.getActiveIndex();
        int childCount = ribbon.getChildCount();
        
        writer.startElement("ul", ribbon);
        writer.writeAttribute("class", Ribbon.NAVIGATOR_CLASS, null);
        writer.writeAttribute("role", "tablist", null);

        if(childCount > 0) {
            List<UIComponent> children = ribbon.getChildren();
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);
                
                if(child instanceof Tab && child.isRendered()) {
                    Tab tab = (Tab) child;
                    String title = tab.getTitle();
                    boolean active = (i == activeIndex);
                    String headerClass = (active) ? Ribbon.ACTIVE_TAB_HEADER_CLASS : Ribbon.INACTIVE_TAB_HEADER_CLASS;

                    //header container
                    writer.startElement("li", null);
                    writer.writeAttribute("class", headerClass, null);
                    writer.writeAttribute("role", "tab", null);
                    writer.writeAttribute("aria-expanded", String.valueOf(active), null);

                    writer.startElement("a", null);
                    writer.writeAttribute("href", tab.getClientId(context), null);
                    if(title != null) {
                        writer.writeText(title, null);
                    }
                    writer.endElement("a");

                    writer.endElement("li");
                }
            }
        }
        writer.endElement("ul");
    }

    protected void encodeTabContents(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int activeIndex = ribbon.getActiveIndex();
        int childCount = ribbon.getChildCount();
        
        writer.startElement("div", ribbon);
        writer.writeAttribute("class", Ribbon.PANELS_CLASS, null);
        
        if(childCount > 0) {
            List<UIComponent> children = ribbon.getChildren();
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);
                
                if(child instanceof Tab && child.isRendered()) {
                    Tab tab = (Tab) child;
                    encodeTabContent(context, ribbon, tab, (i == activeIndex));
                }
            }
        }
        
        writer.endElement("div");
    }
    
    protected void encodeTabContent(FacesContext context, Ribbon ribbon, Tab tab, boolean active) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String contentClass = active ? Ribbon.ACTIVE_TAB_CONTENT_CLASS : Ribbon.INACTIVE_TAB_CONTENT_CLASS;
        int childCount = tab.getChildCount();
        
        writer.startElement("div", ribbon);
        writer.writeAttribute("id", tab.getClientId(context), null);
        writer.writeAttribute("class", contentClass, null);
        
        if(childCount > 0) {
            writer.startElement("ul", ribbon);
            writer.writeAttribute("class", Ribbon.GROUPS_CLASS, null);
            
            List<UIComponent> children = tab.getChildren();
            for(int i = 0; i < childCount;i++) {
                UIComponent child = children.get(i);
                
                if(child instanceof RibbonGroup && child.isRendered()) {
                    RibbonGroup group = (RibbonGroup) child;
                    group.encodeAll(context);
                }
            }
            
            writer.endElement("ul");
        }
        
        writer.endElement("div");
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
