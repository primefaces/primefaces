package org.primefaces.expression;

/**
 * Exchangeable factory which provides a {@link SearchExpressionResolver} for a expression and some other utils.
 */
public interface SearchExpressionResolverFactory {

	/**
	 * Finds a {@link SearchExpressionResolver} for the given expression.
	 * 
	 * @param expression The search expression.
	 * @return The {@link SearchExpressionResolver}.
	 */
	SearchExpressionResolver findResolver(String expression);

	/**
	 * Checks if the given expression must not be resolved by a {@link SearchExpressionResolver},
	 * before rendering it to the client.
	 * e.g. @all or @none.
	 * 
	 * @param expression The search expression.
	 * @return <code>true</code> if it should just be rendered without manipulation or resolving.
	 */
	boolean isPassTroughExpression(String expression);

	/**
	 * Checks if the given expression can just be used for resolving it on the client.
	 * e.g. PFS
	 * 
	 * @param expression The search expression.
	 * @return <code>true</code> if it's a client expression only.
	 */
	boolean isClientExpressionOnly(String expression);

	/**
	 * Checks if the given expression can be nested.
	 * e.g. @form:@parent
	 * This should not be possible e.g. with @none or @all.
	 *
	 * @param expression The search expression.
	 * @return <code>true</code> if it's nestable.
	 */
	boolean isNestable(String expression);
}
