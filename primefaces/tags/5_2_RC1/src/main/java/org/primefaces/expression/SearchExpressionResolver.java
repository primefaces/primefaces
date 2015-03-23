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
package org.primefaces.expression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Interface for resolvers, to resolve a {@link UIComponent} by a expression.
 */
public interface SearchExpressionResolver {

	/**
	 * Resolves a {@link UIComponent} for the last or source {@link UIComponent} and for the given
	 * expression string.
	 *
     * @param context The {@link FacesContext}.
	 * @param source The source component. E.g. a button.
	 * @param last The last resolved component in the chain.
	 * 		If it's not a nested expression, it's the same as the source component.
	 * @param expression The search expression.
	 * @return The resolved {@link UIComponent} or <code>null</code>.
	 */
	UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression);
}
