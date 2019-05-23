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
package org.primefaces.component.rowtoggler;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;


public class RowToggler extends RowTogglerBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.RowToggler";

    public static final String COLLAPSED_ICON = "ui-icon ui-icon-circle-triangle-e";
    public static final String EXPANDED_ICON = "ui-icon ui-icon-circle-triangle-s";

    public static final String ROW_TOGGLER = "primefaces.rowtoggler.aria.ROW_TOGGLER";

    private DataTable parentTable = null;

    public DataTable getParentTable(FacesContext context) {
        if (parentTable == null) {
            UIComponent parent = getParent();

            while (parent != null) {
                if (parent instanceof DataTable) {
                    parentTable = (DataTable) parent;
                    break;
                }

                parent = parent.getParent();
            }
        }

        return parentTable;
    }
}