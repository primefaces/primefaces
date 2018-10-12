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
