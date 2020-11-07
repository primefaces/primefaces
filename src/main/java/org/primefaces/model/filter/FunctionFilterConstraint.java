package org.primefaces.model.filter;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import java.util.Locale;

public class FunctionFilterConstraint implements FilterConstraint {

    private MethodExpression exprVE;

    public FunctionFilterConstraint(MethodExpression exprVE) {
        this.exprVE = exprVE;
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        return (boolean) exprVE.invoke(ctxt.getELContext(), new Object[]{value, filter, locale});
    }
}
