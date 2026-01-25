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
package org.primefaces.showcase.view.app;

import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parse the primefaces.taglib.xml and stores a map of all available tags.
 * Used for generating the VDL (View Declaration Language) documentation links.
 */
@Named
@ApplicationScoped
public class TagLibrary {

    private static final String TAGLIB_XML = "META-INF/primefaces.taglib.xml";

    /**
     * Map of lowercase tag names to the real tag name.
     */
    private final Map<String, String> tags = new HashMap<>();

    @PostConstruct
    public void init() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = TagLibrary.class.getClassLoader();
        }

        try (InputStream is = classLoader.getResourceAsStream(TAGLIB_XML)) {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
            docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // Compliant
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            docBuilderFactory.setValidating(false);
            docBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            if (is == null) {
                throw new IOException("Resource not found: " + TAGLIB_XML);
            }
            Document doc = docBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("tag");

            for (int i = 0; i < nodes.getLength(); i++) {
                NodeList children = nodes.item(i).getChildNodes();

                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if ("tag-name".equals(child.getNodeName())) {
                        String tagName = child.getTextContent();
                        tags.put(tagName.toLowerCase(Locale.ROOT), tagName);
                    }
                }
            }
        }
        catch (IOException | SAXException | ParserConfigurationException e) {
            throw new IllegalStateException("Error while reading XML: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the map of tag names.
     *
     * @return a map where the keys are lowercase tag names and the values are the
     *         real tag names.
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Checks if the VDL (View Declaration Language) tag is available for the given
     * documentation link.
     *
     * @param documentationLink the documentation link to check.
     * @return true if the VDL tag is available, false otherwise.
     */
    public boolean isTagVdlAvailable(String documentationLink) {
        if (LangUtils.isEmpty(documentationLink)) {
            return false;
        }
        return getTags().containsKey(parseLink(documentationLink));
    }

    /**
     * Gets the VDL (View Declaration Language) component for the given
     * documentation link.
     *
     * @param documentationLink the documentation link to get the VDL component for.
     * @return the VDL component name.
     */
    public String getTagVdlComponent(String documentationLink) {
        return getTags().get(parseLink(documentationLink));
    }

    /**
     * Parses the documentation link to extract the tag name.
     *
     * @param documentationLink the documentation link to parse.
     * @return the extracted tag name in lowercase.
     */
    private String parseLink(String documentationLink) {
        if (LangUtils.isBlank(documentationLink)) {
            return documentationLink;
        }

        String separator = "/";
        int lastIndex = documentationLink.lastIndexOf(separator);
        if (lastIndex == -1 || lastIndex == documentationLink.length() - separator.length()) {
            return Constants.EMPTY_STRING;
        }

        return documentationLink.substring(lastIndex + separator.length()).toLowerCase(Locale.ROOT);
    }
}