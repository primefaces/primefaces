/**
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
package org.primefaces.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.primefaces.util.LangUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WebXmlParser {

    private static final Logger LOGGER = Logger.getLogger(WebXmlParser.class.getName());

    private static final String ERROR_PAGE_EXCEPTION_TYPE_EXPRESSION = "*[local-name() = 'error-page']/*[local-name() = 'exception-type']";
    private static final String LOCATION_EXPRESSION = "*[local-name() = 'location']";
    private static final String ERROR_CODE_500_LOCATION_EXPRESSION =
            "*[local-name() = 'error-page'][*[local-name() = 'error-code'] = '500'] / *[local-name() = 'location']";
    private static final String ERROR_PAGE_NO_CODE_AND_TYPE_EXPRESSION = "*[local-name() = 'error-page'][not(*[local-name() = 'error-code']) and not" +
            "(*[local-name() = 'exception-type'])]/*[local-name() = 'location']";
    private WebXmlParser() {
    }

    public static Map<String, String> getErrorPages(FacesContext context) {

        Map<String, String> webXmlErrorPages = getWebXmlErrorPages(context);
        Map<String, String> webFragmentXmlsErrorPages = getWebFragmentXmlsErrorPages(context);

        Map<String, String> errorPages = webXmlErrorPages;
        if (errorPages == null) {
            errorPages = webFragmentXmlsErrorPages;
        }
        else if (webFragmentXmlsErrorPages != null) {
            for (Map.Entry<String, String> entry : webFragmentXmlsErrorPages.entrySet()) {
                if (!errorPages.containsKey(entry.getKey())) {
                    errorPages.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (errorPages == null) {
            errorPages = new HashMap<>();
        }

        return errorPages;
    }

    private static Map<String, String> getWebXmlErrorPages(FacesContext context) {
        try {
            Document webXml = toDocument(context.getExternalContext().getResource("/WEB-INF/web.xml"));
            if (webXml != null) {
                return parseErrorPages(webXml.getDocumentElement());
            }
        }
        catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Could not load or parse web.xml", e);
        }

        return null;
    }

    private static Map<String, String> getWebFragmentXmlsErrorPages(FacesContext context) {
        Map<String, String> webFragmentXmlsErrorPages = null;

        try {
            Enumeration<URL> webFragments = LangUtils.getContextClassLoader().getResources("META-INF/web-fragment.xml");

            while (webFragments.hasMoreElements()) {
                try {
                    URL url = webFragments.nextElement();
                    Document webFragmentXml = toDocument(url);
                    if (webFragmentXml != null) {
                        if (webFragmentXmlsErrorPages == null) {
                            webFragmentXmlsErrorPages = parseErrorPages(webFragmentXml.getDocumentElement());
                        }
                        else {
                            Map<String, String> temp = parseErrorPages(webFragmentXml.getDocumentElement());
                            for (Map.Entry<String, String> entry : temp.entrySet()) {
                                if (!webFragmentXmlsErrorPages.containsKey(entry.getKey())) {
                                    webFragmentXmlsErrorPages.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                }
                catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Could not load or parse web-fragment.xml", e);
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not get web-fragment.xml's from ClassLoader", e);
        }

        return webFragmentXmlsErrorPages;
    }

    private static Document toDocument(URL url) throws Exception {
        // web.xml is optional
        if (url == null) {
            return null;
        }

        try (InputStream is = url.openStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            factory.setExpandEntityReferences(false);

            try {
                factory.setFeature("http://xml.org/sax/features/namespaces", false);
                factory.setFeature("http://xml.org/sax/features/validation", false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
            catch (Throwable e) {
                LOGGER.warning("DocumentBuilderFactory#setFeature not implemented. Skipping...");
            }

            boolean absolute = false;
            try {
                absolute = url.toURI().isAbsolute();
            }
            catch (URISyntaxException e) {
                // noop
            }

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            if (absolute) {
                InputSource source = new InputSource(url.toExternalForm());
                source.setByteStream(is);
                document = builder.parse(source);
            }
            else {
                document = builder.parse(is);
            }

            return document;
        }
    }

    private static Map<String, String> parseErrorPages(Element webXml) throws Exception {

        Map<String, String> errorPages = new HashMap<>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList exceptionTypes = (NodeList) xpath.compile(ERROR_PAGE_EXCEPTION_TYPE_EXPRESSION).evaluate(webXml, XPathConstants.NODESET);

        for (int i = 0; i < exceptionTypes.getLength(); i++) {
            Node node = exceptionTypes.item(i);

            String exceptionType = node.getTextContent().trim();
            String key = Throwable.class.getName().equals(exceptionType) ? null : exceptionType;

            String location = xpath.compile(LOCATION_EXPRESSION).evaluate(node.getParentNode()).trim();

            if (!errorPages.containsKey(key)) {
                errorPages.put(key, location);
            }
        }

        if (!errorPages.containsKey(null)) {
            String defaultLocation = xpath.compile(ERROR_CODE_500_LOCATION_EXPRESSION).evaluate(webXml).trim();

            if (LangUtils.isValueBlank(defaultLocation)) {
                defaultLocation = xpath.compile(ERROR_PAGE_NO_CODE_AND_TYPE_EXPRESSION).evaluate(webXml).trim();
            }

            if (!LangUtils.isValueBlank(defaultLocation)) {
                errorPages.put(null, defaultLocation);
            }
        }

        return errorPages;
    }

}
