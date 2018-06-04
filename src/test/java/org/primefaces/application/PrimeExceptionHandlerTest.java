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
package org.primefaces.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.el.ELException;
import javax.faces.FacesException;
import org.junit.Assert;
import org.junit.Test;
import org.primefaces.application.exceptionhandler.PrimeExceptionHandler;

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
        Map<String, String> errorPages = new HashMap<String, String>();
        errorPages.put(FacesException.class.getName(), FacesException.class.getName());
        errorPages.put(IOException.class.getName(), IOException.class.getName());
        errorPages.put(RuntimeException.class.getName(), RuntimeException.class.getName());
        errorPages.put(Exception.class.getName(), Exception.class.getName());
        errorPages.put(null, "");
        return errorPages;
    }
}
