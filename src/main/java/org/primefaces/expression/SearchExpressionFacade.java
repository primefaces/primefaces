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

    private static final Logger LOG = Logger.getLogger(SearchExpressionFacade.class.getName());

    private static final String SHARED_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_EXPRESSION_BUFFER";
    private static final String SHARED_SPLIT_BUFFER_KEY = SearchExpressionFacade.class.getName() +  ".SHARED_SPLIT_BUFFER_KEY";
    private static final String SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY";
    
    private static final char[] EXPRESSION_SEPARATORS = new char[] { ',', ' ' };

    public class Options {
        public static final int NONE = 0x0;

        /**
         * Checks if the {@link UIComponent} has a renderer or not. This check is currently only useful for the update attributes, as a component without renderer
         * can't be updated.
         */
        public static final int VALIDATE_RENDERER = 0x1;

        public static final int IGNORE_NO_RESULT = 0x2;

        public static final int PARENT_FALLBACK = 0x3;
    }

    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
    public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {
        return resolveComponents(context, source, expressions, Options.NONE);
    }

    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @param options The options.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
    public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions, int options) {

        ArrayList<UIComponent> components = new ArrayList<UIComponent>();

        if (!ComponentUtils.isValueBlank(expressions)) {
            String[] splittedExpressions = splitExpressions(context, source, expressions);

            if (splittedExpressions != null && splittedExpressions.length > 0) {
                
                final char separatorChar = UINamingContainer.getSeparatorChar(context);
                final String separatorString = String.valueOf(separatorChar);
                
                for (String splittedExpression : splittedExpressions) {
                    String expression = splittedExpression.trim();

                    if (ComponentUtils.isValueBlank(expression)) {
                        continue;
                    }

                    // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
                    if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.contains(separatorString)) {
                        components.addAll(resolveComponentsByExpressionChain(context, source, expression, separatorChar, separatorString, options));
                    }
                    else {
                        // it's a keyword and not nested, just ask our resolvers
                        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
                            
                            if (resolver instanceof MultiSearchExpressionResolver) {
                                ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, source, expression, components);
                            }
                            else {
                                UIComponent component = resolver.resolveComponent(context, source, source, expression);
                                
                                if (component == null) {
                                    if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                        cannotFindComponent(context, source, expression);
                                    }
                                }
                                else {
                                    components.add(component);
                                }
                            }
                        } // default ID case
                        else {
                            UIComponent component = resolveComponentById(source, expression, separatorString, context, options);

                            if (component == null) {
                                if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                    cannotFindComponent(context, source, expression);
                                }
                            }
                            else {
                                components.add(component);
                            }
                        }
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
    public static String resolveClientIds(FacesContext context, UIComponent source, String expressions) {

        return resolveClientIds(context, source, expressions, Options.NONE);
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
    public static String resolveClientIds(FacesContext context, UIComponent source, String expressions, int options) {

        if (ComponentUtils.isValueBlank(expressions)) {
            if (isOptionSet(options, Options.PARENT_FALLBACK)) {
                return source.getParent().getClientId(context);
            }

            return null;
        }

        String[] splittedExpressions = splitExpressions(context, source, expressions);

        if (splittedExpressions != null && splittedExpressions.length > 0) {

            final char separatorChar = UINamingContainer.getSeparatorChar(context);
            final String separatorString = String.valueOf(separatorChar);

            StringBuilder expressionsBuffer = SharedStringBuilder.get(context, SHARED_EXPRESSION_BUFFER_KEY);

            for (int i = 0; i < splittedExpressions.length; i++) {
                String expression = splittedExpressions[i].trim();

                if (ComponentUtils.isValueBlank(expression)) {
                    continue;
                }

                validateExpression(context, source, expression, separatorChar, separatorString);

                if (isPassTroughExpression(expression)) {
                    if (expressionsBuffer.length() > 0) {
                        expressionsBuffer.append(" ");
                    }
                    expressionsBuffer.append(expression);
                }
                else {
                    // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
                    if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.contains(separatorString)) {
                        String clientIds = resolveClientIdsByExpressionChain(context, source, expression, separatorChar, separatorString, options);
                        if (!ComponentUtils.isValueBlank(clientIds)) {
                            if (expressionsBuffer.length() > 0) {
                                expressionsBuffer.append(" ");
                            }
                            expressionsBuffer.append(clientIds);
                        }
                    }
                    else {
                        // it's a keyword and not nested, just ask our resolvers
                        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
                            
                            if (resolver instanceof ClientIdSearchExpressionResolver) {
                                String clientIds = ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, source, expression);
                                if (!ComponentUtils.isValueBlank(clientIds)) {
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(clientIds);
                                }
                            }
                            else if (resolver instanceof MultiSearchExpressionResolver) {
                                ArrayList<UIComponent> result = new ArrayList<UIComponent>();
                                ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, source, expression, result);
                                for (int j = 0; j < result.size(); j++) {
                                    UIComponent component = result.get(j);
                                    validateRenderer(context, source, component, expression, options);
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(component.getClientId());
                                }
                            }
                            else {
                                UIComponent component = resolver.resolveComponent(context, source, source, expression);
                                
                                if (component == null) {
                                    if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                        cannotFindComponent(context, source, expression);
                                    }
                                }
                                else {
                                    validateRenderer(context, source, component, expression, options);
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(component.getClientId(context));
                                }
                            }
                        }
                        // default ID case
                        else {
                            UIComponent component = resolveComponentById(source, expression, separatorString, context, options);
                            
                            if (component == null) {
                                if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                    cannotFindComponent(context, source, expression);
                                }
                            }
                            else {
                                validateRenderer(context, source, component, expression, options);
                                if (expressionsBuffer.length() > 0) {
                                    expressionsBuffer.append(" ");
                                }
                                expressionsBuffer.append(component.getClientId(context));
                            }
                        }
                    }
                }  
            }

            String clientIds = expressionsBuffer.toString();
            if (!ComponentUtils.isValueBlank(clientIds)) {
                return clientIds;
            }
        }

        return null;
    }

    protected static void validateRenderer(FacesContext context, UIComponent source, UIComponent component, String expression, int options)
    {
        if (isOptionSet(options, Options.VALIDATE_RENDERER) && context.isProjectStage(ProjectStage.Development)) {
            if (ComponentUtils.isValueBlank(component.getRendererType())) {
                LOG.warning("Can not update component \"" + component.getClass().getName()
                        + "\" with id \"" + component.getClientId(context)
                        + "\" without a attached renderer. Expression \"" + expression
                        + "\" referenced from \"" + source.getClientId(context) + "\"");
            }
        }
    }
    
    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
    public static String resolveClientId(FacesContext context, UIComponent source, String expression) {
        return resolveClientId(context, source, expression, Options.NONE);
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
    public static String resolveClientId(FacesContext context, UIComponent source, String expression, int options) {
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

        UIComponent component;

        // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.contains(separatorString)) {
            component = resolveComponentByExpressionChain(context, source, expression, separatorChar, separatorString, options);
        } // it's a keyword and not nested, just ask our resolvers
        else if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);

            if (resolver instanceof ClientIdSearchExpressionResolver) {
                return ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, source, expression);
            } else {
                component = resolver.resolveComponent(context, source, source, expression);
            }
        } // default ID case
        else {
            component = resolveComponentById(source, expression, separatorString, context, options);
        }

        if (component == null) {
            if (isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                return null;
            } else {
                cannotFindComponent(context, source, expression);
            }
        }

        validateRenderer(context, source, component, expression, options);

        return component.getClientId(context);
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

        return resolveComponent(context, source, expression, Options.NONE);
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
            if (isOptionSet(options, Options.PARENT_FALLBACK)) {
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

        if (ComponentUtils.isValueBlank(expression)) {
            return null;
        }

        UIComponent component;

        // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.contains(separatorString)) {
            component = resolveComponentByExpressionChain(context, source, expression, separatorChar, separatorString, options);
        } // it's a keyword and not nested, just ask our resolvers
        else if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
            component = resolver.resolveComponent(context, source, source, expression);
        } // default ID case
        else {
            component = resolveComponentById(source, expression, separatorString, context, options);
        }

        if (component == null && !isOptionSet(options, Options.IGNORE_NO_RESULT)) {
            cannotFindComponent(context, source, expression);
        }

        return component;
    }

    private static UIComponent resolveComponentByExpressionChain(FacesContext context, UIComponent source, String expression, char separatorChar, String separatorString, int options) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        UIComponent last = source;

        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {
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
                UIComponent temp = resolver.resolveComponent(context, source, last, subExpression);

                if (temp == null) {
                    if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
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

        return last;
    }

    private static UIComponent resolveComponentById(UIComponent source, String expression, String separatorString, FacesContext context, int options) {

        // try #findComponent first
        UIComponent component = source.findComponent(expression);

        // try #invokeOnComponent
        // it's required to support e.g. a full client id for a component which is placed inside one/multiple ui:repeats
        if (component == null) {
            // #invokeOnComponent doesn't support the leading seperator char
            String tempExpression = expression;
            if (tempExpression.startsWith(separatorString)) {
                tempExpression = tempExpression.substring(1);
            }

            IdContextCallback callback = new IdContextCallback();
            context.getViewRoot().invokeOnComponent(context, tempExpression, callback);

            component = callback.getComponent();
        }

        if (component == null && !isOptionSet(options, Options.IGNORE_NO_RESULT)) {
            cannotFindComponent(context, source, expression);
        }

        return component;
    }

    private static ArrayList<UIComponent> resolveComponentsByExpressionChain(FacesContext context, UIComponent source, String expression, char separatorChar,
            String separatorString, int options) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        ArrayList<UIComponent> lastComponents = new ArrayList<UIComponent>();
        lastComponents.add(source);

        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {
            
            ArrayList<UIComponent> tempComponents = new ArrayList<UIComponent>();
            
            for (int i = 0; i < subExpressions.length; i++) {

                String subExpression = subExpressions[i].trim();

                if (ComponentUtils.isValueBlank(subExpression)) {
                    continue;
                }

                // re-add the separator string here
                // the impl will decide to search absolute or relative then
                if (startsWithSeperator
                        && i == 0
                        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                    subExpression = separatorString + subExpression;
                }

                SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);
                
                tempComponents.clear();

                for (int j = 0; j < lastComponents.size(); j++) {
                    UIComponent last = lastComponents.get(j);
                    
                    if (resolver instanceof MultiSearchExpressionResolver) {
                        ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, last, subExpression, tempComponents);
                    } else {
                        UIComponent temp = resolver.resolveComponent(context, source, last, subExpression);

                        if (temp == null) {
                            if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                throw new FacesException("Cannot find component for subexpression \"" + subExpression
                                        + "\" from component with id \"" + last.getClientId(context)
                                        + "\" in full expression \"" + expression
                                        + "\" referenced from \"" + source.getClientId(context) + "\".");
                            }
                        }
                        else {
                            tempComponents.add(temp);
                        }
                    }
                }

                lastComponents.clear();
                lastComponents.addAll(tempComponents);
                tempComponents.clear();
            }
        }

        return lastComponents;
    }

    private static String resolveClientIdsByExpressionChain(FacesContext context, UIComponent source, String expression, char separatorChar,
            String separatorString, int options) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        ArrayList<UIComponent> lastComponents = new ArrayList<UIComponent>();
        lastComponents.add(source);

        StringBuilder clientIdsBuilder = null;
        
        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {
            
            ArrayList<UIComponent> tempComponents = new ArrayList<UIComponent>();
            
            for (int i = 0; i < subExpressions.length; i++) {

                String subExpression = subExpressions[i].trim();

                if (ComponentUtils.isValueBlank(subExpression)) {
                    continue;
                }

                // re-add the separator string here
                // the impl will decide to search absolute or relative then
                if (startsWithSeperator
                        && i == 0
                        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                    subExpression = separatorString + subExpression;
                }

                SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);

                tempComponents.clear();

                for (int j = 0; j < lastComponents.size(); j++) {
                    UIComponent last = lastComponents.get(j);
                    
                    // if it's the last expression and the resolver is a ClientIdSearchExpressionResolver, we can call it
                    if (i == subExpressions.length - 1 && resolver instanceof ClientIdSearchExpressionResolver) {
                        String result = ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, last, subExpression);
                        
                        if (!ComponentUtils.isValueBlank(result)) {

                            if (clientIdsBuilder == null) {
                                clientIdsBuilder = SharedStringBuilder.get(SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY);
                            }
                            else if (clientIdsBuilder.length() > 0) {
                                clientIdsBuilder.append(" ");
                            }

                            clientIdsBuilder.append(result);
                        }
                    }
                    else if (resolver instanceof MultiSearchExpressionResolver) {
                        ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, last, subExpression, tempComponents);
                    } else {
                        UIComponent temp = resolver.resolveComponent(context, source, last, subExpression);

                        if (temp == null) {
                            if (!isOptionSet(options, Options.IGNORE_NO_RESULT)) {
                                throw new FacesException("Cannot find component for subexpression \"" + subExpression
                                        + "\" from component with id \"" + last.getClientId(context)
                                        + "\" in full expression \"" + expression
                                        + "\" referenced from \"" + source.getClientId(context) + "\".");
                            }
                        }
                        else {
                            tempComponents.add(temp);
                        }
                    }
                }

                lastComponents.clear();
                lastComponents.addAll(tempComponents);
                tempComponents.clear();
            }
        }

        // already initialized -> last resolver was a ClientIdExpressionResolver
        if (clientIdsBuilder == null) {
            clientIdsBuilder = SharedStringBuilder.get(SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY);

            for (int i = 0; i < lastComponents.size(); i++) {
                UIComponent result = lastComponents.get(i);
                if (clientIdsBuilder.length() > 0) {
                    clientIdsBuilder.append(" ");
                }
                clientIdsBuilder.append(result.getClientId(context));
            }
        
        }

        return clientIdsBuilder.toString();
    }
    
    protected static void cannotFindComponent(FacesContext context, UIComponent source, String expression)
    {
        throw new FacesException("Cannot find component for expression \""
                + expression + "\" referenced from \""
                + source.getClientId(context) + "\".");
    }
    
    protected static String[] splitExpressions(FacesContext context, UIComponent source, String expressions) {

        // split expressions by blank or comma (and ignore blank and commas inside brackets)
        String[] splittedExpressions = split(context, expressions, EXPRESSION_SEPARATORS);

        if (splittedExpressions != null) {

            validateExpressions(context, source, expressions, splittedExpressions);
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
	protected static void validateExpression(FacesContext context, UIComponent source,
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
	protected static void validateExpressions(FacesContext context, UIComponent source, String expressions, String[] splittedExpressions) {

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
	protected static String[] split(FacesContext context, String value, char... separators) {

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
	protected static boolean isPassTroughExpression(String expression) {
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
	protected static boolean isNestable(String expression) {
        return !(expression.contains(SearchExpressionConstants.ALL_KEYWORD)
				|| expression.contains(SearchExpressionConstants.NONE_KEYWORD)
                || expression.contains(SearchExpressionConstants.PFS_PREFIX));
	}

    protected static boolean isOptionSet(int options, int option) {
        return (options & option) != 0;
    }
}
