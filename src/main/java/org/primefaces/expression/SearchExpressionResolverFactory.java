/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

    private static final HashMap<String, SearchExpressionResolver> RESOLVER_MAPPING = new HashMap<>();

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
