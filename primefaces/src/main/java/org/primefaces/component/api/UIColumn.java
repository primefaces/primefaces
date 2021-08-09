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
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.util.LangUtils;

public interface UIColumn {

    /**
     * Used to extract bean's property from a value expression in dynamic columns
     * (e.g #{car[column.property]} = name)
     */
    Pattern DYNAMIC_FIELD_VE_LEGACY_PATTERN = Pattern.compile("^#\\{\\w+\\[([\\w.]+)]}$");

    /**
     * Used to extract bean's property from a value expression in static columns (e.g "#{car.year}" = year)
     */
    Pattern STATIC_FIELD_VE_LEGACY_PATTERN = Pattern.compile("^#\\{\\w+\\.([\\w.]+)}$");

    /**
     * Used to extract UIColumn#field if not defined. Supports strictly two kind of expressions:
     *
     * #{car.name}: name (for static columns)
     * #{car[column.property]}: name (for dynamic columns)
     *
     * @param context the {@link FacesContext}
     * @param expression the {@link ValueExpression} like "filterBy" or "sortBy"
     * @return the "field" name if found, otherwise null
     */
    default String resolveField(FacesContext context, ValueExpression expression) {
        String exprStr = expression.getExpressionString();

        if (isDynamic()) {
            ELContext elContext = context.getELContext();

            Matcher matcher = DYNAMIC_FIELD_VE_LEGACY_PATTERN.matcher(exprStr);
            if (matcher.find()) {
                exprStr = matcher.group(1);
                expression = context.getApplication().getExpressionFactory()
                        .createValueExpression(elContext, "#{" + exprStr  + "}", String.class);
                return (String) expression.getValue(elContext);
            }
        }
        else {
            Matcher matcher = STATIC_FIELD_VE_LEGACY_PATTERN.matcher(exprStr);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    /**
     * Used to build a valid {@link ValueExpression} using {@link UIData#getVar()} and {@link UIColumn#getField()}
     * Mostly used if sortBy and/or filterBy are not defined.
     *
     * var="car" and field="name" -> #{car.name}
     *
     * @param context the {@link FacesContext}
     * @param var the "var" attribute of the parent table
     * @param field the "field" attribute of the column
     * @return the {@link ValueExpression}
     */
    static ValueExpression createValueExpressionFromField(FacesContext context, String var, String field) {
        if (LangUtils.isBlank(var) || LangUtils.isBlank(field)) {
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
