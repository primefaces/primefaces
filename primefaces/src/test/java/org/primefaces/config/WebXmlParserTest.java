/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.stream.Stream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class WebXmlParserTest {

    private static final String XPATH_FACTORY_SYSTEM_PROPERTY =
            XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI;

    private FacesContext context;
    private ExternalContext extContext;

    private static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("web-namespaces.xml"),
                Arguments.of("web-empty-error-code.xml"),
                Arguments.of("web-no-namespace.xml"));
    }

    private void configureXpathFactory(Class<? extends XPathFactory> factoryClass) {
        System.setProperty(XPATH_FACTORY_SYSTEM_PROPERTY, factoryClass.getName());
        assertEquals(factoryClass, XPathFactory.newInstance().getClass(), "The XpathFactory implementations must match: " + factoryClass.getName());
    }

    @BeforeEach
    public void mockContext() throws Exception {
        context = mock(FacesContext.class);
        extContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(extContext);

    }

    @AfterEach
    public void tearDown() {
        System.clearProperty(XPATH_FACTORY_SYSTEM_PROPERTY);
    }

    private void assertErrorPages(Map<String, String> errorPages) {
        String viewExpiredClassName = "javax.faces.application.ViewExpiredException";
        assertEquals(2, errorPages.size(), "Parsing the web.xml should return 2 errors pages");
        assertEquals("/default", errorPages.get(null), "The key null should return the default location");
        assertEquals("/viewExpired", errorPages.get(viewExpiredClassName), "The key " + viewExpiredClassName + "  should return the viewExpired location");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSaxon(String pathToWebXml) throws MalformedURLException {
        when(extContext.getResource(anyString())).thenReturn(this.getClass().getResource(pathToWebXml));
        configureXpathFactory(net.sf.saxon.xpath.XPathFactoryImpl.class);
        Map<String, String> errorPages = WebXmlParser.getErrorPages(context);
        assertErrorPages(errorPages);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInternalJaxp(String pathToWebXml) throws MalformedURLException {
        when(extContext.getResource(anyString())).thenReturn(this.getClass().getResource(pathToWebXml));
        System.clearProperty(XPATH_FACTORY_SYSTEM_PROPERTY); //back to system-default
        Map<String, String> errorPages = WebXmlParser.getErrorPages(context);
        assertErrorPages(errorPages);

        //ensure JDK´s own XPathFactory is used
        XPathFactory xPathFactory = XPathFactory.newInstance();
        assertEquals("com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", xPathFactory.getClass().getName());
    }
}
