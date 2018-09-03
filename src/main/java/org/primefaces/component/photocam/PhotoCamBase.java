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
package org.primefaces.component.photocam;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class PhotoCamBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PhotoCamRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        process,
        update,
        listener,
        width,
        height,
        photoWidth,
        photoHeight,
        format,
        jpegQuality,
        forceFlash,
        autoStart
    }

    public PhotoCamBase() {
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

    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public javax.el.MethodExpression getListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.listener, null);
    }

    public void setListener(javax.el.MethodExpression listener) {
        getStateHelper().put(PropertyKeys.listener, listener);
    }

    public int getWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.width, 320);
    }

    public void setWidth(int width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public int getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, 240);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public int getPhotoWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.photoWidth, 320);
    }

    public void setPhotoWidth(int photoWidth) {
        getStateHelper().put(PropertyKeys.photoWidth, photoWidth);
    }

    public int getPhotoHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.photoHeight, 240);
    }

    public void setPhotoHeight(int photoHeight) {
        getStateHelper().put(PropertyKeys.photoHeight, photoHeight);
    }

    public String getFormat() {
        return (String) getStateHelper().eval(PropertyKeys.format, null);
    }

    public void setFormat(String format) {
        getStateHelper().put(PropertyKeys.format, format);
    }

    public int getJpegQuality() {
        return (Integer) getStateHelper().eval(PropertyKeys.jpegQuality, 90);
    }

    public void setJpegQuality(int jpegQuality) {
        getStateHelper().put(PropertyKeys.jpegQuality, jpegQuality);
    }

    public boolean isForceFlash() {
        return (Boolean) getStateHelper().eval(PropertyKeys.forceFlash, false);
    }

    public void setForceFlash(boolean forceFlash) {
        getStateHelper().put(PropertyKeys.forceFlash, forceFlash);
    }

    public boolean isAutoStart() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoStart, true);
    }

    public void setAutoStart(boolean autoStart) {
        getStateHelper().put(PropertyKeys.autoStart, autoStart);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}