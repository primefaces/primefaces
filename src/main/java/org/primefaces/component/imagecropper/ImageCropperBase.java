/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.imagecropper;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ImageCropperBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ImageCropperRenderer";

    public enum PropertyKeys {

        widgetVar,
        image,
        alt,
        aspectRatio,
        minSize,
        maxSize,
        backgroundColor,
        backgroundOpacity,
        initialCoords,
        boxWidth,
        boxHeight,
        sizeLimit
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

    public String getImage() {
        return (String) getStateHelper().eval(PropertyKeys.image, null);
    }

    public void setImage(String image) {
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

    public String getBackgroundColor() {
        return (String) getStateHelper().eval(PropertyKeys.backgroundColor, null);
    }

    public void setBackgroundColor(String backgroundColor) {
        getStateHelper().put(PropertyKeys.backgroundColor, backgroundColor);
    }

    public double getBackgroundOpacity() {
        return (Double) getStateHelper().eval(PropertyKeys.backgroundOpacity, 0.6);
    }

    public void setBackgroundOpacity(double backgroundOpacity) {
        getStateHelper().put(PropertyKeys.backgroundOpacity, backgroundOpacity);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}