/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.api;

import java.io.IOException;
import java.util.List;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.columns.Columns;

public class DynamicColumn implements UIColumn {
    
    private int index;
    private Columns columns;

    public DynamicColumn(int index, Columns columns) {
        this.index = index;
        this.columns = columns;
    }

    public int getIndex() {
        return index;
    }
    
    public void applyModel() {
        this.columns.setRowIndex(index);
    }

    public ValueExpression getValueExpression(String property) {
        return this.columns.getValueExpression(property);
    }

    public String getContainerClientId(FacesContext context) {
        return this.columns.getContainerClientId(context);
    }

    public String getClientId() {
        return this.columns.getClientId();
    }

    public String getClientId(FacesContext context) {
        return this.columns.getClientId(context);
    }

    public String getSelectionMode() {
        return this.columns.getSelectionMode();
    }

    public boolean isResizable() {
        return this.columns.isResizable();
    }

    public String getStyle() {
        return this.columns.getStyle();
    }

    public String getStyleClass() {
        return this.columns.getStyleClass();
    }

    public int getRowspan() {
        return this.columns.getRowspan();
    }

    public int getColspan() {
        return this.columns.getColspan();
    }

    public int getWidth() {
        return this.columns.getWidth();
    }

    public boolean isDisabledSelection() {
        return this.columns.isDisabledSelection();
    }

    public String getFilterPosition() {
        return this.columns.getFilterPosition();
    }

    public UIComponent getFacet(String facet) {
        return this.columns.getFacet(facet);
    }

    public String getHeaderText() {
        return this.columns.getHeaderText();
    }

    public String getFooterText() {
        return this.columns.getFooterText();
    }

    public String getFilterStyleClass() {
        return this.columns.getFilterStyleClass();
    }

    public String getFilterStyle() {
        return this.columns.getFilterStyle();
    }

    public String getFilterMatchMode() {
        return this.columns.getFilterMatchMode();
    }

    public int getFilterMaxLength() {
        return this.columns.getFilterMaxLength();
    }

    public Object getFilterOptions() {
        return this.columns.getFilterOptions();
    }

    public CellEditor getCellEditor() {
        return this.columns.getCellEditor();
    }

    public boolean isDynamic() {
        return this.columns.isDynamic();
    }

    public MethodExpression getSortFunction() {
        return this.columns.getSortFunction();
    }

    public List<UIComponent> getChildren() {
        return this.columns.getChildren();
    }

    public boolean isExportable() {
        return this.columns.isExportable();
    }

    public boolean isRendered() {
        return this.columns.isRendered();
    }

    public void encodeAll(FacesContext context) throws IOException {
        this.columns.encodeAll(context);
    }
}
