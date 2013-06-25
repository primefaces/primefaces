package org.primefaces.util.expression;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.util.ComponentUtils;

//TODO comments, tests for resolveComponentS
public class SearchExpressionFacade {

	public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {
		String[] splittedExpressions = expressions.split("(,|\\s)(?![^()]*+\\))");

		ArrayList<UIComponent> components = new ArrayList<UIComponent>();

		for (int i = 0; i < splittedExpressions.length; i++) {
			String expression = splittedExpressions[i].trim();

			UIComponent component = resolveComponent(context, source, expression);
			if (component != null) {
				components.add(component);
			}
		}

		return components;
	}

	public static String resolveComponentsForClient(FacesContext context, UIComponent source, String expressions) {
		String[] splittedExpressions = expressions.split("(,|\\s)(?![^()]*+\\))");

		StringBuilder expressionsBuilder = new StringBuilder();

		for (int i = 0; i < splittedExpressions.length; i++) {
			String expression = splittedExpressions[i].trim();

			if (i != 0 && expressionsBuilder.length() > 0) {
				expressionsBuilder.append(" ");
			}

			String component = resolveComponentForClient(context, source, expression);
			if (component != null) {
				expressionsBuilder.append(component);
			}
		}

		String buildedExpressions = expressionsBuilder.toString();

		if (ComponentUtils.isValueBlank(buildedExpressions)) {
			return "@none";
		}

		return buildedExpressions;
	}

	public static String resolveComponentForClient(FacesContext context, UIComponent source, String expression)
	{
		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		//TODO allow wrapping
		SearchExpressionResolverFactory factory = new SearchExpressionResolverFactoryImpl();

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		expression = expression.trim();

		validateExpression(context, source, expression, factory, separatorChar, separatorString);

		if (factory.isPassTroughExpression(expression)) {
			return expression;
		}

		UIComponent component = resolveComponentInternal(context, source, expression, factory, separatorChar, separatorString);
		if (component == null) {
			return null;
		} else {
			return component.getClientId(context);
		}
	}

	public static UIComponent resolveComponent(FacesContext context, UIComponent source, String expression) {

		if (ComponentUtils.isValueBlank(expression)) {
			return null;
		}

		final char separatorChar = UINamingContainer.getSeparatorChar(context);
		final String separatorString = String.valueOf(separatorChar);

		//TODO allow wrapping
		SearchExpressionResolverFactory factory = new SearchExpressionResolverFactoryImpl();

		expression = expression.trim();

		validateExpression(context, source, expression, factory, separatorChar, separatorString);

		if (expression.equals(SearchExpressionConstants.NONE_KEYWORD)) {
			return null;
		}

		if (factory.isClientExpressionOnly(expression)) {
			throw new FacesException(
					"Client side expression (PFS, @clientId, @widgetVar) are not supported... Expression: " + expression);
		}

		return resolveComponentInternal(context, source, expression, factory, separatorChar, separatorString);
	}

	private static void validateExpression(FacesContext context, UIComponent source,
			String expression, SearchExpressionResolverFactory factory,
			char separatorChar, String separatorString) {

		if (context.isProjectStage(ProjectStage.Development)) {

			if (expression.startsWith(separatorString + SearchExpressionConstants.KEYWORD_PREFIX)) {
				throw new FacesException("A expression should not start with the separater char and a keyword. "
						+ "Expression: \"" + expression + "\" from \"" + source.getClientId(context) + "\"");
			}

			String[] subExpressions = expression.split("(\\" + separatorString + ")(?![^()]*+\\))");

			if (subExpressions.length > 1) {
				for (int j = 0; j < subExpressions.length; j++) {
					String subExpression = subExpressions[j].trim();

					if (!factory.isNestable(subExpression)) {
						throw new FacesException("Subexpression \"" + subExpression
								+ "\" in full expression \"" + expression
								+ "\" from \"" + source.getClientId(context) + "\" can not be nested.");
					}
				}
			}
		}
	}

	private static UIComponent resolveComponentInternal(FacesContext context, UIComponent source,
			String expression, SearchExpressionResolverFactory factory,
			char separatorChar, String separatorString) {

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

				String[] subExpressions = expression.split("(\\" + separatorString + ")(?![^()]*+\\))");
				for (int j = 0; j < subExpressions.length; j++) {

					String subExpression = subExpressions[j].trim();

					if (ComponentUtils.isValueBlank(subExpression)) {
						continue;
					}

					// re-add the seperator string here
					// the impl will decide to search absolute or relative then
					if (!subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)
							&& startsWithSeperator
							&& j == 0) {
						subExpression = separatorString + subExpression;
					}

					SearchExpressionResolver resolver = factory.findResolver(subExpression);
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
				SearchExpressionResolver resolver = factory.findResolver(expression);
				component = resolver.resolve(source, source, expression);

				if (component == null) {
					throw new FacesException("Cannot find component for expression \""
							+ expression + "\" referenced from \""
							+ source.getClientId(context) + "\".");
				}
			}
		} else {
			component = source.findComponent(expression);

			if (component == null) {
				throw new FacesException("Cannot find component with expression \""
						+ expression + "\" referenced from \""
						+ source.getClientId(context) + "\".");
			}
		}

		return component;
	}

}
