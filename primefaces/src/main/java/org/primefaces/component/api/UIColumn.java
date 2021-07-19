/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELContext;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.util.LangUtils;

public interface UIColumn {

    /**
     * Currently there 3 expression type for attributes like filterBy, sortBy:
     * 1) #{car[column.property]}
     *    this is a valid expression and resolves to a real value
     * 2) #{column.property}
     *    this is a invalid expressions and would need modification before calling the VE
     *    currently not supported anymore
     * 3) #{testView.tableModel.getValue(row, column.keyId)}
     *    this is a valid expression and resolves to a real value
     */
    Pattern VALUE_EXPRESSION_VALID_SYNTAX = Pattern.compile("^#\\{\\w+\\[(.+)]}$");

    /**
     * This method is used to extract the optional "field" of SortMeta and FieldMeta,
     * based on the defined "sortBy" or "filterBy" expression.
     *
     * #{car.name} -> name
     * #{car[column.property]} -> name
     * #{testView.tableModel.getValue(row, column.keyId)} -> null
     *
     * @param context the {@link FacesContext}
     * @param expression the {@link ValueExpression} like "filterBy" or "sortBy"
     * @param dynamic if its a dynamic column
     * @return the "field" name, can be null
     */
    static String extractFieldFromValueExpression(FacesContext context, ValueExpression expression, boolean dynamic) {
        String expressionString = expression.getExpressionString();

        if (dynamic) {
            ELContext elContext = context.getELContext();

            Matcher matcher = VALUE_EXPRESSION_VALID_SYNTAX.matcher(expressionString);
            if (matcher.find()) {
                expressionString = matcher.group(1);
                expression = context.getApplication().getExpressionFactory()
                        .createValueExpression(elContext, "#{" + expressionString  + "}", String.class);
                return (String) expression.getValue(elContext);
            }
        }
        else {
            if (expressionString.startsWith("#{")) {
                expressionString = expressionString.substring(2, expressionString.indexOf('}')); //Remove #{}
                return expressionString.substring(expressionString.indexOf('.') + 1); //Remove var
            }
        }

        return null;
    }

    /**
     * This method is used to build a valid {@link ValueExpression} for sorting or filtering, if the "field"
     * shortcut attribute is defined.
     *
     * field="name" -> #{car.name}
     *
     * @param context the {@link FacesContext}
     * @param var the "var" attribute of the parent table
     * @param field the "field" attribute of the column
     * @return the {@link ValueExpression}
     */
    static ValueExpression createValueExpressionFromField(FacesContext context, String var, String field) {
        if (LangUtils.isValueBlank(var) || LangUtils.isValueBlank(field)) {
            throw new FacesException("Table 'var' and Column 'field' attributes must be non null.");
        }

        return context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + var + "." + field + "}", Object.class);
    }

    ValueExpression getValueExpression(String property);

    String getContainerClientId(FacesContext context);

    String getColumnKey();

    /**
     * Special {@link #getColumnKey()} method which must be used when we are inside e.g.
     * the DataTable "row state".
     *
     * @param parent
     * @param rowIndex
     * @return
     */
    default String getColumnKey(UIComponent parent, int rowIndex) {
        return getColumnKey(parent, String.valueOf(rowIndex));
    }

    /**
     * Special {@link #getColumnKey()} method which must be used when we are inside e.g.
     * the DataTable "row state".
     *
     * @param parent
     * @param rowKey
     * @return
     */
    String getColumnKey(UIComponent parent, String rowKey);

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

    boolean isDraggable();

    MethodExpression getFilterFunction();

    String getField();

    int getResponsivePriority();

    boolean isSortable();

    boolean isFilterable();

    boolean isVisible();

    boolean isSelectRow();

    String getAriaHeaderText();

    MethodExpression getExportFunction();

    String getExportValue();

    boolean isGroupRow();

    String getExportHeaderValue();

    String getExportFooterValue();

    String getSortOrder();

    int getSortPriority();

    int getNullSortOrder();

    boolean isCaseSensitiveSort();

    int getDisplayPriority();
}
