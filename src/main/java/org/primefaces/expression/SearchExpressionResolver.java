package org.primefaces.expression;

import javax.faces.component.UIComponent;

/**
 * Interface for resolvers, to resolve a {@link UIComponent} by a expression.
 */
public interface SearchExpressionResolver {

	/**
	 * Resolves a {@link UIComponent} for the last or source {@link UIComponent} and for the given
	 * expression string.
	 *
	 * @param source The source component. E.g. a button.
	 * @param last The last resolved component in the chain.
	 * 		If it's not a nested expression, it's the same as the source component.
	 * @param expression The search expression.
	 * @return The resolved {@link UIComponent} or <code>null</code>.
	 */
	UIComponent resolve(UIComponent source, UIComponent last, String expression);
}
