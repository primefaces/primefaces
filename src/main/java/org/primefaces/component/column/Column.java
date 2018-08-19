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
package org.primefaces.component.column;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.celleditor.CellEditor;


public class Column extends ColumnBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Column";

    @Override
    public CellEditor getCellEditor() {

        CellEditor cellEditor = null;

        for (UIComponent child : getChildren()) {
            if (child instanceof CellEditor && ((CellEditor) child).isRendered()) {
                cellEditor = (CellEditor) child;
                break;
            }
        }

        return cellEditor;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public String getColumnKey() {
        return getClientId();
    }

    @Override
    public List getElements() {
        return getChildren();
    }

    @Override
    public int getElementsCount() {
        return getChildCount();
    }

    @Override
    public void renderChildren(FacesContext context) throws IOException {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                UIComponent child = getChildren().get(i);
                child.encodeAll(context);
            }
        }
    }
}