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
package org.primefaces.component.paginator;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UIData;

public class PageLinkRenderer {

    public void render(FacesContext context, UIData uidata, String linkClass, String iconClass, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = disabled ? linkClass + " ui-state-disabled" : linkClass;

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.writeText("p", null);
        writer.endElement("span");
        
        writer.endElement("span");
    }    
}