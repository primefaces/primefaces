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
        sizeLimit;
    }

    public ImageCropperBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getImage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.image, null);
    }

    public void setImage(java.lang.String _image) {
        getStateHelper().put(PropertyKeys.image, _image);
    }

    public java.lang.String getAlt() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.alt, null);
    }

    public void setAlt(java.lang.String _alt) {
        getStateHelper().put(PropertyKeys.alt, _alt);
    }

    public double getAspectRatio() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.aspectRatio, java.lang.Double.MIN_VALUE);
    }

    public void setAspectRatio(double _aspectRatio) {
        getStateHelper().put(PropertyKeys.aspectRatio, _aspectRatio);
    }

    public java.lang.String getMinSize() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.minSize, null);
    }

    public void setMinSize(java.lang.String _minSize) {
        getStateHelper().put(PropertyKeys.minSize, _minSize);
    }

    public java.lang.String getMaxSize() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.maxSize, null);
    }

    public void setMaxSize(java.lang.String _maxSize) {
        getStateHelper().put(PropertyKeys.maxSize, _maxSize);
    }

    public java.lang.String getBackgroundColor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.backgroundColor, null);
    }

    public void setBackgroundColor(java.lang.String _backgroundColor) {
        getStateHelper().put(PropertyKeys.backgroundColor, _backgroundColor);
    }

    public double getBackgroundOpacity() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.backgroundOpacity, 0.6);
    }

    public void setBackgroundOpacity(double _backgroundOpacity) {
        getStateHelper().put(PropertyKeys.backgroundOpacity, _backgroundOpacity);
    }

    public java.lang.String getInitialCoords() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.initialCoords, null);
    }

    public void setInitialCoords(java.lang.String _initialCoords) {
        getStateHelper().put(PropertyKeys.initialCoords, _initialCoords);
    }

    public int getBoxWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.boxWidth, 0);
    }

    public void setBoxWidth(int _boxWidth) {
        getStateHelper().put(PropertyKeys.boxWidth, _boxWidth);
    }

    public int getBoxHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.boxHeight, 0);
    }

    public void setBoxHeight(int _boxHeight) {
        getStateHelper().put(PropertyKeys.boxHeight, _boxHeight);
    }

    public java.lang.Long getSizeLimit() {
        return (java.lang.Long) getStateHelper().eval(PropertyKeys.sizeLimit, 10485760L);
    }

    public void setSizeLimit(java.lang.Long _sizeLimit) {
        getStateHelper().put(PropertyKeys.sizeLimit, _sizeLimit);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}