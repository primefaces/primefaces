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
package org.primefaces.component.imagecropper;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.imageio.ImageIO;
import org.apache.commons.io.input.BoundedInputStream;

import org.primefaces.model.CroppedImage;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageCropperRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(ImageCropperRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ImageCropper cropper = (ImageCropper) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String coordsParam = cropper.getClientId(context) + "_coords";

        if (params.containsKey(coordsParam)) {
            cropper.setSubmittedValue(params.get(coordsParam));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ImageCropper cropper = (ImageCropper) component;

        encodeMarkup(context, cropper);
        encodeScript(context, cropper);
    }

    protected void encodeScript(FacesContext context, ImageCropper cropper) throws IOException {
        String widgetVar = cropper.resolveWidgetVar(context);
        String clientId = cropper.getClientId(context);
        String image = clientId + "_image";
        String select = null;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithComponentLoad("ImageCropper", widgetVar, clientId, clientId + "_image")
                .attr("image", image);

        if (cropper.getMinSize() != null) {
            wb.append(",minSize:[").append(cropper.getMinSize()).append("]");
        }

        if (cropper.getMaxSize() != null) {
            wb.append(",maxSize:[").append(cropper.getMaxSize()).append("]");
        }

        wb.attr("bgColor", cropper.getBackgroundColor(), null)
                .attr("bgOpacity", cropper.getBackgroundOpacity(), 0.6)
                .attr("aspectRatio", cropper.getAspectRatio(), Double.MIN_VALUE)
                .attr("boxWidth", cropper.getBoxWidth(), 0)
                .attr("boxHeight", cropper.getBoxHeight(), 0);

        Object value = cropper.getValue();
        if (value != null) {
            CroppedImage croppedImage = (CroppedImage) value;

            int x = croppedImage.getLeft();
            int y = croppedImage.getTop();
            int x2 = x + croppedImage.getWidth();
            int y2 = y + croppedImage.getHeight();

            select = "[" + x + "," + y + "," + x2 + "," + y2 + "]";
        }
        else if (cropper.getInitialCoords() != null) {
            select = "[" + cropper.getInitialCoords() + "]";
        }

        wb.append(",setSelect:").append(select);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, ImageCropper cropper) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = cropper.getClientId(context);
        String coordsHolderId = clientId + "_coords";

        writer.startElement("div", cropper);
        writer.writeAttribute("id", clientId, null);

        renderImage(context, cropper, clientId);

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", coordsHolderId, null);
        writer.writeAttribute("name", coordsHolderId, null);
        writer.endElement("input");

        writer.endElement("div");
    }

    private void renderImage(FacesContext context, ImageCropper cropper, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String alt = cropper.getAlt() == null ? "" : cropper.getAlt();

        writer.startElement("img", null);
        writer.writeAttribute("id", clientId + "_image", null);
        writer.writeAttribute("alt", alt, null);
        writer.writeAttribute("src", getResourceURL(context, cropper.getImage()), null);
        writer.endElement("img");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        String coords = (String) submittedValue;

        if (isValueBlank(coords)) {
            return null;
        }

        String[] cropCoords = coords.split("_");

        int x = (int) Double.parseDouble(cropCoords[0]);
        int y = (int) Double.parseDouble(cropCoords[1]);
        int w = (int) Double.parseDouble(cropCoords[2]);
        int h = (int) Double.parseDouble(cropCoords[3]);

        if (w <= 0 || h <= 0) {
            return null;
        }

        ImageCropper cropper = (ImageCropper) component;
        Resource resource = getImageResource(context, cropper);
        InputStream inputStream = null;
        String imagePath = cropper.getImage();
        String contentType = null;

        try {

            if (resource != null && !"RES_NOT_FOUND".equals(resource.toString())) {
                inputStream = resource.getInputStream();
                contentType = resource.getContentType();
            }
            else {

                boolean isExternal = imagePath.startsWith("http");

                if (isExternal) {
                    URL url = new URL(imagePath);
                    URLConnection urlConnection = url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    contentType = urlConnection.getContentType();
                }
                else {
                    ExternalContext externalContext = context.getExternalContext();
                    File file = new File(externalContext.getRealPath("") + imagePath);
                    inputStream = new FileInputStream(file);
                }
            }

            // wrap input stream by BoundedInputStream to prevent uncontrolled resource consumption (#3286)
            if (cropper.getSizeLimit() != null) {
                inputStream = new BoundedInputStream(inputStream, cropper.getSizeLimit());
            }

            BufferedImage outputImage = ImageIO.read(inputStream);

            // avoid java.awt.image.RasterFormatException: (x + width) is outside of Raster
            // see #1208
            if (x + w > outputImage.getWidth()) {
                w = outputImage.getWidth() - x;
            }
            if (y + h > outputImage.getHeight()) {
                h = outputImage.getHeight() - y;
            }

            BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
            ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
            String format = guessImageFormat(contentType, imagePath);
            ImageIO.write(cropped, format, croppedOutImage);

            return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);
        }
        catch (IOException e) {
            throw new ConverterException(e);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
    }

    /**
     * Attempt to obtain the resource from the server by parsing the valueExpression of the image attribute. Returns null
     * if the valueExpression is not of the form #{resource['path/to/resource']} or #{resource['library:name']}. Otherwise
     * returns the value obtained by ResourceHandler.createResource().
     */
    private Resource getImageResource(FacesContext facesContext, ImageCropper imageCropper) {

        Resource resource = null;
        ValueExpression imageValueExpression = imageCropper.getValueExpression(ImageCropper.PropertyKeys.image.toString());

        if (imageValueExpression != null) {
            String imageValueExpressionString = imageValueExpression.getExpressionString();

            if (imageValueExpressionString.matches("^[#][{]resource\\['[^']+'\\][}]$")) {

                imageValueExpressionString = imageValueExpressionString.replaceFirst("^[#][{]resource\\['", "");
                imageValueExpressionString = imageValueExpressionString.replaceFirst("'\\][}]$", "");
                String resourceLibrary = null;
                String resourceName;
                String[] resourceInfo = imageValueExpressionString.split(":");

                if (resourceInfo.length == 2) {
                    resourceLibrary = resourceInfo[0];
                    resourceName = resourceInfo[1];
                }
                else {
                    resourceName = resourceInfo[0];
                }

                if (resourceName != null) {
                    Application application = facesContext.getApplication();
                    ResourceHandler resourceHandler = application.getResourceHandler();

                    if (resourceLibrary != null) {
                        resource = resourceHandler.createResource(resourceName, resourceLibrary);
                    }
                    else {
                        resource = resourceHandler.createResource(resourceName);
                    }
                }
            }
        }

        return resource;
    }

    /**
     * Attempt to obtain the image format used to write the image from the contentType or the image's file extension.
     */
    private String guessImageFormat(String contentType, String imagePath) throws IOException {

        String format = "png";

        if (contentType == null) {
            contentType = URLConnection.guessContentTypeFromName(imagePath);
        }

        if (contentType != null) {
            format = contentType.replaceFirst("^image/([^;]+)[;]?.*$", "$1");
        }
        else {
            int queryStringIndex = imagePath.indexOf('?');

            if (queryStringIndex != -1) {
                imagePath = imagePath.substring(0, queryStringIndex);
            }

            String[] pathTokens = imagePath.split("\\.");

            if (pathTokens.length > 1) {
                format = pathTokens[pathTokens.length - 1];
            }
        }

        return format;
    }
}
