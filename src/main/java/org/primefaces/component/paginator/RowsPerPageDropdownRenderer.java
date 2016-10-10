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
import java.util.logging.Logger;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIData;
import org.primefaces.util.MessageFactory;

public class RowsPerPageDropdownRenderer implements PaginatorElementRenderer {

    private final static Logger logger = Logger.getLogger(RowsPerPageDropdownRenderer.class.getName());
    
    public void render(FacesContext context, Pageable pageable) throws IOException {        
        String template = pageable.getRowsPerPageTemplate();
        UIViewRoot viewroot = context.getViewRoot();
        char separator = UINamingContainer.getSeparatorChar(context);
        
        if(template != null) {
            ResponseWriter writer = context.getResponseWriter();
            int actualRows = pageable.getRows();
            String[] options = pageable.getRowsPerPageTemplate().split("[,\\s]+");
            String label = pageable.getRowsPerPageLabel();
            if(label != null)
                logger.info("RowsPerPageLabel attribute is deprecated, use 'primefaces.paginator.aria.ROWS_PER_PAGE' key instead to override default message.");
            else 
                label = MessageFactory.getMessage(UIData.ROWS_PER_PAGE_LABEL, null);
            
            String clientId = pageable.getClientId(context);
            String ddId = clientId + separator + viewroot.createUniqueId();
            String ddName = clientId + "_rppDD";
            String labelId = null;
            
            if(label != null) {
                labelId = clientId + "_rppLabel";
                
                writer.startElement("label", null);
                writer.writeAttribute("id", labelId, null);
                writer.writeAttribute("for", ddId, null);
                writer.writeAttribute("class", UIData.PAGINATOR_RPP_LABEL_CLASS, null);
                writer.writeText(label, null);
                writer.endElement("label");
            }
                    
            writer.startElement("select", null);
            writer.writeAttribute("id", ddId, null);
            writer.writeAttribute("name", ddName, null);
            if(label != null) {
                writer.writeAttribute("aria-labelledby", labelId, null);
            }
            writer.writeAttribute("class", UIData.PAGINATOR_RPP_OPTIONS_CLASS, null);
            writer.writeAttribute("value", pageable.getRows(), null);
            writer.writeAttribute("autocomplete", "off", null);

            for( String option : options){
                int rows = Integer.parseInt(option);
                writer.startElement("option", null);
                writer.writeAttribute("value", rows, null);

                if(actualRows == rows){
                    writer.writeAttribute("selected", "selected", null);
                }

                writer.writeText(option, null);
                writer.endElement("option");
            }

            writer.endElement("select");
        }
    }   
}
