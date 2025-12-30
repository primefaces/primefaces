/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UITree;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.TreeNode;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

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

        TreeNode root = tree.getValue();

        if (tree.isDragDropRequest(context)) {
            decodeDragDrop(context, tree, root);
        }

        if (tree.isSelectionEnabled()) {
            decodeSelection(context, tree, root);
        }

        decodeBehaviors(context, tree);
    }

    public void decodeSelection(FacesContext context, Tree tree, TreeNode root) {
        boolean multiple = tree.isMultipleSelectionMode();
        Class<?> selectionType = tree.getSelectionType();
        boolean hasSelectionType = selectionType != null;

        if ((multiple && !hasSelectionType) ||
                (multiple && hasSelectionType && !selectionType.isArray()) && !List.class.isAssignableFrom(selectionType)) {
            throw new FacesException("Multiple selection reference must be an Array or a List for Tree " + tree.getClientId());
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tree.getClientId(context);
        String selection = params.get(clientId + "_selection");

        if (isValueBlank(selection)) {
            if (multiple) {
                tree.setSelection(selectionType.isArray() ? new TreeNode[0] : Collections.emptyList());
            }
            else {
                tree.setSelection(null);
            }
        }
        else {
            String[] selectedRowKeys = selection.split(",");

            if (multiple) {
                List<TreeNode> selectedNodes = new ArrayList<>();

                for (int i = 0; i < selectedRowKeys.length; i++) {
                    tree.setRowKey(root, selectedRowKeys[i]);
                    TreeNode rowNode = tree.getRowNode();
                    if (rowNode != null) {
                        selectedNodes.add(rowNode);
                    }
                }

                tree.setSelection(selectionType.isArray() ? selectedNodes.toArray(new TreeNode[selectedNodes.size()]) : selectedNodes);
            }
            else {
                tree.setRowKey(root, selectedRowKeys[0]);
                tree.setSelection(tree.getRowNode());
            }

            tree.setRowKey(root, null);
        }

        if (tree.isCheckboxSelectionMode() && tree.isDynamic() && tree.isSelectionRequest(context) && tree.isPropagateSelectionDown()) {
            String selectedNodeRowKey = params.get(clientId + "_instantSelection");
            tree.setRowKey(root, selectedNodeRowKey);
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
        }
    }

    public void decodeDragDrop(FacesContext context, Tree tree, TreeNode root) {
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
                tree.setRowKey(root, rowKey);
                dragNodeList.add(tree.getRowNode());
            }
            else {
                Tree otherTree = (Tree) SearchExpressionUtils.contextlessResolveComponent(context, context.getViewRoot(), dragSource);
                otherTree.setRowKey(otherTree.getValue(), rowKey);
                dragNodeList.add(otherTree.getRowNode());
            }
        }

        if (isValueBlank(dropNodeRowKey)) {
            dropNode = tree.getValue();
        }
        else {
            tree.setRowKey(root, dropNodeRowKey);
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
        TreeNode root = tree.getValue();

        //enable RTL
        if (ComponentUtils.isRTL(context, tree)) {
            tree.setRTLRendering(true);
        }

        if (tree.isNodeExpandRequest(context)) {
            boolean vertical = tree.getOrientation().equals("vertical");
            String clientId = tree.getClientId(context);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String rowKey = params.get(clientId + "_expandNode");

            if (!vertical && rowKey.equals(UITree.ROOT_ROW_KEY)) {
                encodeHorizontalTreeNodeChildren(context, tree, root, root, tree.getClientId(context), null, tree.isDynamic(),
                        tree.isCheckboxSelectionMode());
            }
            else {
                tree.setRowKey(root, rowKey);
                TreeNode node = tree.getRowNode();
                node.setExpanded(true);

                if (vertical) {
                    encodeTreeNodeChildren(context, tree, root, node, clientId, tree.isDynamic(), tree.isCheckboxSelectionMode(), tree.isDroppable());
                }
                else {
                    encodeHorizontalTreeNodeChildren(context, tree, root, node, tree.getClientId(context), rowKey, tree.isDynamic(),
                            tree.isCheckboxSelectionMode());
                }

                tree.setRowKey(root, null);
            }
        }
        else if (tree.isFilterRequest(context)) {
            String clientId = tree.getClientId();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String filteredValue = params.get(clientId + "_filter");
            Locale filterLocale = LocaleUtils.getCurrentLocale(context);

            tree.getFilteredRowKeys().clear();
            if (LangUtils.isNotBlank(filteredValue)) {
                encodeFilteredNodes(context, tree, tree.getValue(), filteredValue, filterLocale);
            }

            if (root != null && root.getRowKey() == null) {
                root.setRowKey(UITree.ROOT_ROW_KEY);
                tree.buildRowKeys(root);
                tree.initPreselection();
            }

            if (root != null) {
                encodeTreeNodeChildren(context, tree, root, root, clientId, tree.isDynamic(), tree.isCheckboxSelectionMode(), tree.isDroppable());
            }
        }
        else {
            encodeMarkup(context, tree);
            encodeScript(context, tree);
        }
    }

    protected void encodeFilteredNodes(FacesContext context, Tree tree, TreeNode<?> node, String filteredValue, Locale filterLocale)
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

                Object value = tree.getFilterFunction() == null
                        ? filterByVE.getValue(context.getELContext())
                        : childNode;
                if (filterConstraint.isMatching(context, value, filteredValue, filterLocale)) {
                    tree.getFilteredRowKeys().add(childNode.getRowKey());
                }
                encodeFilteredNodes(context, tree, childNode, filteredValue, filterLocale);
            }

            requestMap.remove(var);
        }
    }

    protected void encodeScript(FacesContext context, Tree tree) throws IOException {
        boolean dynamic = tree.isDynamic();
        String widget = tree.getOrientation().equals("vertical") ? "VerticalTree" : "HorizontalTree";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(widget, tree);

        wb.attr("dynamic", dynamic)
                .attr("highlight", tree.isHighlight(), true)
                .attr("animate", tree.isAnimate(), false)
                .attr("droppable", tree.isDroppable(), false)
                .attr("cache", tree.isCache() && dynamic)
                .attr("dragdropScope", tree.getDragdropScope(), null)
                .attr("disabled", tree.isDisabled(), false)
                .callback("onNodeClick", "function(node, event)", tree.getOnNodeClick());

        //selection
        String selectionMode = tree.getSelectionMode();
        if (selectionMode != null) {
            wb.attr("selectionMode", selectionMode);
            wb.attr("propagateUp", tree.isPropagateSelectionUp());
            wb.attr("propagateDown", tree.isPropagateSelectionDown());
        }

        if (tree.isDraggable()) {
            wb.attr("draggable", true)
                    .attr("dragMode", tree.getDragMode())
                    .attr("dropMode", tree.getDropMode())
                    .attr("dropRestrict", tree.getDropRestrict())
                    .attr("multipleDrag", tree.isMultipleDrag())
                    .attr("dropCopyNode", tree.isDropCopyNode());
        }

        if (tree.getOnDrop() != null) {
            wb.attr("controlled", true);
        }

        if (tree.isFiltering()) {
            wb.attr("filter", true)
                    .attr("filterEvent", tree.getFilterEvent(), null)
                    .attr("filterDelay", tree.getFilterDelay(), Integer.MAX_VALUE)
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
            root.setRowKey(UITree.ROOT_ROW_KEY);
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
        boolean selectionEnabled = tree.isSelectionEnabled();
        boolean multipleSelectionMode = selectionEnabled && tree.isMultipleSelectionMode();
        boolean checkboxSelectionMode = selectionEnabled && tree.isCheckboxSelectionMode();
        boolean droppable = tree.isDroppable();
        boolean isDisabled = tree.isDisabled();

        if (root != null && root.getRowKey() == null) {
            root.setRowKey(UITree.ROOT_ROW_KEY);
            tree.buildRowKeys(root);
            tree.initPreselection();
        }

        //container class
        String containerClass = getStyleClassBuilder(context)
                .add(tree.isRTLRendering(), Tree.CONTAINER_RTL_CLASS, Tree.CONTAINER_CLASS)
                .add(tree.getStyleClass())
                .add(tree.isDisabled(), "ui-state-disabled")
                .add(tree.isShowUnselectableCheckbox(), "ui-tree-checkbox-all")
                .build();

        writer.startElement("div", tree);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        if (tree.getStyle() != null) {
            writer.writeAttribute("style", tree.getStyle(), null);
        }

        if (tree.isFiltering()) {
            encodeFilter(context, tree, clientId + "_filter");
        }

        writer.startElement("ul", null);
        writer.writeAttribute("role", "tree", null);
        writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, String.valueOf(multipleSelectionMode), null);
        if (!isDisabled) {
            writer.writeAttribute("tabindex", tree.getTabindex(), null);
        }
        writer.writeAttribute("class", Tree.ROOT_NODES_CLASS, null);

        if (root != null) {
            encodeTreeNodeChildren(context, tree, root, root, clientId, dynamic, checkboxSelectionMode, droppable);
        }

        writer.endElement("ul");

        if (selectionEnabled) {
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

        if (LangUtils.isNotBlank(tree.getFilterPlaceholder())) {
            writer.writeAttribute("placeholder", tree.getFilterPlaceholder(), null);
        }
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
        boolean checkboxSelectionMode = tree.isCheckboxSelectionMode();

        //container class
        String containerClass = getStyleClassBuilder(context)
                .add(tree.isRTLRendering(), Tree.HORIZONTAL_CONTAINER_RTL_CLASS, Tree.HORIZONTAL_CONTAINER_CLASS)
                .add(tree.getStyleClass())
                .add(tree.isDisabled(), "ui-state-disabled")
                .add(tree.isShowUnselectableCheckbox(), "ui-tree-checkbox-all")
                .build();

        writer.startElement("div", tree);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        writer.writeAttribute("role", "tree", null);

        if (root != null) {
            encodeHorizontalTreeNode(context, tree, root, root, clientId, null, NodeOrder.NONE, dynamic, checkboxSelectionMode);
        }

        if (tree.isSelectionEnabled()) {
            encodeStateHolder(context, tree, clientId + "_selection", tree.getSelectedRowKeysAsString());
        }

        writer.endElement("div");
    }

    protected void encodeHorizontalTreeNode(FacesContext context, Tree tree, TreeNode root, TreeNode node, String clientId, String rowKey,
                                            NodeOrder nodeOrder, boolean dynamic, boolean checkbox) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        boolean expanded = node.isExpanded();
        boolean leaf = node.isLeaf();
        boolean selectable = node.isSelectable();
        boolean partialSelected = node.isPartialSelected();
        boolean selected = node.isSelected();

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
            tree.setRowKey(root, rowKey);
            writer.writeAttribute("data-rowkey", rowKey, null);
        }
        else {
            context.getExternalContext().getRequestMap().put(tree.getVar(), tree.getValue().getData());
            writer.writeAttribute("data-rowkey", UITree.ROOT_ROW_KEY, null);
        }

        String nodeClass = getStyleClassBuilder(context)
                .add(leaf, Tree.LEAF_NODE_CLASS, Tree.PARENT_NODE_CLASS)
                .add(!leaf && expanded, "ui-treenode-expanded", "ui-treenode-collapsed")
                .add(selected, "ui-treenode-selected")
                .add(!selected && partialSelected, "ui-treenode-hasselected")
                .add(!selected && !partialSelected, "ui-treenode-unselected")
                .add(uiTreeNode.getStyleClass())
                .build();
        writer.writeAttribute("class", nodeClass, null);

        String nodeContentClass = getStyleClassBuilder(context)
                .add(tree.isSelectionEnabled() && node.isSelectable(), Tree.SELECTABLE_NODE_CONTENT_CLASS_H, Tree.NODE_CONTENT_CLASS_H)
                .add(selected, "ui-state-highlight")
                .build();
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
                encodeHorizontalTreeNodeChildren(context, tree, root, node, clientId, rowKey, dynamic, checkbox);
            }

            writer.endElement("div");
            writer.endElement("td");
        }

        writer.endElement("tr");
        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodeHorizontalTreeNodeChildren(FacesContext context, Tree tree, TreeNode root, TreeNode node, String clientId, String rowKey,
                                                    boolean dynamic, boolean checkbox) throws IOException {

        if (node.getChildCount() == 0) {
            return;
        }

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

            encodeHorizontalTreeNode(context, tree, root, iterator.next(), clientId, childRowKey, no, dynamic, checkbox);

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
        if (nodeOrder != NodeOrder.FIRST) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");

        writer.startElement("tr", null);
        writer.startElement("td", null);
        if (nodeOrder != NodeOrder.LAST) {
            writer.writeAttribute("class", "ui-treenode-connector-line", null);
        }
        writer.endElement("td");
        writer.endElement("tr");

        writer.endElement("tbody");
        writer.endElement("table");

        writer.endElement("td");
    }

    public void encodeTreeNode(FacesContext context, Tree tree, TreeNode root, TreeNode node, String clientId, boolean dynamic, boolean checkbox,
                               boolean dragdrop) throws IOException {

        //preselection
        String rowKey = node.getRowKey();
        if (rowKey == null) {
            tree.buildRowKeys(node.getParent());
            rowKey = node.getRowKey();
        }
        boolean selected = node.isSelected();
        boolean partialSelected = node.isPartialSelected();
        boolean isStrictMode = tree.getFilterMode().equals("strict");

        UITreeNode uiTreeNode = tree.getUITreeNodeByType(node.getType());
        if (!uiTreeNode.isRendered()) {
            return;
        }

        List<String> filteredRowKeys = tree.getFilteredRowKeys();
        boolean match = false;
        boolean hidden = false;
        if (tree.isFiltering() && !filteredRowKeys.isEmpty()) {
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
                hidden = true;
            }
        }

        ResponseWriter writer = context.getResponseWriter();
        tree.setRowKey(root, rowKey);
        boolean isLeaf = node.isLeaf();
        boolean expanded = node.isExpanded();
        boolean selectable = tree.isSelectionEnabled() && node.isSelectable();
        String toggleIcon = expanded
                            ? Tree.EXPANDED_ICON_CLASS_V
                            : (tree.isRTLRendering() ? Tree.COLLAPSED_ICON_RTL_CLASS_V : Tree.COLLAPSED_ICON_CLASS_V);
        String stateIcon = isLeaf ? Tree.LEAF_ICON_CLASS : toggleIcon;
        Object datakey = tree.getDatakey();
        String nodeId = clientId + UINamingContainer.getSeparatorChar(context) + rowKey;

        //style class of node
        String selectedStateClass;
        if (selected) {
            selectedStateClass = "ui-treenode-selected";
        }
        else if (partialSelected) {
            selectedStateClass = "ui-treenode-hasselected";
        }
        else {
            selectedStateClass = "ui-treenode-unselected";
        }

        String containerClass = getStyleClassBuilder(context)
                .add(uiTreeNode.getStyleClass())
                .add(selectedStateClass)
                .add(isLeaf, Tree.LEAF_NODE_CLASS, Tree.PARENT_NODE_CLASS)
                .add(hidden, "ui-treenode-hidden")
                .build();

        writer.startElement("li", null);
        writer.writeAttribute("id", nodeId, null);
        writer.writeAttribute("data-rowkey", rowKey, null);
        writer.writeAttribute("data-nodetype", uiTreeNode.getType(), null);
        writer.writeAttribute("class", containerClass, null);

        writer.writeAttribute("role", "treeitem", null);
        writer.writeAttribute(HTML.ARIA_LABEL, uiTreeNode.getAriaLabel(), null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(expanded), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(selected), null);
        if (checkbox) {
            writer.writeAttribute(HTML.ARIA_CHECKED, String.valueOf(selected), null);
        }

        if (datakey != null) {
            writer.writeAttribute("data-datakey", datakey, null);
        }

        //content
        String contentClass = getStyleClassBuilder(context)
                .add(selectable, Tree.SELECTABLE_NODE_CONTENT_CLASS_V, Tree.NODE_CONTENT_CLASS_V)
                .add(dragdrop, "ui-treenode-droppable")
                .add(selected, "ui-state-highlight")
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", contentClass, null);
        if (!tree.isDisabled()) {
            writer.writeAttribute("tabindex", "-1", null);
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
        writer.startElement("span", null);
        writer.writeAttribute("class", Tree.NODE_LABEL_CLASS, null);
        uiTreeNode.encodeAll(context);
        writer.endElement("span");

        writer.endElement("div");

        //children nodes
        writer.startElement("ul", null);
        writer.writeAttribute("class", Tree.CHILDREN_NODES_CLASS, null);
        writer.writeAttribute("role", "group", null);

        if (!expanded) {
            writer.writeAttribute("style", "display:none", null);
        }

        if ((dynamic && expanded) || !dynamic) {
            encodeTreeNodeChildren(context, tree, root, node, clientId, dynamic, checkbox, dragdrop);
        }

        writer.endElement("ul");

        writer.endElement("li");

        if (dragdrop) {
            encodeDropTarget(context, tree);
        }
    }

    public void encodeTreeNodeChildren(FacesContext context, Tree tree, TreeNode<?> root, TreeNode<?> node, String clientId, boolean dynamic,
                                       boolean checkbox, boolean droppable) throws IOException {

        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                if (i == 0 && droppable) {
                    encodeDropTarget(context, tree);
                }

                encodeTreeNode(context, tree, root, node.getChildren().get(i), clientId, dynamic, checkbox, droppable);
            }
        }
        else if (droppable && UITree.ROOT_ROW_KEY.equals(node.getRowKey())) {
            encodeDropTarget(context, tree);
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
        for (UITreeNode node : nodes.values()) {
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
        renderHiddenInput(context, id, value, false);
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
        if (tree.getFilterFunction() != null) {
            return new FunctionFilterConstraint(tree.getFilterFunction());
        }

        return FilterConstraints.of(tree.getFilterMatchMode());
    }
}
