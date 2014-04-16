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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.link.Link;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.HTML;

public class LinkRenderer extends org.primefaces.component.link.LinkRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Link link = (Link) component;
        boolean shouldWriteId = shouldWriteId(link);
        boolean disabled = link.isDisabled();
        String style = link.getStyle();
        String defaultStyleClass = disabled ? Link.MOBILE_DISABLED_STYLE_CLASS: Link.MOBILE_STYLE_CLASS;
        String styleClass = link.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        if(disabled) {
            writer.startElement("span", link);
            if(shouldWriteId) {
                writer.writeAttribute("id", link.getClientId(context), "id");
            }
            writer.writeAttribute("class", styleClass, "styleClass");
            if(style != null) {
                writer.writeAttribute("style", style, "style");
            }

            renderContent(context, link);
            writer.endElement("span");
        }
        else {
            String outcome = link.getOutcome();
            
            writer.startElement("a", link);
            if(shouldWriteId) {
                writer.writeAttribute("id", link.getClientId(context), "id");
            }
            writer.writeAttribute("class", styleClass, "styleClass");

            if(outcome != null && outcome.startsWith("pm:")) {
                String command = MobileUtils.buildNavigation(outcome) + "return false;";
                writer.writeAttribute("href", "#", null);
                
                renderPassThruAttributes(context, link, HTML.LINK_ATTRS_WITHOUT_EVENTS);
                renderDomEvents(context, link, HTML.OUTPUT_EVENTS_WITHOUT_CLICK);
                renderOnclick(context, component, command);
            }
            else {
                String targetURL = getTargetURL(context, link);
                if(targetURL == null) {
                    targetURL = "#";
                }
                writer.writeAttribute("href", targetURL, null);
                
                renderPassThruAttributes(context, link, HTML.LINK_ATTRS_WITHOUT_EVENTS);
                renderDomEvents(context, link, HTML.OUTPUT_EVENTS);
            }

            renderContent(context, link);
            writer.endElement("a");
        }
    }
            

}
