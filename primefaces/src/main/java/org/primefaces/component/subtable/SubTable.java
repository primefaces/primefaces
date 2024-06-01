/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.subtable;

import java.util.List;

import javax.faces.event.PhaseId;

import org.primefaces.component.api.ForEachRowColumn;
import org.primefaces.component.api.UIColumn;

public class SubTable extends SubTableBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SubTable";

    private List<UIColumn> columns;

    @Override
    public List<UIColumn> getColumns() {
        if (columns != null) {
            return columns;
        }

        List<UIColumn> columnsTmp = ForEachRowColumn.collectColumns(this);

        // lets cache it only when RENDER_RESPONSE is reached, the columns might change before reaching that phase
        // see https://github.com/primefaces/primefaces/issues/2110
        if (getFacesContext().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            columns = columnsTmp;
        }

        return columnsTmp;
    }
}
