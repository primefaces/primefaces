/* 
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
package org.primefaces.component.datatable;

import org.junit.Assert;
import org.junit.Test;

import javax.el.ValueExpression;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataTableTest {

    @Test
    public void testResolveStaticField() {
        DataTable table = new DataTable();
        ValueExpression exprVE = mock(ValueExpression.class);

        when(exprVE.getExpressionString()).thenReturn("#{car.year}");
        String field = table.resolveStaticField(exprVE);
        Assert.assertEquals("year", field);

        when(exprVE.getExpressionString()).thenReturn("#{car.wrapper.year}");
        field = table.resolveStaticField(exprVE);
        Assert.assertEquals("wrapper.year", field);

        when(exprVE.getExpressionString()).thenReturn("#{car}");
        field = table.resolveStaticField(exprVE);
        Assert.assertNull(field);

        when(exprVE.getExpressionString()).thenReturn("car.year");
        field = table.resolveStaticField(exprVE);
        Assert.assertNull(field);
    }
}
