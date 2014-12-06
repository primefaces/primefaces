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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.expression.impl.IdContextCallback;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.SharedStringBuilder;

/**
 * Simple facade for the whole Search Expression module.
 */
public class SearchExpressionFacade {

	public static final int NONE = 0x0;
	
	/**
	 * Checks if the {@link UIComponent} has a renderer or not.
	 * This check is currently only useful for the update attributes, as a component without renderer can't be updated.
	 */
	public static final int VALIDATE_RENDERER = 0x1;

	public static final int IGNORE_NO_RESULT = 0x2;
	
	public static final int PARENT_FALLBACK = 0x3;
	
	
	
	private static final Logger LOG = Logger.getLogger(SearchExpressionFacade.class.getName());

	private static final String SHARED_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_EXPRESSION_BUFFER";
	private static final String SHARED_SPLIT_BUFFER_KEY = SearchExpressionFacade.class.getName() +  ".SHARED_SPLIT_BUFFER_KEY";
	
    private static final char[] EXPRESSION_SEPARATORS = new char[] { ',', ' ' };
    
    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
	public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {

		ArrayList<UIComponent> components = new ArrayList<UIComponent>();

		if (!ComponentUtils.isValueBlank(expressions)) {
		    // split expressions by blank or comma (and ignore blank and commas inside brackets)
			String[] splittedExpressions = split(context, expressions, EXPRESSION_SEPARATORS);

			if (splittedExpressions != null) {
				
				validateExpressions(context, source, expressions, splittedExpressions);

                for (String splittedExpression : splittedExpressions) {
                    String expression = splittedExpression.trim();
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
     * @param expressions The search expressions.
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClient(FacesContext context, UIComponent source, String expressions) {
	    
		return resolveComponentsForClient(context, source, expressions, NONE);
	}
	
    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @param options The options. 
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentsForClient(FacesContext context, UIComponent source, String expressions, int options) {
	    
	    if (ComponentUtils.isValueBlank(expressions)) {
	    	if (isOptionSet(options, PARENT_FALLBACK)) {
	    		return source.getParent().getClientId(context);
	    	}

			return null;
		}
		
		// split expressions by blank or comma (and ignore blank and commas inside brackets)
		String[] splittedExpressions = split(context, expressions, EXPRESSION_SEPARATORS);

		String buildedExpressions = "";

		if (splittedExpressions != null) {
			validateExpressions(context, source, expressions, splittedExpressions);
			
			StringBuilder expressionsBuffer = SharedStringBuilder.get(context, SHARED_EXPRESSION_BUFFER_KEY);
			
			for (int i = 0; i < splittedExpressions.length; i++) {
				String expression = splittedExpressions[i].trim();
	
				if (ComponentUtils.isValueBlank(expression)) {
					continue;
				}
				
				if (i != 0 && expressionsBuffer.length() > 0) {
					expressionsBuffer.append(" ");
				}
	
				String component = resolveComponentForClient(context, source, expression, options);
				if (component != null) {
					expressionsBuffer.append(component);
				}
			}
			
			buildedExpressions = expressionsBuffer.toString();
		}

		if (ComponentUtils.isValueBlank(buildedExpressions)) {
			return null;
		}

		return buildedExpressions;
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
		return resolveComponentForClient(context, source, expression, NONE);
	}
	
    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param options The options. 
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
	public static String resolveComponentForClient(FacesContext context, UIComponent source, String expression, int options) {
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

		return resolveClientIdInternal(context, source, expression, separatorChar, separatorString, options);
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

		return resolveComponent(context, source, expression, NONE);
	}

    /**
     * Resolves a {@link UIComponent} for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param options The options.
     * @return A resolved {@link UIComponent} or <code>null</code>.
     */
	public static UIComponent resolveComponent(FacesContext context, UIComponent source, String expression, int options) {

		if (ComponentUtils.isValueBlank(expression)) {
			if (isOptionSet(options, PARENT_FALLBACK)) {
				return source.getParent();
			}

			return null;
		}

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		expression = expression.trim();

		validateExpression(context, source, expression, separatorChar, separatorString);

		if (expression.equals(SearchExpressionConstants.NONE_KEYWORD)) {
			return null;
		}

		UIComponent component = resolveComponentInternal(context, source, expression, separatorChar, separatorString, options);

		if (component == null && !isOptionSet(options, IGNORE_NO_RESULT)) {
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
						+ "Expression: \"" + expression + "\" referenced from \"" + source.getClientId(context) + "\"");
			}

			// Pattern to split expressions by the separator but not inside parenthesis
			String[] subExpressions = split(context, expression, separatorChar);

			if (subExpressions != null) {
				// checks for unnestable subexpressions (like @all or @none)
				if (subExpressions.length > 1) {
                    for (String subExpression : subExpressions) {
                        subExpression = subExpression.trim();

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
			String expression, char separatorChar, String separatorString, int options) {

		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		UIComponent component;

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

				String[] subExpressions = split(context, expression, separatorChar);
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
						UIComponent temp = resolver.resolveComponent(source, last, subExpression);
	
						
						if (temp == null) {
							if (!isOptionSet(options, IGNORE_NO_RESULT)) {
								throw new FacesException("Cannot find component for subexpression \"" + subExpression
										+ "\" from component with id \"" + last.getClientId(context)
										+ "\" in full expression \"" + expression
										+ "\" referenced from \"" + source.getClientId(context) + "\".");
							} 

							return null;
						}

						last = temp;
					}
				}

				component = last;
			} else {
				// it's a keyword and not nested, just ask our resolvers
				SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
				component = resolver.resolveComponent(source, source, expression);
				
				if (component == null && !isOptionSet(options, IGNORE_NO_RESULT)) {
					throw new FacesException("Cannot find component for expression \""
							+ expression + "\" referenced from \""
							+ source.getClientId(context) + "\".");
				}
			}
		} else {
		    // default ID case
            component = source.findComponent(expression);

            if (component == null) {
                // try #invokeOnComponent
                String tempExpression = expression;
                
                if (tempExpression.startsWith(separatorString)) {
                    tempExpression = tempExpression.substring(1);
                }
                
                IdContextCallback callback = new IdContextCallback();
                context.getViewRoot().invokeOnComponent(context, tempExpression, callback);

                component = callback.getComponent();
            }

			if (component == null && !isOptionSet(options, IGNORE_NO_RESULT)) {
				throw new FacesException("Cannot find component with expression \""
						+ expression + "\" referenced from \""
						+ source.getClientId(context) + "\".");
			}
		}

		return component;
	}
	
	private static String resolveClientIdInternal(FacesContext context, UIComponent source,
			String expression, char separatorChar, String separatorString, int options) {

		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		UIComponent component;

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

				String[] subExpressions = split(context, expression, separatorChar);
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
						UIComponent temp = resolver.resolveComponent(source, last, subExpression);
	
						
						if (temp == null) {
							if (!isOptionSet(options, IGNORE_NO_RESULT)) {
								throw new FacesException("Cannot find component for subexpression \"" + subExpression
										+ "\" from component with id \"" + last.getClientId(context)
										+ "\" in full expression \"" + expression
										+ "\" referenced from \"" + source.getClientId(context) + "\".");
							} 

							return null;
						}

						last = temp;
					}
				}

				component = last;
			} else {
				// it's a keyword and not nested, just ask our resolvers
				SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
                
                if (resolver instanceof ClientIdSearchExpressionResolver) {
                    return ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(source, source, expression);
                }
                else {
                    component = resolver.resolveComponent(source, source, expression);

                    if (component == null && !isOptionSet(options, IGNORE_NO_RESULT)) {
                        throw new FacesException("Cannot find component for expression \""
                                + expression + "\" referenced from \""
                                + source.getClientId(context) + "\".");
                    }
                }
			}
		} else {
		    // default ID case
            component = source.findComponent(expression);

            if (component == null) {
                // try #invokeOnComponent
                String tempExpression = expression;
                
                if (tempExpression.startsWith(separatorString)) {
                    tempExpression = tempExpression.substring(1);
                }
                
                IdContextCallback callback = new IdContextCallback();
                context.getViewRoot().invokeOnComponent(context, tempExpression, callback);

                component = callback.getComponent();
            }

			if (component == null && !isOptionSet(options, IGNORE_NO_RESULT)) {
				throw new FacesException("Cannot find component with expression \""
						+ expression + "\" referenced from \""
						+ source.getClientId(context) + "\".");
			}
		}

        if (component == null)
        {
            return null;
        }
        
        if (isOptionSet(options, VALIDATE_RENDERER) && context.isProjectStage(ProjectStage.Development)) {
            if (ComponentUtils.isValueBlank(component.getRendererType())) {
                LOG.warning("Can not update component \"" + component.getClass().getName()
                        + "\" with id \"" + component.getClientId(context)
                        + "\" without a attached renderer. Expression \"" + expression
                        + "\" referenced from \"" + source.getClientId(context) + "\"");
            }
        }
        
		return component.getClientId(context);
	}
    
	/**
	 * Splits the given string by the given separator, but ignoring separators inside parentheses.
     *
     * @param context The current {@link FacesContext}.
	 * @param value The string value.
	 * @param separators The separators.
	 * @return The splitted string.
	 */
	private static String[] split(FacesContext context, String value, char... separators) {

		if (value == null) {
			return null;
		}

		List<String> tokens = new ArrayList<String>();
		StringBuilder buffer = SharedStringBuilder.get(context, SHARED_SPLIT_BUFFER_KEY);

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
		return expression.contains(SearchExpressionConstants.PFS_PREFIX);
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
        return !(expression.contains(SearchExpressionConstants.ALL_KEYWORD)
				|| expression.contains(SearchExpressionConstants.NONE_KEYWORD)
                || expression.contains(SearchExpressionConstants.PFS_PREFIX));
	}

	private static boolean isOptionSet(int options, int option) {
		return (options & option) != 0;
	}
}
