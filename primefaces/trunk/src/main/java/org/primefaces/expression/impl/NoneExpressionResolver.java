package org.primefaces.expression.impl;

import javax.faces.component.UIComponent;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@none" keyword.
 */
public class NoneExpressionResolver implements SearchExpressionResolver {

	public UIComponent resolve(UIComponent source, UIComponent last, String expression) {
		return null;
	}
}
