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

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class AddRowFeature implements DataTableFeature {

    @Override
    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("AddRowFeature should not encode.");
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        if (table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }

        String clientId = table.getClientId(context);
        int rowIndex = table.getRowCount() - 1;
        table.setRowIndex(table.getRowCount() - 1);

        if (table.isRowAvailable()) {
            renderer.encodeRow(context, table, clientId, rowIndex);
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_addrow");
    }
}
