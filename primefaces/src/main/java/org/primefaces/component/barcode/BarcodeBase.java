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
package org.primefaces.component.barcode;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.html.HtmlGraphicImage;

@FacesComponentBase
public abstract class BarcodeBase extends HtmlGraphicImage implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.BarcodeRenderer";

    public BarcodeBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Type of the barcode.")
    public abstract String getType();

    @Property(defaultValue = "true", description = "Controls browser caching mode of the resources.")
    public abstract boolean isCache();

    @Property(defaultValue = "svg", description = "Format of the generated barcode, valid values are \"svg\" and \"png\".")
    public abstract String getFormat();

    @Property(defaultValue = "0", description = "Orientation in terms of angle. (0, 90, 180, 270)")
    public abstract int getOrientation();

    @Property(defaultValue = "L",
        description = "The QR Code error correction level. L - up to 7% damage. M - up to 15% damage. Q - up to 25% damage. H - up to 30% damage")
    public abstract String getQrErrorCorrection();

    @Property(defaultValue = "bottom", description = "The barcode human readable placement of text either \"none\", \"top\", or \"bottom\".")
    public abstract String getHrp();

    @Property(defaultValue = "2.0", description = "The magnification factor of the barcode.")
    public abstract double getMagnification();

    @Property(defaultValue = "10", description = "The horizontal quiet zone of the barcode in pixels.")
    public abstract int getQuietZoneHorizontal();

    @Property(defaultValue = "1", description = "The vertical quiet zone of the barcode in pixels.")
    public abstract int getQuietZoneVertical();

}
