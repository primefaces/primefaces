/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import de.odysseus.el.util.SimpleContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.el.MyBean;
import org.primefaces.el.MyContainer;
import org.primefaces.mock.FacesContextMock;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import static org.mockito.Mockito.*;

public class DataTableTest {

    @Test
    public void testAllowUnsorting() {
        DataTable table = new DataTable();
        Assertions.assertEquals(false, table.isAllowUnsorting());
    }

    @Test
    public void testResolveStaticField() {
        FacesContext context = new FacesContextMock();

        Column column = new Column();
        ValueExpression exprVE = mock(ValueExpression.class);

        when(exprVE.getExpressionString()).thenReturn("#{car.year}");
        Assertions.assertEquals("year", column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{car.wrapper.year}");
        Assertions.assertEquals("wrapper.year", column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{car}");
        Assertions.assertNull(column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("car.year");
        Assertions.assertNull(column.resolveField(context, exprVE));

        when(exprVE.getExpressionString()).thenReturn("#{i18n[row][property]}");
        Assertions.assertNull(column.resolveField(context, exprVE));
    }

    @Test
    public void testResolveDynamicField() {
        FacesContext context = new FacesContextMock();
        ExpressionFactory expFactory = context.getApplication().getExpressionFactory();

        DynamicColumn column = new DynamicColumn(0, mock(Columns.class), context);

        MyBean bean = new MyBean();
        MyContainer container = new MyContainer();
        container.setValue("MyValue");
        bean.setContainer(container);

        ((SimpleContext)context.getELContext()).setVariable("column",
                expFactory.createValueExpression(bean, MyBean.class));

        // correct syntax
        ValueExpression exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{car[column.container.value]}", String.class);
        Assertions.assertEquals("MyValue", column.resolveField(context, exprVE));

        // unsupported syntax
        exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{i18n[column][property]}", String.class);
        Assertions.assertNull(column.resolveField(context, exprVE));

        // incorrect syntax
        exprVE = expFactory.createValueExpression(context.getELContext(), "#{column.container.value}", String.class);
        Assertions.assertNull(column.resolveField(context, exprVE));

        // unsupported syntax
        exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{i18n[row][column[property]]}", String.class);
        Assertions.assertNull(column.resolveField(context, exprVE));
    }
}
