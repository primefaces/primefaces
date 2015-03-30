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
        if (reference != null) {
            Object base = reference.getBase();
            if (base != null && base instanceof CompositeComponentExpressionHolder) {
                ValueExpression ve = ((CompositeComponentExpressionHolder) base).getExpression((String) reference.getProperty());
                if (ve != null)
                {
                    reference = getReference(elContext, ve);
                }
            }
        }

        return reference;
    }

    public static ValueExpression getExpression(ELContext elContext, ValueExpression expression) {

        ValueReference reference = intercept(elContext, expression);
        if (reference != null) {
            Object base = reference.getBase();
            if (base != null && base instanceof CompositeComponentExpressionHolder) {
                ValueExpression ve = ((CompositeComponentExpressionHolder) base).getExpression((String) reference.getProperty());
                if (ve != null)
                {
                    return ve;
                }
            }
        }

        return expression;
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