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
 * Factory for providing different {@link SearchExpressionResolver} for expressions and some other utils.
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
     * Checks if the given expression must not be resolved by a {@link SearchExpressionResolver},
     * before rendering it to the client.
     * e.g. @all or @none.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it should just be rendered without manipulation or resolving.
     */
	public static boolean isPassTroughExpression(String expression) {
		return isClientExpressionOnly(expression)
				|| expression.contains(SearchExpressionConstants.ALL_KEYWORD)
				|| expression.contains(SearchExpressionConstants.NONE_KEYWORD);
	}

	/**
     * Checks if the given expression can just be used for resolving it on the client.
     * e.g. PFS
     *
     * @param expression The search expression.
     * @return <code>true</code> if it's a client expression only.
     */
	public static boolean isClientExpressionOnly(String expression) {
		return expression.contains(SearchExpressionConstants.PFS_PREFIX)
				|| expression.contains(SearchExpressionConstants.WIDGETVAR_PREFIX)
				|| expression.contains(SearchExpressionConstants.CLIENTID_PREFIX);
	}

	/**
     * Checks if the given expression can be nested.
     * e.g. @form:@parent
     * This should not be possible e.g. with @none or @all.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it's nestable.
     */
	public static boolean isNestable(String expression) {
		return !isPassTroughExpression(expression);
	}

	/**
	 * Prevent instantiation.
	 */
	private SearchExpressionResolverFactory() {

	}
}
