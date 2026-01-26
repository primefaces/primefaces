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
package org.primefaces.component.photocam;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UIInput;

@FacesComponentBase
public abstract class PhotoCamBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PhotoCamRenderer";

    public PhotoCamBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Component(s) to process in partial request.")
    public abstract String getProcess();

    @Property(description = "Component(s) to update in partial request.")
    public abstract String getUpdate();

    @Property(description = "Method expression to invoke when a photo is captured.")
    public abstract MethodExpression getListener();

    @Property(defaultValue = "320", description = "Width of the camera viewfinder.")
    public abstract int getWidth();

    @Property(defaultValue = "240", description = "Height of the camera viewfinder.")
    public abstract int getHeight();

    @Property(defaultValue = "320", description = "Width of the captured photo.")
    public abstract int getPhotoWidth();

    @Property(defaultValue = "240", description = "Height of the captured photo.")
    public abstract int getPhotoHeight();

    @Property(description = "Format of the captured photo. Valid values are 'jpeg' and 'png'.")
    public abstract String getFormat();

    @Property(defaultValue = "90", description = "JPEG quality when format is 'jpeg'. Valid range is 0-100.")
    public abstract int getJpegQuality();

    @Property(defaultValue = "true", description = "When true, camera starts automatically.")
    public abstract boolean isAutoStart();

    @Property(description = "Device ID of the camera to use.")
    public abstract String getDevice();

    @Property(description = "Client side callback to execute when camera error occurs.")
    public abstract String getOnCameraError();

}