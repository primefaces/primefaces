/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.application.resource;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;
import java.io.IOException;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public class QRCodeHandler extends BaseDynamicContentHandler {

    @Override
    public void handle(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        ExternalContext externalContext = context.getExternalContext();
        String sessionKey = params.get(Constants.DYNAMIC_CONTENT_PARAM);
        Map<String, Object> session = externalContext.getSessionMap();
        Map<String, String> barcodeMapping = (Map) session.get(Constants.BARCODE_MAPPING);
        String value = barcodeMapping.get(sessionKey);

        if (value != null) {
            boolean cache = Boolean.parseBoolean(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));

            QrCode qrCode = QrCode.encodeText(value, getErrorCorrection(params.get("qrec")));
            if ("png".equals(params.get("fmt"))) {
                externalContext.setResponseContentType("image/png");
                ImageIO.write(qrCode.toImage(12, 0), "png", externalContext.getResponseOutputStream());
            }
            else {
                externalContext.setResponseContentType("image/svg+xml");
                externalContext.getResponseOutputWriter().write(qrCode.toSvgString(0));
            }
            handleCache(externalContext, cache);
            externalContext.setResponseStatus(200);
            externalContext.responseFlushBuffer();
            context.responseComplete();
        }
    }

    protected Ecc getErrorCorrection(final String value) {
        switch (LangUtils.isNotBlank(value) ? value : Constants.EMPTY_STRING) {
            case "M":
                return Ecc.MEDIUM;
            case "Q":
                return Ecc.QUARTILE;
            case "H":
                return Ecc.HIGH;
            default:
                return Ecc.LOW;
        }
    }

}
