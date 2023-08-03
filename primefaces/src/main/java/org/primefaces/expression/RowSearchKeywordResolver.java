/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.primefaces.expression;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchKeywordContext;
import javax.faces.component.search.SearchKeywordResolver;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columns.Columns;

public class RowSearchKeywordResolver extends SearchKeywordResolver {

    private static final Pattern PATTERN = Pattern.compile("@row\\((\\d+)\\)");

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

            char separatorChar = UINamingContainer.getSeparatorChar(facesContext);

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
