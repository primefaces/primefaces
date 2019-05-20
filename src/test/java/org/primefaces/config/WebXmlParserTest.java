package org.primefaces.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.xpath.XPathFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class WebXmlParserTest {
    private static String XPATH_FACTORY_SYSTEM_PROPERTY = XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI;

    @Parameterized.Parameter
    public String pathToWebXml;

    private FacesContext context;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"web-namespaces.xml"},
                {"web-empty-error-code.xml"},
                {"web-no-namespace.xml"},
        });
    }

    private void configureXpathFactory(Class<? extends XPathFactory> factoryClass) {
        System.setProperty(XPATH_FACTORY_SYSTEM_PROPERTY, factoryClass.getName());
        assertEquals("The XpathFactory implementations must match: " + factoryClass.getName(), factoryClass, XPathFactory.newInstance().getClass());
    }

    @Before
    public void mockContext() throws Exception {
        context = mock(FacesContext.class);
        ExternalContext extContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(extContext);
        when(extContext.getResource(anyString())).thenReturn(this.getClass().getResource(pathToWebXml));
    }

    @After
    public void tearDown() {
        System.clearProperty(XPATH_FACTORY_SYSTEM_PROPERTY);
    }


    private void assertErrorPages(Map<String, String> errorPages) {
        String viewExpiredClassName = "javax.faces.application.ViewExpiredException";
        assertEquals("Parsing the web.xml should return 2 errors pages", 2, errorPages.size());
        assertEquals("The key null should return the default location", "/default", errorPages.get(null));
        assertEquals("The key " + viewExpiredClassName + "  should return the viewExpired location", "/viewExpired", errorPages.get(viewExpiredClassName));
    }

    @Test
    public void testSaxon() {
        configureXpathFactory(net.sf.saxon.xpath.XPathFactoryImpl.class);
        Map<String, String> errorPages = WebXmlParser.getErrorPages(context);
        assertErrorPages(errorPages);
    }

    @Test
    public void testInternalJaxp() {
        configureXpathFactory(com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl.class);
        Map<String, String> errorPages = WebXmlParser.getErrorPages(context);
        assertErrorPages(errorPages);
    }
}
