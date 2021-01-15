/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.skeleton;

import javax.faces.component.UIComponentBase;

public abstract class SkeletonBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SkeletonRenderer";

    public enum PropertyKeys {
        shape,
        size,
        width,
        height,
        borderRadius,
        animation,
        style,
        styleClass
    }

    public SkeletonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getShape() {
        return (String) getStateHelper().eval(PropertyKeys.shape, "rectangle");
    }

    public void setShape(String shape) {
        getStateHelper().put(PropertyKeys.shape, shape);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, null);
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, "100%");
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, "1rem");
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public String getBorderRadius() {
        return (String) getStateHelper().eval(PropertyKeys.borderRadius, null);
    }

    public void setBorderRadius(String borderRadius) {
        getStateHelper().put(PropertyKeys.borderRadius, borderRadius);
    }

    public String getAnimation() {
        return (String) getStateHelper().eval(PropertyKeys.animation, "wave");
    }

    public void setAnimation(String animation) {
        getStateHelper().put(PropertyKeys.animation, animation);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }
}
