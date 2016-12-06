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
package org.primefaces.component.imagecropper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
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

import org.primefaces.model.CroppedImage;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageCropperRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
        ImageCropper cropper = (ImageCropper) component;
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String coordsParam = cropper.getClientId(context) + "_coords";

        if(params.containsKey(coordsParam)) {
            cropper.setSubmittedValue(params.get(coordsParam));
        }
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ImageCropper cropper = (ImageCropper) component;

        encodeMarkup(context, cropper);
		encodeScript(context, cropper);
	}

	protected void encodeScript(FacesContext context, ImageCropper cropper) throws IOException{
		String widgetVar = cropper.resolveWidgetVar();
		String clientId = cropper.getClientId(context);
        String image = clientId + "_image";
        String select = null;
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithComponentLoad("ImageCropper", widgetVar, clientId, clientId + "_image")
            .attr("image", image);
        
        if(cropper.getMinSize() != null) 
            wb.append(",minSize:[").append(cropper.getMinSize()).append("]");
        
        if(cropper.getMaxSize() != null) 
            wb.append(",maxSize:[").append(cropper.getMaxSize()).append("]");
        
        wb.attr("bgColor", cropper.getBackgroundColor(), null)
            .attr("bgOpacity", cropper.getBackgroundOpacity(), 0.6)
            .attr("aspectRatio", cropper.getAspectRatio(), Double.MIN_VALUE)
            .attr("boxWidth", cropper.getBoxWidth(), 0)
            .attr("boxHeight", cropper.getBoxHeight(), 0);
        
        Object value = cropper.getValue();
		if(value != null) {
            CroppedImage croppedImage = (CroppedImage) value;
            
            int x = croppedImage.getLeft();
            int y = croppedImage.getTop();
            int x2 = x + croppedImage.getWidth();
            int y2 = y + croppedImage.getHeight();

            select = "[" + x +  "," + y + "," + x2 + "," + y2 + "]";
		} 
        else if(cropper.getInitialCoords() != null) {
            select = "[" + cropper.getInitialCoords() + "]";
        }
        
        wb.append(",setSelect:").append(select);

        wb.finish();
	}
	
	protected void encodeMarkup(FacesContext context, ImageCropper cropper) throws IOException{
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

	private void renderImage(FacesContext context, ImageCropper cropper, String clientId) throws IOException{
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
        
        if(isValueBlank(coords)) {
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
        InputStream inputStream;
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

            BufferedImage outputImage = ImageIO.read(inputStream);
            inputStream.close();
            BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
            ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
            String format = guessImageFormat(contentType, imagePath);
            ImageIO.write(cropped, format, croppedOutImage);
            
            return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);
        }
        catch (IOException e) {
            throw new ConverterException(e);
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

            if (queryStringIndex != -1 ) {
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
