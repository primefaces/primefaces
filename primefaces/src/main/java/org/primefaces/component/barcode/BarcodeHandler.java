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

import org.primefaces.application.resource.BaseDynamicContentHandler;
import org.primefaces.util.Constants;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import uk.org.okapibarcode.backend.Codabar;
import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.Code2Of5;
import uk.org.okapibarcode.backend.Code3Of9;
import uk.org.okapibarcode.backend.DataMatrix;
import uk.org.okapibarcode.backend.Ean;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.backend.Pdf417;
import uk.org.okapibarcode.backend.Postnet;
import uk.org.okapibarcode.backend.QrCode;
import uk.org.okapibarcode.backend.QrCode.EccLevel;
import uk.org.okapibarcode.backend.Symbol;
import uk.org.okapibarcode.backend.Upc;
import uk.org.okapibarcode.backend.Upc.Mode;
import uk.org.okapibarcode.graphics.Color;
import uk.org.okapibarcode.output.Java2DRenderer;
import uk.org.okapibarcode.output.SvgRenderer;
import uk.org.okapibarcode.util.Integers;

public class BarcodeHandler extends BaseDynamicContentHandler {

    private static final Logger LOGGER = Logger.getLogger(BarcodeHandler.class.getName());

    private static final Map<String, Supplier<Symbol>> GENERATORS = Map.ofEntries(
            Map.entry("codabar", Codabar::new),
            Map.entry("code128", Code128::new),
            Map.entry("code39", Code3Of9::new),
            Map.entry("datamatrix", DataMatrix::new),
            Map.entry("ean13", () -> new Ean(Ean.Mode.EAN13)),
            Map.entry("ean8", () -> new Ean(Ean.Mode.EAN8)),
            Map.entry("int2of5", Code2Of5::new),
            Map.entry("pdf417", Pdf417::new),
            Map.entry("postnet", Postnet::new),
            Map.entry("qr", QrCode::new),
            Map.entry("upca", () -> new Upc(Mode.UPCA)),
            Map.entry("upce", () -> new Upc(Mode.UPCE))
    );

    @Override
    public void handle(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        ExternalContext externalContext = context.getExternalContext();
        String sessionKey = params.get(Constants.DYNAMIC_CONTENT_PARAM);
        Map<String, Object> session = externalContext.getSessionMap();
        Map<String, String> barcodeMapping = (Map<String, String>) session.get(Constants.BARCODE_MAPPING);
        if (barcodeMapping == null) {
            return;
        }

        String value = barcodeMapping.get(sessionKey);
        if (value == null) {
            return;
        }

        try {
            String type = params.get("gen").toLowerCase(Locale.ROOT);
            Symbol generator = GENERATORS.get(type).get();
            String format = params.get("fmt").toLowerCase(Locale.ROOT);
            String hrp = params.get("hrp");
            int rotation = Integers.normalizeRotation(Integer.parseInt(params.get("ori")));
            double magnification = Double.parseDouble(params.get("mag"));
            boolean cache = Boolean.parseBoolean(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));
            int quietZoneHorizontal = Integer.parseInt(params.get("mh"));
            int quietZoneVertical = Integer.parseInt(params.get("mv"));

            // #13548 be lenient with EAN13 and EAN8 for check digits
            if (generator instanceof Ean) {
                Ean ean = (Ean) generator;
                if (ean.getMode() == Ean.Mode.EAN13 && value.length() == 13) {
                    value = value.substring(0, 12);
                }
                else if (ean.getMode() == Ean.Mode.EAN8 && value.length() == 8) {
                    value = value.substring(0, 7);
                }
            }

            // #13548 be lenient with UPC-A for check digits
            if (generator instanceof Upc) {
                Upc upc = (Upc) generator;
                if (upc.getMode() == Mode.UPCA && value.length() == 12) {
                    value = value.substring(0, 11);
                }
            }

            generator.setHumanReadableLocation(HumanReadableLocation.valueOf(hrp.toUpperCase(Locale.ROOT)));
            generator.setContent(value);
            generator.setQuietZoneHorizontal(quietZoneHorizontal);
            generator.setQuietZoneVertical(quietZoneVertical);

            if (generator instanceof QrCode) {
                ((QrCode) generator).setPreferredEccLevel(EccLevel.valueOf(params.get("qrec")));
            }

            // Make Okapi backwards compatible with Barcode4J legacy values, Okapi is clockwise and Barcode4J is counter-clockwise
            switch (rotation) {
                case 90:
                    rotation = 270;
                    break;
                case 270:
                    rotation = 90;
                    break;
            }

            OutputStream out = externalContext.getResponseOutputStream();

            handleCache(externalContext, cache);

            if ("png".equals(format)) {
                externalContext.setResponseContentType("image/png");

                int width = generator.getWidth();
                int height = generator.getHeight();

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D g2d = image.createGraphics();
                Java2DRenderer renderer = new Java2DRenderer(g2d, magnification, Color.WHITE, Color.BLACK, rotation);
                renderer.render(generator);
                g2d.dispose();
                ImageIO.write(image, "png", out);
            }
            else if ("svg".equals(format)) {
                externalContext.setResponseContentType("image/svg+xml");

                SvgRenderer renderer = new SvgRenderer(out, magnification, Color.WHITE, Color.BLACK, true, rotation);
                renderer.render(generator);
            }

            externalContext.setResponseStatus(200);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in streaming barcode resource. {0}", new Object[]{e.getMessage()});
        }
        finally {
            context.responseComplete();
        }
    }
}
