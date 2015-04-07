package org.primefaces.expression;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class TestMultiSearchExpressionResolver  implements SearchExpressionResolver, MultiSearchExpressionResolver {

    private ArrayList<UIComponent> components;

    public TestMultiSearchExpressionResolver(ArrayList<UIComponent> components) {
        this.components = components;
    }

    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression) {
        throw new UnsupportedOperationException();
    }

    public void resolveComponents(FacesContext context, UIComponent source, UIComponent last, String expression, List<UIComponent> components) {
        components.addAll(this.components);
    }
}
