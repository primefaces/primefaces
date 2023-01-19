/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.avatar;

import javax.faces.component.UIComponentBase;

public abstract class AvatarBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AvatarRenderer";

    public enum PropertyKeys {
        label,
        icon,
        size,
        shape,
        dynamicColor,
        title,
        style,
        styleClass,
        gravatar,
        gravatarConfig,
        saturation,
        lightness,
        alpha
    }

    public AvatarBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, null);
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public String getShape() {
        return (String) getStateHelper().eval(PropertyKeys.shape, "square");
    }

    public void setShape(String shape) {
        getStateHelper().put(PropertyKeys.shape, shape);
    }

    public boolean isDynamicColor() {
        return (Boolean) getStateHelper().eval(AvatarBase.PropertyKeys.dynamicColor, false);
    }

    public void setDynamicColor(boolean dynamicColor) {
        getStateHelper().put(AvatarBase.PropertyKeys.dynamicColor, dynamicColor);
    }

    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, getLabel());
    }

    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
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

    public String getGravatar() {
        return (String) getStateHelper().eval(AvatarBase.PropertyKeys.gravatar, null);
    }

    public void setGravatar(String gravatar) {
        getStateHelper().put(AvatarBase.PropertyKeys.gravatar, gravatar);
    }

    public String getGravatarConfig() {
        return (String) getStateHelper().eval(AvatarBase.PropertyKeys.gravatarConfig, null);
    }

    public void setGravatarConfig(String gravatarConfig) {
        getStateHelper().put(AvatarBase.PropertyKeys.gravatarConfig, gravatarConfig);
    }

    public Integer getSaturation() {
        return (Integer) getStateHelper().eval(PropertyKeys.saturation, 100);
    }

    public void setSaturation(Integer size) {
        getStateHelper().put(PropertyKeys.saturation, size);
    }

    public Integer getLightness() {
        return (Integer) getStateHelper().eval(PropertyKeys.lightness, 40);
    }

    public void setLightness(Integer size) {
        getStateHelper().put(PropertyKeys.lightness, size);
    }

    public Integer getAlpha() {
        return (Integer) getStateHelper().eval(PropertyKeys.alpha, 100);
    }

    public void setAlpha(Integer size) {
        getStateHelper().put(PropertyKeys.alpha, size);
    }

}
