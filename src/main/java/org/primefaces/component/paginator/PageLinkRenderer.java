/*
 * Copyright 2009-2015 PrimeTek.
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
package org.primefaces.component.paginator;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.Pageable;

public class PageLinkRenderer {

    public void render(FacesContext context, Pageable pageable, String linkClass, String iconClass, boolean disabled, String ariaLabel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = disabled ? linkClass + " ui-state-disabled" : linkClass;
        int textIndex = iconClass.indexOf("seek-");
        String text = String.valueOf(iconClass.charAt(textIndex + 5)).toUpperCase();
        String tabindex = (disabled) ? "-1" : "0";

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("aria-label", ariaLabel, null);
        writer.writeAttribute("tabindex", tabindex, null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.writeText(text, null);
        writer.endElement("span");
        
        writer.endElement("a");
    }    
}