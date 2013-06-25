package org.primefaces.util.expression.impl;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;

import org.primefaces.util.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@namingcontainer" keyword.
 */
public class NamingContainerExpressionResolver implements SearchExpressionResolver {

	public UIComponent resolve(UIComponent source, UIComponent last, String expression) {
		UIComponent parent = last.getParent();

		while (parent != null) {
			if (parent instanceof NamingContainer) {
				return parent;
			}

			parent = parent.getParent();
		}

		return null;
	}
}
