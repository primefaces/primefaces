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

public class TagHandlerInfo {

    private final Class<?> tagHandlerClass;
    private String description;
    private String tagName;
    private List<PropertyInfo> properties = new ArrayList<>();

    public TagHandlerInfo(Class<?> tagHandlerClass, String description, String tagName) {
        this.tagHandlerClass = tagHandlerClass;
        this.description = description;
        this.tagName = tagName;
    }

    public Class<?> getTagHandlerClass() {
        return tagHandlerClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "BehaviorInfo{" +
                "tagHandlerClass=" + tagHandlerClass +
                ", description='" + description + '\'' +
                ", tagName='" + tagName + '\'' +
                ", properties=" + properties +
                '}';
    }
}