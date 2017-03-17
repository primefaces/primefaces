/*
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.TableState;
import org.primefaces.event.data.PostPageEvent;

public class PageFeature implements DataTableFeature {

    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("PageFeature should not encode.");
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        table.updatePaginationData(context, table);
        
        boolean isPageState = table.isPageStateRequest(context);
        
        if(table.isLazy() && !isPageState) {
            table.loadLazyData();
        }

        if(!isPageState) {
            renderer.encodeTbody(context, table, true);
        }

        context.getApplication().publishEvent(context, PostPageEvent.class, table);
        
        if(table.isMultiViewState()) {
            TableState ts = table.getTableState(true);

            ts.setFirst(table.getFirst());
            ts.setRows(table.getRows());
        }
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return table.isPaginationRequest(context);
    }
    
}
