/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.model.ColumnMeta;

public interface ColumnAware {

    default void forEachColumn(Consumer<UIColumn> callback) {
        forEachColumn(true, callback);
    }

    default void forEachColumn(boolean unwrapDynamicColumns, Consumer<UIColumn> callback) {
        forEachColumn(FacesContext.getCurrentInstance(), (UIComponent) this, unwrapDynamicColumns, callback);
    }

    default void forEachColumn(FacesContext context, UIComponent root, boolean unwrapDynamicColumns, Consumer<UIColumn> callback) {
        for (int i = 0; i < root.getChildCount(); i++) {
            UIComponent child = root.getChildren().get(i);
            if (child.isRendered()) {
                if (child instanceof Columns) {
                    Columns columns = (Columns) child;
                    if (unwrapDynamicColumns) {
                        for (int j = 0; j < columns.getRowCount(); j++) {
                            DynamicColumn dynaColumn = new DynamicColumn(j, columns, context);
                            callback.accept(dynaColumn);
                        }
                    }
                    else {
                        callback.accept(columns);
                    }
                }
                else if (child instanceof Column) {
                    Column column = (Column) child;
                    callback.accept(column);
                }
                else if (child instanceof ColumnGroup) {
                    forEachColumn(context, child, unwrapDynamicColumns, callback);
                }
                else if (child instanceof ColumnAware) {
                    ColumnAware columnHolder = (ColumnAware) child;
                    for (int j = 0; j < ((UIComponent) columnHolder).getChildCount(); j++) {
                        UIComponent columnHolderChild = ((UIComponent) columnHolder).getChildren().get(j);
                        if (columnHolderChild.isRendered()) {
                            forEachColumn(context, columnHolderChild, unwrapDynamicColumns, callback);
                        }
                    }
                }
            }
        }
    }

    default UIColumn findColumn(String columnKey) {
        if ("globalFilter".equals(columnKey)) {
            return null;
        }

        List<UIColumn> columns = getColumns();

        //body columns
        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = columns.get(i);
            if (Objects.equals(column.getColumnKey(), columnKey)) {
                return column;
            }
        }

        //header columns
        if (getFrozenColumnsCount() > 0) {
            UIColumn column = findColumnInGroup(columnKey, getColumnGroup("frozenHeader"));
            if (column == null) {
                column = findColumnInGroup(columnKey, getColumnGroup("scrollableHeader"));
            }

            if (column != null) {
                return column;
            }
        }
        else {
            return findColumnInGroup(columnKey, getColumnGroup("header"));
        }

        throw new FacesException("Cannot find column with key: " + columnKey);
    }

    default int getFrozenColumnsCount() {
        return 0;
    }

    default UIColumn findColumnInGroup(String columnKey, ColumnGroup group) {
        if (group == null) {
            return null;
        }

        for (int i = 0; i < group.getChildCount(); i++) {
            UIComponent row = group.getChildren().get(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                UIComponent rowChild = row.getChildren().get(j);
                if (rowChild instanceof Column) {
                    Column column = (Column) rowChild;
                    if (Objects.equals(column.getColumnKey(), columnKey)) {
                        return column;
                    }
                }
                else if (rowChild instanceof Columns) {
                    Columns columns = (Columns) rowChild;
                    List<DynamicColumn> dynamicColumns = columns.getDynamicColumns();
                    for (UIColumn column : dynamicColumns) {
                        if (Objects.equals(column.getColumnKey(), columnKey)) {
                            return column;
                        }
                    }
                }
            }
        }

        return null;
    }

    default ColumnGroup getColumnGroup(String type) {
        for (int i = 0; i < ((UIComponent) this).getChildCount(); i++) {
            UIComponent child = ((UIComponent) this).getChildren().get(i);
            if (child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                if (Objects.equals(type, colGroup.getType())) {
                    return colGroup;
                }
            }
        }

        return null;
    }

    List<UIColumn> getColumns();

    void setColumns(List<UIColumn> columns);

    default List<UIColumn> collectColumns() {
        List<UIColumn> columns = new ArrayList<>();
        FacesContext context = FacesContext.getCurrentInstance();

        for (int i = 0; i < ((UIComponent) this).getChildCount(); i++) {
            UIComponent child = ((UIComponent) this).getChildren().get(i);
            if (child instanceof Column) {
                columns.add((Column) child);
            }
            else if (child instanceof Columns) {
                Columns uiColumns = (Columns) child;
                for (int j = 0; j < uiColumns.getRowCount(); j++) {
                    DynamicColumn dynaColumn = new DynamicColumn(j, uiColumns, context);
                    columns.add(dynaColumn);
                }
            }
        }

        Map<String, ColumnMeta> columnMeta = getColumnMeta();

        // sort by displayOrder
        columns.sort((c1, c2) -> {
            if (c1 instanceof DynamicColumn) {
                ((DynamicColumn) c1).applyStatelessModel();
            }
            if (c2 instanceof DynamicColumn) {
                ((DynamicColumn) c2).applyStatelessModel();
            }

            Integer dp1 = c1.getDisplayPriority();
            ColumnMeta cm1 = columnMeta.get(c1.getColumnKey());
            if (cm1 != null && cm1.getDisplayPriority() != null) {
                dp1 = cm1.getDisplayPriority();
            }

            Integer dp2 = c2.getDisplayPriority();
            ColumnMeta cm2 = columnMeta.get(c2.getColumnKey());
            if (cm2 != null && cm2.getDisplayPriority() != null) {
                dp2 = cm2.getDisplayPriority();
            }

            return dp1.compareTo(dp2);
        });

        return columns;
    }

    default int getColumnsCount() {
        return getColumnsCount(true);
    }

    default int getColumnsCount(boolean visibleOnly) {
        final LongAdder columnsCount = new LongAdder();

        forEachColumn(column -> {
            if (!visibleOnly || column.isVisible()) {
                columnsCount.increment();
            }
        });

        return columnsCount.intValue();
    }

    default int getColumnsCountWithSpan() {
        return getColumnsCountWithSpan(true);
    }

    default int getColumnsCountWithSpan(boolean visibleOnly) {
        final LongAdder columnsCountWithSpan = new LongAdder();

        forEachColumn(column -> {
            if (!visibleOnly || column.isVisible()) {
                columnsCountWithSpan.add(column.getColspan());
            }
        });

        return columnsCountWithSpan.intValue();
    }

    default void resetDynamicColumns() {
        forEachColumn(false, column ->  {
            if (column instanceof Columns) {
                ((Columns) column).setRowIndex(-1);
                setColumns(null);
            }
        });
    }

    Map<String, ColumnMeta> getColumnMeta();

    void setColumnMeta(Map<String, ColumnMeta> columnMeta);

    default String getOrderedColumnKeys() {
        return getColumns()
                .stream()
                .map(e -> e.getColumnKey())
                .collect(Collectors.joining(","));
    }
}
