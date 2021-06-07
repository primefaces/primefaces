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
package org.primefaces.selenium.internal.junit;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.spi.PrimePageFactory;
import org.primefaces.selenium.spi.WebDriverProvider;

public class PageInjectionExtension implements ParameterResolver, TestInstancePostProcessor {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {

        return AbstractPrimePage.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {

        WebDriver driver = WebDriverProvider.get(true);

        AbstractPrimePage page = PrimePageFactory.create((Class<? extends AbstractPrimePage>) parameterContext.getParameter().getType(), driver);

        PrimeSelenium.goTo(page);

        return page;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getAnnotation(Inject.class) != null
                        && AbstractPrimePage.class.isAssignableFrom(field.getType())
                        && field.get(testInstance) == null) {

                WebDriver driver = WebDriverProvider.get(true);

                AbstractPrimePage page = PrimePageFactory.create((Class<? extends AbstractPrimePage>) field.getType(), driver);

                field.set(testInstance, page);
            }
        }
    }

}
