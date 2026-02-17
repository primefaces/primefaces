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
package org.primefaces.component.export;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.component.rowtoggler.RowToggler;
import org.primefaces.component.tooltip.Tooltip;
import org.primefaces.model.ColumnMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.IOUtils;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.el.MethodExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.component.UISelectMany;
import jakarta.faces.component.ValueHolder;
import jakarta.faces.component.html.HtmlCommandLink;
import jakarta.faces.component.html.HtmlGraphicImage;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

public abstract class TableExporter<T extends UIComponent & UITable, D, O extends ExporterOptions> implements Exporter<T> {

    protected static final Set<FacetType> ALL_FACETS = EnumSet.allOf(FacetType.class);

    private static final Logger LOGGER = Logger.getLogger(TableExporter.class.getName());

    protected D document;

    protected ExportConfiguration exportConfiguration;

    protected final boolean cellJoinComponents;

    // Because more than 1 table can be exported we cache each one for performance
    private final Map<T, List<UIColumn>> exportableColumnsCache = new HashMap<>();

    private final O defaultOptions;

    private final Set<FacetType> supportedFacetTypes;

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
        TABLE,
        COLUMN_GROUP,
        COLUMN
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

            postExport(context);
        }
        finally {
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
        if (!supportedFacetTypes.contains(FacetType.TABLE)) {
            return;
        }

        String facetText = getComponentFacetValue(context, table, columnType.facet());
        if (LangUtils.isNotBlank(facetText)) {
            proxifyWithRowExport(context,
                    table,
                    0,
                    -1,
                    () -> exportTabletFacetValue(context, table, facetText));
        }
    }

    protected void addColumnFacets(FacesContext context, T table, ColumnType columnType) throws IOException {
        if (!supportedFacetTypes.contains(FacetType.COLUMN)) {
            return;
        }

        addRow(context, table, (col, i) -> {
            ColumnValue columnValue = getColumnFacetValue(context, col, columnType);
            exportColumnFacetValue(context, table, columnValue, i);
        });
    }

    protected boolean addColumnGroupFacets(FacesContext context, T table, ColumnType columnType) {
        if (!supportedFacetTypes.contains(FacetType.COLUMN_GROUP)) {
            return false;
        }

        ColumnGroup cg = table.getColumnGroup(columnType.facet());
        if (cg == null || cg.getChildCount() == 0) {
            return false;
        }

        int total = getExportableColumns(table).size();
        table.forEachColumnGroupRow(context, cg, true, row -> {
            final AtomicInteger colIndex = new AtomicInteger(0);

            table.forEachColumn(context, row, true, true, false, column -> {
                if (column.isExportable()) {
                    ColumnValue columnValue = getColumnFacetValue(context, column, columnType);

                    proxifyWithRowExport(context,
                            table,
                            colIndex.get(),
                            total,
                            () -> exportColumnGroupFacetValue(context, table, column, colIndex, columnValue));

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
                exportCellValue(context, table, col, getColumnValue(context, table, col, cellJoinComponents), i)
        );
    }

    protected void exportTabletFacetValue(FacesContext context, T table, String textValue) {
        if (supportedFacetTypes.contains(FacetType.TABLE)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportTabletFacetValue() must be implemented");
        }
    }

    protected void exportColumnFacetValue(FacesContext context, T table, ColumnValue columnValue, int index) {
        if (supportedFacetTypes.contains(FacetType.COLUMN)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportColumnFacetValue() must be implemented");
        }
    }

    protected void exportColumnGroupFacetValue(FacesContext context, T table, UIColumn column, AtomicInteger colIndex, ColumnValue columnValue) {
        if (supportedFacetTypes.contains(FacetType.COLUMN_GROUP)) {
            throw new UnsupportedOperationException(getClass().getName() + "#exportColumnGroupFacetValue() must be implemented");
        }
    }

    protected abstract void exportCellValue(FacesContext context, T table, UIColumn col, ColumnValue columnValue, int index);

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
    protected List<UIColumn> getExportableColumns(T table) {
        if (exportableColumnsCache.containsKey(table)) {
            return exportableColumnsCache.get(table);
        }

        int allColumnsSize = table.getColumns().size();
        List<UIColumn> exportableColumns = new ArrayList<>(allColumnsSize);
        boolean visibleColumnsOnly = exportConfiguration.isVisibleOnly();
        Map<String, ColumnMeta> allColumnMeta = table.getColumnMeta();
        final List<ColumnMeta> exportableColumnsMetadata = new ArrayList<>(allColumnsSize);

        table.forEachColumn(true, true, true, column -> {
            if (column.isExportable()) {
                String columnKey = column.getColumnKey();
                ColumnMeta currentMeta = allColumnMeta.get(columnKey);
                // #9197 - if we have a column without metadata, we need to set it to 0
                if (currentMeta == null) {
                    currentMeta = new ColumnMeta(columnKey);
                    currentMeta.setVisible(true);
                }
                else if (currentMeta.getVisible() == null) {
                    currentMeta.setVisible(true);
                }
                currentMeta.setDisplayPriority(column.getDisplayPriority());
                // #7200 display visible columns only
                if (!visibleColumnsOnly || currentMeta.getVisible()) {
                    exportableColumnsMetadata.add(currentMeta);
                }
            }
            return true;
        });

        // #12731 sort by visible display priority
        Comparator<Integer> sortIntegersNaturallyWithNullsLast = Comparator.nullsLast(Comparator.naturalOrder());
        exportableColumnsMetadata.sort(Comparator.comparing(ColumnMeta::getDisplayPriority, sortIntegersNaturallyWithNullsLast));


        for (ColumnMeta meta : exportableColumnsMetadata) {
            String metaColumnKey = meta.getColumnKey();
            table.invokeOnColumn(metaColumnKey, -1, exportableColumns::add);
        }

        exportableColumnsCache.put(table, exportableColumns);

        return exportableColumns;
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

    // -- UTILS

    public String getComponentValue(FacesContext context, UIComponent component) {

        if (component instanceof HtmlCommandLink) {  //support for PrimeFaces and standard HtmlCommandLink
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) {
                return String.valueOf(value);
            }
            else {
                //export first value holder
                for (UIComponent child : link.getChildren()) {
                    if (child instanceof ValueHolder) {
                        return getComponentValue(context, child);
                    }
                }

                return Constants.EMPTY_STRING;
            }
        }
        else if (component instanceof ValueHolder) {
            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null) {
                return Constants.EMPTY_STRING;
            }

            Converter converter = valueHolder.getConverter();
            if (converter == null) {
                Class<?> valueType = value.getClass();
                converter = context.getApplication().createConverter(valueType);
            }

            if (converter != null) {
                if (component instanceof UISelectMany) {
                    List<Object> collection = null;

                    if (value instanceof List) {
                        collection = (List<Object>) value;
                    }
                    else if (value.getClass().isArray()) {
                        collection = Arrays.asList(value);
                    }
                    else {
                        throw new FacesException("Value of " + component.getClientId(context) + " must be a List or an Array.");
                    }

                    Converter finalConverter = converter;
                    return collection.stream()
                            .map(o -> finalConverter.getAsString(context, component, o))
                            .collect(Collectors.joining(","));
                }
                else {
                    return converter.getAsString(context, component, value);
                }
            }
            else {
                return value.toString();
            }
        }
        else if (component instanceof CellEditor) {
            return getComponentValue(context, ((CellEditor) component).getOutputFacet());
        }
        else if (component instanceof HtmlGraphicImage) {
            return (String) component.getAttributes().get("alt");
        }
        else if (component instanceof OverlayPanel) {
            return Constants.EMPTY_STRING;
        }
        else if (component instanceof RowToggler) {
            return Constants.EMPTY_STRING;
        }
        else if (component instanceof Tooltip) {
            return Constants.EMPTY_STRING;
        }
        else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null) {
                return value.trim();
            }
            else {
                return Constants.EMPTY_STRING;
            }
        }
    }

    public ColumnValue getColumnValue(FacesContext context, UITable table, UIColumn column, boolean joinComponents) {
        if (column.getExportValue() != null) {
            return ColumnValue.of(column.getExportValue());
        }
        else if (column.getExportFunction() != null) {
            MethodExpression exportFunction = column.getExportFunction();
            return ColumnValue.of(exportFunction.invoke(context.getELContext(), new Object[]{column}));
        }
        else if (LangUtils.isNotBlank(column.getField())) {
            String value = table.getConvertedFieldValue(context, column);
            return ColumnValue.of(value);
        }
        else {
            return ColumnValue.of(column.getChildren()
                    .stream()
                    .filter(UIComponent::isRendered)
                    .map(c -> getComponentValue(context, c))
                    .filter(LangUtils::isNotBlank)
                    .limit(!joinComponents ? 1 : column.getChildren().size())
                    .collect(Collectors.joining(Constants.SPACE)));
        }
    }

    public String getComponentFacetValue(FacesContext context, UIComponent parent, String facetname) {
        UIComponent facet = parent.getFacet(facetname);
        if (FacetUtils.shouldRenderFacet(facet)) {
            if (facet instanceof UIPanel) {
                for (UIComponent child : facet.getChildren()) {
                    if (child.isRendered()) {
                        String value = ComponentUtils.getValueToRender(context, child);

                        if (value != null) {
                            return value;
                        }
                    }
                }
            }
            else {
                return ComponentUtils.getValueToRender(context, facet);
            }
        }

        return null;
    }

    public ColumnValue getColumnFacetValue(FacesContext context, UIColumn column, TableExporter.ColumnType columnType) {
        ColumnValue columnValue = ColumnValue.EMPTY_VALUE;
        if (columnType == TableExporter.ColumnType.HEADER) {
            columnValue = ColumnValue.of(Optional.ofNullable(column.getExportHeaderValue()).orElseGet(column::getHeaderText));
        }
        else if (columnType == TableExporter.ColumnType.FOOTER) {
            columnValue = ColumnValue.of(Optional.ofNullable(column.getExportFooterValue()).orElseGet(column::getFooterText));
        }

        UIComponent facet = column.getFacet(columnType.facet());
        if (LangUtils.isBlank(columnValue.toString()) && FacetUtils.shouldRenderFacet(facet)) {
            columnValue = ColumnValue.of(getComponentValue(context, facet));
        }

        return columnValue;
    }

    public String getColumnExportTag(FacesContext context, UIColumn column) {
        // lowerCase really? camelCase at best
        String columnTag = column.getExportTag();
        if (LangUtils.isBlank(columnTag)) {
            columnTag = getColumnFacetValue(context, column, TableExporter.ColumnType.HEADER).toString();
        }
        return EscapeUtils.forXmlTag(columnTag.toLowerCase());
    }
}
