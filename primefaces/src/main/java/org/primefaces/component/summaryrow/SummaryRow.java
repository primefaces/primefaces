/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.summaryrow;

import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.component.api.UIColumn;
import org.primefaces.util.LangUtils;

import jakarta.el.ValueExpression;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = SummaryRow.COMPONENT_TYPE, namespace = SummaryRow.COMPONENT_FAMILY)
@FacesComponentInfo(description = "SummaryRow is a helper component for data grouping.")
@FacesComponentHandler(SummaryRowHandler.class)
public class SummaryRow extends SummaryRowBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SummaryRow";

    /**
     * Returns the expression this summary row groups by. When neither <code>groupBy</code> nor <code>field</code>
     * is set, the group is defined by the table itself (header row, <code>groupRow</code> column or active sort).
     *
     * @param context the {@link FacesContext}
     * @param var the name of the request-scoped variable of the enclosing table
     * @return the group by {@link ValueExpression} or <code>null</code> if this summary row defines no group
     */
    public ValueExpression getGroupByValueExpression(FacesContext context, String var) {
        ValueExpression groupByVE = getValueExpression(PropertyKeys.groupBy);
        if (groupByVE != null) {
            return groupByVE;
        }

        String field = getField();
        return LangUtils.isBlank(field) ? null : UIColumn.createValueExpressionFromField(context, var, field);
    }
}
