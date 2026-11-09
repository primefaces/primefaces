/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.model.FilterMeta;
import org.primefaces.model.TreeNode;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.util.LocaleUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.el.ELContext;
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
        table.resetFilteredNodes();
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
        filter(context, table, table.getValue(), true);
        if (table.isSortingCurrentlyActive()) {
            TreeTableFeatures.sortFeature().sort(context, table);
        }

        // metadata for the callback, also when the filters were cleared
        if (table.isPaginator()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", table.getRowCount());
        }
        if (table.getSelectedRowKeysAsString() != null) {
            PrimeFaces.current().ajax().addCallbackParam("selection", table.getSelectedRowKeysAsString());
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        renderer.encodeTbody(context, table, table.getValue(), true);
    }

    /**
     * Applies the active filters to the given tree and stores the nodes to display in the {@link TreeTable}, see
     * {@link TreeTable#setFilteredNodes(Set)}. The tree itself is not changed, apart from expanding the ancestors of the
     * matching nodes if requested.
     *
     * @param context the {@link FacesContext}
     * @param tt the {@link TreeTable}
     * @param root the root of the tree
     * @param expandMatches whether to expand the ancestors of the matching nodes, so that the matches are displayed
     */
    public void filter(FacesContext context, TreeTable tt, TreeNode<?> root, boolean expandMatches) {
        Map<String, FilterMeta> filterBy = tt.getFilterByAsMap();
        if (root == null || filterBy.values().stream().noneMatch(FilterMeta::isActive)) {
            tt.setFilteredNodes(null);
        }
        else {
            Set<TreeNode<?>> filteredNodes = Collections.newSetFromMap(new IdentityHashMap<>());
            String oldRowKey = tt.getRowKey();
            try {
                collectFilteredNodes(context, tt, root, root, filterBy, LocaleUtils.getCurrentLocale(context), false, expandMatches,
                        tt.isFilterPruneDescendants(), filteredNodes);
            }
            finally {
                tt.setRowKey(root, oldRowKey);
            }
            tt.setFilteredNodes(filteredNodes);
        }
    }

    /**
     * Collects the children of the given node which are displayed: the matching nodes, their ancestors and, unless
     * descendants are pruned, their descendants.
     *
     * @return whether a descendant of the given node matches
     */
    protected boolean collectFilteredNodes(FacesContext context, TreeTable tt, TreeNode<?> root, TreeNode<?> node, Map<String, FilterMeta> filterBy,
            Locale filterLocale, boolean ancestorMatches, boolean expandMatches, boolean pruneDescendants, Set<TreeNode<?>> filteredNodes) {

        boolean descendantMatches = false;

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode<?> childNode = node.getChildren().get(i);
            boolean matches = isMatching(context, tt, root, childNode, filterBy, filterLocale);
            boolean childDescendantMatches = collectFilteredNodes(context, tt, root, childNode, filterBy, filterLocale,
                    ancestorMatches || matches, expandMatches, pruneDescendants, filteredNodes);

            if (matches || childDescendantMatches || (ancestorMatches && !pruneDescendants)) {
                filteredNodes.add(childNode);
            }
            if (childDescendantMatches && expandMatches) {
                childNode.setExpanded(true);
            }

            descendantMatches = descendantMatches || matches || childDescendantMatches;
        }

        return descendantMatches;
    }

    protected boolean isMatching(FacesContext context, TreeTable tt, TreeNode<?> root, TreeNode<?> node, Map<String, FilterMeta> filterBy,
            Locale filterLocale) {

        ELContext elContext = context.getELContext();

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        boolean hasGlobalFilterFunction = globalFilter != null && globalFilter.getConstraint() instanceof FunctionFilterConstraint;

        String rowKey = node.getRowKey();
        tt.setRowKey(root, rowKey);

        AtomicBoolean localMatch = new AtomicBoolean(true);
        AtomicBoolean globalMatch = new AtomicBoolean();

        if (hasGlobalFilterFunction) {
            globalMatch.set(globalFilter.getConstraint().isMatching(context, node, globalFilter.getFilterValue(), filterLocale));
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

        return matches;
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
