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