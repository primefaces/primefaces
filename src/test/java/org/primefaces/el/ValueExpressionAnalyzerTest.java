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
package org.primefaces.el;

import de.odysseus.el.ExpressionFactoryImpl;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.pf.PrimeApplicationContextMock;
import org.primefaces.mock.pf.PrimeRequestContextMock;

public class ValueExpressionAnalyzerTest
{
    @Before
    public void init() {
        FacesContext facesContext = new FacesContextMock();

        PrimeApplicationContextMock applicationContext = new PrimeApplicationContextMock(facesContext);
        PrimeApplicationContext.setCurrentInstance(applicationContext, facesContext);

        PrimeRequestContext requestContext = new PrimeRequestContextMock(facesContext, applicationContext);
        PrimeRequestContext.setCurrentInstance(requestContext, facesContext);
    }

    @After
    public void destroy() {
        PrimeRequestContext.setCurrentInstance(null, FacesContext.getCurrentInstance());
        FacesContext.getCurrentInstance().release();
    }

    @Test
    public void firstLevelValueReference()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean}", MyBean.class));

        Assert.assertEquals(bean, ve.getValue(context));
    }

    @Test
    public void secondLevelValueReference()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();
        bean.setContainer(new MyContainer());

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.container}", MyContainer.class));

        Assert.assertEquals(bean.getContainer(), ve.getValue(context));
    }

    @Test
    public void secondLevelNullValueReference()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.container}", MyContainer.class));

        Assert.assertEquals(null, ve.getValue(context));
    }

    @Test
    public void thirdLevelValueReference()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();
        bean.setContainer(new MyContainer());
        bean.getContainer().setValue("test");

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.container.value}", String.class));

        Assert.assertEquals("test", ve.getValue(context));
    }

    @Test(expected = PropertyNotFoundException.class)
    public void thirdLevelNullValueReference()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.container.value}", String.class));

        Assert.assertEquals(null, ve.getValue(context));
    }


    @Test
    public void secondLevelMethodExpression()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();
        bean.setContainer(new MyContainer());
        bean.getContainer().setValue("test");

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.getContainer().value}", String.class));

        Assert.assertEquals("test", ve.getValue(context));
    }

    @Test
    public void thirdLevelMethodExpression()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();
        bean.setContainer(new MyContainer());
        bean.getContainer().setValue("test");

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.container.getValue()}", String.class));

        Assert.assertEquals("test", ve.getValue(context));
    }

    @Test
    public void secondAndThirdLevelMethodExpression()
    {
        ExpressionFactory factory = newExpressionFactory();

        MyBean bean = new MyBean();
        bean.setContainer(new MyContainer());
        bean.getContainer().setValue("test");

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("bean", factory.createValueExpression(bean, MyBean.class));

        ValueExpression ve = ValueExpressionAnalyzer.getExpression(
                context,
                factory.createValueExpression(context, "#{bean.getContainer().getValue()}", String.class));

        Assert.assertEquals("test", ve.getValue(context));
    }

    public ExpressionFactory newExpressionFactory()
    {
        return new de.odysseus.el.ExpressionFactoryImpl(ExpressionFactoryImpl.Profile.JEE6);
    }
}
