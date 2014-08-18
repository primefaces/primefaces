/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.application.resource.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.primefaces.application.resource.BaseDynamicContentHandler;
import org.primefaces.application.resource.StreamedContentHandler;
import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;
import org.primefaces.util.StringEncrypter;

public class BarcodeHandler extends BaseDynamicContentHandler {
    
    private final static Logger logger = Logger.getLogger(BarcodeHandler.class.getName());
    
    final static Map<String,BarcodeGenerator> GENERATORS;
    
    static {
        GENERATORS = new HashMap<String,BarcodeGenerator>();
        GENERATORS.put("code39", new Code39Generator());
        GENERATORS.put("int2of5", new Int2of5Generator());
    }
    
    public void handle(FacesContext context) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String encryptedValue = (String) params.get(Constants.DYNAMIC_CONTENT_PARAM);

        if(encryptedValue != null) {
            BarcodeGenerator generator = GENERATORS.get(params.get("gen"));
            boolean cache = Boolean.valueOf(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));
            StringEncrypter strEn = RequestContext.getCurrentInstance().getEncrypter();
            String value = strEn.decrypt(encryptedValue);                
            ExternalContext externalContext = context.getExternalContext();

            externalContext.setResponseStatus(200);
            externalContext.setResponseContentType("image/png");
            
            handleCache(externalContext, cache);
            
            OutputStream out = externalContext.getResponseOutputStream();
            
            try {
                generator.generate(out, value);
            }
            catch(IOException e) {
                logger.log(Level.SEVERE, "Error in streaming barcode resource. {0}", new Object[]{e.getMessage()});
            }
            finally {
                externalContext.responseFlushBuffer();
                context.responseComplete();
            }            
        }
    }
}
