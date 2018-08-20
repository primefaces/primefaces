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
package org.primefaces.component.barcode;

import javax.faces.component.html.HtmlGraphicImage;


abstract class BarcodeBase extends HtmlGraphicImage {

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