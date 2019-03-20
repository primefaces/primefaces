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
package org.primefaces.application;

import org.junit.Assert;
import org.junit.Test;
import org.primefaces.application.exceptionhandler.PrimeExceptionHandler;

import javax.el.ELException;
import javax.faces.FacesException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PrimeExceptionHandlerTest extends PrimeExceptionHandler {

    public PrimeExceptionHandlerTest()
    {
        super(null);
    }

    @Test
    public void testEvaluateErrorPageWithExplicitType() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new FacesException());
        Assert.assertEquals(FacesException.class.getName(), errorPage);
    }

    @Test
    public void testEvaluateErrorPageWithHierarchy() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new ELException());
        Assert.assertEquals(RuntimeException.class.getName(), errorPage);
    }

    @Test
    public void testEvaluateErrorPageWithException() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new Exception());
        Assert.assertEquals(Exception.class.getName(), errorPage);
    }

    @Test
    public void testEvaluateErrorPageWithDefaultErrorPage() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new Throwable());
        Assert.assertEquals("", errorPage);
    }

    private Map<String, String> getErrorPages()
    {
        Map<String, String> errorPages = new HashMap<>();
        errorPages.put(FacesException.class.getName(), FacesException.class.getName());
        errorPages.put(IOException.class.getName(), IOException.class.getName());
        errorPages.put(RuntimeException.class.getName(), RuntimeException.class.getName());
        errorPages.put(Exception.class.getName(), Exception.class.getName());
        errorPages.put(null, "");
        return errorPages;
    }
}
