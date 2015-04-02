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
package org.primefaces.component.row.renderer;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;

public class PanelGridFacetRowRenderer extends CoreRenderer implements HelperRowRenderer {

    public void encode(FacesContext context, Row row) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("tr", row);
        writer.writeAttribute("class", "ui-widget-header", null);
        writer.writeAttribute("role", "row", null);
        renderChildren(context, row);
        writer.endElement("tr");
    }
}
