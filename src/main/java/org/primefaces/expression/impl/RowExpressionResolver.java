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
        char separatorChar = UINamingContainer.getSeparatorChar(context);

        StringBuilder clientIds = new StringBuilder();

        for (UIComponent column : data.getChildren()) {
            // handle dynamic columns
            if (column instanceof Columns) {

                List<DynamicColumn> dynamicColumns = ((Columns) column).getDynamicColumns();
                for (int i = 0; i < dynamicColumns.size(); i++) {
                    DynamicColumn dynamicColumn = dynamicColumns.get(i);
                    for (UIComponent comp : column.getChildren()) {

                        if (clientIds.length() > 0) {
                            clientIds.append(" ");
                        }

                        clientIds.append(data.getClientId(context));
                        clientIds.append(separatorChar);
                        clientIds.append(row);
                        clientIds.append(separatorChar);
                        clientIds.append(dynamicColumn.getId());
                        clientIds.append(separatorChar);
                        clientIds.append(i);
                        clientIds.append(separatorChar);
                        clientIds.append(comp.getId());
                    }
                }
            }
            else if (column instanceof UIColumn) {
                for (UIComponent cell : column.getChildren()) {

                    if (clientIds.length() > 0) {
                        clientIds.append(" ");
                    }

                    clientIds.append(data.getClientId(context));
                    clientIds.append(separatorChar);
                    clientIds.append(row);
                    clientIds.append(separatorChar);
                    clientIds.append(cell.getId());
                }
            }
        }

        return clientIds.toString();
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
