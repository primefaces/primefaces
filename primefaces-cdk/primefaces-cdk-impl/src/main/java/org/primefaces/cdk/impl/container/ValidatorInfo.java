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
package org.primefaces.cdk.impl.container;

import org.primefaces.cdk.api.Property;

import java.util.HashMap;
import java.util.Map;

import jakarta.faces.view.facelets.TagHandler;

public class ValidatorInfo {

    private Class<?> validatorClass;
    private String description;
    private String validatorId;
    private String tagName;
    private Class<? extends TagHandler> handlerClass;
    private Map<String, Property> properties = new HashMap<>();

    public ValidatorInfo(Class<?> validatorClass, String description, String validatorId,
                         String tagName, Class<? extends TagHandler> handlerClass) {
        this.validatorClass = validatorClass;
        this.description = description;
        this.validatorId = validatorId;
        this.tagName = tagName;
        this.handlerClass = handlerClass;
    }

    public Class<?> getValidatorClass() {
        return validatorClass;
    }

    public void setValidatorClass(Class<?> validatorClass) {
        this.validatorClass = validatorClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Class<? extends TagHandler> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<? extends TagHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}