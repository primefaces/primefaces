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
