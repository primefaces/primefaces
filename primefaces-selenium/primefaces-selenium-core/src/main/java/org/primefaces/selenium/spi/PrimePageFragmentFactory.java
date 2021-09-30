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
package org.primefaces.selenium.spi;

import java.lang.reflect.*;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageFragment;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.findby.FindByParentPartialId;
import org.primefaces.selenium.findby.FindByParentPartialIdElementLocator;
import org.primefaces.selenium.internal.proxy.ElementLocatorInterceptor;
import org.primefaces.selenium.internal.proxy.ElementsLocatorInterceptor;
import org.primefaces.selenium.internal.proxy.LazyElementLocator;
import org.primefaces.selenium.internal.proxy.ProxyUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.primefaces.selenium.internal.OnloadScripts;

public class PrimePageFragmentFactory {

    private PrimePageFragmentFactory() {
    }

    public static <T extends WebElement> T create(Class<T> fragment, WebElement element) {
        ElementLocator el;
        if (element instanceof AbstractPrimePageFragment) {
            el = ((AbstractPrimePageFragment) element).getElementLocator();
        }
        else {
            el = new ElementLocator() {
                @Override
                public WebElement findElement() {
                    return element;
                }

                @Override
                public List<WebElement> findElements() {
                    return null;
                }
            };
        }

        return create(fragment, element, el);
    }

    public static <T extends WebElement> T create(Class<T> fragment, WebElement element, ElementLocator el) {
        try {
            T proxy = proxy(fragment,
                    InvocationHandlerAdapter.of((Object p, Method method, Object[] args) -> {
                        OnloadScripts.execute();
                        return method.invoke(el.findElement(), args);
                    }));

            WebDriver driver = WebDriverProvider.get();

            if (proxy instanceof AbstractPrimePage) {
                ((AbstractPrimePage) proxy).setWebDriver(driver);
            }
            if (proxy instanceof AbstractPrimePageFragment) {
                ((AbstractPrimePageFragment) proxy).setWebDriver(driver);
                ((AbstractPrimePageFragment) proxy).setElementLocator(el);
            }

            setMembers(driver, new DefaultElementLocatorFactory(proxy), proxy);

            return proxy;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void setMembers(WebDriver driver, ElementLocatorFactory elf, Object obj) {

        for (Field field : ProxyUtils.collectFields(obj)) {
            if (field.getAnnotation(FindBy.class) != null
                        || field.getAnnotation(FindAll.class) != null
                        || field.getAnnotation(FindBys.class) != null) {
                ElementLocator el = new LazyElementLocator(elf, field);

                setMember(driver, el, field, obj);
            }

            FindByParentPartialId findByParentPartialId = field.getAnnotation(FindByParentPartialId.class);
            if (findByParentPartialId != null) {
                ElementLocator parentEl = null;
                if (obj instanceof AbstractPrimePageFragment) {
                    parentEl = ((AbstractPrimePageFragment) obj).getElementLocator();
                }

                ElementLocator el = new FindByParentPartialIdElementLocator(driver, parentEl, findByParentPartialId);

                setMember(driver, el, field, obj);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void setMember(WebDriver driver, ElementLocator el, Field field, Object obj) {
        Object value = null;

        if (WebElement.class.isAssignableFrom(field.getType())) {
            value = proxy((Class<T>) field.getType(),
                        MethodDelegation.to(new ElementLocatorInterceptor(el)));

            if (value instanceof AbstractPrimePage) {
                ((AbstractPrimePage) value).setWebDriver(driver);
            }
            if (value instanceof AbstractPrimePageFragment) {
                ((AbstractPrimePageFragment) value).setWebDriver(driver);
                ((AbstractPrimePageFragment) value).setElementLocator(el);
            }

            DefaultElementLocatorFactory delf = new DefaultElementLocatorFactory((SearchContext) value);

            setMembers(driver, delf, value);
        }

        if (List.class.isAssignableFrom(field.getType())) {
            Class<? extends WebElement> genericClass = extractGenericListType(field);
            if (genericClass != null) {
                InvocationHandler handler = new ElementsLocatorInterceptor(el, genericClass);

                value = Proxy.newProxyInstance(
                            ProxyUtils.class.getClassLoader(), new Class[] {List.class}, handler);
            }
        }

        try {
            field.setAccessible(true);
            field.set(obj, value);
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Can not set field in PageFragment!", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends WebElement> extractGenericListType(Field field) {
        // Attempt to discover the generic type of the list
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }

        Type listGenericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        try {
            Class<? extends WebElement> listGenericClass = (Class<? extends WebElement>) Class.forName(listGenericType.getTypeName());
            if (WebElement.class.isAssignableFrom(listGenericClass)) {
                return listGenericClass;
            }
        }
        catch (ClassNotFoundException ex) {
            // do nothing
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> clazz, Implementation interceptor) {
        Class<T> proxyClass = (Class<T>) new ByteBuddy()
                    .subclass(clazz)
                    .implement(WrapsElement.class)
                    .method(ElementMatchers.isDeclaredBy(WebElement.class)
                                .or(ElementMatchers.isDeclaredBy(WrapsElement.class))
                                .or(ElementMatchers.named("hashCode"))
                                .or(ElementMatchers.named("equals")))
                    .intercept(interceptor)
                    .make()
                    .load(PrimeSelenium.class.getClassLoader())
                    .getLoaded();

        try {
            return proxyClass.getDeclaredConstructor().newInstance();
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
