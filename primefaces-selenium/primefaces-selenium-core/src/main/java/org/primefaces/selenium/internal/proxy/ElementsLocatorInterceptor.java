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
package org.primefaces.selenium.internal.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.primefaces.selenium.spi.PrimePageFragmentFactory;

public class ElementsLocatorInterceptor implements InvocationHandler {

    private final ElementLocator locator;
    private final Class<? extends WebElement> genericClass;

    public ElementsLocatorInterceptor(ElementLocator locator, Class<? extends WebElement> genericClass) {
        this.locator = locator;
        this.genericClass = genericClass;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        List<WebElement> elements = locator.findElements();

        if (genericClass != WebElement.class) {
            ArrayList<WebElement> fragments = new ArrayList<>();

            for (int i = 0; i < elements.size(); i++) {
                WebElement element = elements.get(i);
                WebElement fragment = PrimePageFragmentFactory.create(genericClass, element, new IndexedElementLocator(locator, i));

                fragments.add(fragment);
            }

            elements = fragments;
        }

        try {
            return method.invoke(elements, objects);
        }
        catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }

    static class IndexedElementLocator implements ElementLocator {

        private final ElementLocator locator;
        private final int i;

        public IndexedElementLocator(ElementLocator locator, int i) {
            this.locator = locator;
            this.i = i;
        }

        @Override
        public WebElement findElement() {
            return locator.findElements().get(i);
        }

        @Override
        public List<WebElement> findElements() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
