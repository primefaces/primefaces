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
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.renderkit.CoreRenderer;

public class MindmapRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Mindmap map = (Mindmap) component;

        if(map.isNodeSelectRequest(context)) {
            MindmapNode node = map.getSelectedNode();
            
            encodeNode(context, map, node, map.getSelectedNodeKey(context));
        }
        else {
            encodeMarkup(context, map);
            encodeScript(context, map);
        }
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
            encodeNode(context, map, root, "root");
        }
        
        writer.write(",effectSpeed:" + map.getEffectSpeed());
        
        encodeClientBehaviors(context, map);

        writer.write("});});");
        endScript(writer);
    }
    
    protected void encodeMarkup(FacesContext context, Mindmap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = map.getClientId(context);
        String style = map.getStyle();
        String styleClass = map.getStyleClass();
        styleClass = (styleClass == null) ? Mindmap.STYLE_CLASS : Mindmap.STYLE_CLASS + " " + styleClass;
        
        writer.startElement("div", map);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        writer.endElement("div");
    }
    
    protected void encodeNode(FacesContext context, Mindmap map, MindmapNode node, String nodeKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter(); 
        List<MindmapNode> children = node.getChildren();
        MindmapNode parent = node.getParent();
        
        writer.write("{");
        
        encodeNodeConfig(context, map, node, nodeKey);
        
        if(parent != null) {
            String parentNodeKey = (nodeKey.indexOf("_") != -1) ? nodeKey.substring(0, nodeKey.lastIndexOf("_")) : "root";
            
            writer.write(",\"parent\":{");
            encodeNodeConfig(context, map, parent, parentNodeKey);
            writer.write("}");
        }
        
        if(!children.isEmpty()) {
            int size = children.size();
            
            writer.write(",\"children\":[");
            
            for(int i = 0; i < size; i++) {
                String childKey = (nodeKey.equals("root")) ? String.valueOf(i) : nodeKey + "_" + i;
                
                MindmapNode child = children.get(i);
                encodeNode(context, map, child, childKey);
                
                if(i != (size - 1)) {
                    writer.write(",");
                }
            }
            
            writer.write("]");
        }
        
        writer.write("}");
    }
    
    protected void encodeNodeConfig(FacesContext context, Mindmap map, MindmapNode node, String nodeKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter(); 
        
        writer.write("\"label\":\"" + node.getLabel() + "\"");
        
        if(nodeKey != null) writer.write(",\"key\":\"" + nodeKey + "\"");
        if(node.getFill() != null) writer.write(",\"fill\":\"" + node.getFill() + "\"");
        if(node.isSelectable()) writer.write(",\"selectable\":true");
    }
}
