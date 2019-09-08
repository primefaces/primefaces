/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.mindmap;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class MindmapRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Mindmap map = (Mindmap) component;

        if (map.isNodeSelectRequest(context)) {
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

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Mindmap", map.resolveWidgetVar(context), clientId)
                .attr("effectSpeed", map.getEffectSpeed());

        if (root != null) {
            writer.write(",model:");
            encodeNode(context, map, root, "root");
        }

        encodeClientBehaviors(context, map);

        wb.finish();
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
        if (style != null) {
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

        if (parent != null) {
            String parentNodeKey = (nodeKey.indexOf('_') != -1) ? nodeKey.substring(0, nodeKey.lastIndexOf('_')) : "root";

            writer.write(",\"parent\":{");
            encodeNodeConfig(context, map, parent, parentNodeKey);
            writer.write("}");
        }

        if (!children.isEmpty()) {
            int size = children.size();

            writer.write(",\"children\":[");

            for (int i = 0; i < size; i++) {
                String childKey = (nodeKey.equals("root")) ? String.valueOf(i) : nodeKey + "_" + i;

                MindmapNode child = children.get(i);
                encodeNode(context, map, child, childKey);

                if (i != (size - 1)) {
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

        if (nodeKey != null) {
            writer.write(",\"key\":\"" + nodeKey + "\"");
        }
        if (node.getFill() != null) {
            writer.write(",\"fill\":\"" + node.getFill() + "\"");
        }
        if (node.isSelectable()) {
            writer.write(",\"selectable\":true");
        }
    }
}
