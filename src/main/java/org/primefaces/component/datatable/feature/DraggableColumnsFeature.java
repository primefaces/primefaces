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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.TableState;
import org.primefaces.util.LangUtils;

public class DraggableColumnsFeature implements DataTableFeature {

    @Override
    public void decode(FacesContext context, DataTable table) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String columnOrderParam = params.get(table.getClientId(context) + "_columnOrder");
        if (LangUtils.isValueBlank(columnOrderParam)) {
            return;
        }

        table.setColumns(table.findOrderedColumns(columnOrderParam));

        if (table.isMultiViewState()) {
            TableState ts = table.getTableState(true);
            ts.setOrderedColumnsAsString(columnOrderParam);
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("DraggableColumns Feature should not encode.");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return table.isDraggableColumns();
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }

}
