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

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.util.ComponentUtils;

public class ForEachRowColumn {

    public enum ColumnHint implements Predicate<Object> {

        RENDERED {
            @Override
            public boolean test(Object o) {
                return o instanceof UIColumn ? ((UIColumn) o).isRendered() : ((UIComponent) o).isRendered();
            }
        },

        EXPORTABLE {
            @Override
            public boolean test(Object o) {
                return !(o instanceof UIColumn) || ((UIColumn) o).isExportable();
            }
        },

        VISIBLE {
            @Override
            public boolean test(Object o) {
                return !(o instanceof UIColumn) || ((UIColumn) o).isVisible();
            }
        }
    }

    private Set<ColumnHint> hints = EnumSet.of(ColumnHint.RENDERED);

    private UIComponent root;

    private Integer columnStart;

    private Integer columnEnd;

    private RowColumnVisitor callback;

    private String columnKey;

    public static List<UIColumn> collectColumns(UIComponent root) {
        return from(root).invoke(new RowColumnVisitor.ColumnCollector()).getColumns();
    }

    public static int countColumns(UIComponent root) {
        return from(root).invoke(new RowColumnVisitor.ColumnCounter()).getCount();
    }

    public static ForEachRowColumn from(UIComponent root) {
        ForEachRowColumn visitor = new ForEachRowColumn();
        visitor.root = root;
        return visitor;
    }

    public ForEachRowColumn hints(ColumnHint... hints) {
        this.hints = EnumSet.copyOf(Arrays.asList(hints));
        return this;
    }

    public ForEachRowColumn columnStart(int columnStart) {
        this.columnStart = columnStart;
        return this;
    }

    public ForEachRowColumn columnEnd(int columnEnd) {
        this.columnEnd = columnEnd;
        return this;
    }

    public ForEachRowColumn columnKey(String columnKey) {
        this.columnKey = columnKey;
        return this;
    }

    public <V extends RowColumnVisitor> V invoke(V callback) {
        this.callback = callback;

        try {
            VisitResult result = visit(0, root);
            if (result != VisitResult.COMPLETE) {
                visitChildren(root);
            }
        }
        catch (Exception e) {
            throw new FacesException(e);
        }
        finally {
            this.callback = null;
        }

        return callback;
    }

    protected VisitResult visit(int index, UIComponent target) throws IOException {
        if (target instanceof UIColumn) {
            return handleColumn(index, (UIColumn) target);
        }
        else if (target instanceof ColumnGroup) {
            return handleColumnGroup(index, (ColumnGroup) target);
        }
        else if (target instanceof Row) {
            return handleRow(index, (Row) target);
        }
        else if (target instanceof SubTable) {
            return handleSubTable(index, (SubTable) target);
        }
        else if (root == target) {
            return VisitResult.ACCEPT;
        }
        return VisitResult.REJECT;
    }

    protected void visitChildren(UIComponent target) throws IOException {
        if (target.getChildCount() > 0) {
            int offset = 0;
            int end = target.getChildCount();

            // column truncation only for column container unlike UIPanel
            if (columnStart != null && columnEnd != null && isColumnAware(target)) {
                offset = columnStart;
                end = Math.min(columnEnd, target.getChildCount());
            }

            int idx = 0; // relative position of child to his parent

            for (int i = offset; i < end; i++) {
                UIComponent child = target.getChildren().get(i);
                if (ComponentUtils.isTargetableComponent(child)) {
                    VisitResult result = visit(idx, child);
                    if (result == VisitResult.COMPLETE) {
                        break;
                    }

                    if (result != VisitResult.REJECT) {
                        idx++;
                    }
                }
            }
        }
    }

    private VisitResult handleRow(int index, Row target) throws IOException {
        if (isVisitable(target)) {
            callback.visitRow(index, target);
            visitChildren(target);
            callback.visitRowEnd(index, target);
            return completeIfRoot(target);
        }
        return VisitResult.REJECT;
    }

    private VisitResult handleColumnGroup(int index, ColumnGroup target) throws IOException {
        if (isVisitable(target)) {
            callback.visitColumnGroup(index, target);
            visitChildren(target);
            return completeIfRoot(target);
        }
        return VisitResult.REJECT;
    }

    private VisitResult handleColumn(int index, UIColumn target) throws IOException {
        if (target instanceof Columns) {
            Columns cols = (Columns) target;
            cols.setRowIndex(-1);
            for (int i = 0; i < cols.getRowCount(); i++) {
                DynamicColumn column = new DynamicColumn(i, cols, FacesContext.getCurrentInstance());
                if (VisitResult.COMPLETE == visitIfPossible(index + i, column)) {
                    cols.setRowIndex(-1);
                    return VisitResult.COMPLETE;
                }
            }
            cols.setRowIndex(-1);
        }
        else if (VisitResult.COMPLETE == visitIfPossible(index, target)) {
            return VisitResult.COMPLETE;
        }

        return completeIfRoot(target);
    }

    private VisitResult handleSubTable(int index, SubTable target) throws IOException {
        if (isVisitable(target)) {
            callback.visitSubTable(index, target);
            visitChildren(target);
            return completeIfRoot(target);
        }
        return VisitResult.REJECT;
    }

    private VisitResult completeIfRoot(Object target) {
        return root == target ? VisitResult.COMPLETE : VisitResult.ACCEPT;
    }

    private VisitResult visitIfPossible(int index, UIColumn column) throws IOException {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyModel();
        }
        if (isVisitable(column)) {
            callback.visitColumn(index, column);
            if (columnKey != null) {
                return VisitResult.COMPLETE;
            }
        }
        return VisitResult.ACCEPT;
    }

    private boolean isVisitable(Object target) {
        return !(columnKey != null
                && target instanceof UIColumn && !columnKey.equals(((UIColumn) target).getColumnKey()))
                && hints.stream().allMatch(o -> o.test(target));
    }

    private static boolean isColumnAware(UIComponent component) {
        return component instanceof ColumnAware;
    }
}
