/**
 * Copyright 2009-2018 PrimeTek.
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
import org.primefaces.component.api.UIData;

public class JumpToPageDropdownRenderer implements PaginatorElementRenderer {

    @Override
    public void render(FacesContext context, Pageable pageable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int currentPage = pageable.getPage();
        int pageCount = pageable.getPageCount();

        writer.startElement("select", null);
        writer.writeAttribute("class", UIData.PAGINATOR_JTP_SELECT_CLASS, null);
        writer.writeAttribute("value", pageable.getPage(), null);

        for (int i = 0; i < pageCount; i++) {
            writer.startElement("option", null);
            writer.writeAttribute("value", i, null);

            if (i == currentPage) {
                writer.writeAttribute("selected", "selected", null);
            }

            writer.writeText((i + 1), null);
            writer.endElement("option");
        }

        writer.endElement("select");
    }

}
