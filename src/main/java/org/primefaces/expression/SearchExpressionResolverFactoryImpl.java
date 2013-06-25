package org.primefaces.expression;

import java.util.HashMap;

import org.primefaces.expression.impl.AllExpressionResolver;
import org.primefaces.expression.impl.CompositeExpressionResolver;
import org.primefaces.expression.impl.FormExpressionResolver;
import org.primefaces.expression.impl.IdExpressionResolver;
import org.primefaces.expression.impl.NamingContainerExpressionResolver;
import org.primefaces.expression.impl.ParentExpressionResolver;
import org.primefaces.expression.impl.ThisExpressionResolver;

/**
 * Default implementation of the {@link SearchExpressionResolverFactory}.
 */
public class SearchExpressionResolverFactoryImpl implements SearchExpressionResolverFactory {

	private static final HashMap<String, SearchExpressionResolver> RESOLVER_MAPPING = new HashMap<String, SearchExpressionResolver>();
	private static final IdExpressionResolver ID_EXPRESSION_RESOLVER = new IdExpressionResolver();

	static {
		RESOLVER_MAPPING.put(SearchExpressionConstants.THIS_KEYWORD, new ThisExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.COMPOSITE_KEYWORD, new CompositeExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.PARENT_KEYWORD, new ParentExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.FORM_KEYWORD, new FormExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.ALL_KEYWORD, new AllExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.NAMINGCONTAINER_KEYWORD, new NamingContainerExpressionResolver());
		RESOLVER_MAPPING.put(SearchExpressionConstants.NONE_KEYWORD, new NamingContainerExpressionResolver());
	}

	public SearchExpressionResolver findResolver(String expression) {
		SearchExpressionResolver resolver = RESOLVER_MAPPING.get(expression);

		// if no resolver found and if it's not a keyword, take it as id
		if (resolver == null && !expression.startsWith(SearchExpressionConstants.KEYWORD_PREFIX)) {
			resolver = ID_EXPRESSION_RESOLVER;
		}

		return resolver;
	}

	public boolean isPassTroughExpression(String expression) {
		return isClientExpressionOnly(expression)
				|| expression.contains(SearchExpressionConstants.ALL_KEYWORD)
				|| expression.contains(SearchExpressionConstants.NONE_KEYWORD);
	}

	public boolean isClientExpressionOnly(String expression) {
		return expression.contains(SearchExpressionConstants.PFS_PREFIX)
				|| expression.contains(SearchExpressionConstants.WIDGETVAR_PREFIX)
				|| expression.contains(SearchExpressionConstants.CLIENTID_PREFIX);
	}

	public boolean isNestable(String expression) {
		return !isPassTroughExpression(expression);
	}
}
