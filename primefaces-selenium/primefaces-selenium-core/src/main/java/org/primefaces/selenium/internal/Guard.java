/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.selenium.internal;

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.spi.WebDriverProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Guard {

    private Guard() {

    }

    public static <T> T custom(T target, int timeout, ExpectedCondition... expectedConditions) {
        return custom(target, 0, timeout, expectedConditions);
    }

    public static <T> T custom(T target, int delay, int timeout, ExpectedCondition... expectedConditions) {
        OnloadScripts.execute();

        return proxy(target, (Object p, Method method, Object[] args) -> {
            try {
                Object result = method.invoke(target, args);

                // if JS uses setTimeout on the client we want to wait before trying to capture AJAX call
                if (delay > 0) {
                    Thread.sleep(delay);
                }

                WebDriver driver = WebDriverProvider.get();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout), Duration.ofMillis(100));
                wait.until(ExpectedConditions.and(expectedConditions));

                return result;
            }
            catch (TimeoutException e) {
                throw new TimeoutException("Timeout while waiting for custom guard!", e);
            }
        });
    }

    public static <T> T http(T target) {
        OnloadScripts.execute();

        return proxy(target, (Object p, Method method, Object[] args) -> {
            try {
                PrimeSelenium.executeScript("pfselenium.submitting = true;");

                Object result = method.invoke(target, args);

                WebDriver driver = WebDriverProvider.get();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigProvider.getInstance().getTimeoutHttp()), Duration.ofMillis(100));
                wait.until((ExpectedCondition<Boolean>) d -> {
                    if (PrimeExpectedConditions.validationFailed().apply(driver)) {
                        return true;
                    }
                    return ExpectedConditions.and(
                            PrimeExpectedConditions.documentLoaded(),
                            PrimeExpectedConditions.notNavigating(),
                            PrimeExpectedConditions.notSubmitting()).apply(driver);
                });

                return result;
            }
            catch (TimeoutException e) {
                throw new TimeoutException("Timeout while waiting for document ready!", e);
            }
        });
    }

    public static <T> T ajax(String script, Object... args) {
        OnloadScripts.execute();

        WebDriver driver = WebDriverProvider.get();
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        try {
            executor.executeScript("pfselenium.xhr = 'somethingJustNotNull';");

            T result = (T) executor.executeScript(script, args);

            waitUntilAjaxCompletes(driver);

            return result;
        }
        catch (TimeoutException e) {
            throw new TimeoutException("Timeout while waiting for AJAX complete!", e);
        }
    }

    public static <T> T ajax(T target) {
        return ajax(target, 0);
    }

    public static <T> T ajax(T target, int delay) {
        OnloadScripts.execute();

        return proxy(target, (Object p, Method method, Object[] args) -> {
            WebDriver driver = WebDriverProvider.get();
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            try {
                executor.executeScript("pfselenium.xhr = 'somethingJustNotNull';");

                // System.out.println("Guard#ajax; ajaxDebugInfo before methode.invoke: " + getAjaxDebugInfo(executor));
                Object result = method.invoke(target, args);

                // if JS uses setTimeout on the client we want to wait before trying to capture AJAX call
                if (delay > 0) {
                    Thread.sleep(delay);
                }

                waitUntilAjaxCompletes(driver);
                // System.out.println("Guard#ajax; ajaxDebugInfo after methode.invoke and wait: " + getAjaxDebugInfo(executor));

                return result;
            }
            catch (TimeoutException e) {
                throw new TimeoutException("Timeout while waiting for AJAX complete!", e);
            }
            catch (InterruptedException e) {
                throw new TimeoutException("AJAX Guard delay was interrupted!", e);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof WebDriverException) {
                    throw e.getCause();
                }
                else {
                    throw e;
                }
            }
        });
    }

    private static String getAjaxDebugInfo(JavascriptExecutor executor) {
        return "document.readyState=" + executor.executeScript("return document.readyState;") + ", " +
                    "!window.jQuery=" + executor.executeScript("return !window.jQuery;") + ", " +
                    "jQuery.active=" + executor.executeScript("return jQuery.active;") + ", " +
                    "!window.PrimeFaces=" + executor.executeScript("return !window.PrimeFaces;") + ", " +
                    "PrimeFaces.ajax.Queue.isEmpty()=" + executor.executeScript("return PrimeFaces.ajax.Queue.isEmpty();") + ", " +
                    "PrimeFaces.animationActive=" + executor.executeScript("return PrimeFaces.animationActive;") + ", " +
                    "!window.pfselenium=" + executor.executeScript("return !window.pfselenium;") + ", " +
                    "pfselenium.xhr=" + executor.executeScript("return pfselenium.xhr;") + ", " +
                    "pfselenium.anyXhrStarted=" + executor.executeScript("return pfselenium.anyXhrStarted;") + ", " +
                    "pfselenium.navigating=" + executor.executeScript("return pfselenium.navigating;");
    }

    private static void waitUntilAjaxCompletes(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigProvider.getInstance().getTimeoutAjax()), Duration.ofMillis(50));
        wait.until(d -> {
            return (Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return (window.pfselenium && pfselenium.validationFailed === true) || (document.readyState === 'complete'"
                                    + " && (!window.jQuery || jQuery.active == 0)"
                                    + " && (!window.PrimeFaces || (PrimeFaces.ajax.Queue.isEmpty() && PrimeFaces.animationActive === false))"
                                    + " && (!window.pfselenium || (pfselenium.xhr == null && pfselenium.navigating === false)));");
        });
    }

    private static <T> T proxy(T target, InvocationHandler handler) {
        Class<?> classToProxy = target.getClass();
        List<Class> interfacesToImplement = new ArrayList<>();
        ElementMatcher.Junction methods = ElementMatchers.isPublic();

        // class is not proxyable - lets try to implement interfaces
        if (Modifier.isPrivate(classToProxy.getModifiers()) || Modifier.isFinal(classToProxy.getModifiers())) {
            interfacesToImplement = Arrays.asList(classToProxy.getInterfaces());
            classToProxy = Object.class;
            methods = null;

            for (Class c : interfacesToImplement) {
                if (methods == null) {
                    methods = ElementMatchers.isDeclaredBy(c);
                }
                else {
                    methods = methods.or(ElementMatchers.isDeclaredBy(c));
                }
            }
        }

        Class<T> proxyClass = new ByteBuddy()
                .subclass(classToProxy)
                .implement(interfacesToImplement)
                .method(methods)
                .intercept(InvocationHandlerAdapter.of(handler))
                .make()
                .load(target.getClass().getClassLoader())
                .getLoaded();

        try {
            try {
                // try default constructor first
                Constructor<T> defaultCtor = proxyClass.getDeclaredConstructor();
                return defaultCtor.newInstance();
            }
            catch (NoSuchMethodException ex) {
                // ignore
            }

            try {
                // try WebElement constructor, used e.g. by Select
                if (target instanceof WrapsElement) {
                    Constructor<T> ctor = proxyClass.getDeclaredConstructor(WebElement.class);
                    return ctor.newInstance(((WrapsElement) target).getWrappedElement());
                }
            }
            catch (NoSuchMethodException ex) {
                // ignore
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Could not proxy class "
                + classToProxy.getName()
                + " because of missing constructor (default or WebElement)");
    }
}
