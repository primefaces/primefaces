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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.MultiSearchExpressionResolver;
import org.primefaces.expression.SearchExpressionResolver;
import org.primefaces.util.ComponentTraversalUtils;

public class IdExpressionResolver implements SearchExpressionResolver, MultiSearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@id\\(([\\w-]+)\\)");

    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {
        throw new FacesException("@id likely returns multiple components, therefore it's not supported in #resolveComponent... expression \"" + expression
                + "\" referenced from \"" + source.getClientId(context) + "\".");
    }

    public void resolveComponents(FacesContext context, UIComponent source, UIComponent last, String expression, List<UIComponent> components, int options) {
        ComponentTraversalUtils.withId(
                extractId(expression),
                last,
                components);
    }

    protected String extractId(String expression)
    {
        try {
            Matcher matcher = PATTERN.matcher(expression);

            if (matcher.matches()) {

                return matcher.group(1);

            } else {
                throw new FacesException("Expression does not match following pattern @id(id). Expression: \"" + expression + "\"");
            }

        } catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @id(id). Expression: \"" + expression + "\"", e);
        }
    }
}
