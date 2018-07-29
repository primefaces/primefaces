/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.expression;

import java.util.HashMap;

import javax.faces.FacesException;

import org.primefaces.expression.impl.AllExpressionResolver;
import org.primefaces.expression.impl.ChildExpressionResolver;
import org.primefaces.expression.impl.CompositeExpressionResolver;
import org.primefaces.expression.impl.FormExpressionResolver;
import org.primefaces.expression.impl.FindComponentExpressionResolver;
import org.primefaces.expression.impl.IdExpressionResolver;
import org.primefaces.expression.impl.JQuerySelectorExpressionResolver;
import org.primefaces.expression.impl.NamingContainerExpressionResolver;
import org.primefaces.expression.impl.NextExpressionResolver;
import org.primefaces.expression.impl.NoneExpressionResolver;
import org.primefaces.expression.impl.ParentExpressionResolver;
import org.primefaces.expression.impl.PreviousExpressionResolver;
import org.primefaces.expression.impl.RootExpressionResolver;
import org.primefaces.expression.impl.RowExpressionResolver;
import org.primefaces.expression.impl.ThisExpressionResolver;
import org.primefaces.expression.impl.WidgetVarExpressionResolver;

/**
 * Factory for providing different {@link SearchExpressionResolver} for expressions.
 */
public class SearchExpressionResolverFactory {

    private static final HashMap<String, SearchExpressionResolver> RESOLVER_MAPPING = new HashMap<String, SearchExpressionResolver>();

    private static final FindComponentExpressionResolver FIND_COMPONENT_EXPRESSION_RESOLVER = new FindComponentExpressionResolver();

    static {
        RESOLVER_MAPPING.put(SearchExpressionConstants.THIS_KEYWORD, new ThisExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.COMPOSITE_KEYWORD, new CompositeExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.PARENT_KEYWORD, new ParentExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.FORM_KEYWORD, new FormExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.ALL_KEYWORD, new AllExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.NAMINGCONTAINER_KEYWORD, new NamingContainerExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.NONE_KEYWORD, new NoneExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.NEXT_KEYWORD, new NextExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.PREVIOUS_KEYWORD, new PreviousExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.CHILD_KEYWORD, new ChildExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.WIDGETVAR_KEYWORD, new WidgetVarExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.KEYWORD_PREFIX, new JQuerySelectorExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.ROW_KEYWORD, new RowExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.ID_KEYWORD, new IdExpressionResolver());
        RESOLVER_MAPPING.put(SearchExpressionConstants.ROOT_KEYWORD, new RootExpressionResolver());
    }

    /*
     * Prevent instantiation.
     */
    private SearchExpressionResolverFactory() {

    }

    /**
     * Finds a {@link SearchExpressionResolver} for the given expression.
     *
     * @param expression The search expression.
     * @return The {@link SearchExpressionResolver}.
     */
    public static SearchExpressionResolver findResolver(final String expression) {
        SearchExpressionResolver resolver = null;

        if (expression.startsWith(SearchExpressionConstants.KEYWORD_PREFIX)) {
            // check if it's an expression with parameter
            int parenthesisPosition = expression.indexOf('(');
            if (parenthesisPosition > 0) {
                String expressionWithoutParam = expression.substring(0, parenthesisPosition);
                resolver = RESOLVER_MAPPING.get(expressionWithoutParam);
            }
            else {
                resolver = RESOLVER_MAPPING.get(expression);
            }
        }
        else {
            // if it's not a keyword, just delegate it to JSF
            resolver = FIND_COMPONENT_EXPRESSION_RESOLVER;
        }

        if (resolver == null) {
            throw new FacesException("No SearchExpressionResolver available for expression \"" + expression + "\"");
        }

        return resolver;
    }

    public static void registerResolver(final String keyword, final SearchExpressionResolver resolver) {
        RESOLVER_MAPPING.put(keyword, resolver);
    }

    public static SearchExpressionResolver removeResolver(final String keyword) {
        return RESOLVER_MAPPING.remove(keyword);
    }
}
