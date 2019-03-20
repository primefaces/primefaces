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
package org.primefaces.el;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.context.FacesContext;
import javax.faces.el.CompositeComponentExpressionHolder;
import org.primefaces.context.PrimeApplicationContext;

public class ValueExpressionAnalyzer {

    private ValueExpressionAnalyzer() {
    }

    public static ValueReference getReference(ELContext elContext, ValueExpression expression) {

        if (expression == null) {
            return null;
        }

        ValueReference reference = toValueReference(expression, elContext);

        // check for a CC expression
        if (reference != null && isCompositeComponentReference(reference)) {
            ValueExpression unwrapped = unwrapCompositeComponentReference(reference);

            // check for nested CC expressions
            if (unwrapped != null) {
                ValueReference unwrappedRef = toValueReference(unwrapped, elContext);
                if (isCompositeComponentReference(unwrappedRef)) {
                    return getReference(elContext, unwrapped);
                }
                else {
                    return unwrappedRef;
                }
            }
        }

        return reference;
    }

    public static ValueExpression getExpression(ELContext elContext, ValueExpression expression) {

        if (expression == null) {
            return null;
        }

        // Unwrapping is required e.g. for p:graphicImage to support nested expressions in composites
        // The unwrapping requires EL 2.2
        if (PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getEnvironment().isAtLeastEl22()) {
            ValueReference reference = toValueReference(expression, elContext);

            // check for a CC expression
            if (reference != null && isCompositeComponentReference(reference)) {
                ValueExpression unwrapped = unwrapCompositeComponentReference(reference);

                if (unwrapped != null) {
                    // check for nested CC expressions
                    if (isCompositeComponentReference(toValueReference(unwrapped, elContext))) {
                        return getExpression(elContext, unwrapped);
                    }
                    else {
                        return unwrapped;
                    }
                }
            }
        }

        return expression;
    }

    public static boolean isCompositeComponentReference(ValueReference vr) {
        return vr != null && vr.getBase() != null && vr.getBase() instanceof CompositeComponentExpressionHolder;
    }

    public static ValueExpression unwrapCompositeComponentReference(ValueReference vr) {
        return ((CompositeComponentExpressionHolder) vr.getBase()).getExpression((String) vr.getProperty());
    }

    public static ValueReference intercept(ELContext elContext, ValueExpression expression) {

        if (expression == null) {
            return null;
        }

        InterceptingResolver resolver = new InterceptingResolver(elContext.getELResolver());
        ELContext interceptingContext = new InterceptingContext(elContext, resolver);

        // #getType throws a PropertyNotFoundException when a sub-expression is null
        expression.getType(interceptingContext);

        // intercept EL calls
        expression.getValue(interceptingContext);

        return resolver.getValueReference();
    }

    public static ValueReference toValueReference(ValueExpression ve, ELContext elContext) {
        ValueReference reference = ve.getValueReference(elContext);

        // e.g. TagValueExpression returns null
        if (reference == null) {
            reference = intercept(elContext, ve);
        }

        return reference;
    }
}
