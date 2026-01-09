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
package org.primefaces.cdk.impl.taglib;

import java.util.ArrayList;
import java.util.List;

public class ComponentInfo {

    private final Class<?> componentClass;
    private String description;
    private String componentType;
    private String rendererType;
    private String tagName;
    private List<PropertyInfo> properties;

    public ComponentInfo(Class<?> componentClass, String description, String componentType, String rendererType,
                         String tagName) {
        this.componentClass = componentClass;
        this.description = description;
        this.componentType = componentType;
        this.rendererType = rendererType;
        this.tagName = tagName;
        this.properties = new ArrayList<>();
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public String getDescription() {
        return description;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getRendererType() {
        return rendererType;
    }

    public void setRendererType(String rendererType) {
        this.rendererType = rendererType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<PropertyInfo> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyInfo> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ComponentInfo{" +
                "componentClass='" + componentClass + '\'' +
                ", description='" + description + '\'' +
                ", componentType='" + componentType + '\'' +
                ", rendererType='" + rendererType + '\'' +
                ", tagName='" + tagName + '\'' +
                ", properties=" + properties.size() +
                '}';
    }

}