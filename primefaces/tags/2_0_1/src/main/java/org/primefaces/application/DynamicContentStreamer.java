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
package org.primefaces.application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.graphictext.GraphicTextRenderer;
import org.primefaces.model.StreamedContent;

public class DynamicContentStreamer implements PhaseListener {

	public static final String DYNAMIC_CONTENT_PARAM = "primefacesDynamicContent";
	public static final String CONTENT_TYPE_PARAM = "contentType";
	public static final String GRAPHIC_TEXT_PARAM = "primefacesGraphicText";
	
	private Logger logger = Logger.getLogger(DynamicContentStreamer.class.getName());

	public void beforePhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(DYNAMIC_CONTENT_PARAM))
			streamDynamicContent(facesContext, params.get(DYNAMIC_CONTENT_PARAM));
		else if(params.containsKey(GRAPHIC_TEXT_PARAM))
			streamGraphicText(facesContext, params);
	}
	
	private void streamDynamicContent(FacesContext facesContext, String expression) {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		ELContext elContext = facesContext.getELContext();
		ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(elContext, "#{" + expression + "}", StreamedContent.class);
		StreamedContent content = (StreamedContent) ve.getValue(elContext);

		if(content != null) {
			if(logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Streaming image: {0}", ve.getExpressionString());
		
			try {
				response.setContentType(content.getContentType());
				
				byte[] buffer = new byte[2048];
		
				int length;
				while ((length = (content.getStream().read(buffer))) >= 0) {
					response.getOutputStream().write(buffer, 0, length);
				}
				
				finalizeResponse(facesContext);
			}
			catch (IOException e) {
				logger.log(Level.WARNING, "Exception in streaming image {0}", ve.getExpressionString());
			}
		}
	}
	
	private void streamGraphicText(FacesContext facesContext, Map<String,String> params) {
		try {
			HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
			String fontNameVal = params.get(GraphicTextRenderer.KEY_FONTNAME); 
			String fontStyleVal = params.get(GraphicTextRenderer.KEY_FONTSTYLE);
			String fontSizeVal = params.get(GraphicTextRenderer.KEY_FONTSIZE);
			String graphicTextVal = params.get(GraphicTextRenderer.KEY_GRAPHICTEXT);
			
			BufferedImage bufferedImg = createBufferedImage(fontNameVal, fontStyleVal, fontSizeVal, graphicTextVal);
			OutputStream outputStream = response.getOutputStream();
			
			ImageIO.write(bufferedImg, "jpg", outputStream);

			finalizeResponse(facesContext);
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "IOException in streaming graphic text image");			
		}
	}

	private BufferedImage createBufferedImage(String fontNameVal, String fontStyleVal,	String fontSizeVal, String graphicTextVal) {
		BufferedImage bufferedImg = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bufferedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int fontStyle = Font.PLAIN;
		if ("bold".equals(fontStyleVal))
			fontStyle = Font.BOLD;
		if ("italic".equals(fontStyleVal))
			fontStyle = Font.ITALIC;
		
		Font font = new Font(fontNameVal, fontStyle, Integer.parseInt(fontSizeVal));
		
		FontRenderContext fc = g2.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(graphicTextVal,fc);

		// calculate the size of the text
		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		bufferedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2 = bufferedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(font);
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0,0,width,height);
		g2.setColor(Color.BLACK);
		g2.drawString(graphicTextVal, 0, (int)-bounds.getY());
		
		return bufferedImg;
	}
	
	private void finalizeResponse(FacesContext facesContext) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		response.setStatus(200);
		response.getOutputStream().flush();
		facesContext.responseComplete();
	}
	
	public void afterPhase(PhaseEvent phaseEvent) {
		//Nothing to do here
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}