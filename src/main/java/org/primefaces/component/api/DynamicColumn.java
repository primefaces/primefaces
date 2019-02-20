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

    private final int index;
    private final Columns columns;
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
        columns.setRowIndex(index);
    }

    public void applyStatelessModel() {
        columns.setRowModel(index);
    }

    public void cleanStatelessModel() {
        columns.setRowModel(-1);
    }

    public void cleanModel() {
        columns.setRowIndex(-1);
    }

    @Override
    public ValueExpression getValueExpression(String property) {
        return columns.getValueExpression(property);
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        return columns.getContainerClientId(context);
    }

    public String getId() {
        return columns.getId();
    }

    @Override
    public String getClientId() {
        return columns.getClientId();
    }

    @Override
    public String getClientId(FacesContext context) {
        return columns.getClientId(context);
    }

    @Override
    public String getSelectionMode() {
        return columns.getSelectionMode();
    }

    @Override
    public boolean isResizable() {
        return columns.isResizable();
    }

    @Override
    public String getStyle() {
        return columns.getStyle();
    }

    @Override
    public String getStyleClass() {
        return columns.getStyleClass();
    }

    @Override
    public int getRowspan() {
        return columns.getRowspan();
    }

    @Override
    public int getColspan() {
        return columns.getColspan();
    }

    @Override
    public String getFilterPosition() {
        return columns.getFilterPosition();
    }

    @Override
    public UIComponent getFacet(String facet) {
        return columns.getFacet(facet);
    }

    @Override
    public String getHeaderText() {
        return columns.getHeaderText();
    }

    @Override
    public String getFooterText() {
        return columns.getFooterText();
    }

    @Override
    public String getFilterStyleClass() {
        return columns.getFilterStyleClass();
    }

    @Override
    public String getFilterStyle() {
        return columns.getFilterStyle();
    }

    @Override
    public String getFilterMatchMode() {
        return columns.getFilterMatchMode();
    }

    @Override
    public int getFilterMaxLength() {
        return columns.getFilterMaxLength();
    }

    @Override
    public Object getFilterOptions() {
        return columns.getFilterOptions();
    }

    @Override
    public CellEditor getCellEditor() {
        return columns.getCellEditor();
    }

    @Override
    public boolean isDynamic() {
        return columns.isDynamic();
    }

    @Override
    public MethodExpression getSortFunction() {
        return columns.getSortFunction();
    }

    @Override
    public List<UIComponent> getChildren() {
        return columns.getChildren();
    }

    @Override
    public boolean isExportable() {
        return columns.isExportable();
    }

    @Override
    public boolean isRendered() {
        return columns.isRendered();
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
        columns.encodeAll(context);
    }

    @Override
    public void renderChildren(FacesContext context) throws IOException {
        columns.encodeChildren(context);
    }

    @Override
    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    @Override
    public String getWidth() {
        return columns.getWidth();
    }

    @Override
    public Object getSortBy() {
        return columns.getSortBy();
    }

    @Override
    public Object getFilterBy() {
        return columns.getFilterBy();
    }

    @Override
    public boolean isToggleable() {
        return columns.isToggleable();
    }

    @Override
    public MethodExpression getFilterFunction() {
        return columns.getFilterFunction();
    }

    @Override
    public String getField() {
        return columns.getField();
    }

    @Override
    public Object getFilterValue() {
        return columns.getFilterValue();
    }

    @Override
    public int getPriority() {
        return columns.getPriority();
    }

    @Override
    public boolean isSortable() {
        return columns.isSortable();
    }

    @Override
    public boolean isFilterable() {
        return columns.isFilterable();
    }

    @Override
    public boolean isVisible() {
        return columns.isVisible();
    }

    @Override
    public boolean isSelectRow() {
        return columns.isSelectRow();
    }

    @Override
    public String getAriaHeaderText() {
        return columns.getAriaHeaderText();
    }

    @Override
    public MethodExpression getExportFunction() {
        return columns.getExportFunction();
    }

    @Override
    public boolean isGroupRow() {
        return columns.isGroupRow();
    }

    @Override
    public String getExportHeaderValue() {
        return columns.getExportHeaderValue();
    }

    @Override
    public String getExportFooterValue() {
        return columns.getExportFooterValue();
    }
}
