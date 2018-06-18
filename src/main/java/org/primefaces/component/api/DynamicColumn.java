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
    private String columnKey;

    public DynamicColumn(int index, Columns columns) {
        this.index = index;
        this.columns = columns;
    }

    public DynamicColumn(int index, Columns columns, String columnKey) {
        this.index = index;
        this.columns = columns;
        this.columnKey = columnKey;
    }

    public int getIndex() {
        return index;
    }

    public void applyModel() {
        this.columns.setRowIndex(index);
    }

    public void applyStatelessModel() {
        this.columns.setRowModel(index);
    }

    public void cleanStatelessModel() {
        this.columns.setRowModel(-1);
    }

    public void cleanModel() {
        this.columns.setRowIndex(-1);
    }

    @Override
    public ValueExpression getValueExpression(String property) {
        return this.columns.getValueExpression(property);
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        return this.columns.getContainerClientId(context);
    }

    public String getId() {
        return this.columns.getId();
    }

    @Override
    public String getClientId() {
        return this.columns.getClientId();
    }

    @Override
    public String getClientId(FacesContext context) {
        return this.columns.getClientId(context);
    }

    @Override
    public String getSelectionMode() {
        return this.columns.getSelectionMode();
    }

    @Override
    public boolean isResizable() {
        return this.columns.isResizable();
    }

    @Override
    public String getStyle() {
        return this.columns.getStyle();
    }

    @Override
    public String getStyleClass() {
        return this.columns.getStyleClass();
    }

    @Override
    public int getRowspan() {
        return this.columns.getRowspan();
    }

    @Override
    public int getColspan() {
        return this.columns.getColspan();
    }

    @Override
    public String getFilterPosition() {
        return this.columns.getFilterPosition();
    }

    @Override
    public UIComponent getFacet(String facet) {
        return this.columns.getFacet(facet);
    }

    @Override
    public String getHeaderText() {
        return this.columns.getHeaderText();
    }

    @Override
    public String getFooterText() {
        return this.columns.getFooterText();
    }

    @Override
    public String getFilterStyleClass() {
        return this.columns.getFilterStyleClass();
    }

    @Override
    public String getFilterStyle() {
        return this.columns.getFilterStyle();
    }

    @Override
    public String getFilterMatchMode() {
        return this.columns.getFilterMatchMode();
    }

    @Override
    public int getFilterMaxLength() {
        return this.columns.getFilterMaxLength();
    }

    @Override
    public Object getFilterOptions() {
        return this.columns.getFilterOptions();
    }

    @Override
    public CellEditor getCellEditor() {
        return this.columns.getCellEditor();
    }

    @Override
    public boolean isDynamic() {
        return this.columns.isDynamic();
    }

    @Override
    public MethodExpression getSortFunction() {
        return this.columns.getSortFunction();
    }

    @Override
    public List<UIComponent> getChildren() {
        return this.columns.getChildren();
    }

    @Override
    public boolean isExportable() {
        return this.columns.isExportable();
    }

    @Override
    public boolean isRendered() {
        return this.columns.isRendered();
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
        this.columns.encodeAll(context);
    }

    @Override
    public void renderChildren(FacesContext context) throws IOException {
        this.columns.encodeChildren(context);
    }

    @Override
    public String getColumnKey() {
        return this.columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    @Override
    public String getWidth() {
        return this.columns.getWidth();
    }

    @Override
    public Object getSortBy() {
        return this.columns.getSortBy();
    }

    @Override
    public Object getFilterBy() {
        return this.columns.getFilterBy();
    }

    @Override
    public boolean isToggleable() {
        return this.columns.isToggleable();
    }

    @Override
    public MethodExpression getFilterFunction() {
        return this.columns.getFilterFunction();
    }

    @Override
    public String getField() {
        return this.columns.getField();
    }

    @Override
    public Object getFilterValue() {
        return this.columns.getFilterValue();
    }

    @Override
    public int getPriority() {
        return this.columns.getPriority();
    }

    @Override
    public boolean isSortable() {
        return this.columns.isSortable();
    }

    @Override
    public boolean isFilterable() {
        return this.columns.isFilterable();
    }

    @Override
    public boolean isVisible() {
        return this.columns.isVisible();
    }

    @Override
    public boolean isSelectRow() {
        return this.columns.isSelectRow();
    }

    @Override
    public String getAriaHeaderText() {
        return this.columns.getAriaHeaderText();
    }

    @Override
    public MethodExpression getExportFunction() {
        return this.columns.getExportFunction();
    }

    @Override
    public boolean isGroupRow() {
        return this.columns.isGroupRow();
    }
    
    @Override
    public String getExportHeaderValue() {
        return this.columns.getExportHeaderValue();
    }
    
    @Override
    public String getExportFooterValue() {
        return this.columns.getExportFooterValue();
    }
}
