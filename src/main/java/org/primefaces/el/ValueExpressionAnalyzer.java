package org.primefaces.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.el.CompositeComponentExpressionHolder;

public class ValueExpressionAnalyzer {

    public static ValueReference getReference(ELContext elContext, ValueExpression expression) {
        
        if (expression == null) {
            return null;
        }
        
        InterceptingResolver resolver = new InterceptingResolver(elContext.getELResolver());

        try {
            expression.setValue(new InterceptingContext(elContext, resolver), null);
        } catch (ELException ele) {
            return null;
        }

        ValueReference reference = resolver.getValueReference();
        if (reference != null) {
            Object base = reference.getBase();
            if (base instanceof CompositeComponentExpressionHolder) {
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
        
        if (expression == null) {
            return null;
        }
        
        InterceptingResolver resolver = new InterceptingResolver(elContext.getELResolver());

        try {
            expression.setValue(new InterceptingContext(elContext, resolver), null);
        } catch (ELException ele) {
            return null;
        }

        ValueReference reference = resolver.getValueReference();
        if (reference != null) {
            Object base = reference.getBase();
            if (base instanceof CompositeComponentExpressionHolder) {
                ValueExpression ve = ((CompositeComponentExpressionHolder) base).getExpression((String) reference.getProperty());
                if (ve != null)
                {
                    return getExpression(elContext, ve);
                }
            }
        }

        return expression;
    }
}