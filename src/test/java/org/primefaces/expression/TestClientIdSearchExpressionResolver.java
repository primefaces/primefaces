package org.primefaces.expression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class TestClientIdSearchExpressionResolver implements SearchExpressionResolver, ClientIdSearchExpressionResolver {

    private String result;

    public TestClientIdSearchExpressionResolver(String result) {
        this.result = result;
    }

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String resolveClientIds(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {
        return result;
    }

}
