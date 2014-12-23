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
package org.primefaces.component.photocam;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.xml.bind.DatatypeConverter;
import org.primefaces.event.CaptureEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class PhotoCamRenderer extends CoreRenderer {
    
    @Override
	public void decode(FacesContext context, UIComponent component) {
		PhotoCam cam = (PhotoCam) component;
        String dataParam = cam.getClientId(context) + "_data";
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
        if(params.containsKey(dataParam)) {
            String image = params.get(dataParam);
            image = image.substring(22);
            
            CaptureEvent event = new CaptureEvent(cam, DatatypeConverter.parseBase64Binary(image), image);
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            
            cam.queueEvent(event);
        }
	}
    
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        PhotoCam cam = (PhotoCam) component;

        encodeMarkup(context, cam);
        encodeScript(context, cam);
	}

    protected void encodeMarkup(FacesContext context, PhotoCam cam) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String clientId = cam.getClientId(context);
        String style = cam.getStyle();
        String styleClass = cam.getStyleClass();
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);
        
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, PhotoCam cam) throws IOException {
		String clientId = cam.getClientId(context);
        String camera = getResourceRequestPath(context, "photocam/webcam.swf");
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("PhotoCam", cam.resolveWidgetVar(), clientId)
            .attr("camera", camera)
            .attr("width", cam.getWidth(), 320)
            .attr("height", cam.getHeight(), 240)
            .attr("photoWidth", cam.getPhotoWidth(), 320)
            .attr("photoHeight", cam.getPhotoHeight(), 240)
            .attr("format", cam.getFormat(), null)
            .attr("jpegQuality", cam.getJpegQuality(), 90)
            .attr("forceFlash", cam.isForceFlash(), false);
        
        if(cam.getUpdate() != null) wb.attr("update", SearchExpressionFacade.resolveClientIds(context, cam, cam.getUpdate()));
        if(cam.getProcess() != null) wb.attr("process", SearchExpressionFacade.resolveClientIds(context, cam, cam.getProcess()));

       wb.finish();
	}
    
}