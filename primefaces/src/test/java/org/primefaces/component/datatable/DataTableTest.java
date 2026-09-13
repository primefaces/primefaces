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

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.el.MyBean;
import org.primefaces.el.MyContainer;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.renderkit.PrimeRendererWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataTableTest {

    @Test
    void allowUnsorting() {
        DataTable table = new DataTable();
        assertFalse(table.isAllowUnsorting());
    }

    /**
     * The row the table stands on ends up in the client id of every descendant and puts the row in the request map
     * under the table's var, and the same holds for the column a p:columns stands on. The scroll, cell edit, row edit
     * and add row ajax requests never reach encodeTbody, which is the only place which used to reset the row index,
     * so the reset has to happen after encodeEnd, for every request which encoded the table.
     */
    @Test
    void cleanupIterationStateLeavesTheTableStandingOnNoRowAndNoColumn() {
        FacesContext context = new FacesContextMock();

        Columns columns = new Columns();
        columns.setId("cols");
        columns.setVar("col");
        columns.setValue(Arrays.asList("id", "name"));

        DataTable table = new DataTable();
        table.setId("table");
        table.setVar("row");
        table.setValue(Arrays.asList("one", "two"));
        table.getChildren().add(columns);

        table.setRowIndex(1);
        columns.setRowIndex(1);

        table.cleanupIterationState(context);

        assertEquals(-1, table.getRowIndex());
        assertEquals(-1, columns.getRowIndex());
        assertFalse(context.getExternalContext().getRequestMap().containsKey("row"));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("col"));
    }

    /**
     * DraggableRowsFeature#decode stands the table on the row which was dragged and returns without resetting it, so
     * the decode has to leave the table standing on no row too. There is no page level reproducer for this: the row
     * reorder request renders one fragment and nothing renders after it.
     */
    @Test
    void decodeLeavesTheTableStandingOnNoRow() {
        FacesContext context = new FacesContextMock();

        DataTable table = new DataTable();
        table.setId("table");
        table.setVar("row");
        table.setValue(new ArrayList<>(Arrays.asList("one", "two", "three")));

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        params.put(table.getClientId(context) + "_rowreorder", "true");
        params.put(table.getClientId(context) + "_fromIndex", "0");
        params.put(table.getClientId(context) + "_toIndex", "2");

        new PrimeRendererWrapper(new DataTableRenderer()).decode(context, table);

        assertEquals(-1, table.getRowIndex());
        assertEquals(table.getClientId(context), table.getContainerClientId(context));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("row"));
    }

    @Test
    void resolveStaticField() {
        FacesContext context = new FacesContextMock();

        Column column = new Column();
        ValueExpression exprVE = mock(ValueExpression.class);

        when(exprVE.getExpressionString()).thenReturn("#{car.year}");
        assertEquals("year", column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{car.wrapper.year}");
        assertEquals("wrapper.year", column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{car['year']}");
        assertEquals("year", column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{car}");
        assertNull(column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("car.year");
        assertNull(column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{i18n[row][property]}");
        assertNull(column.resolveField(context, exprVE));
    }

    @Test
    void resolveDynamicField() {
        FacesContext context = new FacesContextMock();
        ExpressionFactory expFactory = context.getApplication().getExpressionFactory();

        DynamicColumn column = new DynamicColumn(0, mock(Columns.class), context);

        MyBean bean = new MyBean();
        MyContainer container = new MyContainer();
        container.setValue("MyValue");
        bean.setContainer(container);

        context.getELContext().getVariableMapper().setVariable("column",
                expFactory.createValueExpression(bean, MyBean.class));

        // correct syntax
        ValueExpression exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{car[column.container.value]}", String.class);
        assertEquals("MyValue", column.resolveField(context, exprVE));

        // unsupported syntax
        exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{i18n[column][property]}", String.class);
        assertNull(column.resolveField(context, exprVE));

        // incorrect syntax
        exprVE = expFactory.createValueExpression(context.getELContext(), "#{column.container.value}", String.class);
        assertNull(column.resolveField(context, exprVE));

        // unsupported syntax
        exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{i18n[row][column[property]]}", String.class);
        assertNull(column.resolveField(context, exprVE));
    }
}
