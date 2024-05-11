/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.model.ColumnMeta;
import org.primefaces.util.ColumnComparators;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface ColumnAware {

    default void invokeOnColumn(String columnKey, Consumer<UIColumn> callback) {
        ForEachRowColumn.from((UIComponent) this).columnKey(columnKey).invoke(new RowColumnVisitor.Adapter() {
            @Override
            public void visitColumn(int index, UIColumn column) throws IOException {
                callback.accept(column);
            }
        });
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

        throw new FacesException("Cannot find column with key: " + columnKey);
    }

    @Deprecated
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
        List<UIColumn> columns = ForEachRowColumn
                .from((UIComponent) this)
                .invoke(new RowColumnVisitor.ColumnCollector())
                .getColumns();
        Map<String, ColumnMeta> columnMeta = getColumnMeta();
        columns.sort(ColumnComparators.displayOrder(columnMeta));
        return columns;
    }

    default int getColumnsCount() {
        return ForEachRowColumn
                .from((UIComponent) this)
                .hints(ForEachRowColumn.ColumnHint.RENDERED, ForEachRowColumn.ColumnHint.VISIBLE)
                .invoke(new RowColumnVisitor.ColumnCounter())
                .getCount();
    }

    Map<String, ColumnMeta> getColumnMeta();

    void setColumnMeta(Map<String, ColumnMeta> columnMeta);

    default String getOrderedColumnKeys() {
        return getColumns()
                .stream()
                .map(UIColumn::getColumnKey)
                .collect(Collectors.joining(","));
    }

}
