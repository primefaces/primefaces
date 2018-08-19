/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.organigram;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.organigramnode.UIOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

/**
 * Renderer for the {@link Organigram} component.
 */
public class OrganigramRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Organigram organigram = (Organigram) component;

        decodeSelection(context, organigram);
        decodeBehaviors(context, component);
    }

    /**
     * Checks if the current request is a selection request and
     * assigns the found {@link OrganigramNode} to the <code>selection</code> value expression.
     *
     * @param context    The current {@link FacesContext}.
     * @param organigram The {@link Organigram} component.
     */
    protected void decodeSelection(FacesContext context, Organigram organigram) {

        if (ComponentUtils.isRequestSource(organigram, context)) {
            boolean selectionEnabled = organigram.getValueExpression("selection") != null;

            if (selectionEnabled) {
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();

                String rowKey = params.get(organigram.getClientId(context) + "_selectNode");

                if (!isValueBlank(rowKey)) {
                    OrganigramNode node = organigram.findTreeNode(organigram.getValue(), rowKey);
                    assignSelection(context, organigram, node);
                }
            }
        }
    }

    protected void assignSelection(FacesContext context, Organigram organigram, OrganigramNode node) {
        ValueExpression ve = organigram.getValueExpression("selection");
        ve.setValue(context.getELContext(), node);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Organigram organigram = (Organigram) component;

        encodeMarkup(context, organigram);
        encodeScript(context, organigram);
    }

    protected void encodeMarkup(FacesContext context, Organigram organigram) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = organigram.getClientId(context);
        OrganigramNode root = organigram.getValue();
        Map<String, UIOrganigramNode> nodeMapping = lookupNodeMapping(organigram);

        // checks if a node is currently selected to render the node as selected later
        boolean selectionEnabled = organigram.getValueExpression("selection") != null;
        OrganigramNode selection = null;

        if (selectionEnabled) {
            selection = organigram.getSelection();
            if (selection != null) {
                // check if selected node still exists
                OrganigramNode node = OrganigramHelper.findTreeNode(organigram.getValue(), selection);
                if (node == null) {
                    // reset selection
                    selection = null;
                    assignSelection(context, organigram, null);
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
        if (organigram.getStyleClass() != null) {
            styleClass += " " + organigram.getStyleClass();
        }

        writer.startElement("div", organigram);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (organigram.getStyle() != null) {
            writer.writeAttribute("style", organigram.getStyle(), null);
        }

        // render target container
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_target", null);
        writer.writeAttribute("class", "target", null);
        writer.endElement("div");

        // render source container
        writer.startElement("ul", null);
        writer.writeAttribute("id", clientId + "_source", null);

        renderNode(context, writer, nodeMapping, organigram, root, selection, selectionEnabled);

        writer.endElement("ul");
        writer.endElement("div");
    }

    protected void renderNode(FacesContext context, ResponseWriter writer, Map<String, UIOrganigramNode> nodeMapping,
                              Organigram organigram, OrganigramNode node, OrganigramNode selection, boolean selectionEnabled) throws IOException {

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        if (node == null) {
            return;
        }

        UIOrganigramNode uiNode = nodeMapping.get(node.getType());
        if (uiNode == null) {
            throw new FacesException("Unsupported tree node type:" + node.getType());
        }

        // push var with node to EL
        requestMap.put(organigram.getVar(), node);

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
        requestMap.remove(organigram.getVar());

        // render child nodes
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            writer.startElement("ul", null);
            for (OrganigramNode childNode : node.getChildren()) {
                renderNode(context, writer, nodeMapping, organigram, childNode, selection, selectionEnabled);
            }
            writer.endElement("ul");
        }

        writer.endElement("li");
    }

    protected void encodeScript(FacesContext context, Organigram organigram) throws IOException {
        String clientId = organigram.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Organigram", organigram.resolveWidgetVar(), clientId)
                .attr("zoom", organigram.isZoom())
                .attr("leafNodeConnectorHeight", organigram.getLeafNodeConnectorHeight())
                .attr("autoScrollToSelection", organigram.isAutoScrollToSelection());

        encodeClientBehaviors(context, organigram);

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
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
