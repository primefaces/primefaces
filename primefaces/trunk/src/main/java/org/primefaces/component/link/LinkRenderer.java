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
package org.primefaces.component.link;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.HTML;

public class LinkRenderer extends OutcomeTargetRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Link link = (Link) component;
        boolean shouldWriteId = shouldWriteId(link);
        boolean disabled = link.isDisabled();
        String style = link.getStyle();
        String defaultStyleClass = disabled ? Link.DISABLED_STYLE_CLASS: Link.STYLE_CLASS;
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
            String targetURL = getTargetURL(context, link);
            if(targetURL == null) {
                targetURL = "#";
            }

            writer.startElement("a", link);
            if(shouldWriteId) {
                writer.writeAttribute("id", link.getClientId(context), "id");
            }
            writer.writeAttribute("href", targetURL, null);
            writer.writeAttribute("class", styleClass, "styleClass");
            renderPassThruAttributes(context, link, HTML.LINK_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, link, HTML.COMMON_EVENTS);
            renderContent(context, link);
            writer.endElement("a");
        }
    }

    protected void renderContent(FacesContext context, Link link) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = link.getValue();

        if(value != null) {
            if(link.isEscape())
                writer.writeText(value, "value");
            else
                writer.write(value.toString());
        }
        else {
            renderChildren(context, link);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
