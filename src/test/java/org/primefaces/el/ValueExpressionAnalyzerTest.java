/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
