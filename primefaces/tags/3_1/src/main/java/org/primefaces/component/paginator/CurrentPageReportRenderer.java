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

public class CurrentPageReportRenderer implements PaginatorElementRenderer {

    public void render(FacesContext context, UIData uidata) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String template = uidata.getCurrentPageReportTemplate();
        int currentPage = uidata.getPage() + 1;
        int pageCount = uidata.getPageCount();
        if(pageCount == 0) {
            pageCount = 1;
        }
                
        String output = template.replaceAll("\\{currentPage\\}", Integer.toString(currentPage))
        .replaceAll("\\{totalPages\\}", Integer.toString(pageCount))
        .replaceAll("\\{totalRecords\\}", Integer.toString(uidata.getRowCount()))
        .replaceAll("\\{startRecord\\}", Integer.toString(Math.min(uidata.getFirst() + 1, uidata.getRowCount())))
        .replaceAll("\\{endRecord}", Integer.toString(Math.min(uidata.getFirst() + uidata.getRowsToRender(), uidata.getRowCount())));
        
        writer.startElement("span", null);
        writer.writeAttribute("class", UIData.PAGINATOR_CURRENT_CLASS, null);
        writer.writeText(output, null);
        writer.endElement("span");
    }
}