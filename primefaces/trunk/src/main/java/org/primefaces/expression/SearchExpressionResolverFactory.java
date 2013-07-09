/*
 * Copyright 2009-2013 PrimeTek.
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
import org.primefaces.expression.impl.IdExpressionResolver;
import org.primefaces.expression.impl.NamingContainerExpressionResolver;
import org.primefaces.expression.impl.ParentExpressionResolver;
import org.primefaces.expression.impl.ThisExpressionResolver;

/**
 * Factory for providing different {@link SearchExpressionResolver} for expressions.
 */
public class SearchExpressionResolverFactory {

	private static final HashMap<String, SearchExpressionResolver> RESOLVER_MAPPING = new HashMap<String, SearchExpressionResolver>();
	private static final IdExpressionResolver ID_EXPRESSION_RESOLVER = new IdExpressionResolver();
	private static final ChildExpressionResolver CHILD_EXPRESSION_RESOLVER = new ChildExpressionResolver();

	static {
		RESOLVER_MAPPING.put(SearchExpressionConstants.THIS_KEYWORD, new ThisExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.COMPOSITE_KEYWORD, new CompositeExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.PARENT_KEYWORD, new ParentExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.FORM_KEYWORD, new FormExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.ALL_KEYWORD, new AllExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.NAMINGCONTAINER_KEYWORD, new NamingContainerExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.NONE_KEYWORD, new NamingContainerExpressionResolver());
	}

	/**
     * Finds a {@link SearchExpressionResolver} for the given expression.
     *
     * @param expression The search expression.
     * @return The {@link SearchExpressionResolver}.
     */
	public static SearchExpressionResolver findResolver(String expression) {
		SearchExpressionResolver resolver = RESOLVER_MAPPING.get(expression);

		if (resolver == null) {
		    // if no resolver found and if it's not a keyword, take it as id
		    if (!expression.startsWith(SearchExpressionConstants.KEYWORD_PREFIX)) {
		        resolver = ID_EXPRESSION_RESOLVER;
		    }
		    else if (expression.startsWith(SearchExpressionConstants.CHILD_PREFIX)) {
		        resolver = CHILD_EXPRESSION_RESOLVER;
		    }
		}

		if (resolver == null) {
		    throw new FacesException("No SearchExpressionResolver avialable for expression \"" + expression + "\"");
		}

		return resolver;
	}

	/**
	 * Prevent instantiation.
	 */
	private SearchExpressionResolverFactory() {

	}
}
