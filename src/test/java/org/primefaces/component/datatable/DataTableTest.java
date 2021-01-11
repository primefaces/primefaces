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

import de.odysseus.el.util.SimpleContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.el.MyBean;
import org.primefaces.el.MyContainer;
import org.primefaces.mock.FacesContextMock;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import static org.mockito.Mockito.*;
import org.primefaces.component.api.UITable;

public class DataTableTest {

    @Test
    public void testAllowUnsorting() {
        DataTable table = new DataTable();
        Assertions.assertEquals(false, table.isAllowUnsorting());
    }

    @Test
    public void testResolveStaticField() {
        DataTable table = new DataTable();
        ValueExpression exprVE = mock(ValueExpression.class);

        when(exprVE.getExpressionString()).thenReturn("#{car.year}");
        String field = UITable.resolveStaticField(exprVE);
        Assertions.assertEquals("year", field);

        when(exprVE.getExpressionString()).thenReturn("#{car.wrapper.year}");
        field = UITable.resolveStaticField(exprVE);
        Assertions.assertEquals("wrapper.year", field);

        when(exprVE.getExpressionString()).thenReturn("#{car}");
        field = UITable.resolveStaticField(exprVE);
        Assertions.assertEquals("car", field);

        when(exprVE.getExpressionString()).thenReturn("car.year");
        field = UITable.resolveStaticField(exprVE);
        Assertions.assertNull(field);
    }

    @Test
    public void testResolveDynamicField() {
        FacesContext context = new FacesContextMock();
        ExpressionFactory expFactory = context.getApplication().getExpressionFactory();

        MyBean bean = new MyBean();
        MyContainer container = new MyContainer();
        container.setValue("MyValue");
        bean.setContainer(container);

        ((SimpleContext)context.getELContext()).setVariable("column",
                expFactory.createValueExpression(bean, MyBean.class));

        // old syntax
        ValueExpression exprVE = expFactory.createValueExpression(
                context.getELContext(), "#{car[column.container.value]}", String.class);
        String field = UITable.resolveDynamicField(context, exprVE);
        Assertions.assertEquals("MyValue", field);

        // new syntax
        exprVE = expFactory.createValueExpression(context.getELContext(), "#{column.container.value}", String.class);
        field = UITable.resolveDynamicField(context, exprVE);
        Assertions.assertEquals("MyValue", field);
    }
}
