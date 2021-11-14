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
import org.primefaces.event.data.PostSortEvent;
import org.primefaces.model.*;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.component.treetable.TreeTableState;

public class SortFeature implements TreeTableFeature {

    private static final SortFeature INSTANCE = new SortFeature();

    private SortFeature() {
    }

    public static SortFeature getInstance() {
        return INSTANCE;
    }

    private boolean isSortRequest(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_sorting");
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return isSortRequest(context, table);
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return isSortRequest(context, table);
    }

    @Override
    public void decode(FacesContext context, TreeTable table) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        String sortKey = params.get(clientId + "_sortKey");
        String sortDir = params.get(clientId + "_sortDir");

        String[] sortKeys = sortKey.split(",");
        String[] sortOrders = sortDir.split(",");

        if (sortKeys.length != sortOrders.length) {
            throw new FacesException("sortKeys != sortDirs");
        }

        Map<String, SortMeta> sortByMap = table.getSortByAsMap();
        Map<String, Integer> sortKeysIndexes = IntStream.range(0, sortKeys.length).boxed()
                .collect(Collectors.toMap(i -> sortKeys[i], i -> i));

        for (Map.Entry<String, SortMeta> entry : sortByMap.entrySet()) {
            SortMeta sortMeta = entry.getValue();
            if (sortMeta.isHeaderRow()) {
                continue;
            }

            Integer index = sortKeysIndexes.get(entry.getKey());
            if (index != null) {
                sortMeta.setOrder(SortOrder.of(sortOrders[index]));
                sortMeta.setPriority(index);
            }
            else {
                sortMeta.setOrder(SortOrder.UNSORTED);
                sortMeta.setPriority(SortMeta.MIN_PRIORITY);
            }
        }
    }

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable table) throws IOException {
        sort(context, table);

        context.getApplication().publishEvent(context, PostSortEvent.class, table);

        renderer.encodeTbody(context, table, table.getValue(), true);

        String selectedRowKeys = table.getSelectedRowKeysAsString();
        if (selectedRowKeys != null) {
            PrimeFaces.current().ajax().addCallbackParam("selection", selectedRowKeys);
        }

        if (table.isPaginator()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", table.getRowCount());
        }

        if (table.isMultiViewState()) {
            Map<String, SortMeta> sortMeta = table.getSortByAsMap();
            TreeTableState ts = table.getMultiViewState(true);
            ts.setSortBy(sortMeta);
            if (table.isPaginator()) {
                ts.setFirst(table.getFirst());
                ts.setRows(table.getRows());
            }
        }
    }

    public void sort(FacesContext context, TreeTable table) {
        TreeNode root = table.getValue();
        if (root == null) {
            return;
        }

        Map<String, SortMeta> sortBy = table.getActiveSortMeta();
        if (sortBy.isEmpty()) {
            return;
        }

        AtomicInteger comparisonResult = new AtomicInteger();

        String var = table.getVar();
        Locale locale = table.resolveDataLocale();
        Collator collator = Collator.getInstance(locale);

        Object varBackup = context.getExternalContext().getRequestMap().get(var);

        sortNode(table, sortBy, comparisonResult, root, context, var, locale, collator);

        if (varBackup == null) {
            context.getExternalContext().getRequestMap().remove(var);
        }
        else {
            context.getExternalContext().getRequestMap().put(var, varBackup);
        }
    }

    protected void sortNode(TreeTable table, Map<String, SortMeta> sortBy, AtomicInteger comparisonResult,
            TreeNode<?> node, FacesContext context, String var, Locale locale, Collator collator) {
        TreeNodeList<?> children = (TreeNodeList) node.getChildren();

        if (children != null && !children.isEmpty()) {
            Object[] childrenArray = children.toArray();

            Arrays.sort(childrenArray, (o1, o2) -> {
                for (SortMeta sortMeta : sortBy.values()) {
                    comparisonResult.set(0);

                    table.invokeOnColumn(sortMeta.getColumnKey(), column -> {
                        int result = table.compare(context, var, sortMeta,
                                ((TreeNode) o1).getData(),
                                ((TreeNode) o2).getData(),
                                collator, locale);
                        comparisonResult.set(result);
                    });

                    if (comparisonResult.get() != 0) {
                        return comparisonResult.get();
                    }
                }

                return 0;
            });

            for (int i = 0; i < childrenArray.length; i++) {
                children.setSibling(i, (TreeNode) childrenArray[i]);
            }
            for (int i = 0; i < children.size(); i++) {
                sortNode(table, sortBy, comparisonResult, children.get(i), context, var, locale, collator);
            }
        }
    }
}
