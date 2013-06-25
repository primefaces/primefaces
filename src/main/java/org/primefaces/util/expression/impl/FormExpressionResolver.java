package org.primefaces.util.expression.impl;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;

import org.primefaces.util.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@form" keyword.
 */
public class FormExpressionResolver implements SearchExpressionResolver {

	public UIComponent resolve(UIComponent source, UIComponent last, String expression) {
		UIComponent parent = last.getParent();

		while (parent != null) {
			if (parent instanceof UIForm) {
				return parent;
			}

			parent = parent.getParent();
		}

		return null;
	}
}
