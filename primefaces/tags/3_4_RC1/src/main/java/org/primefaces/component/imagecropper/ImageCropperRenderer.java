/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.primefaces.model.CroppedImage;
import org.primefaces.renderkit.CoreRenderer;

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
		ResponseWriter writer = context.getResponseWriter();
		String widgetVar = cropper.resolveWidgetVar();
		String clientId = cropper.getClientId(context);

        startScript(writer, clientId);	

        writer.write("$(PrimeFaces.escapeClientId('" + clientId + "_image')).load(function(){");
        
        writer.write("PrimeFaces.cw('ImageCropper','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",image:'" + clientId + "_image'");
        
        if(cropper.getMinSize() != null) writer.write(",minSize:[" + cropper.getMinSize() + "]");
        if(cropper.getMaxSize() != null) writer.write(",maxSize:[" + cropper.getMaxSize() + "]");
        if(cropper.getBackgroundColor() != null) writer.write(",bgColor:'" + cropper.getBackgroundColor() + "'");
        if(cropper.getBackgroundOpacity() != 0.6) writer.write(",bgOpacity:" + cropper.getBackgroundOpacity());
        if(cropper.getAspectRatio() != Double.MIN_VALUE) writer.write(",aspectRatio:" + cropper.getAspectRatio());

        //Initial crop area
		if(cropper.getValue() != null) {
            CroppedImage croppedImage = (CroppedImage) cropper.getValue();
            
            int x = croppedImage.getLeft();
            int y = croppedImage.getTop();
            int x2 = x + croppedImage.getWidth();
            int y2 = y + croppedImage.getHeight();

			writer.write(",setSelect:[" + x +  "," + y + "," + x2 + "," + y2 + "]");

		} else if(cropper.getInitialCoords() != null) {
            writer.write(",setSelect:[" + cropper.getInitialCoords() + "]");
        }

        
		writer.write("},'imagecropper');});");

		endScript(writer);
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

    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        String coords = (String) submittedValue;
        if(isValueBlank(coords)) {
            return null;
        }
        
        ImageCropper cropper = (ImageCropper) component;
        
        //remove query string
        String imagePath = cropper.getImage();
        int queryStringIndex = imagePath.indexOf("?");
        if(queryStringIndex != -1 ) {
            imagePath = imagePath.substring(0, queryStringIndex);
        }
        
		String[] cropCoords = coords.split("_");
		String format = getFormat(imagePath);
		
		int x = Integer.parseInt(cropCoords[0]);
		int y = Integer.parseInt(cropCoords[1]);
		int w = Integer.parseInt(cropCoords[2]);
		int h = Integer.parseInt(cropCoords[3]);
		
		try {
			BufferedImage outputImage = getSourceImage(context, imagePath);
			BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
			
			ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
	        ImageIO.write(cropped, format, croppedOutImage);
	        
	        return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);
            
		} catch (IOException e) {
			throw new ConverterException(e);
		}
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
	
	protected String getFormat(String path) {
		String[] pathTokens = path.split("\\.");
		
		return pathTokens[pathTokens.length - 1];
	}
		
	protected boolean isExternalImage(ImageCropper cropper) {
		return cropper.getImage().startsWith("http");
	}
	
	private BufferedImage getSourceImage(FacesContext context, String imagePath) throws IOException {
		 BufferedImage outputImage = null;
		 boolean isExternal = imagePath.startsWith("http");
		 
		 if(isExternal) {
			 URL url = new URL(imagePath);
			 
			 outputImage =  ImageIO.read(url);
		 }
		 else {
			ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
			
			outputImage = ImageIO.read(new File(servletContext.getRealPath("") + imagePath));
		}
		 
		return outputImage;
	}
}