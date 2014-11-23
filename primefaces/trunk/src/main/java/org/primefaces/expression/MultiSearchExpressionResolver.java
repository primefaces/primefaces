package org.primefaces.expression;

import java.util.ArrayList;
import javax.faces.component.UIComponent;

/**
 * Interface for resolvers, to resolve multiple {@link UIComponent}s by one expression.
 */
public interface MultiSearchExpressionResolver {

	/**
	 * Resolves multiple {@link UIComponent}s for the last or source {@link UIComponent} and for the given
	 * expression string.
	 *
	 * @param source The source component. E.g. a button.
	 * @param last The last resolved component in the chain.
	 * 		If it's not a nested expression, it's the same as the source component.
	 * @param expression The search expression.
	 * @return The resolved {@link UIComponent}s or <code>null</code>.
	 */
    ArrayList<UIComponent> resolveComponents(UIComponent source, UIComponent last, String expression);
}
