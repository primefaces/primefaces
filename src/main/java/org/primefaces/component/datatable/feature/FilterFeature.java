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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.component.row.Row;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.filter.*;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

public class FilterFeature implements DataTableFeature {

    private static final Map<MatchMode, FilterConstraint> FILTER_CONSTRAINTS = MapBuilder.<MatchMode, FilterConstraint>builder()
        .put(MatchMode.STARTS_WITH, new StartsWithFilterConstraint())
        .put(MatchMode.ENDS_WITH, new EndsWithFilterConstraint())
        .put(MatchMode.CONTAINS, new ContainsFilterConstraint())
        .put(MatchMode.EXACT, new ExactFilterConstraint())
        .put(MatchMode.LESS_THAN, new LessThanFilterConstraint())
        .put(MatchMode.LESS_THAN_EQUALS, new LessThanEqualsFilterConstraint())
        .put(MatchMode.GREATER_THAN, new GreaterThanFilterConstraint())
        .put(MatchMode.GREATER_THAN_EQUALS, new GreaterThanEqualsFilterConstraint())
        .put(MatchMode.EQUALS, new EqualsFilterConstraint())
        .put(MatchMode.IN, new InFilterConstraint())
        .put(MatchMode.GLOBAL, new GlobalFilterConstraint())
        .build();

    private boolean isFilterRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        String globalFilterParam = table.getClientId(context) + UINamingContainer.getSeparatorChar(context) + "globalFilter";
        Map<String, FilterMeta> filterBy = populateFilterBy(context, table, globalFilterParam);
        table.setFilterBy(filterBy);
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //reset state
        String clientId = table.getClientId(context);
        table.updateFilteredValue(context, null);
        table.setValue(null);
        table.setFirst(0);
        table.setRowIndex(-1);

        //update rows with rpp value
        String rppValue = params.get(clientId + "_rppDD");
        if (rppValue != null && !rppValue.equals("*")) {
            table.setRows(Integer.parseInt(rppValue));
        }

        if (table.isLazy()) {
            if (table.isLiveScroll()) {
                table.loadLazyScrollData(0, table.getScrollRows());
            }
            else if (table.isVirtualScroll()) {
                int rows = table.getRows();
                int scrollRows = table.getScrollRows();
                int virtualScrollRows = (scrollRows * 2);
                scrollRows = (rows == 0) ? virtualScrollRows : ((virtualScrollRows > rows) ? rows : virtualScrollRows);

                table.loadLazyScrollData(0, scrollRows);
            }
            else {
                table.loadLazyData();
            }
        }
        else {
            filter(context, table, table.getFilterBy());

            //sort new filtered data to restore sort state
            boolean sorted = table.getValueExpression(DataTable.PropertyKeys.sortBy.toString()) != null
                    || table.getSortBy() != null;
            if (sorted) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);

                if (table.isMultiSort()) {
                    sortFeature.multiSort(context, table);
                }
                else {
                    sortFeature.singleSort(context, table);
                }
            }
        }

        context.getApplication().publishEvent(context, PostFilterEvent.class, table);

        renderer.encodeTbody(context, table, true);

        if (table.isMultiViewState()) {
            Map<String, FilterMeta> filterBy = table.getFilterBy();
            Map<String, FilterMeta> filterByCopy = new HashMap<>(filterBy.size());

            for (Map.Entry<String, FilterMeta> filter : filterBy.entrySet()) {
                filterByCopy.put(filter.getKey(), new FilterMeta(filter.getValue()));
            }

            DataTableState ts = table.getMultiViewState(true);
            ts.setFilterBy(filterByCopy);

            if (table.isPaginator()) {
                ts.setFirst(table.getFirst());
                ts.setRows(table.getRows());
            }
        }
    }

    public void filter(FacesContext context, DataTable table, Map<String, FilterMeta> filterBy) {
        List filteredData = new ArrayList();
        Locale filterLocale = table.resolveDataLocale();
        FilterMeta globalFilter = filterBy.get("globalFilter");
        GlobalFilterConstraint globalFilterConstraint = (GlobalFilterConstraint) FILTER_CONSTRAINTS.get(MatchMode.GLOBAL);
        MethodExpression globalFilterFunction = table.getGlobalFilterFunction();
        ELContext elContext = context.getELContext();

        for (FilterMeta filter : filterBy.values()) {
            if (filter.getColumn() == null) {
                filter.setColumn(table.findColumn(filter.getColumnKey()));
            }
        }

        for (int i = 0; i < table.getRowCount(); i++) {
            table.setRowIndex(i);
            boolean localMatch = true;
            boolean globalMatch = false;

            if (globalFilter != null && globalFilterFunction != null) {
                globalMatch = (Boolean) globalFilterFunction.invoke(elContext,
                        new Object[]{table.getRowData(), globalFilter.getFilterValue(), filterLocale});
            }

            for (FilterMeta filter : filterBy.values()) {
                UIColumn column = filter.getColumn();
                if (column == null) {
                    continue;
                }

                MethodExpression filterFunction = column.getFilterFunction();
                ValueExpression filterByVE = filter.getFilterByVE();
                Object filterValue = filter.getFilterValue();

                if (column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyStatelessModel();
                }

                Object columnValue = filterByVE.getValue(elContext);
                FilterConstraint filterConstraint = getFilterConstraint(column);

                if (globalFilter != null && !globalMatch && globalFilterFunction == null) {
                    globalMatch = globalFilterConstraint.applies(columnValue, globalFilter.getFilterValue(), filterLocale);
                }

                if (filterFunction != null) {
                    localMatch = (Boolean) filterFunction.invoke(elContext, new Object[]{columnValue, filterValue, filterLocale});
                }
                else if (!filterConstraint.applies(columnValue, filterValue, filterLocale)) {
                    localMatch = false;
                }

                if (!localMatch) {
                    break;
                }
            }

            boolean matches = localMatch;
            if (globalFilter != null) {
                matches = localMatch && globalMatch;
            }

            if (matches) {
                filteredData.add(table.getRowData());
            }
        }

        //Metadata for callback
        if (table.isPaginator() || table.isVirtualScroll()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filteredData.size());
        }

        //save filtered data
        table.updateFilteredValue(context, filteredData);

        //update value
        table.updateValue(table.getFilteredValue());

        table.setRowIndex(-1);  //reset datamodel
    }

    public boolean isFilterValueEmpty(Object filterValue) {
        if (filterValue == null) {
            return true;
        }

        if (filterValue.getClass().isArray() && Array.getLength(filterValue) == 0) {
            return true;
        }

        if (LangUtils.isValueBlank(filterValue.toString())) {
            return true;
        }

        return false;
    }

    public String getFilterField(DataTable table, UIColumn column) {
        ValueExpression filterByVE = column.getValueExpression(Column.PropertyKeys.filterBy.toString());

        if (column.isDynamic()) {
            ((DynamicColumn) column).applyStatelessModel();
            String field = column.getField();
            if (field == null) {
                Object filterByProperty = column.getFilterBy();
                field = (filterByProperty == null) ? table.resolveDynamicField(filterByVE) : filterByProperty.toString();
            }
            return field;
        }
        else {
            String field = column.getField();
            if (field == null) {
                field = (filterByVE == null) ? (String) column.getFilterBy() : table.resolveStaticField(filterByVE);
            }
            return field;
        }
    }

    public Map<String, FilterMeta> populateFilterBy(FacesContext context, DataTable table, String globalFilterParam) {
        Map<String, FilterMeta> filterBy = new HashMap<>();

        String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        boolean hasFrozenColumns = table.getFrozenColumns() > 0;

        if (!hasFrozenColumns) {
            ColumnGroup headerGroup = getColumnGroup(table, "header");

            if (headerGroup != null) {
                populateFilterByInColumnGroup(context, filterBy, table, headerGroup, params, separator);
            }
            else {
                populateFilterByWithoutColumnGroups(context, table, filterBy, params, separator);
            }
        }
        else {
            ColumnGroup frozenHeaderGroup = getColumnGroup(table, "frozenHeader");
            ColumnGroup scrollableHeaderGroup = getColumnGroup(table, "scrollableHeader");

            if (frozenHeaderGroup != null) {
                populateFilterByInColumnGroup(context, filterBy, table, frozenHeaderGroup, params, separator);
                populateFilterByInColumnGroup(context, filterBy, table, scrollableHeaderGroup, params, separator);
            }
            else {
                populateFilterByWithoutColumnGroups(context, table, filterBy, params, separator);
            }
        }

        if (params.containsKey(globalFilterParam)) {
            Object filterValue = params.get(globalFilterParam);
            if (isFilterValueEmpty(filterValue)) {
                filterValue = null;
            }
            filterBy.put("globalFilter", new FilterMeta("globalFilter", filterValue));
        }

        return filterBy;
    }

    private void populateFilterByInColumnGroup(FacesContext context, Map<String, FilterMeta> filterBy,
            DataTable dataTable, ColumnGroup group, Map<String, String> params, String separator) {

        if (group == null) {
            return;
        }

        for (UIComponent child : group.getChildren()) {
            Row headerRow = (Row) child;

            if (headerRow.isRendered()) {
                for (UIComponent headerRowChild : headerRow.getChildren()) {
                    if (headerRowChild instanceof Column) {
                        Column column = (Column) headerRowChild;
                        if (column.isRendered()) {
                            ValueExpression filterVE = column.getValueExpression(Column.PropertyKeys.filterBy.toString());
                            if (filterVE != null) {
                                UIComponent filterFacet = column.getFacet("filter");
                                Object filterValue = ComponentUtils.shouldRenderFacet(filterFacet)
                                                     ? ((ValueHolder) filterFacet).getLocalValue()
                                                     : params.get(column.getClientId(context) + separator + "filter");
                                if (isFilterValueEmpty(filterValue)) {
                                    filterValue = null;
                                }

                                String filterField = getFilterField(dataTable, column);
                                filterBy.put(filterField, new FilterMeta(filterField,
                                        column.getColumnKey(),
                                        filterVE,
                                        MatchMode.byName(column.getFilterMatchMode()),
                                        filterValue));
                            }
                        }
                    }
                    else if (headerRowChild instanceof Columns) {
                        Columns uiColumns = (Columns) headerRowChild;
                        List<DynamicColumn> dynamicColumns = uiColumns.getDynamicColumns();

                        for (DynamicColumn dynaColumn : dynamicColumns) {
                            dynaColumn.applyStatelessModel();
                            if (dynaColumn.isRendered()) {
                                ValueExpression filterVE = dynaColumn.getValueExpression(Column.PropertyKeys.filterBy.toString());
                                if (filterVE != null) {
                                    String filterId = dynaColumn.getContainerClientId(context) + separator + "filter";
                                    UIComponent filterFacet = dynaColumn.getFacet("filter");
                                    Object filterValue = ComponentUtils.shouldRenderFacet(filterFacet)
                                                          ? ((ValueHolder) filterFacet).getLocalValue()
                                                          : params.get(filterId);
                                    if (isFilterValueEmpty(filterValue)) {
                                        filterValue = null;
                                    }

                                    String filterField = getFilterField(dataTable, dynaColumn);
                                    filterBy.put(filterField, new FilterMeta(filterField,
                                            dynaColumn.getColumnKey(),
                                            filterVE,
                                            MatchMode.byName(dynaColumn.getFilterMatchMode()),
                                            filterValue));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void populateFilterByWithoutColumnGroups(FacesContext context, DataTable table, Map<String, FilterMeta> filterBy,
                                                           Map<String, String> params, String separator) {

        for (UIColumn column : table.getColumns()) {
            ValueExpression filterVE = column.getValueExpression(Column.PropertyKeys.filterBy.toString());
            if (filterVE != null) {
                UIComponent filterFacet = column.getFacet("filter");
                Object filterValue = null;
                String filterId;
                String filterMatchMode = null;

                if (column instanceof Column) {
                    filterId = column.getClientId(context) + separator + "filter";
                    filterValue = ComponentUtils.shouldRenderFacet(filterFacet) ? ((ValueHolder) filterFacet).getLocalValue() : params.get(filterId);
                    filterMatchMode = column.getFilterMatchMode();
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();
                    filterId = dynamicColumn.getContainerClientId(context) + separator + "filter";
                    filterValue = ComponentUtils.shouldRenderFacet(filterFacet) ? ((ValueHolder) filterFacet).getLocalValue() : params.get(filterId);
                    filterMatchMode = column.getFilterMatchMode();
                    dynamicColumn.cleanModel();
                }

                if (isFilterValueEmpty(filterValue)) {
                    filterValue = null;
                }

                String filterField = getFilterField(table, column);
                filterBy.put(filterField, new FilterMeta(filterField,
                        column.getColumnKey(),
                        filterVE,
                        MatchMode.byName(filterMatchMode),
                        filterValue));
            }
        }
    }

    private ColumnGroup getColumnGroup(DataTable table, String target) {
        for (UIComponent child : table.getChildren()) {
            if (child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if (type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }

    public FilterConstraint getFilterConstraint(UIColumn column) {
        String filterMatchMode = column.getFilterMatchMode();

        MatchMode matchMode = MatchMode.byName(filterMatchMode);
        if (matchMode == null) {
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        FilterConstraint filterConstraint = FILTER_CONSTRAINTS.get(matchMode);
        if (filterConstraint == null) {
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        return filterConstraint;
    }

}