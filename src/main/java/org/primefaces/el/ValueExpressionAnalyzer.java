/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.el;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.el.CompositeComponentExpressionHolder;

public class ValueExpressionAnalyzer {

    public static ValueReference getReference(ELContext elContext, ValueExpression expression) {

        ValueReference reference = intercept(elContext, expression);

        // check for a CC expression
        if (reference != null && isCompositeComponentReference(reference)) {
            ValueExpression ve = unwrapCompositeComponentReference(reference);
            
            // check for nested CC expressions
            if (ve != null && isCompositeComponentReference(ve.getValueReference(elContext))) {    
                reference = getReference(elContext, ve);
            }
        }

        return reference;
    }

    public static ValueExpression getExpression(ELContext elContext, ValueExpression expression) {

        ValueReference reference = intercept(elContext, expression);

        // check for a CC expression
        if (reference != null && isCompositeComponentReference(reference)) {
            ValueExpression ve = unwrapCompositeComponentReference(reference);

            if (ve != null) {
                // check for nested CC expressions
                if (isCompositeComponentReference(ve.getValueReference(elContext))) {
                    return getExpression(elContext, ve);
                }
                else {
                    return ve;
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
}