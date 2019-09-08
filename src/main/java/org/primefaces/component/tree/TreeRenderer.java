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
package org.primefaces.component.tree;

import java.io.IOException;
import java.util.*;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UITree;
import org.primefaces.model.TreeNode;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.*;

import static org.primefaces.component.api.UITree.ROOT_ROW_KEY;

public class TreeRenderer extends CoreRenderer {

    private static final String SB_DECODE_SELECTION = TreeRenderer.class.getName() + "#decodeSelection";

    protected enum NodeOrder {
        FIRST,
        MIDDLE,
        LAST,
        NONE
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Tree tree = (Tree) component;

        if (tree.isDragDropRequest(context)) {
            decodeDragDrop(context, tree);
        }

        if (tree.getSelectionMode() != null) {
            decodeSelection(context, tree);
        }

        decodeBehaviors(context, tree);
    }

    public void decodeSelection(FacesContext context, Tree tree) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tree.getClientId(context);
        String selection = params.get(clientId + "_selection");

        boolean isSingle = tree.getSelectionMode().equalsIgnoreCase("single");

        if (isValueBlank(selection)) {
            if (isSingle) {
                tree.setSelection(null);
            }
            else {
                tree.setSelection(new TreeNode[0]);
            }
        }
        else {
            String[] selectedRowKeys = selection.split(",");

            if (isSingle) {
                tree.setRowKey(selectedRowKeys[0]);
                tree.setSelection(tree.getRowNode());
            }
            else {
                List<TreeNode> selectedNodes = new ArrayList<>();

                for (int i = 0; i < selectedRowKeys.length; i++) {
                    tree.setRowKey(selectedRowKeys[i]);
                    TreeNode rowNode = tree.getRowNode();
                    if (rowNode != null) {
                        selectedNodes.add(rowNode);
                    }
                }

                tree.setSelection(selectedNodes.toArray(new TreeNode[selectedNodes.size()]));
            }

            tree.setRowKey(null);
        }

        if (tree.isCheckboxSelection() && tree.isDynamic() && tree.isSelectionRequest(context) && tree.isPropagateSelectionDown()) {
            String selectedNodeRowKey = params.get(clientId + "_instantSelection");
            tree.setRowKey(selectedNodeRowKey);
            TreeNode selectedNode = tree.getRowNode();
            List<String> descendantRowKeys = new ArrayList<>();
            tree.populateRowKeys(selectedNode, descendantRowKeys);
            int size = descendantRowKeys.size();
            StringBuilder sb = SharedStringBuilder.get(context, SB_DECODE_SELECTION);

            for (int i = 0; i < size; i++) {
                sb.append(descendantRowKeys.get(i));
                if (i != (size - 1)) {
                    sb.append(",");
                }
            }

            PrimeFaces.current().ajax().addCallbackParam("descendantRowKeys", sb.toString());
            sb.setLength(0);
            descendantRowKeys = null;
        }
    }

    public void decodeDragDrop(FacesContext context, Tree tree) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tree.getClientId(context);
        String dragNodeRowKey = params.get(clientId + "_dragNode");
        String dropNodeRowKey = params.get(clientId + "_dropNode");
        String dragSource = params.get(clientId + "_dragSource");
        int dndIndex = Integer.parseInt(params.get(clientId + "_dndIndex"));
        boolean isDroppedNodeCopy = Boolean.parseBoolean(params.get(clientId + "_isDroppedNodeCopy"));
        String[] dragNodeRowKeyArr = dragNodeRowKey.split(",");
        List<TreeNode> dragNodeList = new ArrayList<>();
        TreeNode dropNode;

        for (String rowKey : dragNodeRowKeyArr) {
            if (dragSource.equals(clientId)) {
                tree.setRowKey(rowKey);
                dragNodeList.add(tree.getRowNode());
            }
            else {
                Tree otherTree = (Tree) tree.findComponent(":" + dragSource);
                otherTree.setRowKey(rowKey);
                dragNodeList.add(otherTree.getRowNode());
            }
        }

        if (isValueBlank(dropNodeRowKey)) {
            dropNode = tree.getValue();
        }
        else {
            tree.setRowKey(dropNodeRowKey);
            dropNode = tree.getRowNode();
        }

        tree.setDropNode(dropNode);

        TreeNode[] dragNodes = new TreeNode[dragNodeList.size()];
        dragNodes = dragNodeList.toArray(dragNodes);
        if (tree.isMultipleDrag()) {
            tree.setDragNodes(dragNodes);
        }
        else {
            tree.setDragNode(dragNodes[0]);
        }

        if (!tree.isTreeNodeDropped()) {
            return;
        }

        List<TreeNode> dropNodeChildren = dropNode.getChildren();

        if (tree.isMultipleDrag()) {
            for (TreeNode dragNode : dragNodes) {
                dropNodeChildren.remove(dragNode);
            }
        }

        for (TreeNode dragNode : dragNodes) {
            if (isDroppedNodeCopy) {
                dragNode = tree.createCopyOfTreeNode(dragNode);
            }

            if (dndIndex >= 0 && dndIndex < dropNode.getChildCount()) {
                dropNodeChildren.add(dndIndex, dragNode);
            }
            else {
                dropNodeChildren.add(dragNode);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Tree tree = (Tree) component;

        if (tree.isNodeExpandRequest(context)) {
            boolean vertical = tree.getOrientation().equals("vertical");
            String clientId = tree.getClientId(context);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String rowKey = params.get(clientId + "_expandNode");

            if (!vertical && rowKey.equals(ROOT_ROW_KEY)) {
                encodeHorizontalTreeNodeChildren(context, tree, tree.getValue(), tree.getClientId(context), null, tree.isDynamic(),
                        tree.isCheckboxSelection());
            }
            else {
                tree.setRowKey(rowKey);
                TreeNode node = tree.getRowNode();
                node.setExpanded(true);

                if (vertical) {
                    encodeTreeNodeChildren(context, tree, node, clientId, tree.isDynamic(), tree.isCheckboxSelection(), tree.isDroppable());
                }
                else {
                    encodeHorizontalTreeNodeChildren(context, tree, node, tree.getClientId(context), rowKey, tree.isDynamic(),
                            tree.isCheckboxSelection());
                }

                tree.setRowKey(null);
            }
        }
        else if (tree.isFilterRequest(context)) {
            String clientId = tree.getClientId();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String filteredValue = params.get(clientId + "_filter");
            Locale filterLocale = LocaleUtils.getCurrentLocale(context);

            tree.getFilteredRowKeys().clear();
            encodeFilteredNodes(context, tree, tree.getValue(), filteredValue, filterLocale);
            TreeNode root = tree.getValue();

            if (root != null && root.getRowKey() == null) {
                root.setRowKey(ROOT_ROW_KEY);
                tree.buildRowKeys(root);
                tree.initPreselection();
            }

            if (root != null && (LangUtils.isValueBlank(filteredValue) || tree.getFilteredRowKeys().size() > 0)) {
                encodeTreeNodeChildren(context, tree, root, clientId, tree.isDynamic(), tree.isCheckboxSelection(), tree.isDroppable());
            }
        }
        else {
            encodeMarkup(context, tree);
            encodeScript(context, tree);
        }
    }

    protected void encodeFilteredNodes(FacesContext context, Tree tree, TreeNode node, String filteredValue, Locale filterLocale)
            throws IOException {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            String var = tree.getVar();
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            ValueExpression filterByVE = tree.getValueExpression(Tree.PropertyKeys.filterBy.toString());
            FilterConstraint filterConstraint = getFilterConstraint(tree);

            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                requestMap.put(var, childNode.getData());

                if (filterConstraint.applies(filterByVE.getValue(context.getELContext()), filteredValue, filterLocale)) {
                    tree.getFilteredRowKeys().add(childNode.getRowKey());
                }
                encodeFilteredNodes(context, tree, childNode, filteredValue, filterLocale);
            }

            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, Tree tree) throws IOException {
        String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean filter = (tree.getValueExpression("filterBy") != null);
        String widget = tree.getOrientation().equals("vertical") ? "VerticalTree" : "HorizontalTree";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(widget, tree.resolveWidgetVar(context), clientId);

        wb.attr("dynamic", dynamic)
                .attr("highlight", tree.isHighlight(), true)
                .attr("animate", tree.isAnimate(), false)
                .attr("droppable", tree.isDroppable(), false)
                .attr("cache", tree.isCache() && dynamic)
                .attr("dragdropScope", tree.getDragdropScope(), null)
                .attr("disabled", tree.isDisabled(), false)
                .callback("onNodeClick", "function(node, event)", tree.getOnNodeClick());

        //selection
        if (selectionMode != null) {
            wb.attr("selectionMode", selectionMode);
            wb.attr("propagateUp", tree.isPropagateSelectionUp());
            wb.attr("propagateDown", tree.isPropagateSelectionDown());
        }

        if (tree.isDraggable()) {
            wb.attr("draggable", true)
                    .attr("dragMode", tree.getDragMode())
                    .attr("dropRestrict", tree.getDropRestrict())
                    .attr("multipleDrag", tree.isMultipleDrag())
                    .attr("dropCopyNode", tree.isDropCopyNode());
        }

        if (tree.getOnDrop() != null) {
            wb.attr("controlled", true);
        }

        if (filter) {
            wb.attr("filter", true)
                    .attr("filterMode", tree.getFilterMode(), "lenient");
        }

        encodeIconStates(context, tree, wb);
        encodeClientBehaviors(context, tree);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Tree tree) throws IOException {
        boolean vertical = tree.getOrientation().equals("vertical");
        TreeNode root = tree.getValue();

        if (root != null && root.getRowKey() == null) {
            root.setRowKey(ROOT_ROW_KEY);
            tree.buildRowKeys(root);
            tree.initPreselection();
        }

        if (vertical) {
            encodeVerticalTree(context, tree, root);
        }
        else {
            encodeHorizontalTree(context, tree, root);
        }
    }

    public void encodeVerticalTree(FacesContext context, Tree tree, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean selectable = selectionMode != null;
        boolean multiselectable = selectable && selectionMode.equals("single");
        boolean checkbox = selectable && selectionMode.equals("checkbox");
        boolean droppable = tree.isDroppable();
        boolean filter = (tree.getValueExpression("filterBy") != null);
        boolean isDisabled = tree.isDisabled();

        if (root != null && root.getRowKey() == null) {
            root.setRowKey(ROOT_ROW_KEY);
            tree.buildRowKeys(root);
            tree.initPreselection();
        }

        //enable RTL
        if (ComponentUtils.isRTL(context, tree)) {
            tree.setRTLRendering(true);
        }

        //container class
        String containerClass = tree.isRTLRendering() ? Tree.CONTAINER_RTL_CLASS : Tree.CONTAINER_CLASS;
        containerClass = isDisabled ? containerClass + " ui-state-disabled" : containerClass;
        if (tree.getStyleClass() != null) {
            containerClass = containerClass + " " + tree.getStyleClass();
        }
        if (tree.isShowUnselectableCheckbox()) {
            containerClass = containerClass + " ui-tree-checkbox-all";
        }

        writer.startElement("div", tree);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);
        if (!isDisabled) {
            writer.writeAttribute("tabindex", tree.getTabindex(), null);
        }

        writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, String.valueOf(multiselectable), null);
        if (tree.getStyle() != null) {
            writer.writeAttribute("style", tree.getStyle(), null);
        }

        if (filter) {
            encodeFilter(context, tree, clientId + "_filter");
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        if (root != null) {
            encodeTreeNodeChildren(context, tree, root, clientId, dynamic, checkbox, droppable);
        }

        writer.endElement("ul");

        if (selectable) {
            encodeStateHolder(context, tree, clientId + "_selection", tree.getSelectedRowKeysAsString());
        }

        encodeStateHolder(context, tree, clientId + "_scrollState", tree.getScrollState());

        writer.endElement("div");
    }

    protected void encodeFilter(FacesContext context, Tree tree, String name) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Tree.FILTER_CONTAINER, null);

        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("class", Tree.FILTER_CLASS, null);
        writer.endElement("input");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeHorizontalTree(FacesContext context, Tree tree, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tree.getClientId(context);
        boolean dynamic = tree.isDynamic();
        String selectionMode = tree.getSelectionMode();
        boolean checkbox = (selectionMode != null) && selectionMode.equals("checkbox");

        String containerClass = tree.getStyleClass() == null
                                ? Tree.HORIZONTAL_CONTAINER_CLASS
                                : Tree.HORIZONTAL_CONTAINER_CLASS + " " + tree.getStyleClass();
        containerClass = tree.isDisabled() ? containerClass + " ui-state-disabled" : containerClass;
        if (tree.isShowUnselectableCheckbox()) {
            containerClass = containerClass + " ui-tree-checkbox-all";
        }

        writer.startElement("div", tree);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);

        if (root != null) {
            encodeHorizontalTreeNode(context, tree, root, clientId, null, NodeOrder.NONE, dynamic, checkbox);
        }

        if (selectionMode != null) {
            encodeStateHolder(context, tree, clientId + "_selection", tree.getSelectedRowKeysAsString());
        }

        writer.endElement("div");
    }

    protected void encodeHorizontalTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey,
                                            NodeOrder nodeOrder, boolean dynamic, boolean checkbox) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        boolean expanded = node.isExpanded();
        boolean leaf = node.isLeaf();
        boolean selectable = node.isSelectable();
        boolean partialSelected = node.isPartialSelected();
        boolean selected = node.isSelected();

        String nodeClass;
        if (leaf) {
            nodeClass = Tree.LEAF_NODE_CLASS;
        }
        else {
            nodeClass = Tree.PARENT_NODE_CLASS;
            nodeClass = expanded ? nodeClass + " ui-treenode-expanded" : nodeClass + " ui-treenode-collapsed";
        }

        if (selected) {
            nodeClass += " ui-treenode-selected";
        }
        else if (partialSelected) {
            nodeClass += " ui-treenode-hasselected";
        }
        else {
            nodeClass += " ui-treenode-unselected";
        }

        writer.startElement("table", tree);
        writer.startElement("tbody", null);
        writer.startElement("tr", null);

        //connector
        if (nodeOrder != NodeOrder.NONE) {
            encodeConnector(context, tree, nodeOrder);
        }

        //node
        writer.startElement("td", null);
        writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);

        if (rowKey != null) {
            tree.setRowKey(rowKey);
            writer.writeAttribute("data-rowkey", rowKey, null);
        }
        else {
            context.getExternalContext().getRequestMap().put(tree.getVar(), tree.getValue().getData());
            writer.writeAttribute("data-rowkey", ROOT_ROW_KEY, null);
        }

        nodeClass = uiTreeNode.getStyleClass() == null ? nodeClass : nodeClass + " " + uiTreeNode.getStyleClass();
        writer.writeAttribute("class", nodeClass, null);

        String nodeContentClass = (tree.getSelectionMode() != null && node.isSelectable()) ? Tree.SELECTABLE_NODE_CONTENT_CLASS_H : Tree.NODE_CONTENT_CLASS_H;
        if (selected) {
            nodeContentClass += " ui-state-highlight";
        }
        writer.startElement("div", null);
        writer.writeAttribute("class", nodeContentClass, null);

        //toggler
        if (!leaf) {
            String toggleIcon = expanded ? Tree.EXPANDED_ICON_CLASS_H : Tree.COLLAPSED_ICON_CLASS_H;
            writer.startElement("span", null);
            writer.writeAttribute("class", toggleIcon, null);
            writer.endElement("span");
        }

        //checkbox
        if (checkbox) {
            RendererUtils.encodeCheckbox(context, selected, partialSelected, !selectable, Tree.CHECKBOX_CLASS);
        }

        //icon
        encodeIcon(context, uiTreeNode, expanded);

        //label
        writer.startElement("span", null);
        writer.writeAttribute("class", Tree.NODE_LABEL_CLASS, null);
        if (!tree.isDisabled()) {
            writer.writeAttribute("tabindex", "-1", null);
        }

        writer.writeAttribute("role", "treeitem", null);
        writer.writeAttribute("aria-label", uiTreeNode.getAriaLabel(), null);
        uiTreeNode.encodeAll(context);
        writer.endElement("span");

        writer.endElement("div");
        writer.endElement("td");

        //children
        if (!leaf) {
            writer.startElement("td", null);
            writer.writeAttribute("class", "ui-treenode-children-container", null);

            if (!expanded) {
                writer.writeAttribute("style", "display:none", null);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS, null);

            if ((dynamic && expanded) || !dynamic) {
                encodeHorizontalTreeNodeChildren(context, tree, node, clientId, rowKey, dynamic, checkbox);
            }

            writer.endElement("div");
            writer.endElement("td");
        }

        writer.endElement("tr");
        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodeHorizontalTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String clientId, String rowKey,
                                                    boolean dynamic, boolean checkbox) throws IOException {

        int childIndex = 0;
        for (Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext(); ) {
            String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + UITree.SEPARATOR + childIndex;

            NodeOrder no = null;
            if (node.getChildCount() == 1) {
                no = NodeOrder.NONE;
            }
            else if (childIndex == 0) {
                no = NodeOrder.FIRST;
            }
            else if (childIndex == (node.getChildCount() - 1)) {
                no = NodeOrder.LAST;
            }
            else {
                no = NodeOrder.MIDDLE;
            }

            encodeHorizontalTreeNode(context, tree, iterator.next(), clientId, childRowKey, no, dynamic, checkbox);

            childIndex++;
        }
    }

    protected void encodeConnector(FacesContext context, Tree tree, NodeOrder nodeOrder) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("td", null);
        writer.writeAttribute("class", "ui-treenode-connector", null);

        writer.startElement("table", null);
        writer.writeAttribute("class", "ui-treenode-connector-table", null);
        writer.startElement("tbody", null);

        writer.startElement("tr", null);
        writer.startElement("td", null);
        if (!nodeOrder.equals(NodeOrder.FIRST)) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");

        writer.startElement("tr", null);
        writer.startElement("td", null);
        if (!nodeOrder.equals(NodeOrder.LAST)) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");

        writer.endElement("tbody");
        writer.endElement("table");

        writer.endElement("td");
    }

    public void encodeTreeNode(FacesContext context, Tree tree, TreeNode node, String clientId, boolean dynamic, boolean checkbox,
                               boolean dragdrop) throws IOException {

        //preselection
        String rowKey = node.getRowKey();
        boolean selected = node.isSelected();
        boolean partialSelected = node.isPartialSelected();
        boolean filter = (tree.getValueExpression("filterBy") != null);
        boolean isStrictMode = tree.getFilterMode().equals("strict");

        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        if (!uiTreeNode.isRendered()) {
            return;
        }

        List<String> filteredRowKeys = tree.getFilteredRowKeys();
        boolean match = false;
        if (filter && !filteredRowKeys.isEmpty()) {
            for (String filteredRowKey : filteredRowKeys) {
                String rowKeyExt = rowKey + "_";
                String filteredRowKeyExt = filteredRowKey + "_";
                boolean isNodeAncestorOfMatch = filteredRowKey.startsWith(rowKeyExt);
                boolean isNodeDescendantOfMatch = rowKey.startsWith(filteredRowKeyExt);
                if (isNodeAncestorOfMatch || (!isStrictMode && isNodeDescendantOfMatch)
                        || filteredRowKey.equals(rowKey)) {
                    match = true;
                    if (!node.isLeaf() && isNodeAncestorOfMatch) {
                        node.setExpanded(true);
                    }
                }
                else if (match) {
                    break;
                }
            }

            if (!match) {
                return;
            }
        }

        ResponseWriter writer = context.getResponseWriter();
        tree.setRowKey(rowKey);
        boolean isLeaf = node.isLeaf();
        boolean expanded = node.isExpanded();
        boolean selectable = tree.getSelectionMode() != null && node.isSelectable();
        String toggleIcon = expanded
                            ? Tree.EXPANDED_ICON_CLASS_V
                            : (tree.isRTLRendering() ? Tree.COLLAPSED_ICON_RTL_CLASS_V : Tree.COLLAPSED_ICON_CLASS_V);
        String stateIcon = isLeaf ? Tree.LEAF_ICON_CLASS : toggleIcon;
        Object datakey = tree.getDatakey();
        String nodeId = clientId + UINamingContainer.getSeparatorChar(context) + rowKey;

        //style class of node
        String containerClass = isLeaf ? Tree.LEAF_NODE_CLASS : Tree.PARENT_NODE_CLASS;

        if (selected) {
            containerClass += " ui-treenode-selected";
        }
        else if (partialSelected) {
            containerClass += " ui-treenode-hasselected";
        }
        else {
            containerClass += " ui-treenode-unselected";
        }

        containerClass = uiTreeNode.getStyleClass() == null ? containerClass : containerClass + " " + uiTreeNode.getStyleClass();

        writer.startElement("li", null);
        writer.writeAttribute("id", nodeId, null);
        writer.writeAttribute("data-rowkey", rowKey, null);
        writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);
        writer.writeAttribute("class", containerClass, null);

        if (datakey != null) {
            writer.writeAttribute("data-datakey", datakey, null);
        }

        //content
        String contentClass = selectable ? Tree.SELECTABLE_NODE_CONTENT_CLASS_V : Tree.NODE_CONTENT_CLASS_V;
        if (dragdrop) {
            contentClass += " ui-treenode-droppable";
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", contentClass, null);
        writer.writeAttribute("role", "treeitem", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(expanded), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(selected), null);
        if (checkbox) {
            writer.writeAttribute(HTML.ARIA_CHECKED, String.valueOf(selected), null);
        }

        //state icon
        writer.startElement("span", null);
        writer.writeAttribute("class", stateIcon, null);
        writer.endElement("span");

        //checkbox
        if (checkbox) {
            RendererUtils.encodeCheckbox(context, selected, partialSelected, !selectable, Tree.CHECKBOX_CLASS);
        }

        //node icon
        encodeIcon(context, uiTreeNode, expanded);

        //label
        String nodeLabelClass = selected ? Tree.NODE_LABEL_CLASS + " ui-state-highlight" : Tree.NODE_LABEL_CLASS;

        writer.startElement("span", null);
        writer.writeAttribute("class", nodeLabelClass, null);
        if (!tree.isDisabled()) {
            writer.writeAttribute("tabindex", "-1", null);
        }

        writer.writeAttribute("role", "treeitem", null);
        writer.writeAttribute(HTML.ARIA_LABEL, uiTreeNode.getAriaLabel(), null);
        uiTreeNode.encodeAll(context);
        writer.endElement("span");

        writer.endElement("span");

        //children nodes
        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS, null);
        writer.writeAttribute("role", "group", null);

        if (!expanded) {
            writer.writeAttribute("style", "display:none", null);
        }

        if ((dynamic && expanded) || !dynamic) {
            encodeTreeNodeChildren(context, tree, node, clientId, dynamic, checkbox, dragdrop);
        }

        writer.endElement("ul");

        writer.endElement("li");

        if (dragdrop) {
            encodeDropTarget(context, tree);
        }
    }

    public void encodeTreeNodeChildren(FacesContext context, Tree tree, TreeNode node, String clientId, boolean dynamic,
                                       boolean checkbox, boolean droppable) throws IOException {

        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                if (i == 0 && droppable) {
                    encodeDropTarget(context, tree);
                }

                encodeTreeNode(context, tree, node.getChildren().get(i), clientId, dynamic, checkbox, droppable);
            }
        }
    }

    protected void encodeDropTarget(FacesContext context, Tree tree) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("li", null);
        writer.writeAttribute("class", "ui-tree-droppoint", null);
        writer.endElement("li");
    }

    protected void encodeIconStates(FacesContext context, Tree tree, WidgetBuilder wb) throws IOException {
        Map<String, UITreeNode> nodes = tree.getTreeNodes();

        wb.append(",iconStates:{");

        boolean firstWritten = false;
        for (Iterator<String> it = nodes.keySet().iterator(); it.hasNext(); ) {
            String type = it.next();
            UITreeNode node = nodes.get(type);
            String expandedIcon = node.getExpandedIcon();
            String collapsedIcon = node.getCollapsedIcon();

            if (expandedIcon != null && collapsedIcon != null) {
                if (firstWritten) {
                    wb.append(",");
                }

                wb.append("'" + node.getType() + "' : {");
                wb.append("expandedIcon:'" + expandedIcon + "'");
                wb.append(",collapsedIcon:'" + collapsedIcon + "'");
                wb.append("}");

                firstWritten = true;
            }
        }

        wb.append("}");
    }

    protected void encodeIcon(FacesContext context, UITreeNode uiTreeNode, boolean expanded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", null);
        String icon = uiTreeNode.getIconToRender(expanded);
        if (icon != null) {
            writer.writeAttribute("class", Tree.NODE_ICON_CLASS + " " + icon, null);
        }
        writer.endElement("span");
    }

    protected void encodeStateHolder(FacesContext context, Tree tree, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("value", value, null);
        writer.endElement("input");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public FilterConstraint getFilterConstraint(Tree tree) {
        String filterMatchMode = tree.getFilterMatchMode();
        FilterConstraint filterConstraint = Tree.FILTER_CONSTRAINTS.get(filterMatchMode);

        if (filterConstraint == null) {
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        return filterConstraint;
    }
}
