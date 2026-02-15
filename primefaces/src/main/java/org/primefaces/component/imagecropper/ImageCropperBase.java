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
package org.primefaces.component.imagecropper;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeUIInput;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class ImageCropperBase extends PrimeUIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ImageCropperRenderer";

    public ImageCropperBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Binary data to stream or context relative path.", required = true)
    public abstract Object getImage();

    @Property(description = "Alternate text of the image.")
    public abstract String getAlt();

    @Property(description = "Aspect ratio of the cropper area.")
    public abstract double getAspectRatio();

    @Property(description = "Minimum size of the cropper area (width,height).")
    public abstract String getMinSize();

    @Property(description = "Maximum size of the cropper area (width,height).")
    public abstract String getMaxSize();

    @Property(description = "Initial coordinates of the cropper area (x, y, width,height).")
    public abstract String getInitialCoords();

    @Property(defaultValue = "0", description = "Maximum box width of the cropping area.")
    public abstract int getBoxWidth();

    @Property(defaultValue = "0", description = "Maximum box height of the cropping area.")
    public abstract int getBoxHeight();

    @Property(defaultValue = "10485760L", description = "Maximum number of bytes the image.")
    public abstract Long getSizeLimit();

    @Property(defaultValue = "true", description = "Re-render the cropper when resizing the window.")
    public abstract boolean isResponsive();

    @Property(defaultValue = "true", description = "Show the dashed lines in the crop box.")
    public abstract boolean isGuides();

    @Property(defaultValue = "1", description = "Define the view mode of the cropper. " +
        "ViewMode to 0, the crop box can extend outside the canvas, while a value of 1, 2 or 3 will restrict the crop box to the size of the canvas. " +
        "ViewMode of 2 or 3 will additionally restrict the canvas to the container. " +
        "Note that if the proportions of the canvas and the container are the same, there is no difference between 2 and 3.")
    public abstract int getViewMode();

    @Property(defaultValue = "true", description = "Controls browser caching mode of the resource.")
    public abstract boolean isCache();

    @Property(defaultValue = "true", description = "Enable to zoom the image by dragging touch.")
    public abstract boolean isZoomOnTouch();

    @Property(defaultValue = "true", description = "Enable to zoom the image by wheeling mouse.")
    public abstract boolean isZoomOnWheel();

}