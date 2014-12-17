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
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.util.SharedStringBuilder;

public class SearchExpressionFacadeUtils {
    
    private static final String SHARED_SPLIT_BUFFER_KEY = SearchExpressionFacadeUtils.class.getName() +  ".SHARED_SPLIT_BUFFER_KEY";
    
    private static final char[] EXPRESSION_SEPARATORS = new char[] { ',', ' ' };
    
    public static void cannotFindComponent(FacesContext context, UIComponent source, String expression)
    {
        throw new FacesException("Cannot find component for expression \""
                + expression + "\" referenced from \""
                + source.getClientId(context) + "\".");
    }
    
    public static String[] splitExpressions(FacesContext context, UIComponent source, String expressions) {

        // split expressions by blank or comma (and ignore blank and commas inside brackets)
        String[] splittedExpressions = SearchExpressionFacadeUtils.split(context, expressions, EXPRESSION_SEPARATORS);

        if (splittedExpressions != null) {

            SearchExpressionFacadeUtils.validateExpressions(context, source, expressions, splittedExpressions);
        }

        return splittedExpressions;
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
	public static void validateExpression(FacesContext context, UIComponent source,
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
	 * @param expressions The search expression.
	 * @param splittedExpressions The already splitted expressions.
	 */
	public static void validateExpressions(FacesContext context, UIComponent source, String expressions, String[] splittedExpressions) {

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
    
	/**
	 * Splits the given string by the given separator, but ignoring separators inside parentheses.
     *
     * @param context The current {@link FacesContext}.
	 * @param value The string value.
	 * @param separators The separators.
	 * @return The splitted string.
	 */
	public static String[] split(FacesContext context, String value, char... separators) {

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
	public static boolean isPassTroughExpression(String expression) {
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
	public static boolean isNestable(String expression) {
        return !(expression.contains(SearchExpressionConstants.ALL_KEYWORD)
				|| expression.contains(SearchExpressionConstants.NONE_KEYWORD)
                || expression.contains(SearchExpressionConstants.PFS_PREFIX));
	}

	public static boolean isOptionSet(int options, int option) {
		return (options & option) != 0;
	}
    
}
