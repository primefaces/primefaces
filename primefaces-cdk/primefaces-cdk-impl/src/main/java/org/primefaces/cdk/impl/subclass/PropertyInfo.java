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
package org.primefaces.cdk.impl.subclass;

import org.primefaces.cdk.api.Property;

import javax.lang.model.element.ExecutableElement;

public class PropertyInfo {

    private final String name;
    private final String type;
    private final ExecutableElement getterElement;
    private final ExecutableElement setterElement;
    private final boolean generateSetter;
    private final String description;
    private final String defaultValue;
    private final String implicitDefaultValue;
    private final boolean required;
    private final boolean callSuper;
    private final boolean hide;

    PropertyInfo(String name, String type, ExecutableElement getterElement,
                 ExecutableElement setterElement, Property annotation) {
        this.name = name;
        this.type = type;
        this.getterElement = getterElement;
        this.setterElement = setterElement;
        this.description = annotation.description();
        this.defaultValue = annotation.defaultValue();
        this.implicitDefaultValue = annotation.implicitDefaultValue();
        this.required = annotation.required();
        this.callSuper = annotation.callSuper();
        this.generateSetter = true;
        this.hide = annotation.hide();
    }

    PropertyInfo(String name, String type, ExecutableElement getterElement,
                 ExecutableElement setterElement, String description, String defaultValue, String implicitDefaultValue, boolean required) {
        this.name = name;
        this.type = type;
        this.getterElement = getterElement;
        this.setterElement = setterElement;
        this.description = description;
        this.defaultValue = defaultValue;
        this.implicitDefaultValue = implicitDefaultValue;
        this.required = required;
        this.callSuper = false;
        // in this case, this is an extracted property from a parent component without @Property annotation
        // -> skip generation
        this.generateSetter = false;
        this.hide = false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ExecutableElement getGetterElement() {
        return getterElement;
    }

    public ExecutableElement getSetterElement() {
        return setterElement;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isGenerateSetter() {
        return generateSetter;
    }

    public String getImplicitDefaultValue() {
        return implicitDefaultValue;
    }

    public boolean isCallSuper() {
        return callSuper;
    }

    public boolean isHide() {
        return hide;
    }
}