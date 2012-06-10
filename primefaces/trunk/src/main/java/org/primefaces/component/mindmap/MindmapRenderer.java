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
package org.primefaces.component.mindmap;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.renderkit.CoreRenderer;

public class MindmapRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Mindmap map = (Mindmap) component;

        encodeMarkup(context, map);
        encodeScript(context, map);
    }

    protected void encodeScript(FacesContext context, Mindmap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = map.getClientId(context);
        MindmapNode root = map.getValue();

        startScript(writer, clientId);
        
        writer.write("$(function(){");
        writer.write("PrimeFaces.cw('Mindmap','" + map.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
      
        if(root != null) {
            writer.write(",model:");
            encodeNode(context, map, root);
        }

        writer.write("});});");
        endScript(writer);
    }
    
    protected void encodeMarkup(FacesContext context, Mindmap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = map.getClientId(context);
        String style = map.getStyle();
        String styleClass = map.getStyleClass();
        
        writer.startElement("div", map);
        writer.writeAttribute("id", clientId, "id");
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");

        //content is rendered on client side by the widget
        
        writer.endElement("div");
    }
    
    protected void encodeNode(FacesContext context, Mindmap map, MindmapNode node) throws IOException {
        ResponseWriter writer = context.getResponseWriter(); 
        List<MindmapNode> children = node.getChildren();
        
        writer.write("{");
        writer.write("data:'" + node.getData() + "'");
        
        if(node.getFill() != null) {
            writer.write(",fill:'" + node.getFill() + "'");
        }
        
        if(!children.isEmpty()) {
            writer.write(",children:[");
            for(Iterator<MindmapNode> it = children.iterator(); it.hasNext();) {
                MindmapNode child = it.next();
                
                encodeNode(context, map, child);
                
                if(it.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");
        }
        
        writer.write("}");
    }
}
