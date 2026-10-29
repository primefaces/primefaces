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
package org.primefaces.component.columns;

import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnsTest {

    @Test
    void columnIndexVarIsExposedAsRowIndexVar() {
        Columns columns = new Columns();
        columns.setColumnIndexVar("colIndex");

        assertEquals("colIndex", columns.getColumnIndexVar());
        assertEquals("colIndex", columns.getRowIndexVar());
    }

    @Test
    void columnIndexVarIsPutIntoRequestMapWhileIterating() {
        FacesContext context = new FacesContextMock();

        Columns columns = new Columns();
        columns.setId("cols");
        columns.setVar("col");
        columns.setColumnIndexVar("colIndex");
        columns.setValue(Arrays.asList("A", "B", "C"));

        columns.setRowIndex(2);

        assertEquals("C", context.getExternalContext().getRequestMap().get("col"));
        assertEquals(2, context.getExternalContext().getRequestMap().get("colIndex"));
    }
}
