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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.primefaces.application.resource.BaseDynamicContentHandler;
import org.primefaces.context.RequestContext;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.StringEncrypter;
import org.w3c.dom.DocumentFragment;

public class BarcodeHandler extends BaseDynamicContentHandler {
    
    private final static Logger logger = Logger.getLogger(BarcodeHandler.class.getName());
    
    private final Map<String,BarcodeGenerator> generators;
    
    public BarcodeHandler() {
        generators = new HashMap<String,BarcodeGenerator>();
        generators.put("int2of5", new Int2of5Generator());
        generators.put("codabar", new CodabarGenerator());
        generators.put("code39", new Code39Generator());
        generators.put("code128", new Code128Generator());
        generators.put("ean8", new EAN8Generator());
        generators.put("ean13", new EAN13Generator());
        generators.put("upca", new UPCAGenerator());
        generators.put("upce", new UPCEGenerator());
        generators.put("postnet", new PostnetGenerator());
        generators.put("pdf417", new PDF417Generator());
        generators.put("datamatrix", new DataMatrixGenerator());
    }
    
    public void handle(FacesContext context) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ExternalContext externalContext = context.getExternalContext();
        String encryptedValue = (String) params.get(Constants.DYNAMIC_CONTENT_PARAM);
        
        if(encryptedValue != null) {
            try {
                BarcodeGenerator generator = generators.get(params.get("gen"));
                String format = params.get("fmt");
                int orientation = Integer.parseInt(params.get("ori"));
                boolean cache = Boolean.valueOf(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));
                StringEncrypter strEn = RequestContext.getCurrentInstance().getEncrypter();
                String value = strEn.decrypt(encryptedValue);
                
                if(AgentUtils.isLessThanIE(context, 9)) {
                    format = "png";
                }
                
                OutputStream out = externalContext.getResponseOutputStream();
            
                handleCache(externalContext, cache);
                
                if(format.equals("png")) {
                    externalContext.setResponseContentType("image/png");
                    
                    BitmapCanvasProvider bitmapCanvasProvider = new BitmapCanvasProvider(out, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, orientation);
                    generator.generate(bitmapCanvasProvider, value);
                    bitmapCanvasProvider.finish();
                }
                else if(format.equals("svg")) {
                    externalContext.setResponseContentType("image/svg+xml");
                    
                    SVGCanvasProvider svgCanvasProvider = new SVGCanvasProvider(false, orientation);
                    generator.generate(svgCanvasProvider, value);
                    DocumentFragment frag = svgCanvasProvider.getDOMFragment();

                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(frag);
                    Result res = new javax.xml.transform.stream.StreamResult(out);
                    trans.transform(src, res);
                }

                externalContext.setResponseStatus(200);
            }
            catch(Exception e) {
                logger.log(Level.SEVERE, "Error in streaming barcode resource. {0}", new Object[]{e.getMessage()});
            }
            finally {
                externalContext.responseFlushBuffer();
                context.responseComplete();
            }            
        }
    }
}
