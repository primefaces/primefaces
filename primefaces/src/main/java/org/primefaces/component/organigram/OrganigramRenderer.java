/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.organigram;

import org.primefaces.component.organigramnode.UIOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

/**
 * Renderer for the {@link Organigram} component.
 */
@FacesRenderer(rendererType = Organigram.DEFAULT_RENDERER, componentFamily = Organigram.COMPONENT_FAMILY)
public class OrganigramRenderer extends CoreRenderer<Organigram> {

    @Override
    public void decode(FacesContext context, Organigram component) {
        decodeSelection(context, component);
        decodeBehaviors(context, component);
    }

    /**
     * Checks if the current request is a selection request and
     * assigns the found {@link OrganigramNode} to the <code>selection</code> value expression.
     *
     * @param context    The current {@link FacesContext}.
     * @param component The {@link Organigram} component.
     */
    protected void decodeSelection(FacesContext context, Organigram component) {

        if (ComponentUtils.isRequestSource(component, context)) {
            boolean selectionEnabled = component.getValueExpression("selection") != null;

            if (selectionEnabled) {
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();

                String rowKey = params.get(component.getClientId(context) + "_selectNode");

                if (!isValueBlank(rowKey)) {
                    OrganigramNode node = component.findTreeNode(component.getValue(), rowKey);
                    assignSelection(context, component, node);
                }
            }
        }
    }

    protected void assignSelection(FacesContext context, Organigram component, OrganigramNode node) {
        ValueExpression ve = component.getValueExpression("selection");
        ve.setValue(context.getELContext(), node);
    }

    @Override
    public void encodeEnd(FacesContext context, Organigram component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Organigram component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        OrganigramNode root = component.getValue();
        Map<String, UIOrganigramNode> nodeMapping = lookupNodeMapping(component);

        // checks if a node is currently selected to render the node as selected later
        boolean selectionEnabled = component.getValueExpression("selection") != null;
        OrganigramNode selection = null;

        if (selectionEnabled) {
            selection = component.getSelection();
            if (selection != null) {
                // check if selected node still exists
                OrganigramNode node = OrganigramHelper.findTreeNode(component.getValue(), selection);
                if (node == null) {
                    // reset selection
                    selection = null;
                    assignSelection(context, component, null);
                }
            }
        }

        if (root != null) {
            if (root.getRowKey() == null) {
                root.setRowKey("root");
            }

            OrganigramHelper.buildRowKeys(root);
        }

        // render container
        String styleClass = "ui-widget ui-organigram";
        if (component.getStyleClass() != null) {
            styleClass += " " + component.getStyleClass();
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        // render target container
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_target", null);
        writer.writeAttribute("class", "target", null);
        writer.endElement("div");

        // render source container
        writer.startElement("ul", null);
        writer.writeAttribute("id", clientId + "_source", null);

        renderNode(context, writer, nodeMapping, component, root, selection, selectionEnabled);

        writer.endElement("ul");
        writer.endElement("div");
    }

    protected void renderNode(FacesContext context, ResponseWriter writer, Map<String, UIOrganigramNode> nodeMapping,
                              Organigram component, OrganigramNode node, OrganigramNode selection, boolean selectionEnabled) throws IOException {

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        if (node == null) {
            return;
        }

        UIOrganigramNode uiNode = nodeMapping.get(node.getType());
        if (uiNode == null) {
            throw new FacesException("Unsupported tree node type:" + node.getType());
        }

        // push var with node to EL
        requestMap.put(component.getVar(), node);

        writer.startElement("li", null);
        writer.writeAttribute("data-rowkey", node.getRowKey(), null);

        if (node.getParent() != null) {
            writer.writeAttribute("data-parent-rowkey", node.getParent().getRowKey(), null);
        }

        if (!isValueBlank(uiNode.getStyle())) {
            writer.writeAttribute("style", uiNode.getStyle(), null);
        }

        String styleClass = buildNodeStyleClass(node, uiNode, selectionEnabled, selection);
        if (!isValueBlank(styleClass)) {
            writer.writeAttribute("class", styleClass, null);
        }

        // encode expanded/collapsed icon
        if (node.isCollapsible()) {
            if (!isValueBlank(uiNode.getCollapsedIcon())) {
                writer.writeAttribute("data-collapsed-icon", uiNode.getCollapsedIcon(), null);
            }
            if (!isValueBlank(uiNode.getExpandedIcon())) {
                writer.writeAttribute("data-expanded-icon", uiNode.getExpandedIcon(), null);
            }
        }

        // encode icon
        if (!isValueBlank(uiNode.getIcon())) {
            writer.writeAttribute("data-icon", uiNode.getIcon(), null);
            if (!isValueBlank(uiNode.getIconPos())) {
                writer.writeAttribute("data-icon-pos", uiNode.getIconPos(), null);
            }
        }

        // encode node
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-organigram-node-content", null);
        uiNode.encodeAll(context);
        writer.endElement("div");

        // remove var
        requestMap.remove(component.getVar());

        // render child nodes
        if (node.getChildren() != null && node.getChildCount() > 0) {
            writer.startElement("ul", null);
            for (OrganigramNode childNode : node.getChildren()) {
                renderNode(context, writer, nodeMapping, component, childNode, selection, selectionEnabled);
            }
            writer.endElement("ul");
        }

        writer.endElement("li");
    }

    protected void encodeScript(FacesContext context, Organigram component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Organigram", component)
                .attr("zoom", component.isZoom())
                .attr("leafNodeConnectorHeight", component.getLeafNodeConnectorHeight())
                .attr("autoScrollToSelection", component.isAutoScrollToSelection());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected Map<String, UIOrganigramNode> lookupNodeMapping(Organigram organigram) {
        Map<String, UIOrganigramNode> nodes = new HashMap<>();
        for (UIComponent child : organigram.getChildren()) {
            UIOrganigramNode node = (UIOrganigramNode) child;
            nodes.put(node.getType(), node);
        }

        return nodes;
    }

    protected String buildNodeStyleClass(OrganigramNode node, UIOrganigramNode uiNode, boolean selectionEnabled, OrganigramNode selection) {
        String styleClass = "";
        if (node.isDraggable()) {
            styleClass += " draggable";
        }

        if (node.isDroppable()) {
            styleClass += " droppable";
        }

        if (node.isLeaf()) {
            styleClass += " leaf";
        }

        if (uiNode.isSkipLeafHandling()) {
            styleClass += " skip-leaf";
        }

        if (selectionEnabled && node.isSelectable()) {
            styleClass += " selectable";

            if (selection != null && selection.equals(node)) {
                styleClass += " selected";
            }
        }

        if (node.isCollapsible()) {
            styleClass += " collapsible";

            if (!node.isExpanded()) {
                styleClass += " collapsed";
            }
        }

        if (!isValueBlank(uiNode.getStyleClass())) {
            styleClass += " " + uiNode.getStyleClass();
        }

        if (!isValueBlank(node.getType())) {
            styleClass += " " + node.getType();
        }

        return styleClass.trim();
    }

    @Override
    public void encodeChildren(FacesContext context, Organigram component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
