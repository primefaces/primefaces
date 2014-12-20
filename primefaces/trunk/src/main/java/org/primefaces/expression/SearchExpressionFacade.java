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
     * Checks if the {@link UIComponent} has a renderer or not. This check is currently only useful for the update attributes, as a component without renderer
     * can't be updated.
     */
    public static final int VALIDATE_RENDERER = 0x1;

    public static final int IGNORE_NO_RESULT = 0x2;

    public static final int PARENT_FALLBACK = 0x3;

    private static final Logger LOG = Logger.getLogger(SearchExpressionFacade.class.getName());

    private static final String SHARED_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_EXPRESSION_BUFFER";

    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
    public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {
        return resolveComponents(context, source, expressions, NONE);
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
            String[] splittedExpressions = SearchExpressionFacadeUtils.splitExpressions(context, source, expressions);

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
                                
                                if (component == null && !SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
                                    SearchExpressionFacadeUtils.cannotFindComponent(context, source, expression);
                                }

                                components.add(component);
                            }
                        } // default ID case
                        else {
                            UIComponent component = resolveComponentById(source, expression, separatorString, context, options);
                            
                            if (component == null && !SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
                                SearchExpressionFacadeUtils.cannotFindComponent(context, source, expression);
                            }

                            components.add(component);
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
            if (SearchExpressionFacadeUtils.isOptionSet(options, PARENT_FALLBACK)) {
                return source.getParent().getClientId(context);
            }

            return null;
        }

        String buildedExpressions = "";

        String[] splittedExpressions = SearchExpressionFacadeUtils.splitExpressions(context, source, expressions);

        if (splittedExpressions != null && splittedExpressions.length > 0) {

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

        SearchExpressionFacadeUtils.validateExpression(context, source, expression, separatorChar, separatorString);

        if (SearchExpressionFacadeUtils.isPassTroughExpression(expression)) {
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
            if (SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
                return null;
            } else {
                SearchExpressionFacadeUtils.cannotFindComponent(context, source, expression);
            }
        }

        if (SearchExpressionFacadeUtils.isOptionSet(options, VALIDATE_RENDERER) && context.isProjectStage(ProjectStage.Development)) {
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
            if (SearchExpressionFacadeUtils.isOptionSet(options, PARENT_FALLBACK)) {
                return source.getParent();
            }

            return null;
        }

        final char separatorChar = UINamingContainer.getSeparatorChar(context);
        final String separatorString = String.valueOf(separatorChar);

        expression = expression.trim();

        SearchExpressionFacadeUtils.validateExpression(context, source, expression, separatorChar, separatorString);

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

        if (component == null && !SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
            SearchExpressionFacadeUtils.cannotFindComponent(context, source, expression);
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

        String[] subExpressions = SearchExpressionFacadeUtils.split(context, expression, separatorChar);
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
                    if (!SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
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

        if (component == null && !SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
            SearchExpressionFacadeUtils.cannotFindComponent(context, source, expression);
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

        String[] subExpressions = SearchExpressionFacadeUtils.split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {
            
            ArrayList<UIComponent> tempComponents = new ArrayList<UIComponent>();
            
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

                tempComponents.clear();

                for (UIComponent last : lastComponents) {
                    SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);

                    if (resolver instanceof MultiSearchExpressionResolver) {
                        ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, last, expression, tempComponents);
                    } else {
                        UIComponent temp = resolver.resolveComponent(context, source, last, subExpression);

                        if (temp == null) {
                            if (!SearchExpressionFacadeUtils.isOptionSet(options, IGNORE_NO_RESULT)) {
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
}
