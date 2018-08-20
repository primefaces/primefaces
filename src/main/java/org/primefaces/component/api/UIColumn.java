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
