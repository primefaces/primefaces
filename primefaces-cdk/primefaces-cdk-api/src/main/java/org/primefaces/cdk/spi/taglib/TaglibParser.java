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
package org.primefaces.cdk.spi.taglib;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TaglibParser {

    public Taglib parse(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();

        String namespace = getChildText(root, "namespace");
        String shortName = getChildText(root, "short-name");
        String displayName = getChildText(root, "display-name");

        List<Tag> tags = new ArrayList<>();
        NodeList tagNodes = root.getElementsByTagName("tag");
        for (int i = 0; i < tagNodes.getLength(); i++) {
            Element tagElement = (Element) tagNodes.item(i);
            tags.add(parseTag(tagElement));
        }

        List<Function> functions = new ArrayList<>();
        NodeList functionNodes = root.getElementsByTagName("function");
        for (int i = 0; i < functionNodes.getLength(); i++) {
            Element functionElement = (Element) functionNodes.item(i);
            functions.add(parseFunction(functionElement));
        }

        return new Taglib(namespace, shortName, displayName, tags, functions);
    }

    private Tag parseTag(Element tagElement) {
        TagType type       = null;
        String tagName     = getChildText(tagElement, "tag-name");
        String description = getChildText(tagElement, "description");

        String handlerClass  = null;

        String componentType = null;
        String rendererType  = null;

        String behaviorId  = null;
        String validatorId = null;
        String converterId = null;

        // 1. COMPONENT
        Element component = getDirectChild(tagElement, "component");
        if (component != null) {
            type = TagType.COMPONENT;
            componentType = getChildText(component, "component-type");
            handlerClass  = getChildText(component, "handler-class");
            rendererType  = getChildText(component, "renderer-type");
        }

        // 2. VALIDATOR
        Element validator = getDirectChild(tagElement, "validator");
        if (validator != null) {
            type = TagType.VALIDATOR;
            validatorId  = getChildText(validator, "validator-id");
            handlerClass = getChildText(validator, "handler-class");
        }

        // 3. CONVERTER
        Element converter = getDirectChild(tagElement, "converter");
        if (converter != null) {
            type = TagType.CONVERTER;
            converterId  = getChildText(converter, "converter-id");
            handlerClass = getChildText(converter, "handler-class");
        }

        // 4. BEHAVIOR
        Element behavior = getDirectChild(tagElement, "behavior");
        if (behavior != null) {
            type = TagType.BEHAVIOR;
            behaviorId  = getChildText(behavior, "behavior-id");
            handlerClass = getChildText(behavior, "handler-class");
        }

        // 4. TAG_HANDLER
        if (handlerClass == null) {
            type = TagType.TAG_HANDLER;
            handlerClass = getChildText(tagElement, "handler-class");
        }

        List<TagAttribute> attributes = new ArrayList<>();
        NodeList attrNodes = tagElement.getElementsByTagName("attribute");

        for (int i = 0; i < attrNodes.getLength(); i++) {
            Element attrElement = (Element) attrNodes.item(i);
            attributes.add(parseAttribute(attrElement));
        }

        return new Tag(
                type,
                tagName,
                description,
                handlerClass,
                componentType,
                rendererType,
                behaviorId,
                validatorId,
                converterId,
                attributes
        );
    }

    private Element getDirectChild(Element parent, String tagName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element && tagName.equals(node.getNodeName())) {
                return (Element) node;
            }
        }
        return null;
    }

    private TagAttribute parseAttribute(Element attrElement) {
        String name        = getChildText(attrElement, "name");
        String description = getChildText(attrElement, "description");
        String requiredStr = getChildText(attrElement, "required");
        String type        = getChildText(attrElement, "type");

        boolean required = "true".equalsIgnoreCase(requiredStr);
        return new TagAttribute(name, description, required, type);
    }

    private Function parseFunction(Element functionElement) {
        String name = getChildText(functionElement, "function-name");
        String functionClass = getChildText(functionElement, "function-class");
        String signature = getChildText(functionElement, "function-signature");
        String description = getChildText(functionElement, "description");

        return new Function(name, functionClass, signature, description);
    }

    private String getChildText(Element parent, String childTagName) {
        NodeList children = parent.getElementsByTagName(childTagName);
        if (children.getLength() == 0) {
            return null;
        }
        // Make sure we're getting a direct child, not a deeply nested one
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getParentNode() == parent) {
                return child.getTextContent().strip();
            }
        }
        return null;
    }

}