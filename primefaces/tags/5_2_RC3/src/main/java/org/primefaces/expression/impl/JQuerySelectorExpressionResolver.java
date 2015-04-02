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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.ClientIdSearchExpressionResolver;
import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the jQuery selectors (PFS).
 */
public class JQuerySelectorExpressionResolver implements SearchExpressionResolver, ClientIdSearchExpressionResolver {

    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression) {
        throw new FacesException("jQuery selectors aka PFS are not supported on the server side... expression \"" + expression
                        + "\" referenced from \""+ source.getClientId(context) + "\".");
    }

    public String resolveClientIds(FacesContext context, UIComponent source, UIComponent last, String expression) {
        // just return the complete expression, the client side will take care of it
        // e.g. @(#myButton)
        return expression;
    }
}
