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
package org.primefaces.expression.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import org.primefaces.expression.ClientIdSearchExpressionResolver;
import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@widgetVar" keyword.
 */
public class WidgetVarExpressionResolver implements SearchExpressionResolver, ClientIdSearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@widgetVar\\((\\w+)\\)");
        
	public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression) {
        
        try {
            Matcher matcher = PATTERN.matcher(expression);

            if (matcher.matches()) {

                WidgetVarVisitCallback visitCallback = new WidgetVarVisitCallback(matcher.group(1));
                context.getViewRoot().visitTree(VisitContext.createVisitContext(context), visitCallback);

                return visitCallback.getComponent();

            } else {
                throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + expression + "\"");
            }

        } catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + expression + "\"", e);
        }
	}

    
    public String resolveClientIds(FacesContext context, UIComponent source, UIComponent last, String expression) {
        // just return the complete expression, the client side will take care of it
        // e.g. @widgetVar(myWidget)
        return expression;
    }
}
