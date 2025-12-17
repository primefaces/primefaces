/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import jakarta.faces.view.facelets.TagHandler;

public class BehaviorInfo {

    private final Class<?> behaviorClass;
    private String description;
    private String behaviorId;
    private String rendererType;
    private String tagName;
    private Class<? extends TagHandler> handlerClass;
    private List<PropertyInfo> properties = new ArrayList<>();

    public BehaviorInfo(Class<?> behaviorClass, String description, String behaviorId, String rendererType,
                        String tagName, Class<? extends TagHandler> handlerClass) {
        this.behaviorClass = behaviorClass;
        this.description = description;
        this.behaviorId = behaviorId;
        this.rendererType = rendererType;
        this.tagName = tagName;
        this.handlerClass = handlerClass;
    }

    public Class<?> getBehaviorClass() {
        return behaviorClass;
    }

    public String getDescription() {
        return description;
    }

    public String getBehaviorId() {
        return behaviorId;
    }

    public void setBehaviorId(String behaviorId) {
        this.behaviorId = behaviorId;
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

    public Class<? extends TagHandler> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<? extends TagHandler> handlerClass) {
        this.handlerClass = handlerClass;
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
                "behaviorClass=" + behaviorClass +
                ", description='" + description + '\'' +
                ", behaviorId='" + behaviorId + '\'' +
                ", rendererType='" + rendererType + '\'' +
                ", tagName='" + tagName + '\'' +
                ", handlerClass='" + handlerClass + '\'' +
                ", properties=" + properties +
                '}';
    }
}