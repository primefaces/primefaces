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
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.primefaces.application.resource.BaseDynamicContentHandler;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.Constants;
import org.w3c.dom.DocumentFragment;

public class BarcodeHandler extends BaseDynamicContentHandler {

    private static final Logger LOGGER = Logger.getLogger(BarcodeHandler.class.getName());

    private final Map<String, BarcodeGenerator> generators;

    public BarcodeHandler() {
        generators = new HashMap<>();
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

    @Override
    public void handle(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        ExternalContext externalContext = context.getExternalContext();
        String sessionKey = params.get(Constants.DYNAMIC_CONTENT_PARAM);
        Map<String, Object> session = externalContext.getSessionMap();
        Map<String, String> barcodeMapping = (Map<String, String>) session.get(Constants.BARCODE_MAPPING);
        String value = barcodeMapping.get(sessionKey);

        if (value != null) {
            try {
                BarcodeGenerator generator = generators.get(params.get("gen"));
                String format = params.get("fmt");
                String hrp = params.get("hrp");
                int orientation = Integer.parseInt(params.get("ori"));
                boolean cache = Boolean.parseBoolean(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));

                generator.getBarcodeBean().setMsgPosition(HumanReadablePlacement.byName(hrp));

                if (AgentUtils.isLessThanIE(context, 9)) {
                    format = "png";
                }

                OutputStream out = externalContext.getResponseOutputStream();

                handleCache(externalContext, cache);

                if (format.equals("png")) {
                    externalContext.setResponseContentType("image/png");

                    BitmapCanvasProvider bitmapCanvasProvider = new BitmapCanvasProvider(
                            out, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, orientation);
                    generator.generate(bitmapCanvasProvider, value);
                    bitmapCanvasProvider.finish();
                }
                else if (format.equals("svg")) {
                    externalContext.setResponseContentType("image/svg+xml");

                    SVGCanvasProvider svgCanvasProvider = new SVGCanvasProvider(false, orientation);
                    generator.generate(svgCanvasProvider, value);
                    DocumentFragment frag = svgCanvasProvider.getDOMFragment();

                    TransformerFactory factory = TransformerFactory.newInstance();
                    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(frag);
                    Result res = new javax.xml.transform.stream.StreamResult(out);
                    trans.transform(src, res);
                }

                externalContext.setResponseStatus(200);
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in streaming barcode resource. {0}", new Object[]{e.getMessage()});
            }
            finally {
                externalContext.responseFlushBuffer();
                context.responseComplete();
            }
        }
    }
}
