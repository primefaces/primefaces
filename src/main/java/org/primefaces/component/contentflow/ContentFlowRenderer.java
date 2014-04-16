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
package org.primefaces.component.contentflow;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ContentFlowRenderer extends CoreRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ContentFlow cf = (ContentFlow) component;
		
        encodeMarkup(context, cf);
		encodeScript(context, cf);
	}
    
    protected void encodeMarkup(FacesContext context, ContentFlow cf) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = cf.getStyle();
        String styleClass = cf.getStyleClass();
        String containerClass = (styleClass == null) ? ContentFlow.CONTAINER_CLASS : ContentFlow.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", cf);
        writer.writeAttribute("id", cf.getClientId(context), null);
        writer.writeAttribute("class", containerClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        //indicator
        writer.startElement("div", null);
        writer.writeAttribute("class", "loadindicator", null);
        writer.startElement("div", null);
        writer.writeAttribute("class", "indicator", null);
        writer.endElement("div");
        writer.endElement("div");
        
        //content
        encodeContent(context, cf);
        
        //caption
        writer.startElement("div", null);
        writer.writeAttribute("class", "globalCaption", null);
        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    protected void encodeContent(FacesContext context, ContentFlow cf) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = cf.getVar();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", "flow", null);
        
        if (var == null) {
            for(UIComponent child : cf.getChildren()) {
                if(child.isRendered()) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", "item", null);
                    child.encodeAll(context);
                    writer.endElement("div");
                }
            }
        }
        else {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            Collection<?> value = (Collection<?>) cf.getValue();
            if (value != null) {
                for (Iterator<?> it = value.iterator(); it.hasNext();) {
                    requestMap.put(var, it.next());
                    
                    writer.startElement("div", null);
                    writer.writeAttribute("class", "item", null);
                    renderChildren(context, cf);
                    writer.endElement("div");
                }
            }
        }
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ContentFlow cf) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        
        if (context.isPostback()) {
        	wb.initWithDomReady("ContentFlow", cf.resolveWidgetVar(), cf.getClientId(context), "contentflow");
        }  else {
        	wb.initWithWindowLoad("ContentFlow", cf.resolveWidgetVar(), cf.getClientId(context), "contentflow");
        }
        
        wb.finish();
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