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
package org.primefaces.application.resource;

import java.io.IOException;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.primefaces.util.Constants;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
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

            externalContext.setResponseStatus(200);
            externalContext.setResponseContentType("image/png");

            handleCache(externalContext, cache);

            ErrorCorrectionLevel ecl = ErrorCorrectionLevel.L;
            String errorCorrection = params.get("qrec");
            if (!LangUtils.isValueBlank(errorCorrection)) {
                ecl = ErrorCorrectionLevel.valueOf(errorCorrection);
            }

            QRCode.from(value).to(ImageType.PNG).withErrorCorrection(ecl).withCharset("UTF-8")
                        .writeTo(externalContext.getResponseOutputStream());

            externalContext.responseFlushBuffer();
            context.responseComplete();
        }
    }

}
