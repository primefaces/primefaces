/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.imagecropper;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;

public abstract class ImageCropperBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ImageCropperRenderer";

    public enum PropertyKeys {

        widgetVar,
        image,
        alt,
        aspectRatio,
        minSize,
        maxSize,
        initialCoords,
        boxWidth,
        boxHeight,
        sizeLimit,
        responsive,
        guides,
        viewMode,
        cache
    }

    public ImageCropperBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public Object getImage() {
        return getStateHelper().eval(PropertyKeys.image, null);
    }

    public void setImage(Object image) {
        getStateHelper().put(PropertyKeys.image, image);
    }

    public String getAlt() {
        return (String) getStateHelper().eval(PropertyKeys.alt, null);
    }

    public void setAlt(String alt) {
        getStateHelper().put(PropertyKeys.alt, alt);
    }

    public double getAspectRatio() {
        return (Double) getStateHelper().eval(PropertyKeys.aspectRatio, Double.MIN_VALUE);
    }

    public void setAspectRatio(double aspectRatio) {
        getStateHelper().put(PropertyKeys.aspectRatio, aspectRatio);
    }

    public String getMinSize() {
        return (String) getStateHelper().eval(PropertyKeys.minSize, null);
    }

    public void setMinSize(String minSize) {
        getStateHelper().put(PropertyKeys.minSize, minSize);
    }

    public String getMaxSize() {
        return (String) getStateHelper().eval(PropertyKeys.maxSize, null);
    }

    public void setMaxSize(String maxSize) {
        getStateHelper().put(PropertyKeys.maxSize, maxSize);
    }

    public String getInitialCoords() {
        return (String) getStateHelper().eval(PropertyKeys.initialCoords, null);
    }

    public void setInitialCoords(String initialCoords) {
        getStateHelper().put(PropertyKeys.initialCoords, initialCoords);
    }

    public int getBoxWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.boxWidth, 0);
    }

    public void setBoxWidth(int boxWidth) {
        getStateHelper().put(PropertyKeys.boxWidth, boxWidth);
    }

    public int getBoxHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.boxHeight, 0);
    }

    public void setBoxHeight(int boxHeight) {
        getStateHelper().put(PropertyKeys.boxHeight, boxHeight);
    }

    public Long getSizeLimit() {
        return (Long) getStateHelper().eval(PropertyKeys.sizeLimit, 10485760L);
    }

    public void setSizeLimit(Long sizeLimit) {
        getStateHelper().put(PropertyKeys.sizeLimit, sizeLimit);
    }

    public boolean isResponsive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.responsive, true);
    }

    public void setResponsive(boolean responsive) {
        getStateHelper().put(PropertyKeys.responsive, responsive);
    }

    public boolean isGuides() {
        return (Boolean) getStateHelper().eval(PropertyKeys.guides, true);
    }

    public void setGuides(boolean guides) {
        getStateHelper().put(PropertyKeys.guides, guides);
    }

    public void setViewMode(int viewMode) {
        getStateHelper().put(PropertyKeys.viewMode, viewMode);
    }

    public int getViewMode() {
        return (Integer) getStateHelper().eval(PropertyKeys.viewMode, 1);
    }

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

}