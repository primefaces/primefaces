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
package org.primefaces.component.imagecropper;

import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.ResourceUtils;
import org.primefaces.util.WidgetBuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.Resource;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BoundedInputStream;

@FacesRenderer(rendererType = ImageCropper.DEFAULT_RENDERER, componentFamily = ImageCropper.COMPONENT_FAMILY)
public class ImageCropperRenderer extends CoreRenderer<ImageCropper> {

    private static final Pattern IMAGE_TYPE_PATTERN = Pattern.compile("^image/([^;]+);?.*$");

    @Override
    public void decode(FacesContext context, ImageCropper component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String coordsParam = component.getClientId(context) + "_coords";

        if (params.containsKey(coordsParam)) {
            component.setSubmittedValue(params.get(coordsParam));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, ImageCropper component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, ImageCropper component) throws IOException {
        String widgetVar = component.resolveWidgetVar(context);
        String clientId = component.getClientId(context);
        String image = clientId + "_image";
        String select = null;

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithComponentLoad("ImageCropper", widgetVar, clientId, image)
                .attr("image", image)
                .attr("viewMode", component.getViewMode(), 0)
                .attr("aspectRatio", component.getAspectRatio(), Double.MIN_VALUE)
                .attr("responsive", component.isResponsive(), true)
                .attr("zoomOnTouch", component.isZoomOnTouch(), true)
                .attr("zoomOnWheel", component.isZoomOnWheel(), true)
                .attr("guides", component.isGuides(), true);

        if (component.getMinSize() != null) {
            wb.append(",minSize:[").append(component.getMinSize()).append("]");
        }

        if (component.getMaxSize() != null) {
            wb.append(",maxSize:[").append(component.getMaxSize()).append("]");
        }

        Object value = component.getValue();
        if (value != null) {
            CroppedImage croppedImage = (CroppedImage) value;

            int x = croppedImage.getLeft();
            int y = croppedImage.getTop();
            int x2 = x + croppedImage.getWidth();
            int y2 = y + croppedImage.getHeight();

            select = "[" + x + "," + y + "," + x2 + "," + y2 + "]";
        }
        else if (LangUtils.isNotBlank(component.getInitialCoords())) {
            select = "[" + component.getInitialCoords() + "]";
        }

        if (select != null) {
            wb.append(",initialCoords:").append(select);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, ImageCropper component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String coordsHolderId = clientId + "_coords";

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);

        String style = Constants.EMPTY_STRING;
        if (component.getBoxHeight() > 0) {
            style = style + " max-height:" + component.getBoxHeight() + "px; ";
        }

        if (component.getBoxWidth() > 0) {
            style = style + " max-width:" + component.getBoxWidth() + "px; ";
        }
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, null);
        }

        renderImage(context, component, clientId);

        renderHiddenInput(context, coordsHolderId, null, false);

        writer.endElement("div");
    }

    private void renderImage(FacesContext context, ImageCropper component, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String alt = component.getAlt() == null ? Constants.EMPTY_STRING : component.getAlt();

        writer.startElement("img", null);
        writer.writeAttribute("id", clientId + "_image", null);
        writer.writeAttribute("alt", alt, null);

        String src = DynamicContentSrcBuilder.build(context, component,
                component.getValueExpression(ImageCropperBase.PropertyKeys.image.name()),
                new Lazy<>(component::getImage), component.isCache(), true);
        writer.writeAttribute("src", src, null);

        writer.writeAttribute("height", "auto", null);
        writer.writeAttribute("width", "100%", null);
        writer.writeAttribute("style", "max-width: 100%;", null);
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
        StreamedContent stream = null;
        try {
            stream = createStreamedContentForCropping(context, cropper);
        }
        catch (IOException e) {
            throw new ConverterException(e);
        }

        try (InputStream inputStream = stream.getStream().get()) {
            BufferedImage outputImage = ImageIO.read(inputStream);

            // avoid java.awt.image.RasterFormatException: (x + width) is outside of Raster
            // see #1208
            if (x + w > outputImage.getWidth()) {
                w = outputImage.getWidth() - x;
            }
            if (y + h > outputImage.getHeight()) {
                h = outputImage.getHeight() - y;
            }

            String originalFileName = stream.getName();
            BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
            ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
            String format = guessImageFormat(stream.getContentType(), originalFileName);
            ImageIO.write(cropped, format, croppedOutImage);
            return new CroppedImage(originalFileName, croppedOutImage.toByteArray(), x, y, w, h);
        }
        catch (IOException e) {
            throw new ConverterException(e);
        }
    }

    /**
     * Attempt to obtain the image format used to write the image from the contentType or the image's file extension.
     */
    private String guessImageFormat(String contentType, String imagePath) {

        String format = "png";

        if (contentType == null) {
            contentType = URLConnection.guessContentTypeFromName(imagePath);
        }

        if (contentType != null) {
            format = IMAGE_TYPE_PATTERN.matcher(contentType).replaceFirst("$1");
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

    protected StreamedContent createStreamedContentForCropping(FacesContext context, ImageCropper component) throws IOException {
        InputStream inputStream = null;
        String contentType = null;
        String imagePath = null;
        String originalFileName = null;

        // try to evaluate as Resource object, otherwise we would need to handle the Resource#resourcePath which would be more awkward
        ValueExpression imageVE = component.getValueExpression(ImageCropperBase.PropertyKeys.image.toString());
        Resource resource = ResourceUtils.evaluateResourceExpression(context, imageVE);
        if (resource != null) {
            inputStream = resource.getInputStream();
            contentType = resource.getContentType();
            originalFileName = resource.getResourceName();
        }
        else {
            Object imageObject = component.getImage();
            if (imageObject instanceof String) {
                imagePath = (String) imageObject;
                originalFileName = imagePath;

                boolean isExternal = imagePath.startsWith("http");

                if (isExternal) {
                    URL url;
                    try {
                        url = new URI(imagePath).toURL();
                    }
                    catch (URISyntaxException e) {
                        throw new FacesException(e);
                    }
                    URLConnection urlConnection = url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    contentType = urlConnection.getContentType();
                }
                else {
                    ExternalContext externalContext = context.getExternalContext();
                    String webRoot = externalContext.getRealPath(Constants.EMPTY_STRING);
                    String fileSeparator = Constants.EMPTY_STRING;
                    if (!(webRoot.endsWith("\\") || webRoot.endsWith("/"))
                            && !(imagePath.startsWith("\\") || imagePath.startsWith("/"))) {
                        fileSeparator = "/";
                    }

                    // GitHub #3268 OWASP Path Traversal
                    String path = webRoot + fileSeparator + imagePath;
                    File file = new File(path);
                    FileUploadUtils.requireValidFilePath(file.getCanonicalPath(), false);
                    inputStream = Files.newInputStream(file.toPath());
                }
            }
            else if (imageObject instanceof StreamedContent) {
                StreamedContent streamedContentTmp = (StreamedContent) imageObject;
                inputStream = streamedContentTmp.getStream().get();
                contentType = streamedContentTmp.getContentType();
                originalFileName = streamedContentTmp.getName();
            }
            else {
                throw new IllegalArgumentException("ImageCropper 'image' must be either a String relative path or a StreamedObject.");
            }
        }

        // wrap input stream by BoundedInputStream to prevent uncontrolled resource consumption (#3286)
        if (component.getSizeLimit() != null) {
            inputStream = new BoundedInputStream(inputStream, component.getSizeLimit());
        }

        originalFileName = FilenameUtils.getName(originalFileName);
        if (LangUtils.isBlank(originalFileName)) {
            // most likely stream.getName was not set by user
            String format = guessImageFormat(contentType, Constants.EMPTY_STRING);
            originalFileName = "unknown." + format;
        }

        InputStream finalInputStream = inputStream;
        return DefaultStreamedContent.builder()
                .name(originalFileName)
                .stream(() -> finalInputStream)
                .contentType(contentType)
                .build();
    }
}
