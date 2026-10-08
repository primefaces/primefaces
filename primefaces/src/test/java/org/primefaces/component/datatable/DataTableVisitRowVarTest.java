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
package org.primefaces.component.datatable;

import org.primefaces.component.column.Column;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.TestVisitContext;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.visit.VisitHint;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * #13967 A tree visit must not disturb the row var of a table that is still rendering.
 *
 * <p>Resolving a search expression such as {@code update="@root:@id(panel)"} on a command inside a
 * column makes the Faces {@code @id} keyword resolver visit the whole view while the table is
 * rendering a row. Every UIData reached by that visit iterates its rows, and on the way out calls
 * {@code setRowIndex(-1)}, which removes the var from the request map unconditionally. When a second
 * table shares the var name, that removal takes the rendering table's var with it and the remaining
 * columns render empty.</p>
 */
class DataTableVisitRowVarTest {

    private static DataTable table(String id, String var, Object... values) {
        DataTable table = new DataTable();
        table.setId(id);
        table.setVar(var);
        table.setValue(Arrays.asList(values));
        table.getChildren().add(new Column());
        return table;
    }

    private static Map<String, Object> requestMap(FacesContext context) {
        return context.getExternalContext().getRequestMap();
    }

    /** The reported failure: a second table sharing the var name wipes it on the way out. */
    @Test
    void visitOfTableSharingTheVarNameKeepsTheRenderingVar() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        DataTable rendering = table("rendering", "row", "a", "b");
        DataTable other = table("other", "row", "x", "y");

        rendering.setRowIndex(0);
        assertEquals("a", requestMap(context).get("row"), "precondition");

        other.visitTree(new TestVisitContext(context), (ctx, target) -> VisitResult.ACCEPT);

        assertEquals("a", requestMap(context).get("row"));
    }

    /** The reported shape: a whole-view visit, as @root:@id(...) performs. */
    @Test
    void wholeViewVisitKeepsTheRenderingVar() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        UIViewRoot root = new UIViewRoot();
        DataTable rendering = table("rendering", "row", "a", "b");
        root.getChildren().add(rendering);
        root.getChildren().add(table("inDialog", "row", "x", "y"));

        rendering.setRowIndex(0);

        root.visitTree(new TestVisitContext(context), (ctx, target) -> VisitResult.ACCEPT);

        assertEquals("a", requestMap(context).get("row"));
    }

    /** A table standing on a row of its own still exposes it after visiting itself. */
    @Test
    void visitOfTheRenderingTableItselfKeepsItsVar() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        DataTable rendering = table("rendering", "row", "a", "b");
        rendering.setRowIndex(0);

        rendering.visitTree(new TestVisitContext(context), (ctx, target) -> VisitResult.ACCEPT);

        assertEquals("a", requestMap(context).get("row"));
    }

    /** A visit of an idle table must not leave a var behind either. */
    @Test
    void visitOfIdleTableLeavesNoVar() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        DataTable idle = table("idle", "row", "x", "y");

        idle.visitTree(new TestVisitContext(context), (ctx, target) -> VisitResult.ACCEPT);

        assertFalse(requestMap(context).containsKey("row"), "visit must not publish a var");
    }

    /** Unrelated var names were never affected, and must stay that way. */
    @Test
    void visitOfTableWithADifferentVarNameKeepsBoth() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        DataTable rendering = table("rendering", "row", "a", "b");
        DataTable other = table("other", "otherRow", "x", "y");

        rendering.setRowIndex(0);

        other.visitTree(new TestVisitContext(context), (ctx, target) -> VisitResult.ACCEPT);

        assertEquals("a", requestMap(context).get("row"));
        assertFalse(requestMap(context).containsKey("otherRow"));
    }

    /** SKIP_ITERATION never triggered the row walk, so it was and stays safe. */
    @Test
    void visitWithSkipIterationKeepsTheRenderingVar() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        DataTable rendering = table("rendering", "row", "a", "b");
        DataTable other = table("other", "row", "x", "y");

        rendering.setRowIndex(0);

        other.visitTree(new TestVisitContext(context, EnumSet.of(VisitHint.SKIP_ITERATION)),
                (ctx, target) -> VisitResult.ACCEPT);

        assertEquals("a", requestMap(context).get("row"));
    }
}
