/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.model.ColumnMeta;
import org.primefaces.util.Constants;
import org.primefaces.util.IOUtils;
import org.primefaces.util.LangUtils;

public abstract class TableExporter<T extends UIComponent & UITable, D, O extends ExporterOptions> implements Exporter<T> {

    protected static final Set<FacetType> ALL_FACETS = EnumSet.allOf(FacetType.class);

    private static final Logger LOGGER = Logger.getLogger(TableExporter.class.getName());

    protected D document;

    protected ExportConfiguration exportConfiguration;

    // Because more than 1 table can be exported we cache each one for performance
    private final Map<UITable, List<UIColumn>> exportableColumns = new HashMap<>();

    private final O defaultOptions;

    private final Set<FacetType> supportedFacetTypes;

    private final boolean cellJoinComponents;

    protected enum ColumnType {
        HEADER("header"),
        FOOTER("footer");

        private final String facet;

        ColumnType(String facet) {
            this.facet = facet;
        }

        public String facet() {
            return facet;
        }

        @Override
        public String toString() {
            return facet;
        }
    }

    protected enum FacetType {
        TABLE_FACET,

        COLUMN_GROUP_FACET,

        COLUMN_FACET
    }

    protected TableExporter(O defaultOptions) {
        this(defaultOptions, ALL_FACETS, true);
    }

    protected TableExporter(O defaultOptions, Set<FacetType> supportedFacetTypes, boolean cellJoinComponents) {
        this.defaultOptions = defaultOptions;
        this.supportedFacetTypes = supportedFacetTypes;
        this.cellJoinComponents = cellJoinComponents;
    }

    @Override
    public void export(FacesContext context, List<T> tables, ExportConfiguration exportConfiguration) throws IOException {
        this.exportConfiguration = exportConfiguration;

        try {
            preExport(context);

            ExportVisitCallback exportCallback = new ExportVisitCallback(tables);
            exportCallback.export(context);
        }
        finally {
            postExport(context);
            if (document instanceof AutoCloseable) {
                IOUtils.closeQuietly((AutoCloseable) document, e -> LOGGER.log(Level.SEVERE, e.getMessage(), e));
            }
        }
    }

    protected void exportTable(FacesContext context, T table, int index) throws IOException {
        if (exportConfiguration.getOnTableRender() != null) {
            exportConfiguration.getOnTableRender().invoke(context.getELContext(), getOnTableRenderArgs());
        }

        if (exportConfiguration.isExportHeader()) {
            addTableFacets(context, table, ColumnType.HEADER);
            boolean headerGroup = addColumnGroupFacets(context, table, ColumnType.HEADER);
            if (!headerGroup) {
                addColumnFacets(context, table, ColumnType.HEADER);
            }
        }

        if (exportConfiguration.isPageOnly()) {
            exportPageOnly(context, table);
        }
        else if (exportConfiguration.isSelectionOnly()) {
            exportSelectionOnly(context, table);
        }
        else {
            exportAll(context, table);
        }

        if (exportConfiguration.isExportFooter()) {
            if (table.hasFooterColumn()) {
                addColumnFacets(context, table, ColumnType.FOOTER);
            }

            addTableFacets(context, table, ColumnType.FOOTER);
        }
    }

    protected void addTableFacets(FacesContext context, T table, ColumnType columnType) {
        if (!supportedFacetTypes.contains(FacetType.TABLE_FACET)) {
            return;
        }

        String facetText = ExporterUtils.getComponentFacetValue(context, table, columnType.facet());
        if (LangUtils.isNotBlank(facetText)) {
            proxifyWithRowExport(context,
                    table,
                    0,
                    -1,
                    () -> exportTabletFacetValue(context, table, facetText));
        }
    }

    protected void addColumnFacets(FacesContext context, T table, ColumnType columnType) throws IOException {
        if (!supportedFacetTypes.contains(FacetType.COLUMN_FACET)) {
            return;
        }

        addRow(context, table, (col, i) -> {
            String textValue = ExporterUtils.getColumnFacetValue(context, col, columnType);
            exportColumnFacetValue(context, table, Objects.toString(textValue, Constants.EMPTY_STRING), i);
        });
    }

    protected boolean addColumnGroupFacets(FacesContext context, T table, ColumnType columnType) {
        if (!supportedFacetTypes.contains(FacetType.COLUMN_GROUP_FACET)) {
            return false;
        }

        ColumnGroup cg = table.getColumnGroup(columnType.facet());
        if (cg == null || cg.getChildCount() == 0) {
            return false;
        }

        int total = getExportableColumns(table).size();
        AtomicInteger rowIndex = new AtomicInteger();
        table.forEachColumnGroupRow(context, cg, true, row -> {
            final AtomicInteger colIndex = new AtomicInteger(0);
            rowIndex.getAndIncrement();

            AtomicInteger i = new AtomicInteger(0);
            table.forEachColumn(context, row, true, true, false, column -> {
                if (column.isRendered() && column.isExportable()) {
                    String text = ExporterUtils.getColumnFacetValue(context, column, columnType);

                    proxifyWithRowExport(context,
                            table,
                            i.get(),
                            total,
                            () -> exportColumnGroupFacetValue(context, table, column, rowIndex.get(), colIndex, text, i.get()));

                    i.getAndIncrement();
                    colIndex.incrementAndGet();
                }
                return true;
            });
            return true;
        });
        return true;
    }

    protected void addCells(FacesContext context, T table) {
        addRow(context, table, (col, i) ->
                exportCellValue(context, table, col, ExporterUtils.getColumnValue(context, table, col, cellJoinComponents), i)
        );
    }

    protected void exportTabletFacetValue(FacesContext context, T table, String textValue) {
        if (supportedFacetTypes.contains(FacetType.TABLE_FACET)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportTabletFacetValue() must be implemented");
        }
    }

    protected void exportColumnFacetValue(FacesContext context, T table, String text, int index) {
        if (supportedFacetTypes.contains(FacetType.COLUMN_FACET)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportColumnFacetValue() must be implemented");
        }
    }

    protected void exportColumnGroupFacetValue(FacesContext context, T table, UIColumn column, int rowIndex, AtomicInteger colIndex, String text, int i) {
        if (supportedFacetTypes.contains(FacetType.COLUMN_GROUP_FACET)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportColumnGroupFacetValue() must be implemented");
        }
    }

    protected abstract void exportCellValue(FacesContext context, T table, UIColumn col, String text, int index);

    protected abstract void exportPageOnly(FacesContext context, T table);

    protected abstract void exportAll(FacesContext context, T table);

    protected abstract void exportSelectionOnly(FacesContext context, T table);

    protected void preExport(FacesContext context) throws IOException {
        document = createDocument(context);
        if (exportConfiguration.getPreProcessor() != null) {
            exportConfiguration.getPreProcessor().invoke(context.getELContext(), new Object[]{document});
        }
    }

    protected void postExport(FacesContext context) throws IOException {
        if (exportConfiguration.getPostProcessor() != null) {
            exportConfiguration.getPostProcessor().invoke(context.getELContext(), new Object[]{document});
        }
    }

    protected void preRowExport(FacesContext context, T table) {
        // NOOP
    }

    protected void postRowExport(FacesContext context, T table) {
        if (exportConfiguration.getOnRowExport() != null) {
            exportConfiguration.getOnRowExport().invoke(context.getELContext(), new Object[]{document});
        }
    }

    protected abstract D createDocument(FacesContext context) throws IOException;

    private void addRow(FacesContext context, T table, ObjIntConsumer<UIColumn> callback) {
        List<UIColumn> columns = getExportableColumns(table);
        for (int i = 0; i < columns.size(); i++) {
            UIColumn col = columns.get(i);
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            final int finalI = i;
            proxifyWithRowExport(context,
                    table,
                    i,
                    columns.size(),
                    () -> callback.accept(col, finalI));
        }
    }

    private void proxifyWithRowExport(FacesContext context, T table, int colIndex, int colTotal, Runnable callback) {
        if (colIndex == 0) {
            preRowExport(context, table);
        }

        callback.run();

        if (colIndex == colTotal - 1) {
            postRowExport(context, table);
        }
    }

    private class ExportVisitCallback implements VisitCallback {

        private final List<T> tables;
        private int index;

        public ExportVisitCallback(List<T> tables) {
            this.tables = tables;
            this.index = 0;
        }

        @Override
        public VisitResult visit(VisitContext context, UIComponent component) {
            try {
                exportTable(context.getFacesContext(), (T) component, index);
                index++;
            }
            catch (IOException e) {
                throw new FacesException(e);
            }

            return VisitResult.ACCEPT;
        }

        public void export(FacesContext context) {
            List<String> tableIds = tables.stream()
                    .map(dt -> dt.getClientId(context))
                    .collect(Collectors.toList());

            VisitContext visitContext = VisitContext.createVisitContext(context, tableIds, null);
            context.getViewRoot().visitTree(visitContext, this);
        }
    }

    /**
     * Gets and caches the list of UIColumns that are exportable="true", visible="true", and rendered="true".
     * Orders them by displayPriority so they match the UI display of the columns.
     *
     * @param table the Table with columns to export
     * @return the List<UIColumn> that are exportable
     */
    protected List<UIColumn> getExportableColumns(UITable table) {
        if (exportableColumns.containsKey(table)) {
            return exportableColumns.get(table);
        }

        int allColumnsSize = table.getColumns().size();
        List<UIColumn> exportcolumns = new ArrayList<>(allColumnsSize);
        boolean visibleColumnsOnly = exportConfiguration.isVisibleOnly();
        final AtomicBoolean hasNonDefaultSortPriorities = new AtomicBoolean(false);
        final List<ColumnMeta> visibleColumnMetadata = new ArrayList<>(allColumnsSize);
        Map<String, ColumnMeta> allColumnMeta = table.getColumnMeta();

        table.forEachColumn(true, true, true, column -> {
            if (column.isExportable()) {
                String columnKey = column.getColumnKey();
                ColumnMeta currentMeta = allColumnMeta.get(columnKey);
                if (!visibleColumnsOnly || (visibleColumnsOnly && (currentMeta == null || currentMeta.getVisible()))) {
                    int displayPriority = column.getDisplayPriority();
                    ColumnMeta metaCopy = new ColumnMeta(columnKey);
                    metaCopy.setDisplayPriority(displayPriority);
                    visibleColumnMetadata.add(metaCopy);
                    if (displayPriority != 0) {
                        hasNonDefaultSortPriorities.set(true);
                    }
                }
            }
            return true;
        });

        if (hasNonDefaultSortPriorities.get()) {
            // sort by display priority
            Comparator<Integer> sortIntegersNaturallyWithNullsLast = Comparator.nullsLast(Comparator.naturalOrder());
            visibleColumnMetadata.sort(Comparator.comparing(ColumnMeta::getDisplayPriority, sortIntegersNaturallyWithNullsLast));
        }

        for (ColumnMeta meta : visibleColumnMetadata) {
            String metaColumnKey = meta.getColumnKey();
            table.invokeOnColumn(metaColumnKey, -1, exportcolumns::add);
        }

        exportableColumns.put(table, exportcolumns);

        return exportcolumns;
    }

    protected O options() {
        ExporterOptions opts = exportConfiguration.getOptions();
        if (opts != null) {
            if (defaultOptions.getClass().isAssignableFrom(opts.getClass())) {
                return (O) opts;
            }
            else {
                throw new IllegalArgumentException("Options must be an instance of " + defaultOptions.getClass().getName());
            }
        }

        return defaultOptions;
    }

    protected OutputStream os() {
        return exportConfiguration.getOutputStream();
    }

    protected Object[] getOnTableRenderArgs() {
        return new Object[]{document};
    }
}
