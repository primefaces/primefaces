package org.primefaces.expression.impl;

import javax.faces.component.UIComponent;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for simple id's.
 */
public class IdExpressionResolver implements SearchExpressionResolver
{
	public UIComponent resolve(UIComponent source, UIComponent last, String expression)
	{
		return last.findComponent(expression);
	}
}
