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
package org.primefaces.component.treetable.feature;

import org.primefaces.PrimeFaces;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.component.treetable.TreeTableState;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.TreeNode;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LocaleUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.el.ELContext;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;

public class FilterFeature implements TreeTableFeature {

    @Override
    public void decode(FacesContext context, TreeTable table) {
        // FilterMeta#column must be updated since local value
        // (from column) must be decoded by FilterFeature#decodeFilterValue
        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
        table.updateFilterByValuesWithFilterRequest(context, filterBy);

        // reset state
        table.updateFilteredValue(context, null);
        table.setValue(null);
        table.setFirst(0);

        // update rows with rpp value
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        String rppValue = params.get(clientId + "_rppDD");
        if (rppValue != null && !"*".equals(rppValue)) {
            table.setRows(Integer.parseInt(rppValue));
        }

        if (table.isMultiViewState()) {
            TreeTableState ts = table.getMultiViewState(true);
            ts.setFilterBy(filterBy);
            if (table.isPaginator()) {
                ts.setFirst(table.getFirst());
                ts.setRows(table.getRows());
            }
        }
    }

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable table) throws IOException {
        filter(context, table, table.getValue());
        if (table.isSortingCurrentlyActive()) {
            TreeTableFeatures.sortFeature().sort(context, table);
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        renderer.encodeTbody(context, table, table.getValue(), true);
    }

    public void filter(FacesContext context, TreeTable tt, TreeNode root) {
        Map<String, FilterMeta> filterBy = tt.getFilterByAsMap();
        if (filterBy.isEmpty()) {
            return;
        }

        Locale filterLocale = LocaleUtils.getCurrentLocale(context);

        // collect filtered / valid node rowKeys
        List<String> filteredRowKeys = tt.getFilteredRowKeys();
        filteredRowKeys.clear();
        collectFilteredRowKeys(context, tt, root, root, filterBy, filterLocale, filteredRowKeys);

        // recreate tree node
        TreeNode filteredValue = cloneTreeNode(root, root.getParent());
        createFilteredValueFromRowKeys(tt, root, filteredValue, filteredRowKeys, tt.isFilterPruneDescendants());

        tt.updateFilteredValue(context, filteredValue);
        tt.setValue(filteredValue);
        tt.setRowKey(root, null);

        //Metadata for callback
        if (ComponentUtils.isRequestSource(tt, context)) {
            if (tt.isPaginator()) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", filteredValue.getChildCount());
            }
            if (tt.getSelectedRowKeysAsString() != null) {
                PrimeFaces.current().ajax().addCallbackParam("selection", tt.getSelectedRowKeysAsString());
            }
        }
    }


    protected void collectFilteredRowKeys(FacesContext context, TreeTable tt, TreeNode<?> root, TreeNode<?> node,  Map<String, FilterMeta> filterBy,
            Locale filterLocale, List<String> filteredRowKeys) {

        ELContext elContext = context.getELContext();

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        boolean hasGlobalFilterFunction = globalFilter != null && globalFilter.getConstraint() instanceof FunctionFilterConstraint;

        int childCount = node.getChildCount();

        AtomicBoolean localMatch = new AtomicBoolean();
        AtomicBoolean globalMatch = new AtomicBoolean();

        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = node.getChildren().get(i);
            String rowKey = childNode.getRowKey();
            tt.setRowKey(root, rowKey);
            localMatch.set(true);
            globalMatch.set(false);

            if (hasGlobalFilterFunction) {
                globalMatch.set(globalFilter.getConstraint().isMatching(context, childNode, globalFilter.getFilterValue(), filterLocale));
            }

            tt.forEachColumn(column -> {
                FilterMeta filter = filterBy.get(column.getColumnKey(tt, rowKey));
                if (filter == null || filter.isGlobalFilter()) {
                    return true;
                }
                Object columnValue = filter.getLocalValue(elContext, column);

                if (globalFilter != null && globalFilter.isActive() && !globalMatch.get() && !hasGlobalFilterFunction) {
                    FilterConstraint constraint = globalFilter.getConstraint();
                    Object filterValue = globalFilter.getFilterValue();
                    globalMatch.set(constraint.isMatching(context, columnValue, filterValue, filterLocale));
                }

                if (!filter.isActive()) {
                    return true;
                }

                FilterConstraint constraint = filter.getConstraint();
                Object filterValue = filter.getFilterValue();

                localMatch.set(constraint.isMatching(context, columnValue, filterValue, filterLocale));

                return localMatch.get();
            });

            boolean matches = localMatch.get();
            if (globalFilter != null && globalFilter.isActive()) {
                matches = matches && globalMatch.get();
            }

            if (matches) {
                filteredRowKeys.add(rowKey);
            }

            collectFilteredRowKeys(context, tt, root, childNode, filterBy, filterLocale, filteredRowKeys);
        }
    }

    protected void createFilteredValueFromRowKeys(TreeTable tt, TreeNode<?> node, TreeNode<?> filteredNode,
            List<String> filteredRowKeys, boolean pruneDescendants) {
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = node.getChildren().get(i);
            String rowKeyOfChildNode = childNode.getRowKey();

            for (String rk : filteredRowKeys) {
                if (rk.equals(rowKeyOfChildNode) || rk.startsWith(rowKeyOfChildNode + "_")
                        || (!pruneDescendants && rowKeyOfChildNode.startsWith(rk + "_"))) {
                    TreeNode newNode = cloneTreeNode(childNode, filteredNode);
                    if (rk.startsWith(rowKeyOfChildNode + "_")) {
                        newNode.setExpanded(true);
                    }

                    createFilteredValueFromRowKeys(tt, childNode, newNode, filteredRowKeys, pruneDescendants);
                    break;
                }
            }
        }
    }

    protected TreeNode cloneTreeNode(TreeNode<?> node, TreeNode<?> parent) {
        TreeNode clone = null;

        // equals check instead of instanceof to allow subclassing
        if (CheckboxTreeNode.class.equals(node.getClass())) {
            clone = new CheckboxTreeNode(node.getType(), node.getData(), parent);
        }
        // equals check instead of instanceof to allow subclassing
        else if (DefaultTreeNode.class.equals(node.getClass())) {
            clone = new DefaultTreeNode(node.getType(), node.getData(), parent);
        }
        else if (node instanceof Cloneable) {
            try {
                Method cloneMethod = node.getClass().getMethod("clone");
                cloneMethod.setAccessible(true);
                clone = (TreeNode) cloneMethod.invoke(node);
            }
            catch (ReflectiveOperationException e) {
                throw new FacesException("Cloning using " + node.getClass().getSimpleName() + "#clone() failed", e);
            }
        }
        else {
            try {
                Constructor<? extends TreeNode> ctor = node.getClass().getConstructor(node.getClass());
                clone = ctor.newInstance(node);
                if (parent != null) {
                    parent.getChildren().add(clone);
                }
            }
            catch (ReflectiveOperationException e) {
                if (!(e instanceof NoSuchMethodException)) {
                    throw new FacesException("Calling copy constructor of " + node.getClass().getSimpleName() + " failed: " + e.getMessage());
                }
            }

            if (clone == null) {
                try {
                    Constructor<? extends TreeNode> ctor = node.getClass().getConstructor(String.class, Object.class, TreeNode.class);
                    clone = ctor.newInstance(node.getType(), node.getData(), parent);
                }
                catch (ReflectiveOperationException e) {
                    if (!(e instanceof NoSuchMethodException)) {
                        throw new FacesException("Calling constructor " + node.getClass().getSimpleName()
                                + "(String type, Object data, TreeNode parent) failed", e);
                    }
                }
            }

            if (clone == null) {
                throw new FacesException("Custom node requires to implement either Cloneable, "
                        + "a constructor: " + node.getClass().getSimpleName() + "(String type, Object data, TreeNode parent), "
                        + "or a copy contructor");
            }
        }

        clone.setSelectable(node.isSelectable());
        clone.setSelected(node.isSelected());
        clone.setExpanded(node.isExpanded());

        return clone;
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return context.getCurrentPhaseId() == PhaseId.PROCESS_VALIDATIONS && isFilterRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return isFilterRequest(context, table);
    }

    private boolean isFilterRequest(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

}
