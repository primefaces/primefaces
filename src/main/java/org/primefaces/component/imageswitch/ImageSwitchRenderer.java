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
package org.primefaces.component.imageswitch;

import java.io.IOException;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ImageSwitchRenderer extends CoreRenderer {

    @Override
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ImageSwitch imageSwitch = (ImageSwitch) component;
        ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = imageSwitch.getClientId(facesContext);

        writer.startElement("div", imageSwitch);
		writer.writeAttribute("id", clientId, "id");
        
		if(imageSwitch.getStyle() != null) writer.writeAttribute("style", imageSwitch.getStyle(), null);
		if(imageSwitch.getStyleClass() != null) writer.writeAttribute("class", imageSwitch.getStyleClass(), null);
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ImageSwitch imageSwitch = (ImageSwitch) component;
        String clientId = imageSwitch.getClientId(facesContext);
        ResponseWriter writer = facesContext.getResponseWriter();
        int slideshowSpeed = imageSwitch.isSlideshowAuto() ? imageSwitch.getSlideshowSpeed() : 0;

        writer.endElement("div");
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('ImageSwitch','" + imageSwitch.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",fx:'" + imageSwitch.getEffect() + "'");
		writer.write(",speed:" + imageSwitch.getSpeed());
		writer.write(",timeout:" + slideshowSpeed);
		writer.write("},'imageswitch');});");

		endScript(writer);
	}

}