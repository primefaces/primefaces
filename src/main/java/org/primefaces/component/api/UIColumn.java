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

public interface UIColumn {

    ValueExpression getValueExpression(String property);

    String getContainerClientId(FacesContext context);

    String getColumnKey();

    String getClientId();

    String getClientId(FacesContext context);

    String getSelectionMode();

    boolean isResizable();

    String getStyle();

    String getStyleClass();

    int getRowspan();

    int getColspan();

    String getFilterPosition();

    UIComponent getFacet(String facet);

    Object getFilterBy();

    Object getFilterValue();

    String getHeaderText();

    String getFooterText();

    String getFilterStyleClass();

    String getFilterStyle();

    String getFilterMatchMode();

    int getFilterMaxLength();

    Object getFilterOptions();

    CellEditor getCellEditor();

    boolean isDynamic();

    MethodExpression getSortFunction();

    Object getSortBy();

    List<UIComponent> getChildren();

    boolean isExportable();

    boolean isRendered();

    void encodeAll(FacesContext context) throws IOException;

    void renderChildren(FacesContext context) throws IOException;

    String getWidth();

    boolean isToggleable();

    MethodExpression getFilterFunction();

    String getField();

    int getPriority();

    boolean isSortable();

    boolean isFilterable();

    boolean isVisible();

    boolean isSelectRow();

    String getAriaHeaderText();

    MethodExpression getExportFunction();

    boolean isGroupRow();

    String getExportHeaderValue();

    String getExportFooterValue();
}
