/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.expression.impl;

import javax.faces.component.UIComponent;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@all" keyword.
 */
public class AllExpressionResolver implements SearchExpressionResolver {

	public UIComponent resolveComponent(UIComponent source, UIComponent last, String expression) {
		UIComponent parent = last.getParent();

		while (parent.getParent() != null) {
			parent = parent.getParent();
		}

		return parent;
	}
}
