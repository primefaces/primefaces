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
package org.primefaces.component.subtable;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;


public class SubTable extends SubTableBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SubTable";

    private List<Column> columns;

    public List<Column> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();

            for (UIComponent child : getChildren()) {
                if (child.isRendered() && child instanceof Column) {
                    columns.add((Column) child);
                }
            }
        }

        return columns;
    }

    public ColumnGroup getColumnGroup(String target) {
        for (UIComponent child : getChildren()) {
            if (child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if (type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }
}