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
package org.primefaces.component.column.renderer;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class ColumnGroupHelperRenderer implements HelperColumnRenderer {

    public void encode(FacesContext context, Column column) throws IOException {
        ColumnGroup group = findGroup(column);
        DataTable table = (DataTable) group.getParent();
        String type = group.getType();
        DataTableRenderer renderer = (DataTableRenderer) context.getRenderKit().getRenderer("org.primefaces.component", "org.primefaces.component.DataTableRenderer");
        
        if(type.equals("header") || type.equals("frozenHeader") || type.equals("scrollableHeader"))
            renderer.encodeColumnHeader(context, table, column);
        else if(type.equals("footer") || type.equals("frozenFooter") || type.equals("scrollableFooter"))
            renderer.encodeColumnFooter(context, table, column);
    }
    
    private ColumnGroup findGroup(Column column) {
        UIComponent parent = column.getParent();
        
        while(!(parent instanceof ColumnGroup)) {
            parent = parent.getParent();
        }
        
        return (ColumnGroup) parent;
    }
    
}
