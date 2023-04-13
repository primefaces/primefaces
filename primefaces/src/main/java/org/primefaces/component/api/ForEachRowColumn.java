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
package org.primefaces.component.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitResult;

import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.util.ComponentUtils;

// Initial plan was to use VisitCallback API but not optimal for progressive visit. Partial visit requires to know
// in advance what are the component to be visited
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
                return o instanceof UIColumn && ((UIColumn) o).isExportable();
            }
        },

        VISIBLE {
            @Override
            public boolean test(Object o) {
                return o instanceof UIColumn && ((UIColumn) o).isVisible();
            }
        }
    }

    private Set<ColumnHint> hints = EnumSet.of(ColumnHint.RENDERED);

    private UIComponent root;

    private int columnStart = 0;

    private int columnEnd = Integer.MAX_VALUE;

    private RowColumnVisitor callback;

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

    public void invoke(RowColumnVisitor callback) {
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
        else if (root == target) {
            return VisitResult.ACCEPT;
        }
        return VisitResult.REJECT;
    }

    protected void visitChildren(UIComponent target) throws IOException {
        if (target.getChildCount() > 0) {
            // ignore column start/end in case it's not a column container like row
            boolean columnContainer = isColumnContainer(target);
            int offset = columnContainer ? columnStart : 0;
            int end = columnContainer ? Math.min(columnEnd, target.getChildCount()) : target.getChildCount();
            int idx = 0; // relative position of child to his parent

            for (int i = offset; idx < end; i++) {
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
        if (shouldIgnoreUnRendered(target)) {
            callback.visitRow(index, target);
            visitChildren(target);
            return completeIfRoot(target);
        }
        return VisitResult.REJECT;
    }


    private VisitResult handleColumnGroup(int index, ColumnGroup target) throws IOException {
        if (shouldIgnoreUnRendered(target)) {
            callback.visitColumnGroup(index, target);
            visitChildren(target);
            return completeIfRoot(target);
        }
        return VisitResult.REJECT;
    }

    private VisitResult handleColumn(int index, UIColumn target) throws IOException {
        if (target instanceof Columns) {
            Columns cols = (Columns) target;
            for (int i = 0; i < cols.getDynamicColumns().size(); i++) {
                DynamicColumn column = cols.getDynamicColumns().get(i);
                applyPredicateAndVisitIfPossible(index + i, column);
            }
        }
        else {
            applyPredicateAndVisitIfPossible(index, target);
        }

        return completeIfRoot(target);
    }

    private VisitResult completeIfRoot(Object target) {
        return root == target ? VisitResult.COMPLETE : VisitResult.ACCEPT;
    }

    private void applyPredicateAndVisitIfPossible(int index, UIColumn column) throws IOException {
        boolean eligible = true;
        if (!hints.isEmpty()) {
            if (column instanceof DynamicColumn) {
                ((DynamicColumn) column).applyStatelessModel();
            }
            eligible = hints.stream().allMatch(o -> o.test(column));
        }

        if (eligible) {
            callback.visitColumn(index, column);
        }
    }

    private boolean shouldIgnoreUnRendered(Object target) {
        return !hints.contains(ColumnHint.RENDERED) || ColumnHint.RENDERED.test(target);
    }

    private static boolean isColumnContainer(UIComponent component) {
        return component instanceof ColumnAware || component instanceof Row || component instanceof ColumnGroup;
    }
}
