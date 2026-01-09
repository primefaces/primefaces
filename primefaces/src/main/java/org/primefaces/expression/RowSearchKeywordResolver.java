/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.expression;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columns.Columns;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIData;
import jakarta.faces.component.search.SearchExpressionContext;
import jakarta.faces.component.search.SearchKeywordContext;
import jakarta.faces.component.search.SearchKeywordResolver;
import jakarta.faces.context.FacesContext;

public class RowSearchKeywordResolver extends SearchKeywordResolver {

    private static final Pattern PATTERN = Pattern.compile("row\\((\\d+)\\)");

    @Override
    public boolean isResolverForKeyword(SearchExpressionContext searchExpressionContext, String keyword) {
        return keyword != null && keyword.startsWith("row(");
    }

    @Override
    public boolean isPassthrough(SearchExpressionContext searchExpressionContext, String keyword) {
        return false;
    }

    @Override
    public boolean isLeaf(SearchExpressionContext searchExpressionContext, String keyword) {
        return true;
    }

    @Override
    public void resolve(SearchKeywordContext expressionContext, UIComponent current, String keyword) {

        FacesContext facesContext = expressionContext.getSearchExpressionContext().getFacesContext();
        int row = validate(facesContext,
                expressionContext.getSearchExpressionContext().getSource(),
                current, keyword);

        UIData data = (UIData) current;

        int rowIndex = data.getRowIndex();
        try {
            data.setRowIndex(row);
            if (!data.isRowAvailable()) {
                return;
            }

            for (UIComponent column : data.getChildren()) {
                // handle dynamic columns
                if (column instanceof Columns) {
                    List<DynamicColumn> dynamicColumns = ((Columns) column).getDynamicColumns();
                    for (int i = 0; i < dynamicColumns.size(); i++) {
                        for (UIComponent comp : column.getChildren()) {
                            expressionContext.invokeContextCallback(comp);
                        }
                    }
                }
                else if (column instanceof UIColumn) {
                    for (UIComponent cell : column.getChildren()) {
                        expressionContext.invokeContextCallback(cell);
                    }
                }
            }
        }
        finally {
            data.setRowIndex(rowIndex);
        }
    }

    protected int validate(FacesContext context, UIComponent source, UIComponent current, String keyword) {

        if (!(current instanceof UIData)) {
            throw new FacesException("The last resolved component must be instance of UIData to support @row. Expression: \"" + keyword
                    + "\" referenced from \"" + current.getClientId(context) + "\".");
        }

        try {
            Matcher matcher = PATTERN.matcher(keyword);

            if (matcher.matches()) {

                int row = Integer.parseInt(matcher.group(1));
                if (row < 0) {
                    throw new FacesException("Row number must be greater than 0. Expression: \"" + keyword + "\"");
                }

                UIData data = (UIData) current;
                if (data.getRowCount() < row + 1) {
                    throw new FacesException("The row count of the target is lesser than the row number. Expression: \"" + keyword + "\"");
                }

                return row;

            }
            else {
                throw new FacesException("Expression does not match following pattern @row(n). Expression: \"" + keyword + "\"");
            }

        }
        catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @row(n). Expression: \"" + keyword + "\"", e);
        }
    }
}
