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
package org.primefaces.expression.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.columns.Columns;
import org.primefaces.expression.ClientIdSearchExpressionResolver;
import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@row" keyword.
 */
public class RowExpressionResolver implements SearchExpressionResolver, ClientIdSearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@row\\((\\d+)\\)");

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {
        throw new FacesException("@row likely returns multiple components, therefore it's not supported in #resolveComponent... expression \""
                + expression + "\" referenced from \"" + source.getClientId(context) + "\".");
    }

    @Override
    public String resolveClientIds(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {

        int row = validate(context, source, last, expression);
        UIData data = (UIData) last;
        char seperatorChar = UINamingContainer.getSeparatorChar(context);

        String clientIds = "";

        for (UIComponent column : data.getChildren()) {
            // handle dynamic columns
            if (column instanceof Columns) {

                List<DynamicColumn> dynamicColumns = ((Columns) column).getDynamicColumns();
                for (int i = 0; i < dynamicColumns.size(); i++) {
                    DynamicColumn dynamicColumn = dynamicColumns.get(i);
                    for (UIComponent comp : column.getChildren()) {

                        if (clientIds.length() > 0) {
                            clientIds += " ";
                        }

                        clientIds += data.getClientId(context) + seperatorChar + row + seperatorChar
                                + dynamicColumn.getId() + seperatorChar + i + seperatorChar + comp.getId();
                    }
                }
            }
            else if (column instanceof UIColumn) {
                for (UIComponent cell : column.getChildren()) {

                    if (clientIds.length() > 0) {
                        clientIds += " ";
                    }

                    clientIds += data.getClientId(context) + seperatorChar + row + seperatorChar + cell.getId();
                }
            }
        }

        return clientIds;
    }

    protected int validate(FacesContext context, UIComponent source, UIComponent last, String expression) {

        if (!(last instanceof UIData)) {
            throw new FacesException("The last resolved component must be instance of UIData to support @row. Expression: \"" + expression
                    + "\" referenced from \"" + last.getClientId(context) + "\".");
        }

        try {
            Matcher matcher = PATTERN.matcher(expression);

            if (matcher.matches()) {

                int row = Integer.parseInt(matcher.group(1));
                if (row < 0) {
                    throw new FacesException("Row number must be greater than 0. Expression: \"" + expression + "\"");
                }

                UIData data = (UIData) last;
                if (data.getRowCount() < row + 1) {
                    throw new FacesException("The row count of the target is lesser than the row number. Expression: \"" + expression + "\"");
                }

                return row;

            }
            else {
                throw new FacesException("Expression does not match following pattern @row(n). Expression: \"" + expression + "\"");
            }

        }
        catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @row(n). Expression: \"" + expression + "\"", e);
        }
    }

}
