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
package org.primefaces.component.imageswitch;

import java.io.IOException;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageSwitchRenderer extends CoreRenderer {

    @Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ImageSwitch imageSwitch = (ImageSwitch) component;
        ResponseWriter writer = context.getResponseWriter();
		String clientId = imageSwitch.getClientId(context);

        writer.startElement("div", imageSwitch);
		writer.writeAttribute("id", clientId, "id");
        
		if(imageSwitch.getStyle() != null) writer.writeAttribute("style", imageSwitch.getStyle(), null);
		if(imageSwitch.getStyleClass() != null) writer.writeAttribute("class", imageSwitch.getStyleClass(), null);
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ImageSwitch imageSwitch = (ImageSwitch) component;
        String clientId = imageSwitch.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        int slideshowSpeed = imageSwitch.isSlideshowAuto() ? imageSwitch.getSlideshowSpeed() : 0;
        
        writer.endElement("div");
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("ImageSwitch", imageSwitch.resolveWidgetVar(), clientId, "imageswitch")            .attr("fx", imageSwitch.getEffect())
            .attr("speed", imageSwitch.getSpeed())
            .attr("timeout", slideshowSpeed)
            .attr("startingSlide", imageSwitch.getActiveIndex(), 0);

        wb.finish();
	}

}