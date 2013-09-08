/*
 * Copyright 2009-2013 PrimeTek.
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.util.ComponentUtils;

/**
 * Simple facade for the whole Search Expression module.
 */
public class SearchExpressionFacade {

	private static final Logger LOG = Logger.getLogger(SearchExpressionFacade.class.getName());
	
    private static final char[] EXPRESSION_SEPARATORS = new char[] { ',', ' ' };
    
    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
	public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {

		ArrayList<UIComponent> components = new ArrayList<UIComponent>();

		if (!ComponentUtils.isValueBlank(expressions)) {
		    // split expressions by blank or comma (and ignore blank and commas inside brackets)
			String[] splittedExpressions = split(expressions, EXPRESSION_SEPARATORS);

			if (splittedExpressions != null) {
				
				validateExpressions(context, source, expressions, splittedExpressions);
				
				for (int i = 0; i < splittedExpressions.length; i++) {
					String expression = splittedExpressions[i].trim();
		
					if (ComponentUtils.isValueBlank(expression)) {
						continue;
					}
		
					UIComponent component = resolveComponent(context, source, expression);
					if (component != null) {
						components.add(component);
					}
				}
			}
		}

		return components;
	}

    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClient(FacesContext context, UIComponent source, String expressions) {
	    
		return resolveComponentsForClient(context, source, expressions, false);
	}
	
    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param checkForRenderer Checks if the {@link UIComponent} has a renderer or not.
     * 			This check is currently only useful for the update attributes, as a component without renderer can't be updated. 
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClient(FacesContext context, UIComponent source, String expressions, boolean checkForRenderer) {
	    
		if (ComponentUtils.isValueBlank(expressions)) {
			return null;
		}
		
		// split expressions by blank or comma (and ignore blank and commas inside brackets)
		String[] splittedExpressions = split(expressions, EXPRESSION_SEPARATORS);

		String buildedExpressions = "";

		if (splittedExpressions != null) {
			validateExpressions(context, source, expressions, splittedExpressions);
			
			StringBuilder expressionsBuilder = new StringBuilder();
			
			for (int i = 0; i < splittedExpressions.length; i++) {
				String expression = splittedExpressions[i].trim();
	
				if (ComponentUtils.isValueBlank(expression)) {
					continue;
				}
				
				if (i != 0 && expressionsBuilder.length() > 0) {
					expressionsBuilder.append(" ");
				}
	
				String component = resolveComponentForClient(context, source, expression, checkForRenderer);
				if (component != null) {
					expressionsBuilder.append(component);
				}
			}
			
			buildedExpressions = expressionsBuilder.toString();
		}

		if (ComponentUtils.isValueBlank(buildedExpressions)) {
			return null;
		}

		return buildedExpressions;
	}

    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     * If the expressions are <code>null</code> or empty, the parent's clientId will be returned.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClientWithParentFallback(FacesContext context, UIComponent source, String expressions) {
	    if (ComponentUtils.isValueBlank(expressions)) {
	    	return source.getParent().getClientId(context);
	    }
		
		return resolveComponentsForClient(context, source, expressions, false);
	}
	
    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     * If the expressions are <code>null</code> or empty, the parent's clientId will be returned.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param checkForRenderer Checks if the {@link UIComponent} has a renderer or not.
     * 			This check is currently only useful for the update attributes, as a component without renderer can't be updated. 
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClientWithParentFallback(FacesContext context, UIComponent source, String expressions, boolean checkForRenderer) {
	    if (ComponentUtils.isValueBlank(expressions)) {
	    	return source.getParent().getClientId(context);
	    }
	    
	    return resolveComponentsForClient(context, source, expressions, checkForRenderer);
	}
	
    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression. 
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentForClient(FacesContext context, UIComponent source, String expression) {
		return resolveComponentForClient(context, source, expression, false);
	}
	
    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param checkForRenderer Checks if the {@link UIComponent} has a renderer or not.
     * 			This check is currently only useful for the update attributes, as a component without renderer can't be updated. 
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentForClient(FacesContext context, UIComponent source, String expression, boolean checkForRenderer) {
		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		expression = expression.trim();

		validateExpression(context, source, expression, separatorChar, separatorString);

		if (isPassTroughExpression(expression)) {
			return expression;
		}

		UIComponent component = resolveComponentInternal(context, source, expression, separatorChar, separatorString);
		if (component == null) {
			return null;
		} else {
			
			if (checkForRenderer && context.isProjectStage(ProjectStage.Development)) {
				if (ComponentUtils.isValueBlank(component.getRendererType())) {
					LOG.warning("Can not update component without a attached renderer. "
							+ "Component class: \"" + component.getClass() + "\"");
				}
			}
			
			return component.getClientId(context);
		}
	}

    /**
     * Resolves a {@link UIComponent} for the given expression.
     * If the expression is <code>null</code> or empty, the parent's clientId will be returned.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param fallbackToParent If the expression is null, the parent component will be used.
     * @return A resolved {@link UIComponent} or <code>null</code>.
     */
	public static UIComponent resolveComponentWithParentFallback(FacesContext context, UIComponent source, String expression) {
		if (ComponentUtils.isValueBlank(expression)) {
			return source.getParent();
		}

		return resolveComponent(context, source, expression);
	}
	
    /**
     * Resolves a {@link UIComponent} for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A resolved {@link UIComponent} or <code>null</code>.
     */
	public static UIComponent resolveComponent(FacesContext context, UIComponent source, String expression) {

		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		expression = expression.trim();

		validateExpression(context, source, expression, separatorChar, separatorString);

		if (expression.equals(SearchExpressionConstants.NONE_KEYWORD)) {
			return null;
		}

		if (isClientExpressionOnly(expression)) {
			throw new FacesException(
					"Client side expression (PFS and @widgetVar) are not supported... Expression: " + expression);
		}

		UIComponent component = resolveComponentInternal(context, source, expression, separatorChar, separatorString);
		
		if (component == null) {
			throw new FacesException("Cannot find component with expression \""
					+ expression + "\" referenced from \""
					+ source.getClientId(context) + "\".");
		}

		return component;
	}

	/**
	 * Validates the given search expression.
	 * We only validate it, for performance reasons, if the current {@link ProjectStage} is {@link ProjectStage#Development}.
	 *
	 * @param context The {@link FacesContext}.
	 * @param source The source component. E.g. a button.
	 * @param expression The search expression.
	 * @param separatorChar The separator as char.
	 * @param separatorString The separator as string.
	 */
	private static void validateExpression(FacesContext context, UIComponent source,
			String expression, char separatorChar, String separatorString) {

		if (context.isProjectStage(ProjectStage.Development)) {

		    // checks the whole expression doesn't start with ":@"
		    // keywords are always related to the current component, not absolute or relative
			if (expression.startsWith(separatorString + SearchExpressionConstants.KEYWORD_PREFIX)) {
				throw new FacesException("A expression should not start with the separater char and a keyword. "
						+ "Expression: \"" + expression + "\" from \"" + source.getClientId(context) + "\"");
			}

			// Pattern to split expressions by the separator but not inside parenthesis
			String[] subExpressions = split(expression, separatorChar);

			if (subExpressions != null) {
				// checks for unnestable subexpressions (like @all or @none)
				if (subExpressions.length > 1) {
					for (int j = 0; j < subExpressions.length; j++) {
						String subExpression = subExpressions[j].trim();
	
						if (!isNestable(subExpression)) {
							throw new FacesException("Subexpression \"" + subExpression
									+ "\" in full expression \"" + expression
									+ "\" from \"" + source.getClientId(context) + "\" can not be nested.");
						}
					}
				}
			}
		}
	}

	/**
	 * Validates the given search expressions.
	 * We only validate it, for performance reasons, if the current {@link ProjectStage} is {@link ProjectStage#Development}.
	 *
	 * @param context The {@link FacesContext}.
	 * @param source The source component. E.g. a button.
	 * @param expression The search expression.
	 * @param splittedExpressions The already splitted expressions.
	 */
	private static void validateExpressions(FacesContext context, UIComponent source, String expressions, String[] splittedExpressions) {

		if (context.isProjectStage(ProjectStage.Development)) {
			if (splittedExpressions.length > 1) {
				if (expressions.contains(SearchExpressionConstants.NONE_KEYWORD)
						|| expressions.contains(SearchExpressionConstants.ALL_KEYWORD)) {
					
					throw new FacesException("It's not possible to use @none or @all combined with other expressions."
							+ " Expressions: \"" + expressions
							+ "\" referenced from \"" + source.getClientId(context) + "\"");
				}
			}
		}
	}
	
	private static UIComponent resolveComponentInternal(FacesContext context, UIComponent source,
			String expression, char separatorChar, String separatorString) {

		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		UIComponent component = null;

		// if the complete expression does not contain '@', just call #findComponent on the source component
		if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {

			// if it's not a nested expression (e.g. @parent:@parent), we don't need to loop
			if (expression.contains(separatorString)) {
				boolean startsWithSeperator = expression.charAt(0) == separatorChar;

				// check if the first subExpression starts with ":",
				// this will be re-added later to the first expression (only if it's a ID expression),
				// to check if we need a absolute or relative search
				if (startsWithSeperator) {
					expression = expression.substring(1);
				}

				UIComponent last = source;

				String[] subExpressions = split(expression, separatorChar);
				if (subExpressions != null) {
					for (int j = 0; j < subExpressions.length; j++) {
	
						String subExpression = subExpressions[j].trim();
	
						if (ComponentUtils.isValueBlank(subExpression)) {
							continue;
						}
	
						// re-add the separator string here
						// the impl will decide to search absolute or relative then
						if (startsWithSeperator
						        && j == 0
						        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
							subExpression = separatorString + subExpression;
						}
	
						SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);
						UIComponent temp = resolver.resolve(source, last, subExpression);
	
						if (temp == null) {
							throw new FacesException("Cannot find component for subexpression \"" + subExpression
									+ "\" from component with id \"" + last.getClientId(context)
									+ "\" in full expression \"" + expression
									+ "\" referenced from \"" + source.getClientId(context) + "\".");
						}
	
						last = temp;
					}
				}

				component = last;
			} else {
				// it's a keyword and not nested, just ask our resolvers
				SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
				component = resolver.resolve(source, source, expression);

				if (component == null) {
					throw new FacesException("Cannot find component for expression \""
							+ expression + "\" referenced from \""
							+ source.getClientId(context) + "\".");
				}
			}
		} else {
		    // default ID case
			component = source.findComponent(expression);

			if (component == null) {
				throw new FacesException("Cannot find component with expression \""
						+ expression + "\" referenced from \""
						+ source.getClientId(context) + "\".");
			}
		}

		return component;
	}
	
	/**
	 * Splits the given string by the given separator, but ignoring separator inside parenthese.
     *
	 * @param value The string value.
	 * @param separators The separators.
	 * @return The splitted string.
	 */
	private static String[] split(String value, char... separators) {

		if (value == null) {
			return null;
		}

		List<String> tokens = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();

		int parenthesesCounter = 0;

		char[] charArray = value.toCharArray();
		
		for (char c : charArray) {
			if (c == '(') {
				parenthesesCounter++;
			}

			if (c == ')') {
				parenthesesCounter--;
			}

			if (parenthesesCounter == 0) {
				boolean isSeparator = false;
				for (char separator : separators) {
					if (c == separator) {
						isSeparator = true;
					}
				}
	
				if (isSeparator) {
					// lets add token inside buffer to our tokens
					tokens.add(buffer.toString());
					// now we need to clear buffer
					buffer.delete(0, buffer.length());
				} else {
					buffer.append(c);
				}
			} else {
				buffer.append(c);
			}
		}

		// lets not forget about part after the separator
		tokens.add(buffer.toString());

		return tokens.toArray(new String[tokens.size()]);
	}
	

	/**
     * Checks if the given expression must not be resolved by a {@link SearchExpressionResolver},
     * before rendering it to the client.
     * e.g. @all or @none.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it should just be rendered without manipulation or resolving.
     */
	private static boolean isPassTroughExpression(String expression) {
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
	private static boolean isClientExpressionOnly(String expression) {
		return expression.contains(SearchExpressionConstants.PFS_PREFIX)
				|| expression.contains(SearchExpressionConstants.WIDGETVAR_PREFIX);
	}

	/**
     * Checks if the given expression can be nested.
     * e.g. @form:@parent
     * This should not be possible e.g. with @none or @all.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it's nestable.
     */
	private static boolean isNestable(String expression) {
		return !isPassTroughExpression(expression);
	}
}
