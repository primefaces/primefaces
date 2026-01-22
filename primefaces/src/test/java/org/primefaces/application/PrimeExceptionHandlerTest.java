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
package org.primefaces.application;

import org.primefaces.application.exceptionhandler.PrimeExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.el.ELException;
import jakarta.faces.FacesException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimeExceptionHandlerTest extends PrimeExceptionHandler {

    public PrimeExceptionHandlerTest() {
        super(null);
    }

    @Test
    void evaluateErrorPageWithExplicitType() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new FacesException());
        assertEquals(FacesException.class.getName(), errorPage);
    }

    @Test
    void evaluateErrorPageWithHierarchy() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new ELException());
        assertEquals(RuntimeException.class.getName(), errorPage);
    }

    @Test
    void evaluateErrorPageWithException() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new Exception());
        assertEquals(Exception.class.getName(), errorPage);
    }

    @Test
    void evaluateErrorPageWithDefaultErrorPage() {
        Map<String, String> errorPages = getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, new Throwable());
        assertEquals("", errorPage);
    }

    private Map<String, String> getErrorPages() {
        Map<String, String> errorPages = new HashMap<>();
        errorPages.put(FacesException.class.getName(), FacesException.class.getName());
        errorPages.put(IOException.class.getName(), IOException.class.getName());
        errorPages.put(RuntimeException.class.getName(), RuntimeException.class.getName());
        errorPages.put(Exception.class.getName(), Exception.class.getName());
        errorPages.put(null, "");
        return errorPages;
    }
}
