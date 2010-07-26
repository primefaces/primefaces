/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.primefaces.model.CroppedImage;
import org.primefaces.renderkit.CoreRenderer;

public class ImageCropperRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		String clientId = component.getClientId(facesContext);
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(getCoordsHolder(clientId));
		
		((ImageCropper) component).setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ImageCropper cropper = (ImageCropper) component;

		encodeScript(facesContext, cropper);
		encodeMarkup(facesContext, cropper);
	}

	private void encodeScript(FacesContext facesContext, ImageCropper cropper) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String cropperVar = createUniqueWidgetVar(facesContext, cropper);
		String clientId = cropper.getClientId(facesContext);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);		
		writer.write("YAHOO.util.Event.addListener(window, 'load', function() {\n");
		writer.write(cropperVar + " = new YAHOO.widget.ImageCropper('" + getImageId(clientId) + "'");

		if(cropper.getValue() != null) {
			writer.write(",{");
			CroppedImage croppedImage = (CroppedImage) cropper.getValue();
			writer.write("initialXY:[" + croppedImage.getLeft() + "," + croppedImage.getTop() + "]");
			writer.write(",initWidth:" + croppedImage.getWidth());
			writer.write(",initHeight:" + croppedImage.getHeight());
			writer.write("}");
		}
		writer.write(");\n");

		writer.write(cropperVar + ".on('moveEvent', PrimeFaces.widget.ImageCropperUtils.attachedCroppedArea, {hiddenFieldId:\"" + getCoordsHolder(clientId) + "\"});\n");

		writer.write("});\n");

		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, ImageCropper cropper) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = cropper.getClientId(facesContext);
		String coordsHolder = getCoordsHolder(clientId);
		
		writer.startElement("div", cropper);
		writer.writeAttribute("id", clientId, null);
		
		renderImage(facesContext, cropper, clientId);
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", coordsHolder, null);
		writer.writeAttribute("name", coordsHolder, null);
		writer.writeAttribute("value", "", null);
		writer.endElement("input");
		
		writer.endElement("div");
	}
	
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		if(submittedValue.equals(""))
			return null;
		
		ImageCropper cropper = (ImageCropper) component;
		String[] cropCoords = ((String)submittedValue).split("_");
		String format = getFormat(cropper.getImage());
		
		int y = Integer.parseInt(cropCoords[0]);
		int x = Integer.parseInt(cropCoords[1]);
		int w = Integer.parseInt(cropCoords[2]);
		int h = Integer.parseInt(cropCoords[3]);
		
		try {
			BufferedImage outputImage = getSourceImage(facesContext, cropper);
			BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
			
			ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
	        ImageIO.write(cropped, format, croppedOutImage);
	        
	        return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);
		} catch (IOException e) {
			throw new ConverterException(e);
		}
	}

	private void renderImage(FacesContext facesContext, ImageCropper cropper, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("id", getImageId(clientId), null);
		writer.writeAttribute("src", getResourceURL(facesContext, cropper.getImage()), null);
		writer.endElement("img");
	}
	
	private String getFormat(String path) {
		String[] pathTokens = path.split("\\.");
		
		return pathTokens[pathTokens.length -1];
	}
	
	private String getCoordsHolder(String clientId) {
		return clientId + "_coords";
	}
	
	private String getImageId(String clientId) {
		return clientId + "_image";
	}
	
	private boolean isExternalImage(ImageCropper cropper) {
		return cropper.getImage().startsWith("http");
	}
	
	private BufferedImage getSourceImage(FacesContext facesContext, ImageCropper cropper) throws IOException {
		 BufferedImage outputImage = null;
		 boolean isExternal = isExternalImage(cropper);
		 
		 if(isExternal) {
			 URL url = new URL(cropper.getImage());
			 
			 outputImage =  ImageIO.read(url);
		 }
		 else {
			ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
			String imagePath = servletContext.getRealPath("") + cropper.getImage();
			
			outputImage = ImageIO.read(new File(imagePath));
		}
		 
		return outputImage;
	}
}