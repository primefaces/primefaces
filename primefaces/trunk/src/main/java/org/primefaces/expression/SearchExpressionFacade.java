package org.primefaces.expression;

import java.util.ArrayList;
import java.util.List;

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
	    // split expressions by blank or comma (and ignore blank and commas inside brackets)
		String[] splittedExpressions = split(expressions, EXPRESSION_SEPARATORS);

		ArrayList<UIComponent> components = new ArrayList<UIComponent>();

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
	    // split expressions by blank or comma (and ignore blank and commas inside brackets)
		String[] splittedExpressions = split(expressions, EXPRESSION_SEPARATORS);

		StringBuilder expressionsBuilder = new StringBuilder();

		for (int i = 0; i < splittedExpressions.length; i++) {
			String expression = splittedExpressions[i].trim();

			if (ComponentUtils.isValueBlank(expression)) {
				continue;
			}
			
			if (i != 0 && expressionsBuilder.length() > 0) {
				expressionsBuilder.append(" ");
			}

			String component = resolveComponentForClient(context, source, expression);
			if (component != null) {
				expressionsBuilder.append(component);
			}
		}

		String buildedExpressions = expressionsBuilder.toString();

		// empty expression should resolve to @none
		if (ComponentUtils.isValueBlank(buildedExpressions)) {
			return SearchExpressionConstants.NONE_KEYWORD;
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
	public static String resolveComponentForClient(FacesContext context, UIComponent source, String expression)
	{
		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		expression = expression.trim();

		validateExpression(context, source, expression, separatorChar, separatorString);

		if (SearchExpressionResolverFactory.isPassTroughExpression(expression)) {
			return expression;
		}

		UIComponent component = resolveComponentInternal(context, source, expression, separatorChar, separatorString);
		if (component == null) {
			return null;
		} else {
			return component.getClientId(context);
		}
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

		if (SearchExpressionResolverFactory.isClientExpressionOnly(expression)) {
			throw new FacesException(
					"Client side expression (PFS and @widgetVar) are not supported... Expression: " + expression);
		}

		return resolveComponentInternal(context, source, expression, separatorChar, separatorString);
	}

	/**
	 * Validates the given search expression.
	 * We only validate it, for performance reasons, if the current {@link ProjectStage} is {@link ProjectStage#Development}.
	 *
	 * @param context The {@link FacesContext}.
	 * @param source The source component. E.g. a button.
	 * @param expression The search expression.
	 * @param factory The {@link SearchExpressionResolverFactory}.
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

			// checks for unnestable subexpresions (like @all or @none)
			if (subExpressions.length > 1) {
				for (int j = 0; j < subExpressions.length; j++) {
					String subExpression = subExpressions[j].trim();

					if (!SearchExpressionResolverFactory.isNestable(subExpression)) {
						throw new FacesException("Subexpression \"" + subExpression
								+ "\" in full expression \"" + expression
								+ "\" from \"" + source.getClientId(context) + "\" can not be nested.");
					}
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
				for (int j = 0; j < subExpressions.length; j++) {

					String subExpression = subExpressions[j].trim();

					if (ComponentUtils.isValueBlank(subExpression)) {
						continue;
					}

					// re-add the seperator string here
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

		List<String> tokens = new ArrayList<>();
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
}
