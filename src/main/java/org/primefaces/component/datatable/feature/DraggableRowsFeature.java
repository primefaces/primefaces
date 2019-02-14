/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class DraggableRowsFeature implements DataTableFeature {

    private static final Logger LOGGER = Logger.getLogger(DraggableRowsFeature.class.getName());

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowreorder");
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public void decode(FacesContext context, DataTable table) {
        MethodExpression me = table.getDraggableRowsFunction();
        if (me != null) {
            me.invoke(context.getELContext(), new Object[]{table});
        }
        else {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = table.getClientId(context);
            int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
            int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));
            table.setRowIndex(fromIndex);
            Object value = table.getValue();

            if (value instanceof List) {
                List list = (List) value;

                if (toIndex >= fromIndex) {
                    Collections.rotate(list.subList(fromIndex, toIndex + 1), -1);
                }
                else {
                    Collections.rotate(list.subList(toIndex, fromIndex + 1), 1);
                }
            }
            else {
                LOGGER.info("Row reordering is only available for list backed datatables, "
                        + "use rowReorder ajax behavior with listener for manual handling of model update.");
            }
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("DraggableRows Feature should not encode.");
    }

}
