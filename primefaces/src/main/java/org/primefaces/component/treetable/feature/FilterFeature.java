/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.filter.*;

import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.component.treetable.TreeTableState;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.LocaleUtils;

public class FilterFeature implements TreeTableFeature {

    private static final Logger LOGGER = Logger.getLogger(FilterFeature.class.getName());
    private static final FilterFeature INSTANCE = new FilterFeature();

    private FilterFeature() {
    }

    public static FilterFeature getInstance() {
        return INSTANCE;
    }

    private boolean isFilterRequest(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return context.getCurrentPhaseId() == PhaseId.PROCESS_VALIDATIONS && isFilterRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return isFilterRequest(context, table);
    }

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
            SortFeature.getInstance().sort(context, table);
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        renderer.encodeTbody(context, table, table.getValue(), true);
    }

    public void filter(FacesContext context, TreeTable tt, TreeNode root) throws IOException {
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
        TreeNode filteredValue = cloneTreeNode(tt, root, root.getParent());
        createFilteredValueFromRowKeys(tt, root, filteredValue, filteredRowKeys);

        tt.updateFilteredValue(context, filteredValue);
        tt.setValue(filteredValue);
        tt.setRowKey(root, null);

        //Metadata for callback
        if (tt.isPaginator()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filteredValue.getChildCount());
        }
        if (tt.getSelectedRowKeysAsString() != null) {
            PrimeFaces.current().ajax().addCallbackParam("selection", tt.getSelectedRowKeysAsString());
        }
    }


    protected void collectFilteredRowKeys(FacesContext context, TreeTable tt, TreeNode<?> root, TreeNode<?> node,  Map<String, FilterMeta> filterBy,
            Locale filterLocale, List<String> filteredRowKeys) throws IOException {

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

    private void createFilteredValueFromRowKeys(TreeTable tt, TreeNode<?> node, TreeNode<?> filteredNode, List<String> filteredRowKeys) {
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = node.getChildren().get(i);
            String rowKeyOfChildNode = childNode.getRowKey();

            for (String rk : filteredRowKeys) {
                if (rk.equals(rowKeyOfChildNode) || rk.startsWith(rowKeyOfChildNode + "_") || rowKeyOfChildNode.startsWith(rk + "_")) {
                    TreeNode newNode = cloneTreeNode(tt, childNode, filteredNode);
                    if (rk.startsWith(rowKeyOfChildNode + "_")) {
                        newNode.setExpanded(true);
                    }

                    createFilteredValueFromRowKeys(tt, childNode, newNode, filteredRowKeys);
                    break;
                }
            }
        }
    }

    protected TreeNode cloneTreeNode(TreeTable tt, TreeNode<?> node, TreeNode<?> parent) {
        TreeNode clone = null;

        // equals check instead of instanceof to allow subclassing
        if (CheckboxTreeNode.class.equals(node.getClass())) {
            clone = new CheckboxTreeNode(node.getType(), node.getData(), parent);
        }
        // equals check instead of instanceof to allow subclassing
        else if (DefaultTreeNode.class.equals(node.getClass())) {
            clone = new DefaultTreeNode(node.getType(), node.getData(), parent);
        }

        if (clone == null && tt.isCloneOnFilter()) {
            if (node instanceof Cloneable) {
                try {
                    Method cloneMethod = node.getClass().getMethod("clone");
                    if (cloneMethod != null) {
                        cloneMethod.setAccessible(true);
                        clone = (TreeNode) cloneMethod.invoke(node);
                    }
                }
                catch (NoSuchMethodException e) {
                    LOGGER.warning(node.getClass().getName() + " declares Cloneable but no clone() method found!");
                }
                catch (InvocationTargetException | IllegalAccessException e) {
                    LOGGER.warning(node.getClass().getName() + "#clone() not accessible!");
                }
            }
            else {
                try {
                    Constructor<? extends TreeNode> ctor = node.getClass().getConstructor(node.getClass());
                    clone = ctor.newInstance(node);
                }
                catch (NoSuchMethodException e) {
                    // ignore
                }
                catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    LOGGER.warning("Could not clone " + node.getClass().getName()
                            + " via public " + node.getClass().getSimpleName() + "() constructor!");
                }

                if (clone == null) {
                    try {
                        Constructor<? extends TreeNode> ctor = node.getClass().getConstructor(String.class, Object.class, TreeNode.class);
                        clone = ctor.newInstance(node.getType(), node.getData(), parent);
                    }
                    catch (NoSuchMethodException e) {
                        // ignore
                    }
                    catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                        LOGGER.warning("Could not clone " + node.getClass().getName()
                                + " via public " + node.getClass().getSimpleName() + "(String type, Object data, TreeNode parent) constructor!");
                    }
                }
            }
        }

        if (clone == null) {
            if (node instanceof CheckboxTreeNode) {
                clone = new CheckboxTreeNode(node.getType(), node.getData(), parent);
            }
            else {
                clone = new DefaultTreeNode(node.getType(), node.getData(), parent);
            }
        }

        clone.setSelectable(node.isSelectable());
        clone.setSelected(node.isSelected());
        clone.setExpanded(node.isExpanded());

        return clone;
    }
}