/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import javax.faces.component.html.HtmlGraphicImage;


public abstract class BarcodeBase extends HtmlGraphicImage {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.BarcodeRenderer";

    public enum PropertyKeys {

        type,
        cache,
        format,
        orientation,
        qrErrorCorrection,
        hrp
    }

    public BarcodeBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

    public String getFormat() {
        return (String) getStateHelper().eval(PropertyKeys.format, "svg");
    }

    public void setFormat(String format) {
        getStateHelper().put(PropertyKeys.format, format);
    }

    public int getOrientation() {
        return (Integer) getStateHelper().eval(PropertyKeys.orientation, 0);
    }

    public void setOrientation(int orientation) {
        getStateHelper().put(PropertyKeys.orientation, orientation);
    }

    public String getQrErrorCorrection() {
        return (String) getStateHelper().eval(PropertyKeys.qrErrorCorrection, "L");
    }

    public void setQrErrorCorrection(String qrErrorCorrection) {
        getStateHelper().put(PropertyKeys.qrErrorCorrection, qrErrorCorrection);
    }

    public String getHrp() {
        return (String) getStateHelper().eval(PropertyKeys.hrp, "bottom");
    }

    public void setHrp(String hrp) {
        getStateHelper().put(PropertyKeys.hrp, hrp);
    }

}