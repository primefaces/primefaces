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

import javax.lang.model.element.ExecutableElement;

public class PropertyInfo {

    private String name;
    private Property annotation;
    private ExecutableElement getterElement;
    private ExecutableElement setterElement;
    private boolean generateGetter;
    private boolean generateSetter;
    private String typeName;

    public PropertyInfo(String name, Property annotation, ExecutableElement getterElement, ExecutableElement setterElement, String typeName) {
        this.name = name;
        this.annotation = annotation;
        this.getterElement = getterElement;
        this.setterElement = setterElement;
        this.generateGetter = true;
        this.generateSetter = true;
        this.typeName = typeName;
    }

    public PropertyInfo(String name, Property annotation) {
        this.name = name;
        this.annotation = annotation;
        this.getterElement = null;
        this.setterElement = null;
        // in this case, this is an extracted property from a parent component without @Property annotation
        // -> skip generation
        this.generateGetter = false;
        this.generateSetter = false;
        this.typeName = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Property annotation) {
        this.annotation = annotation;
    }

    public ExecutableElement getGetterElement() {
        return getterElement;
    }

    public void setGetterElement(ExecutableElement getterElement) {
        this.getterElement = getterElement;
    }

    public ExecutableElement getSetterElement() {
        return setterElement;
    }

    public void setSetterElement(ExecutableElement setterElement) {
        this.setterElement = setterElement;
    }

    public boolean isGenerateGetter() {
        return generateGetter;
    }

    public void setGenerateGetter(boolean generateGetter) {
        this.generateGetter = generateGetter;
    }

    public boolean isGenerateSetter() {
        return generateSetter;
    }

    public void setGenerateSetter(boolean generateSetter) {
        this.generateSetter = generateSetter;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}